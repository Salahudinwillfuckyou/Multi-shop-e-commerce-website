 package com.multishop.controller;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.multishop.enums.OrderStatus;
import com.multishop.model.Category;
import com.multishop.model.Delivery_Person;
import com.multishop.model.Order;
import com.multishop.model.Product;
import com.multishop.model.ProductShop;
import com.multishop.model.Shop;
import com.multishop.service.CategoryService;
import com.multishop.service.DeliveryService;
import com.multishop.service.OrderService;
import com.multishop.service.ProductService;
import com.multishop.service.ProductShopService;
import com.multishop.service.ShopService;
import com.multishop.service.StorageService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ecom")
public class ShopController  
{
	@Autowired
	private ShopService shopSer;
	@Autowired
	private CategoryService catSer;
	@Autowired
	private ProductService prodSer;
	@Autowired
	private ProductShopService prodShopSer;
	@Autowired
	private DeliveryService deliSer;
	@Autowired
	private StorageService storageSer;
	@Autowired
	private OrderService orderSer;
	
	// folder name to store product images
	private final String products = "products";
	private final String profiles = "profiles";
	private final String deliProfile = "deli-profiles";
	
	@GetMapping("/shop")
	public String start() {
		return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/login")
	public String logInShopPage(HttpSession session) {
		session.invalidate();
		return "/shop/login_shop";
	}
	
	@GetMapping("/shop/login-shop")
	public String logInShop(@RequestParam("email")String em, 
							@RequestParam("password") String pass, 
							Model model, 
							HttpSession session)
	{
		Optional<Shop> shop = shopSer.findByOwnerEmailandPassword(em, pass);
		if(!shop.isEmpty())
		{
			if(shop.get().isMember)
			{
				session.setAttribute("loggedShop", shop.get());
	        	return "redirect:/ecom/shop/index";
			}
			model.addAttribute("errorMessage", "Your shop is not still verified! Try again later.");
            return "/shop/login_shop";
		}
		else
		{
			model.addAttribute("errorMessage", "Your credentials are wrong!. Please Try again.");
			return "/shop/login_shop";
		}
	}
	
	@GetMapping("/shop/register")
	public String registerShopPage(Model model) {
		model.addAttribute("shop",new Shop());
		return "/shop/register_shop";
	}
	
	@PostMapping("/shop/register-shop")
	public String registerShop(@ModelAttribute("shop") Shop shop, @RequestParam("file") MultipartFile file,Model model) {
		String email = shop.getOwnerEmail();
	    if (shopSer.findByownerEmail(email).isEmpty()) {
	    	if (!file.isEmpty()) {
		        String imagePath = storageSer.saveFile(file, profiles); 
		        shop.setShopImage(imagePath); // Save the file path, not the file itself
		    } else {
		        shop.setShopImage("/Images/uploads/profiles/shop-default-profile.avif");
		    }
	        shopSer.saveShop(shop);
	        return "redirect:/ecom/shop/login";
	    }
	    model.addAttribute("errorMessage", "Your email already exists. Try again with a different email.");
	    return "/shop/register_shop";
	}
	
	@GetMapping("/shop/index")
	public String shopIndex(HttpSession session, Model model)
	{
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if (shop != null) 
		{
			model.addAttribute("productSize", shop.productShopList.size());
			model.addAttribute("deliSize", shop.deliveryPersonList.size());
			model.addAttribute("totalSales", formatNumber(getTotalEarning(shop.orders)));
			model.addAttribute("orderSize", shop.orders.size());
			model.addAttribute("todaySales", formatNumber(getTodayEarning(shop.orders)));
	        model.addAttribute("shop", shop);
	        return "/shop/home_shop";
	    } 
		else 
	        return "redirect:/ecom/shop/login";
	}

	@GetMapping("/shop/deliveries")
	public String shopDeliMng(HttpSession session, Model model,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "6") int size) {
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if(shop != null) {
			model.addAttribute("shop", shop);
			Pageable pageable = PageRequest.of(page, size);
	        Page<Delivery_Person> deliPage = deliSer.findDeliByShopId(pageable, shop.shopId);
	        model.addAttribute("deliPage", deliPage);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", deliPage.getTotalPages());
			return "shop/deli-mng";
		}
		else
			return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/search-deli")
	public String searchDeli(@RequestParam(value = "query", required = false) String query, HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "6") int size) 
	{
		Shop shop = (Shop)session.getAttribute("loggedShop");
		if (shop != null) {
			model.addAttribute("shop", shop);
			if (query != null && !query.trim().isEmpty()) {
			    Pageable pageable = PageRequest.of(page, size);
		        Page<Delivery_Person> deliPage = deliSer.findDeliByNameAndShopId(pageable, query, shop.shopId);
			    model.addAttribute("deliPage", deliPage);
		        model.addAttribute("currentPage", page);
		        model.addAttribute("totalPages", deliPage.getTotalPages());
			}
			return "/shop/deli-mng";
		} 
		return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/products")
	public String productManagement(HttpSession session, Model model){
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if (shop != null) {
			model.addAttribute("shop", shop);
			List<Product> products = prodShopSer.findByShopId(shop.getShop_id());
			model.addAttribute("products", products);
	        model.addAttribute("allCategories", catSer.findAllRecord());
	        return "/shop/product_mng";
	    } 
		else 
	        return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/search-product")
	public String searchProducts(Model model, HttpSession session, @RequestParam(required = false) String query,@RequestParam(required = false) String categoryId) {
	    Shop shop = (Shop)session.getAttribute("loggedShop");
	    if(shop == null)
	    	return "redirect:/ecom/shop/login";
	    Long parsedCategoryId = parseLongSafe(categoryId);
	    List<Product> filteredProducts = prodSer.searchProducts(query, parsedCategoryId,shop.shopId);
	    model.addAttribute("shop", shop);
	    model.addAttribute("products", filteredProducts);
	    model.addAttribute("allCategories", catSer.findAllRecord());
	    List<Shop> shops = getVerifiedShops(shopSer.findAllRecord());
	    model.addAttribute("allShops", shops);
	    model.addAttribute("selectedCategoryId", parsedCategoryId);
	    return "/shop/product_mng";
	}
	
	@GetMapping("/shop/view-orders")
	public String orderManagement(HttpSession session, Model model)
	{
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if (shop != null) 
		{
			List<Order> orders = orderSer.findOrdersByShopId(shop.getShop_id());
			model.addAttribute("orders", orders);
	        model.addAttribute("shop", shop);
	        return "/shop/view-orders";
	    } 
		else 
	        return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/product-register")
	public String registerProductPage(HttpSession session, Model model)
	{
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if (shop != null) 
		{
			model.addAttribute("shop", shop);
			model.addAttribute("product", new Product());
			model.addAttribute("categories",catSer.findAllRecord());
			return "/shop/register_product";
		}
		else
			return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/edit-shop/{shopId}")
	public String editShop(@PathVariable("shopId")long shopId, Model model, HttpSession session)
	{
		Shop shop = shopSer.findShopById(shopId).get();
		if(shop != null)
		{
			model.addAttribute("shopTmp", shop);
			model.addAttribute("shop", new Shop());
			return "/shop/edit_shop";
		}
		else
			return "redirect:/ecom/shop/login";
	}
	
	@PostMapping("/shop/update-shop/{shopId}")
	public String updateShop(@PathVariable("shopId") long shopId, 
							@ModelAttribute("shopTmp") Shop shopTmp,
							@RequestParam("file") MultipartFile file,
							Model model, HttpSession session)
	{
		Shop shop = (Shop)session.getAttribute("loggedShop");
		if(shop != null)
		{
			Optional<Shop> tmp = shopSer.findShopById(shopId);
		    	tmp.get().setShopName(shopTmp.shopName);
		    	tmp.get().setOwnerEmail(shopTmp.ownerEmail);
		    	tmp.get().setContactNumber(shopTmp.contactNumber);
		    	tmp.get().setAddress(shopTmp.address);
		    	tmp.get().setStatus(shopTmp.status);
		    	tmp.get().setDescription(shopTmp.description);
		    	tmp.get().setMember(shopTmp.isMember);
		    	if (!file.isEmpty()) 
			    {
			        String imagePath = storageSer.saveFile(file, profiles); 
			        tmp.get().setShopImage(imagePath); // Save the file path, not the file itself
			    }
	            
	            // Save the updated customer
	            shopSer.saveShop(tmp.get());

	            return "redirect:/ecom/shop/edit-shop/" + shop.shopId;
		}
		return "redirect:/ecom/shop/login";
	}
	
	@PostMapping("/shop/register-product/{shopId}")
	public String registerProduct(@PathVariable("shopId") Shop shop, Model model,
								@ModelAttribute("product") Product product, @RequestParam("file") MultipartFile file, BindingResult result){
		if(result.hasErrors()){
			model.addAttribute("errorMessage", "Please fill all the fields.");
			return "redirect:/ecom/shop/product-register";
		}
		Optional<Shop> tmp = shopSer.findShopById(shop.getShop_id());
	    if (tmp.isEmpty()) {
	        model.addAttribute("errorMessage", "Invalid Shop ID.");
	        return "redirect:/ecom/shop/product-register";
	    }
	    if (!file.isEmpty()) {
	        String imagePath = storageSer.saveFile(file, products); 
	        product.setProductImage(imagePath); // Save the file path, not the file itself
	    } else {
	        product.setProductImage("/Images/uploads/products/product-default.svg");
	    }
	    prodSer.saveProd(product);
	    prodShopSer.addProductToShop(product, shop);
	    return "redirect:/ecom/shop/products";
	}
	
	@PostMapping("/shop/update-product/{productId}")
	public String updateProduct(@PathVariable("productId") long productId, Model model, HttpSession session,
	                        @ModelAttribute("productTmp") Product productTmp, @RequestParam("file") MultipartFile file) {
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if(shop == null)
			return "redirect:/ecom/shop/login";
		Product tmp = prodSer.findProductById(productId);
	    tmp.setProductName(productTmp.productName);
	    if(productTmp.getCategory() != null) {
	        Category category = catSer.getCategory(productTmp.getCategory().getCategoryId());
	        tmp.setCategory(category);
	    }
	    tmp.setUnitPrice(productTmp.unitPrice);
	    if (!file.isEmpty()) {
	        String imagePath = storageSer.saveFile(file, products); 
	        tmp.setImage(imagePath);
	    }
	    prodSer.saveProd(tmp);
	    return "redirect:/ecom/shop/products";
	}
	
	@GetMapping("/shop/stock-edit/{productId}")
	public String editStockPage(@PathVariable("productId")long productId, 
								HttpSession session, 
								Model model)
	{
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if (shop != null) 
		{
			model.addAttribute("shop", shop);
			ProductShop tmp = prodShopSer.findProductByProductIdAndShopId(shop.shopId, productId);
			if(tmp == null)
			{
				model.addAttribute("errorMessage", "Invalid Shop ID.");
				return "redirect:/ecom/shop/products";
			}
			else
			{
				model.addAttribute("productShop", tmp);
				return "/shop/edit_stock";
			}
		}
		else
			return "redirect:/ecom/shop/product-register";
	}
	
	@GetMapping("/shop/edit-stock/{productId}")
	public String editStock(@PathVariable("productId")long productId, 
							@RequestParam("quantity") int quantity, 
							HttpSession session, 
							Model model)
	{
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if (shop != null) {
			ProductShop tmp = prodShopSer.findProductByProductIdAndShopId(shop.shopId, productId);
			if (tmp == null) 
			{
			    model.addAttribute("errorMessage", "Unexpected error occurred. Action Cancelled.");
			    return "redirect:/ecom/shop/stock-edit/{productId}";
			}

			if (quantity <= 0) 
			{
			    model.addAttribute("errorMessage", "Quantity must be greater than 0.");
			    return "redirect:/ecom/shop/stock-edit/{productId}";
			}

			tmp.setStockQuantity(tmp.getStockQuantity() + quantity);
			prodShopSer.save(tmp);
			return "redirect:/ecom/shop/stock-edit/{productId}";
		    
		}
		model.addAttribute("errorMessage", "Session expired. Please log in again.");
	    return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/delete-product/{productId}")
	public String deleteProduct(@PathVariable("productId") long productId, HttpSession session, Model model,
								RedirectAttributes redirectAttributes)
	{
		Shop shop = (Shop)session.getAttribute("loggedShop");
		if(shop != null)
		{
			Product tmp = prodSer.findProductById(productId);
			long childSize = prodSer.getChild(productId);
			if(childSize > 0)
			{
				redirectAttributes.addFlashAttribute("error", "Deletion Failed: This record cannot be deleted because it has linked child records. Please remove the associated records first.");
				return "redirect:/ecom/shop/products";
			}
			tmp.isDeleted = true;
			prodSer.saveProd(tmp);
			return "redirect:/ecom/shop/products";
		}
		return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/verify-deli/{deliveryId}")
	public String verifyShopDeli(@PathVariable("deliveryId") long deliId, Model model, HttpSession session)
	{
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if(shop != null)
		{
			model.addAttribute("shop", shop);
			Delivery_Person deli = deliSer.findDeliveryById(deliId).get();
			if(deli == null)
			{
				model.addAttribute("errorMessage", "Delivery-Agent Not Found! Action aborted.");
				return "redirect:/ecom/shop/deliveries";
			}
			else
			{
				Delivery_Person tmp = deli;
				Shop managedShop = shopSer.findShopById(tmp.getShop().shopId).get();
				tmp.setShop(managedShop);
				deli.setMember(true);
				deliSer.saveDelivery(deli);
				return "redirect:/ecom/shop/deliveries";
			}
		}
		return "redirect:/ecom/shop/login";
	}
	
	@GetMapping("/shop/product-edit/{productId}")
	public String editProductPage(@PathVariable("productId")long productId, 
								HttpSession session, 
								Model model)
	{
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if (shop != null) 
		{
			model.addAttribute("shop", shop);
			Product tmp = prodSer.findProductById(productId);
			List<Category> categories = catSer.findAllRecord();
			model.addAttribute("productTmp", tmp);
			model.addAttribute("categories", categories);
			model.addAttribute("product", new Product());
			return "/shop/edit-product";
		}
		else
			return "redirect:/ecom/shop/login";
	}

	@GetMapping("/shop/deli-edit/{deliId}")
	public String editDeli(@PathVariable("deliId") long deliId, Model model, HttpSession session) {
		Shop shop = (Shop) session.getAttribute("loggedShop");
		if(shop != null){
			Optional<Delivery_Person> tmp = deliSer.findDeliveryById(deliId);
			if(tmp.isEmpty()){
				model.addAttribute("shop", shop);
				return "redirect:/ecom/shop/deliveries";
			} else{
				model.addAttribute("shop", shop);
				model.addAttribute("deliTmp", tmp.get());
				model.addAttribute("deli", new Delivery_Person());
				return "/shop/edit-deli";
			}
		}
		else
	        return "redirect:/ecom/shop/login";
	}
	
	@PostMapping("/shop/edit-deli/{deliId}")
	public String updateDeliShop(@PathVariable("deliId") long deliId, Model model, HttpSession session, @RequestParam("file") MultipartFile file,
							@ModelAttribute("deliTmp") Delivery_Person deliTmp) {
	    Optional<Delivery_Person> tmp = deliSer.findDeliveryById(deliId);
	    if (tmp.isEmpty()) 
	        return "redirect:/ecom/shop/deliveries";
	    else {
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
			        existingDeli.setProfileImage(imagePath); // Save the file path, not the file itself
			    }
	            // Save the updated customer
	            deliSer.saveDelivery(tmp.get());

	           	Shop shop = (Shop) session.getAttribute("loggedShop");
	            if (shop != null) 
	            {
	                model.addAttribute("shop", shop);
	                model.addAttribute("deliveries", deliSer.findAllRecord());
	                return "/shop/deli-mng";
	            } 
	            else 
	                return "redirect:/ecom/shop/login";
	    }
	}
	
	@GetMapping("/shop/assign-deli/{orderId}")
	public ResponseEntity<List<DeliveryPersonDTO>> getDeliveryAgents(
	        @PathVariable String orderId, 
	        HttpSession session) 
	{
	    Shop shop = (Shop) session.getAttribute("loggedShop");
	    long shopId = shop.getShop_id();
	    
	    Shop freshShop = shopSer.findShopById(shopId).get();
	    List<DeliveryPersonDTO> agents = freshShop.deliveryPersonList.stream()
	        .filter(deli -> {
	            // Count only orders with IN_TRANSIT status (enum comparison)
	            long activeOrders = deli.getAssignedOrders().stream()
	                .filter(order -> order.getOrderStatus() == OrderStatus.IN_TRANSIT)
	                .count();
	            return activeOrders <= 10;
	        })
	        .map(DeliveryPersonDTO::new)
	        .collect(Collectors.toList());
	    return ResponseEntity.ok(agents);
	}
	
    @PostMapping("/shop/assign-delivery/{orderId}/{deliveryPersonId}")
    public ResponseEntity<?> assignDeliveryAgent(HttpSession session,
    											@PathVariable("deliveryPersonId") long deliId,
    											@PathVariable("orderId") long orderId) 
    {
    	Order order = orderSer.findOrderById(orderId).get();
    	Delivery_Person deli = deliSer.findDeliveryById(deliId).get();
    	order.setDelivery(deli);
    	deliSer.saveDelivery(deli);
    	orderSer.saveOrder(order);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/shop/view-order-detail/{orderId}")
	public String getOrderDetail(@PathVariable String orderId, Model model, HttpSession session) 
	{
		Shop tmp = (Shop) session.getAttribute("loggedShop");
		if(tmp == null)
			return "redirect:/ecom/shop/login";
		Order order = orderSer.findOrderById(Long.parseLong(orderId)).get();
		model.addAttribute("order", order);
		model.addAttribute("shop", tmp);
		return "/shop/order-detail";
    }
    
	@PostMapping("/shop/delete-order/{orderId}")
	public String deleteOrder(@PathVariable("orderId") long orderId, HttpSession session, Model model) 
	{
	    Shop shop = (Shop)session.getAttribute("loggedShop");
	    if (shop != null) 
	    {
	        Optional<Order> tmp = orderSer.findOrderById(orderId);
	        tmp.get().setDeleted(true);
	        tmp.get().getOrderDetails().clear();
	        orderSer.saveOrder(tmp.get());
	    }
	    return "redirect:/ecom/shop/view-orders";
	}
	
	@GetMapping("/shop/filter-orders")
	public String getFilteredOrders(
	    Model model, HttpSession session,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	    @RequestParam(required = false) int status, RedirectAttributes redirectAttributes) {
		Shop shop = (Shop)session.getAttribute("loggedShop");
	    if(shop == null)
	        return "redirect:/ecom/shop/login";
	    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
	        redirectAttributes.addFlashAttribute("error", "Start date cannot be after end date");
	        return "redirect:/ecom/shop/view-orders";
	    }
	    try {
	        if(status == 3) {
	            List<Order> filteredOrders = orderSer.filterByDatesByShopId(
	                startDate != null ? startDate.atStartOfDay() : null,
	                endDate != null ? endDate.atTime(LocalTime.MAX) : null,
	                		shop.shopId
	            );
	            model.addAttribute("shop", shop);
	            model.addAttribute("orders", filteredOrders);
	            return "/shop/view-orders";
	        }
	        OrderStatus orderStatus = OrderStatus.fromCode(status);
	        List<Order> filteredOrders = orderSer.getFilteredOrdersByShopId(
	            startDate != null ? startDate.atStartOfDay() : null,
	            endDate != null ? endDate.atTime(LocalTime.MAX) : null,
	            orderStatus, shop.shopId
	        );
	        model.addAttribute("shop", shop);
	        model.addAttribute("orders", filteredOrders);
	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error", "Invalid Date Selection!");
	    }
	    return "/shop/view-orders";
	}
	
	@GetMapping("/shop/search-orders")
	public String searchOrder(
	    @RequestParam String orderId,
	    Model model, HttpSession session, RedirectAttributes redirect) 
	{
	    Shop shop = (Shop)session.getAttribute("loggedShop");
	    if(shop == null)
	        return "redirect:/ecom/shop/login";
	    
	    if(orderId == null || orderId.isEmpty()) {
	        redirect.addFlashAttribute("error", "Order ID cannot be empty");
	        return "redirect:/ecom/shop/view-orders";
	    }
	    
	    try {
	        Optional<Order> searchResult = orderSer.findByOrderIdAndShopId(Long.parseLong(orderId), shop.shopId);
	        if (searchResult.isPresent()) {
	            model.addAttribute("orders", Collections.singletonList(searchResult.get()));
	        } else 
	        {
	            redirect.addFlashAttribute("error", "Order not found");
	            return "redirect:/ecom/shop/view-orders";
	        }
	    } catch (NumberFormatException e) {
	        redirect.addFlashAttribute("error", "Invalid Order ID");
	        return "redirect:/ecom/user/order-history";
	    }
	    
	    model.addAttribute("shop", shop);
	    return "/shop/view-orders";
	}
	
	private double getTotalEarning(List<Order> orders)
	{
		double earning = 0;
		for (Order o : orders) 
		{
			earning += o.getTotalAmount();
		}
		return earning;
	}
	
	private double getTodayEarning(List<Order> orders) 
	{
	    LocalDate today = LocalDate.now();
	    double earning = 0;
	    for (Order o : orders) {
	        if (o.deliveredDate != null && o.deliveredDate.toLocalDate().equals(today)) {
	            earning += o.totalAmount;
	        }
	    }
	    return earning;
	}
	
	private static String formatNumber(double amount) 
	{
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(amount);
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
	
	public class DeliveryPersonDTO 
	{
		private long deliveryPersonId;
	    private String firstName;
	    private String lastName;
	    private int assignedOrdersCount;

	    public DeliveryPersonDTO(Delivery_Person person) {
	        this.setDeliveryPersonId(person.getDeliveryPersonId());
	        this.setFirstName(person.getFirstName());
	        this.setLastName(person.getLastName());
	        this.setAssignedOrdersCount((int) person.getAssignedOrders().stream()
	                .filter(order -> order.getOrderStatus() == OrderStatus.IN_TRANSIT)
	                .count());
	    }

		public long getDeliveryPersonId() {
			return deliveryPersonId;
		}

		public void setDeliveryPersonId(long deliveryPersonId) {
			this.deliveryPersonId = deliveryPersonId;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public int getAssignedOrdersCount() {
			return assignedOrdersCount;
		}

		public void setAssignedOrdersCount(int assignedOrdersCount) {
			this.assignedOrdersCount = assignedOrdersCount;
		}
	    
	}
}
