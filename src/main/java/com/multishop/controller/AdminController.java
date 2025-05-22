package com.multishop.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.multishop.enums.OrderStatus;
import com.multishop.model.Admin;
import com.multishop.model.Category;
import com.multishop.model.Customer;
import com.multishop.model.Delivery_Person;
import com.multishop.model.Order;
import com.multishop.model.Shop;
import com.multishop.service.AdminService;
import com.multishop.service.CategoryService;
import com.multishop.service.CustomerService;
import com.multishop.service.DeliveryService;
import com.multishop.service.OrderService;
import com.multishop.service.ProductService;
import com.multishop.service.ProductShopService;
import com.multishop.service.ShopService;
import com.multishop.service.StorageService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ecom")
public class AdminController 
{
	@Autowired 
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AdminService admSer;
	@Autowired
	private CustomerService cusSer;
	@Autowired
	private DeliveryService deliSer;
	@Autowired
	private ShopService shopSer;
	@Autowired
	private CategoryService catSer;
	@Autowired
	private ProductService prodSer;
	@Autowired
	private StorageService storageSer;
	@Autowired
	private ProductShopService productShopService;
	@Autowired
	private OrderService orderSer;

	private final String profiles = "profiles";
	private final String deliProfile = "deli-profiles";
	private final String customerProfile = "customer-profiles";
	
	@GetMapping("/admin")
	public String start()
	{
		return "redirect:/ecom/login_page_admin";
	}
	
	
	@GetMapping("/login_page_admin")
	public String logInPage()
	{
		return "/admin/login_admin";
	}
	
	@GetMapping("/register_page_admin")
	public String registerPage(Model model)
	{
		model.addAttribute("admin",new Admin());
		return "/admin/register_admin";
	}
	
	@GetMapping("/method_register")
	public String register(@ModelAttribute("admin") Admin admin, Model model)
	{
		String email = admin.getEmail();
		String encodedPassword = passwordEncoder.encode(admin.getPassword());
	    if (admSer.findByEmail(email).isEmpty()) 
	    {
	    	admin.setPassword(encodedPassword);
	        admSer.saveAdmin(admin);
	        return "/admin/login_admin";
	    }
	    model.addAttribute("errorMessage", "Your email already exists. Try again with a different email.");
	    return "/admin/register_admin";
	}
	
	@GetMapping("/method_login")
	public String logInAdmin(@RequestParam("email")String em, @RequestParam("password") String pass, Model model, HttpSession session)
	{
		Optional<Admin> admin = admSer.findByEmailandPassword(em, pass);
		if(!admin.isEmpty())
		{
			session.setAttribute("loggedInAdmin", admin);
        	return "redirect:/ecom/admin_home";
		}
		else
		{
			model.addAttribute("errorMessage", "Your credentials are wrong!. Please Try again.");
			return "/admin/login_admin";
		}
	}

	@GetMapping("/admin_home")
	public String adminHomePage(HttpSession session, Model model)
	{
		Admin adm = isSessionAvailable(session);
		Map<String, Integer> categoryStats = catSer.getCategoryStats();
        List<String> categoryNames = new ArrayList<>(categoryStats.keySet());
        List<Integer> categoryCounts = new ArrayList<>(categoryStats.values());
        Map<String, Double> monthlySales = orderSer.getMonthlySales();

		if (adm != null) {
	        model.addAttribute("admin", adm);
	        model.addAttribute("customerSize" ,cusSer.findAllRecord().size());
	        model.addAttribute("shopSize", getVerifiedShops(shopSer.findAllRecord()).size());
	        model.addAttribute("deliSize", deliSer.findAllRecord().size());
	        model.addAttribute("productSize", prodSer.findAllRecord().size());
	        model.addAttribute("categorySize", catSer.findAllRecord().size());
	        model.addAttribute("orderSize", orderSer.findAllRecord().size());
	        model.addAttribute("recentOrders", orderSer.getRecentOrders());
	        model.addAttribute("categoryNames", categoryNames);
	        model.addAttribute("categoryCounts", categoryCounts);
	        model.addAttribute("months", monthlySales.keySet());
	        model.addAttribute("monthlySalesData", monthlySales.values());
	        return "/admin/home_admin";
	    } else {
	        return "redirect:/ecom/admin";
	    }
	}

	@GetMapping("/shop_mng_page")
	public String shopManagement(HttpSession session, Model model,
									@RequestParam(defaultValue = "0") int page, 
			                        @RequestParam(defaultValue = "6") int size) {
		Admin adm = isSessionAvailable(session);
		if (adm != null) {
		    model.addAttribute("admin", adm);
		    Pageable pageable = PageRequest.of(page, size);
		    model.addAttribute("shopPage", shopSer.findAllShops(pageable));
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", shopSer.findAllShops(pageable).getTotalPages());
		    return "/admin/shop_mng";
		}
		return "redirect:/ecom/admin";
	}
	@GetMapping("/admin/delete-shop/{shopId}") 
	public String deleteShop(@PathVariable("shopId") long shopId, HttpSession session, Model model,
								RedirectAttributes redirectAttributes) {
	    Admin admin = isSessionAvailable(session);
	    if (admin != null) {
	        Optional<Shop> tmp = shopSer.findShopById(shopId);
	        if (tmp.isEmpty()) 
	        	redirectAttributes.addFlashAttribute("error", "An error occured! Try Again.");
	        else {
	        	Long childSize = productShopService.isChildExists(shopId);
	        	if(childSize > 0) {
	        		redirectAttributes.addFlashAttribute("error", "Deletion Failed: This record cannot be deleted because it has linked child records. Please remove the associated records first.");
	    			return "redirect:/ecom/shop_mng_page";
	        	}
	        	tmp.get().setDeleted(true);
	        	shopSer.saveShop(tmp.get());
	        }
	    }
	    return "redirect:/ecom/shop_mng_page";
	}
	
	@GetMapping("/admin/search-shops")
	public String searchShops(@RequestParam(value = "query", required = false) String query, HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "6") int size) {
		Admin adm = isSessionAvailable(session);
		if (adm != null) {
			model.addAttribute("admin", adm);
			if (query != null && !query.trim().isEmpty()) {
			    Pageable pageable = PageRequest.of(page, size);
		        Page<Shop> shopPage = shopSer.findShopByName(pageable, query);
			    model.addAttribute("shopPage", shopPage);
		        model.addAttribute("currentPage", page);
		        model.addAttribute("totalPages", shopPage.getTotalPages());
			}
			else 
				model.addAttribute("shops", shopSer.findAllRecord());
			return "/admin/shop_mng";
		} 
		return "redirect:/ecom/admin";
	}
	
	@PostMapping("/delete-order/{orderId}")
	public String deleteOrder(@PathVariable("orderId") long orderId, HttpSession session, Model model,
								RedirectAttributes redirectAttributes) 
	{
	    Admin admin = isSessionAvailable(session);
	    if (admin != null) 
	    {
	        Optional<Order> tmp = orderSer.findOrderById(orderId);
	        if (tmp.isEmpty()) 
	        {
	        	redirectAttributes.addFlashAttribute("error", "An error occured! Try Again.");
	        } 
	        else 
	        {
	        	tmp.get().setDeleted(true);
	        	tmp.get().getOrderDetails().clear();
	        	orderSer.saveOrder(tmp.get());
	        }
	    }
	    return "redirect:/ecom/view-orders";
	}

	@GetMapping("/product_mng_page")
	public String productManagement(HttpSession session, Model model) {
		Object attribute = session.getAttribute("loggedInAdmin");
		if (attribute instanceof Optional<?> optional && optional.isPresent() && optional.get() instanceof Admin admin) {
		    model.addAttribute("admin", admin);
			model.addAttribute("products", prodSer.findAllRecord());
			model.addAttribute("allCategories", catSer.findAllRecord());
			model.addAttribute("allShops", getVerifiedShops(shopSer.findAllRecord()));
			return "/admin/product_mng";
		} return "redirect:/ecom/admin";
	}
	
	@GetMapping("/admin/search-product")
	public String searchProducts(
	    @RequestParam(required = false) String query, Model model, HttpSession session, @RequestParam(required = false) String shopId,
	    @RequestParam(required = false) String categoryId ) {
	    Admin adm = isSessionAvailable(session);
	    if(adm == null)
	    	return "redirect:/ecom/admin";
	    model.addAttribute("admin", adm);
	    model.addAttribute("products", prodSer.searchProducts( query, parseLongSafe(categoryId), parseLongSafe(shopId) ));
	    model.addAttribute("allCategories", catSer.findAllRecord());
	    model.addAttribute("allShops", getVerifiedShops(shopSer.findAllRecord()));
	    model.addAttribute("selectedCategoryId", parseLongSafe(categoryId));
	    model.addAttribute("selectedShopId", parseLongSafe(shopId));
	    return "/admin/product_mng";
	}

	@GetMapping("/customer_mng_page")
	public String customerManagement(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "6") int size) {
		Admin admin = isSessionAvailable(session);
		if (admin != null) {
	        model.addAttribute("admin", admin);
	        Pageable pageable = PageRequest.of(page, size);
	        Page<Customer> customerPage = cusSer.findAllCustomer(pageable);
	        model.addAttribute("customerPage", customerPage);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", customerPage.getTotalPages());
	        return "/admin/customer_mng";
	    }
	    return "redirect:/ecom/admin";
	}
	@GetMapping("/delete-customer/{customerId}")
	public String deleteCustomer(@PathVariable("customerId") long customerId, HttpSession session, Model model,
								RedirectAttributes redirectAttributes) {
	    Admin admin = isSessionAvailable(session);
	    if (admin != null) {
	        Optional<Customer> tmp = cusSer.findCustomerById(customerId);
	        if (tmp.isEmpty()) 
	        	redirectAttributes.addFlashAttribute("error", "An error occured! Try Again.");
	        else 
	        {
	        	int childSize = tmp.get().orders.size();
	        	if(childSize > 0) {
	        		redirectAttributes.addFlashAttribute("error", "Deletion Failed: This record cannot be deleted because it has linked child records. Please remove the associated records first.");
	    			return "redirect:/ecom/customer_mng_page";
	        	}
	        	tmp.get().setDeleted(true);
	        	tmp.get().orders.clear();
	        	cusSer.saveCustomer(tmp.get());
	        }
	    }
	    return "redirect:/ecom/customer_mng_page";
	}
	
	@GetMapping("/admin/search-customer")
	public String searchCustomer(@RequestParam(value = "query", required = false) String query, 
            HttpSession session, 
            Model model,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "6") int size) 
	{
		Admin adm = isSessionAvailable(session);
		if (adm != null) 
		{
			model.addAttribute("admin", adm);
			if (query != null && !query.trim().isEmpty()) 
			{
			    Pageable pageable = PageRequest.of(page, size);
		        Page<Customer> customerPage = cusSer.findCustomerByName(pageable, query);
			    model.addAttribute("customerPage", customerPage);
		        model.addAttribute("currentPage", page);
		        model.addAttribute("totalPages", customerPage.getTotalPages());
			}
			return "/admin/customer_mng";
		} 
		return "redirect:/ecom/admin";
	}

	@GetMapping("/product_category_mgn_page")
	public String productCategoryManagement(HttpSession session, Model model) {
		Admin adm = isSessionAvailable(session);
		if (adm != null) {
			model.addAttribute("admin", adm);
			model.addAttribute("categories", catSer.findAllRecord());
			model.addAttribute("category", new Category());
			return "/admin/product_category_mng";
		} return "redirect:/ecom/admin";
	}
	@GetMapping("/add_new_category")
	public String addNewCategory(@ModelAttribute("category") Category category, Model model) {
		Category inputCategory = catSer.getCategory(category.getCategoryName());
		if(inputCategory == null) {
			catSer.saveCategory(category);
			return "redirect:/ecom/product_category_mgn_page";
		}
		model.addAttribute("errorMessage", "Already Existed Category. Try with a different one");
		return "redirect:/ecom/product_category_mgn_page";
	}
	@GetMapping("/update-category")
	public String updateCategory( @RequestParam("categoryId") long categoryId, @RequestParam("updatedName") String updatedName,
	        Model model, HttpSession session) {
	    Admin admin = isSessionAvailable(session);
	    Category cat = catSer.getCategory(categoryId);
	    cat.setCategoryName(updatedName);
	    catSer.saveCategory(cat);
	    model.addAttribute("admin", admin);
	    model.addAttribute("categories", catSer.findAllRecord());
	    model.addAttribute("category", new Category());
	    return "/admin/product_category_mng";
	}
	@GetMapping("/delete-category")
	public String deleteCategory(@RequestParam("categoryId") long categoryId, Model model, HttpSession session,
								RedirectAttributes redirectAttributes) {
		Admin adm = isSessionAvailable(session);
		Category cat = catSer.getCategory(categoryId);
		Long childSize = catSer.isChildExists(cat.categoryId);
		if(childSize > 0) {
			redirectAttributes.addFlashAttribute("error", "Deletion Failed: This record cannot be deleted because it has linked child records. Please remove the associated records first.");
			return "redirect:/ecom/product_category_mgn_page";
		}
		cat.isDeleted = true;
		catSer.saveCategory(cat);
		model.addAttribute("admin", adm);
	    model.addAttribute("categories", catSer.findAllRecord());
	    model.addAttribute("category", new Category());
	    return "/admin/product_category_mng";
	}
	
	@GetMapping("/admin/search-deli")
	public String searchDeli(@RequestParam(value = "query", required = false) String query, 
            HttpSession session, 
            Model model,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "6") int size) 
	{
		Admin adm = isSessionAvailable(session);
		if (adm != null) 
		{
			model.addAttribute("admin", adm);
			if (query != null && !query.trim().isEmpty()) 
			{
			    Pageable pageable = PageRequest.of(page, size);
		        Page<Delivery_Person> deliPage = deliSer.findDeliByName(pageable, query);
			    model.addAttribute("deliPage", deliPage);
		        model.addAttribute("currentPage", page);
		        model.addAttribute("totalPages", deliPage.getTotalPages());
			}
			return "/admin/deli_mng";
		} 
		return "redirect:/ecom/admin";
	}
	
	@GetMapping("/delivery_mng_page")
	public String deliMng(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page,
							@RequestParam(defaultValue = "6") int size) {
		Admin adm = isSessionAvailable(session);
		if(adm != null) {
			model.addAttribute("admin", adm);
			Pageable pageable = PageRequest.of(page, size);
			Page<Delivery_Person> deliPage = deliSer.findAllDeli(pageable);
			model.addAttribute("deliPage", deliPage);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", deliPage.getTotalPages());
			return "admin/deli_mng";
		}
		return "redirect:/ecom/admin";

	}
	
	@GetMapping("/admin/delete-deli/{deliId}")
	public String deleteDeli(@PathVariable("deliId") long deliId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
	    Admin admin = isSessionAvailable(session);
	    if (admin != null) {
	        Optional<Delivery_Person> tmp = deliSer.findDeliveryById(deliId);
	        if (tmp.isEmpty()) 
	        	redirectAttributes.addFlashAttribute("error", "An error occured! Try Again.");
	        else 
	        {
	        	Long childSize = deliSer.isChildExists(deliId);
	        	if(childSize > 0) {
	        		redirectAttributes.addFlashAttribute("error", "Deletion Failed: This record cannot be deleted because it has linked child records. Please remove the associated records first.");
	    			return "redirect:/ecom/delivery_mng_page";
	        	}
	        	tmp.get().setDeleted(true);
	        	tmp.get().setShop(null);
	        	deliSer.saveDelivery(tmp.get());
	        }
	    }
	    return "redirect:/ecom/delivery_mng_page";
	}
	
	@GetMapping("/edit-profile/{adminId}")
	public String editProfile(@PathVariable("adminId") long adminId,
			Model model, HttpSession session) 
	{
		Admin adm = admSer.getAdminById(adminId);
		if(adm != null)
		{
			model.addAttribute("adminTmp", adm);
			model.addAttribute("admin", new Admin());
			return "/admin/edit-profile";
		}
		else 
		{
	        return "redirect:/ecom/admin";
	    }
	}
	
	@GetMapping("/edit_customer/{customerId}")
	public String editCustomer(@PathVariable("customerId") long customerId, Model model, HttpSession session) {
		Admin adm = isSessionAvailable(session);
		if(adm != null) {
			Optional<Customer> tmp = cusSer.findCustomerById(customerId);
			if(tmp.isEmpty()) {
				model.addAttribute("admin", adm);
				model.addAttribute("errorMessage", "Invalid Customer! Action aborted.");
				return "redirect:/ecom/customer_mng_page";
			}
			else {
				model.addAttribute("admin", adm);
				model.addAttribute("customerTmp", tmp.get());
				model.addAttribute("customer", new Customer());
				return "/admin/edit_customer";
			}
		}
	    return "redirect:/ecom/admin";
	}
	
	@GetMapping("/edit_shop/{shopId}")
	public String editShop(@PathVariable("shopId") long shopId, Model model, HttpSession session) 
	{
		Admin adm = isSessionAvailable(session);
		if(adm != null)
		{
			Optional<Shop> tmp = shopSer.findShopById(shopId);
			if(tmp.isEmpty())
			{
				model.addAttribute("admin", adm);
				model.addAttribute("errorMessage", "Invalid Shop! Action aborted.");
				return "redirect:/ecom/shop_mng_page";
			}
			else
			{
				model.addAttribute("admin", adm);
				model.addAttribute("shopTmp", tmp.get());
				model.addAttribute("shop", new Shop());
				return "/admin/edit_shop";
			}
		}
		else 
		{
	        return "redirect:/ecom/admin";
	    }
	}

	
	@GetMapping("/edit_deli/{deliId}")
	public String editDeli(@PathVariable("deliId") long deliId, Model model, HttpSession session) 
	{
		Admin adm = isSessionAvailable(session);
		if(adm != null)
		{
			Optional<Delivery_Person> tmp = deliSer.findDeliveryById(deliId);
			if(tmp.isEmpty())
			{
				model.addAttribute("admin", adm);
				return "redirect:/ecom/delivery_mng_page";
			}
			else
			{
				model.addAttribute("admin", adm);
				model.addAttribute("deliTmp", tmp.get());
				model.addAttribute("deli", new Delivery_Person());
				return "/admin/edit_deli";
			}
		}
		else
	        return "redirect:/ecom/admin";
	}
	
	@PostMapping("/update-profile/{adminId}")
	public String updateProfile(@PathVariable("adminId") long adminId, 
								@ModelAttribute("adminTmp") Customer admTmp, 
								Model model, 
								HttpSession session,
								RedirectAttributes redirectAttributes) 
	{
	    Admin tmp = admSer.getAdminById(adminId);
	    tmp.setFirstName(admTmp.getFirstName());
	    tmp.setLastName(admTmp.getLastName());
	    tmp.setEmail(admTmp.getEmail());
	    tmp.setPhoneNumber(admTmp.getPhoneNumber());
	    admSer.saveAdmin(tmp);
	    
	    Object attribute = session.getAttribute("loggedInAdmin");
	    if (attribute instanceof Optional<?> optional && optional.isPresent() && optional.get() instanceof Admin admin) 
	    {
	         model.addAttribute("adminTmp", admin);
	         model.addAttribute("customers", cusSer.findAllRecord());
	         redirectAttributes.addFlashAttribute("success", "Updated Successfully!");
	         return "redirect:/ecom/admin_home";
	    } else 
	    {
	         return "redirect:/ecom/admin";
	    }
	}
	
	@PostMapping("/update_deli/{deliId}")
	public String updateDeli(@PathVariable("deliId") long deliId, Model model, HttpSession session,@RequestParam("file") MultipartFile file,
	                        @ModelAttribute("deliTmp") Delivery_Person deliTmp ) {
	    Optional<Delivery_Person> tmp = deliSer.findDeliveryById(deliId);
	    if (tmp.isEmpty())
	        return "redirect:/ecom/delivery_mng_page";
	    else 
	    {
	        Delivery_Person existingDeli = tmp.get();
	        Shop managedShop = shopSer.findShopById(existingDeli.shop.shopId).get();
	        existingDeli.setShop(managedShop); 
	        existingDeli.setFirstName(deliTmp.getFirstName());
	        existingDeli.setLastName(deliTmp.getLastName());
	        existingDeli.setEmail(deliTmp.getEmail());
	        existingDeli.setAddress(deliTmp.getAddress());
	        existingDeli.setPhoneNumber(deliTmp.getPhoneNumber());
	        existingDeli.setMember(deliTmp.isMember);
	        if (!file.isEmpty()) {
	            String imagePath = storageSer.saveFile(file, deliProfile); 
	            existingDeli.setProfileImage(imagePath);
	        }
	        deliSer.saveDelivery(existingDeli);
	        Admin adm = isSessionAvailable(session);
	        if (adm!=null) 
	            return "redirect:/ecom/delivery_mng_page";
	        return "redirect:/ecom/admin";
	    }
	}
	
	@GetMapping("/verify_deli/{deliveryId}")
	public String verifyDeli(@PathVariable("deliveryId") long deliId, Model model, HttpSession session) {
	    Admin adm = isSessionAvailable(session);
	    if (adm != null) {
	        model.addAttribute("admin", adm);
	        Optional<Delivery_Person> optionalDelivery = deliSer.findDeliveryById(deliId);
	        if (!optionalDelivery.isPresent()) {
	            model.addAttribute("errorMessage", "Delivery-Agent Not Found! Action aborted.");
	            return "redirect:/ecom/delivery_mng_page";
	        }
	        Delivery_Person tmp = optionalDelivery.get();
	        Shop managedShop = shopSer.findShopById(tmp.getShop().shopId).get();
	        tmp.setShop(managedShop);
	        tmp.setMember(true);
	        deliSer.saveDelivery(tmp);
	        model.addAttribute("errorMessage", "Updated Successfully.");
	        return "redirect:/ecom/delivery_mng_page";
	    }
	    return "redirect:/ecom/admin";
	}
	
	@PostMapping("/update_customer/{customerId}")
	public String updateCustomer(@PathVariable("customerId") long customerId, Model model, HttpSession session,@RequestParam("file") MultipartFile file,
								@ModelAttribute("customerTmp") Customer customerTmp) {
	    Optional<Customer> tmp = cusSer.findCustomerById(customerId);
	    if (tmp.isEmpty()) 
	        return "redirect:/ecom/customer_mng_page";
	    else 
	    {
	        tmp.get().setFirstName(customerTmp.getFirstName());
	        tmp.get().setLastName(customerTmp.getLastName());
	        tmp.get().setEmail(customerTmp.getEmail());
	        tmp.get().setAddress(customerTmp.getAddress());
	        tmp.get().setPhoneNumber(customerTmp.getPhoneNumber());
	        tmp.get().setMember(customerTmp.isMember);
	        if (!file.isEmpty()) {
	        	String imagePath = storageSer.saveFile(file, customerProfile); 
			    tmp.get().setProfileImage(imagePath);
	        }
	        cusSer.saveCustomer(tmp.get());
	        model.addAttribute("successMessage", "Customer updated successfully!");
	        Admin adm = isSessionAvailable(session);
	        if (adm != null) 
	        	return "redirect:/ecom/customer_mng_page";
	        return "redirect:/ecom/admin";
	    }
	}
	@GetMapping("/verify_customer/{customerId}")
	public String verifyCustomer(@PathVariable("customerId") long customerId, Model model, HttpSession session) {
		Admin adm = isSessionAvailable(session);
		if(adm != null) {
			Optional<Customer> tmp = cusSer.findCustomerById(customerId);
			if(tmp.isEmpty())
				return "redirect:/ecom/customer_mng_page";
			else {
				tmp.get().setMember(true);
				cusSer.saveCustomer(tmp.get());
				model.addAttribute("errorMessage", "Customer " + Long.toString(tmp.get().customerId) + " has been verified." );
				return "redirect:/ecom/customer_mng_page";
			}
		}
		return "redirect:/ecom/admin";
	}
	
	@PostMapping("/update_shop/{shopId}")
	public String updateShop(@PathVariable("shopId") long shopId, Model model, HttpSession session, @ModelAttribute("shopTmp") Shop shopTmp,
							@RequestParam("file") MultipartFile file ) {
		Admin adm = isSessionAvailable(session);
		if(adm != null) {
			Optional<Shop> tmp = shopSer.findShopById(shopId);
		    if (tmp.isEmpty()) {
		    	model.addAttribute("admin", adm);
		    	model.addAttribute("errorMessage", "Customer Not Found! Action aborted.");
		        return "redirect:/ecom/customer_mng_page";
		    }
		    else {
		    	tmp.get().setShopName(shopTmp.shopName);
		    	tmp.get().setOwnerEmail(shopTmp.ownerEmail);
		    	tmp.get().setContactNumber(shopTmp.contactNumber);
		    	tmp.get().setAddress(shopTmp.address);
		    	tmp.get().setStatus(shopTmp.status);
		    	tmp.get().setDescription(shopTmp.description);
		    	tmp.get().setMember(shopTmp.isMember);
		    	if (!file.isEmpty()) {
			        String imagePath = storageSer.saveFile(file, profiles); 
			        tmp.get().setShopImage(imagePath);
			    }
	            shopSer.saveShop(tmp.get());
	            model.addAttribute("erroreMessage", "Shop updated successfully!");
	            return "redirect:/ecom/shop_mng_page";
		    }
		}
		return "redirect:/ecom/admin";
	}
	
	@GetMapping("/verify_shop/{shopId}")
	public String verifyShop(@PathVariable("shopId") long shopId, Model model, HttpSession session)
	{
		Admin adm = isSessionAvailable(session);
		if(adm != null) {
			Optional<Shop> tmp = shopSer.findShopById(shopId);
			if(tmp.isEmpty())
				return "redirect:/ecom/shop_mng_page";
			else {
				tmp.get().setMember(true);
				shopSer.saveShop(tmp.get());
				return "redirect:/ecom/shop_mng_page";
			}
		}
		return "redirect:/ecom/admin";
	}
	
	@GetMapping("/view-orders")
	public String viewOrders(HttpSession session, Model model)
	{
		Admin admin = isSessionAvailable(session);
		if (admin != null) 
		{
			List<Order> orders = orderSer.findAllRecord();
			model.addAttribute("orders", orders);
	        model.addAttribute("admin", admin);
	        model.addAttribute("shops", getVerifiedShops(shopSer.findAllRecord()));
	        return "/admin/view-orders";
	    } 
		else 
	        return "redirect:/ecom/admin";
	}
	
    @GetMapping("/view-order-detail/{orderId}")
	public String getOrderDetail(@PathVariable String orderId, Model model, HttpSession session) {
		Admin admin = isSessionAvailable(session);
		if(admin == null)
			return "redirect:/ecom/admin";
		model.addAttribute("order", orderSer.findOrderById(Long.parseLong(orderId)).get());
		model.addAttribute("admin", admin);
		return "/admin/order-detail";
    }
    
    @GetMapping("/admin/filter-orders")
    public String getFilteredOrders(
        Model model, HttpSession session, RedirectAttributes redirectAttributes, @RequestParam(defaultValue = "0") Long shopId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) int status ) {
        Admin admin = isSessionAvailable(session);
        if(admin == null)
            return "redirect:/ecom/admin";
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            redirectAttributes.addFlashAttribute("error", "Start date cannot be after end date");
            return "redirect:/ecom/view-orders";
        }
        try {
            if(status == 3) {
                List<Order> filteredOrders = orderSer.filterByDatesByShopId(
                    startDate != null ? startDate.atStartOfDay() : null,
                    endDate != null ? endDate.atTime(LocalTime.MAX) : null,
                    shopId
                );
                model.addAttribute("orders", filteredOrders);
            } else {
                OrderStatus orderStatus = OrderStatus.fromCode(status);
                List<Order> filteredOrders = orderSer.getFilteredOrdersByShopId(
                    startDate != null ? startDate.atStartOfDay() : null,
                    endDate != null ? endDate.atTime(LocalTime.MAX) : null,
                    orderStatus,
                    shopId
                );
                model.addAttribute("orders", filteredOrders);
            }
            model.addAttribute("admin", admin);
            model.addAttribute("shops", getVerifiedShops(shopSer.findAllRecord()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid Date Selection!");
        }
        return "/admin/view-orders";
    }
	
	@GetMapping("/search-orders")
	public String searchOrder(
	    @RequestParam String orderId,
	    Model model, HttpSession session, RedirectAttributes redirect) 
	{
	    Admin admin = isSessionAvailable(session);
	    if(admin == null)
	        return "redirect:/ecom/admin";
	    
	    if(orderId == null || orderId.isEmpty()) 
	    {
	        redirect.addFlashAttribute("error", "Order ID cannot be empty");
	        return "redirect:/ecom/view-orders";
	    }
	    
	    try {
	        Optional<Order> searchResult = orderSer.findOrderById(Long.parseLong(orderId));
	        if (searchResult.isPresent()) 
	        {
	            model.addAttribute("orders", Collections.singletonList(searchResult.get()));
	        } else 
	        {
	            redirect.addFlashAttribute("error", "Order not found");
	            return "redirect:/ecom/view-orders";
	        }
	    } catch (NumberFormatException e) {
	        redirect.addFlashAttribute("error", "Invalid Order ID");
	        return "redirect:/ecom/view-orders";
	    }
	    
	    model.addAttribute("admin", admin);
	    return "/admin/view-orders";
	}
	
	private Admin isSessionAvailable(HttpSession session) 
	{
	    Object attribute = session.getAttribute("loggedInAdmin");
	    if (attribute instanceof Optional<?> optional && optional.isPresent() && optional.get() instanceof Admin adm) 
	    {
	        return adm;
	    }
	    return null;
	}
	
	private List<Shop> getVerifiedShops(List<Shop> shops)
	{
		List<Shop> verifiedShops = new ArrayList<Shop>();
		for(Shop s: shops)
		{
			if(s.isMember)
				verifiedShops.add(s);
		}
		return verifiedShops;
	}
	
	private Long parseLongSafe(String value) {
	    if (value == null || value.trim().isEmpty()) {
	        return null;
	    }
	    try {
	        return Long.parseLong(value);
	    } catch (NumberFormatException e) {
	        return null;
	    }
	}
}
