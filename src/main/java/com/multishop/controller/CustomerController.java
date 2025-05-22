package com.multishop.controller;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.multishop.model.Cart;
import com.multishop.model.CartItem;
import com.multishop.model.Customer;
import com.multishop.model.Order;
import com.multishop.model.OrderDetail;
import com.multishop.model.Product;
import com.multishop.model.ProductShop;
import com.multishop.model.Shop;
import com.multishop.service.CategoryService;
import com.multishop.service.CustomerService;
import com.multishop.service.OrderService;
import com.multishop.service.ProductService;
import com.multishop.service.ProductShopService;
import com.multishop.service.ShopService;
import com.multishop.service.StorageService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ecom")
public class CustomerController 
{
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CustomerService cusSer;
	@Autowired
	private ShopService shopSer;
	@Autowired
	private ProductShopService productShopService;
	@Autowired
	private OrderService orderSer;
	@Autowired 
	private ProductService prodSer;
	@Autowired
	private StorageService storageSer;
	@Autowired
	private CategoryService catSer;
	
	private final String customerProfile = "customer-profiles";
	
	@GetMapping("/user")
	public String start(Model model)
	{
        model.addAttribute("featuredShops", shopSer.findTopThreeShopsByOrders());
        model.addAttribute("categories", catSer.getTopSixCategories());
		return "/customer/home";
	}
	
	@GetMapping("/user/user-login")
	public String logInPage()
	{
		return "/customer/login_customer";
	}
	
	@GetMapping("/user/user-register")
	public String registerPage(Model model)
	{
		model.addAttribute("customer",new Customer());
		return "/customer/register_customer";
	}
	
	@PostMapping("/user/register-customer")
	public String setData(@ModelAttribute("customer") Customer customer,@RequestParam("file") MultipartFile file,Model model ) {
	    String encodedPassword = passwordEncoder.encode(customer.getPassword());
	    if (cusSer.findByEmail(customer.getEmail()).isEmpty()) {
	    	if (!file.isEmpty()) {
		        String imagePath = storageSer.saveFile(file, customerProfile); 
		        customer.setProfileImage(imagePath);
		    } else {
		        customer.setProfileImage("/Images/uploads/customer-profiles/customer-default-profile.webp");
		    }
	    	customer.setPassword(encodedPassword);
	        cusSer.saveCustomer(customer);
	        return "/customer/login_customer";
	    }
	    model.addAttribute("errorMessage", "Your email already exists. Try again with a different email.");
	    return "/customer/register_customer";
	}
	
	@GetMapping("/user/login-user")
	public String logInCustomer(@RequestParam("email")String em, @RequestParam("password") String pass, Model model, HttpSession session)
	{
		Optional<Customer> customer = cusSer.findByEmailandPassword(em, pass);
        if (!customer.isEmpty()) 
        {
        	if(customer.get().isMember)
        	{
                session.setAttribute("loggedCustomer", customer);
                return "redirect:/ecom/user/home";
        	}
            model.addAttribute("errorMessage", "You are not still verified! Try again later.");
            return "/customer/login_customer";
        } 
        else 
        {
            model.addAttribute("errorMessage", "Your credentials are wrong! Please try again.");
            return "/customer/login_customer";
        }
	}
	
	@GetMapping("/user/home")
	public String memberHomePage(HttpSession session, Model model)
	{
        Customer tmp = isSessionAvailable(session);
    	if (tmp != null) 
    	{
    		model.addAttribute("customer", tmp);
	        model.addAttribute("featuredShops", shopSer.findTopThreeShopsByOrders());
	        model.addAttribute("categories", catSer.getTopSixCategories());
	        return "/customer/member_home";
    	}
    	else
    		return "redirect:/ecom/user/user-login";
	}

	@GetMapping("/user/view-shops")
	public String viewShops(HttpSession session, Model model)
	{
		Customer customer = isSessionAvailable(session);
		if(customer != null)
		{
			model.addAttribute("customer", customer);
			model.addAttribute("shops", shopSer.findAllRecord());
			return "/customer/view_shops";
		}
		else
    		return "redirect:/ecom/user/user-login";
	}
	
	@GetMapping("/user/search-shops")
	public String searchShops(@RequestParam(value = "query", required = false) String query, 
            HttpSession session, 
            Model model) 
	{
		Customer customer = isSessionAvailable(session);
		if (customer != null) 
		{
			model.addAttribute("customer", customer);
			if (query != null && !query.trim().isEmpty()) 
				model.addAttribute("shops", shopSer.findShopsByName(query));
			else 
				model.addAttribute("shops", shopSer.findAllRecord());
			
			return "/customer/view_shops";
		} 
		return "redirect:/ecom/user/user-login";
	}
	
	@GetMapping("/user/view-products")
	public String viewProducts(Model model, HttpSession session,
	                          @RequestParam(required = false) Long categoryId,
	                          @RequestParam(required = false) String searchTerm) {
	    Customer customer = isSessionAvailable(session);
	    if(customer == null) return "redirect:/ecom/user/user-login";
	    model.addAttribute("customer", customer);
	    model.addAttribute("categories", catSer.findAllRecord());
	    
	    List<Product> products;
	    if (categoryId != null)
	        products = prodSer.findByCategory(catSer.getCategory(categoryId));
	    else if (searchTerm != null && !searchTerm.isEmpty())
	        products = prodSer.findProductsByProductName(searchTerm);
	    else
	        products = prodSer.findAllRecord();
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart == null) {
	        cart = new Cart();
	        session.setAttribute("cart", cart);
	    }
	    model.addAttribute("cart", cart);
	    model.addAttribute("products", products);
	    model.addAttribute("shops", shopSer.findAllRecord());
	    return "/customer/products";
	}

	@GetMapping("/user/view-shop/{shopId}")
	public String viewShop(@PathVariable("shopId") long shopId, HttpSession session, Model model)
	{
		Customer customer = isSessionAvailable(session);
		if(customer != null)
		{
			Optional<Shop> tmp = shopSer.findShopById(shopId);
			if(tmp.isEmpty())
			{
				model.addAttribute("customer", customer);
				return "redirect:/ecom/user/view-shops";
			}
			else
			{
				Cart cart = (Cart) session.getAttribute("cart");
			    if (cart == null) 
			    {
			        cart = new Cart();
			        session.setAttribute("cart", cart);
			    }
			    
			    model.addAttribute("cart", cart);
				model.addAttribute("customer", customer);
				model.addAttribute("categories", shopSer.findCategoriesByShopId(shopId));
				model.addAttribute("products", prodSer.findByShopId(shopId));
				model.addAttribute("shop", tmp.get());
				return "/customer/view_shop";
			}
		}
		return "redirect:/ecom/user/user-login";
	}
	
	@PostMapping("/user/add-to-cart/{shopId}")
	public String addToCartShop(@RequestParam long productId, @RequestParam int quantity, HttpSession session,
	                       @PathVariable("shopId") long shopId) {
	    Product product = prodSer.findProductById(productId);
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart == null) cart = new Cart();
	    cart.addItem(new CartItem(product, quantity, shopId));
	    session.setAttribute("cart", cart);
	    return "redirect:/ecom/user/view-shop/" + shopId;
	}
	
	@PostMapping("/user/remove-from-cart/{shopId}")
	public String removeFromCart(@PathVariable("shopId") long shopId, @RequestParam long productId, HttpSession session) {
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart != null) 
	        cart.removeItem(productId);
	    return "redirect:/ecom/user/view-shop/" + shopId;
	}
	
	@PostMapping("/user/update-quantity/{shopId}")
	public String updateQuantity(@PathVariable("shopId") long shopId, @RequestParam long productId,@RequestParam int quantity,HttpSession session) {
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart != null) 
	        cart.updateQuantity(productId, quantity);
	    return "redirect:/ecom/user/view-shop/" + shopId;
	}
	
	@PostMapping("/user/place-order/{shopId}/{customerId}")
	public String placeOrder(HttpSession session,Model model, @PathVariable("shopId") long shopId,@PathVariable("customerId") long customerId) {
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart == null || cart.getCartItems().isEmpty()) 
	        return "redirect:/ecom/user/view-shop/" + shopId;
	    model.addAttribute("customer", cusSer.findCustomerById(customerId).get());
	    session.setAttribute("cart", cart);
	    return "/customer/order-summary";
	}
	
	@PostMapping("/user/confirm-order/{shopId}")
	public String confirmOrder(
	    @PathVariable("shopId") long shopId,
	    HttpSession session,
	    RedirectAttributes redirectAttributes)  // Add RedirectAttributes
	{
	    Customer customer = isSessionAvailable(session);
	    if (customer == null) {
	        return "redirect:/ecom/login_page_customer";
	    }
	    
	    Cart cart = (Cart) session.getAttribute("cart");    
	    List<Order> orders = processOrdersForAllShops(customer.customerId, cart);
	    if(orders == null) 
	    {
	        redirectAttributes.addFlashAttribute("error", "Order failed. Please try again.");
	    } else 
	    {
	        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
	    }
	    
	    session.removeAttribute("cart");
	    return "redirect:/ecom/user/view-shop/" + shopId;
	}
	
	@PostMapping("/user/add-to-cart")
	public String addToCart(@RequestParam long productId, 
	                       @RequestParam int quantity,
	                       HttpSession session) {
	    Product product = prodSer.findProductById(productId);
	    Cart cart = (Cart) session.getAttribute("cart");

	    if (cart == null) cart = new Cart();
	    ProductShop prodShop = productShopService.findProductShopByProductId(product.productId).get();
	    // Include shopId when creating CartItem
	    cart.addItem(new CartItem(product, quantity, prodShop.shop.shopId));
	    session.setAttribute("cart", cart);

	    return "redirect:/ecom/user/view-products";
	}
	
	@PostMapping("/user/remove-from-cart")
	public String removeFromCart(@RequestParam long productId, HttpSession session) {
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart != null) 
	    {
	        cart.removeItem(productId);
	    }
	    return "redirect:/ecom/user/view-products";
	}
	
	@PostMapping("/user/update-quantity")
	public String updateQuantity(@RequestParam long productId,
	                            @RequestParam int quantity,
	                            HttpSession session) 
	{
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart != null) 
	    {
	        cart.updateQuantity(productId, quantity);
	    }
	    return "redirect:/ecom/user/view-products";
	}
	
	@PostMapping("/user/place-order/{customerId}")
	public String placeOrder(HttpSession session,Model model
							, @PathVariable("customerId") long customerId) 
	{
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart == null || cart.getCartItems().isEmpty()) 
	    {
	        return "redirect:/ecom/user/view-products";
	    }
	    
	    model.addAttribute("customer", cusSer.findCustomerById(customerId).get());
	    session.setAttribute("cart", cart);
	    return "/customer/product-summary";
	}
	
	@PostMapping("/user/confirm-order")
	public String confirmOrder(
	    HttpSession session,
	    RedirectAttributes redirectAttributes)  // Add RedirectAttributes
	{
	    Customer customer = isSessionAvailable(session);
	    if (customer == null) {
	        return "redirect:/ecom/login_page_customer";
	    }
	    
	    Cart cart = (Cart) session.getAttribute("cart");    
	    List<Order> orders = processOrdersForAllShops(customer.customerId, cart);
	    if(orders == null) 
	    {
	        redirectAttributes.addFlashAttribute("error", "Order failed. Please try again.");
	    } else 
	    {
	        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
	    }
	    
	    session.removeAttribute("cart");
	    return "redirect:/ecom/user/view-products";
	}
	
	@PostMapping("/user/delete-order/{orderId}")
	public String deleteOrder(@PathVariable("orderId") long orderId, HttpSession session, Model model,
								RedirectAttributes redirectAttributes) {
	    Customer customer = isSessionAvailable(session);
	    if (customer != null) {
	        Optional<Order> tmp = orderSer.findOrderById(orderId);
	        if (tmp.isEmpty()) 
	        	redirectAttributes.addFlashAttribute("error", "An error occured! Try Again.");
	        else {
	        	tmp.get().setDeleted(true);
	        	tmp.get().getOrderDetails().clear();
	        	orderSer.saveOrder(tmp.get());
	        }
	    }
	    return "redirect:/ecom/user/order-history";
	}
	
	@GetMapping("/user/order-history")
	public String orderHistory(HttpSession session, Model model) {
		Customer tmp = isSessionAvailable(session);
		if(tmp == null)
			return "redirect:/ecom/user/user-login";
		model.addAttribute("orders", orderSer.findOrdersByCustomerId(tmp.customerId));
		model.addAttribute("customer", tmp);
		return "/customer/order-history";
	}
	
	@GetMapping("/user/view-order-detail/{orderId}")
	public String getOrderDetail(@PathVariable String orderId, Model model, HttpSession session) 
	{
		Customer tmp = isSessionAvailable(session);
		if(tmp == null)
			return "redirect:/ecom/user/user-login";
		Order order = orderSer.findOrderById(Long.parseLong(orderId)).get();
		model.addAttribute("order", order);
		model.addAttribute("customer", tmp);
		return "/customer/order-detail";
    }
	
	@GetMapping("/user/filter-orders")
	public String getFilteredOrders(
	    Model model, HttpSession session,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	    @RequestParam(required = false) int status, RedirectAttributes redirectAttributes) {
	    Customer tmp = isSessionAvailable(session);
	    if(tmp == null)
	        return "redirect:/ecom/user/user-login";
	    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
	        redirectAttributes.addFlashAttribute("error", "Start date cannot be after end date");
	        return "redirect:/ecom/user/order-history";
	    }
	    try {
	        if(status == 3) {
	            List<Order> filteredOrders = orderSer.filterByDatesByCustomerId(
	                startDate != null ? startDate.atStartOfDay() : null,
	                endDate != null ? endDate.atTime(LocalTime.MAX) : null,
	                		tmp.customerId
	            );
	            model.addAttribute("customer", tmp);
	            model.addAttribute("orders", filteredOrders);
	            return "/customer/order-history";
	        }
	        
	        OrderStatus orderStatus = OrderStatus.fromCode(status);
	        List<Order> filteredOrders = orderSer.getFilteredOrdersByCustomerId(
	            startDate != null ? startDate.atStartOfDay() : null,
	            endDate != null ? endDate.atTime(LocalTime.MAX) : null,
	            orderStatus, tmp.customerId
	        );
	        model.addAttribute("customer", tmp);
	        model.addAttribute("orders", filteredOrders);
	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error", "Invalid Date Selection!");
	    }
	    
	    return "/customer/order-history";
	}
	
	@GetMapping("/user/search-products/{shopId}")
	public String viewShop(@PathVariable long shopId,
	                      @RequestParam(name = "query", required = false) String query,
	                      Model model, HttpSession session) 
	{
	    Shop shop = shopSer.findShopById(shopId).get();	    

	    List<Product> products;
	    if (query != null && !query.trim().isEmpty()) 
	    {
	        products = prodSer.searchProductsInShop(shopId, query);
	    } else 
	    {
	        products = prodSer.findByShopId(shopId);
	    }
	    
	    // Add attributes to model
	    model.addAttribute("shop", shop);
	    model.addAttribute("products", products);
	    model.addAttribute("categories", shopSer.findCategoriesByShopId(shopId));
	    Customer tmp = isSessionAvailable(session);
	    if(tmp == null)
	    	return "redirect:/ecom/user/user-login";
	    model.addAttribute("customer", tmp);
	    Cart cart = (Cart) session.getAttribute("cart");
	    if (cart == null) 
	    {
	        cart = new Cart();
	        session.setAttribute("cart", cart);
	    }
	    
	    model.addAttribute("cart", cart);
	    return "/customer/view_shop";
	}
	
	@GetMapping("/user/search-orders")
	public String searchOrder(
	    @RequestParam String orderId,
	    Model model, HttpSession session, RedirectAttributes redirect) 
	{
	    Customer tmp = isSessionAvailable(session);
	    if(tmp == null)
	        return "redirect:/ecom/user/user-login";
	    
	    if(orderId == null || orderId.isEmpty()) {
	        redirect.addFlashAttribute("error", "Order ID cannot be empty");
	        return "redirect:/ecom/user/order-history";
	    }
	    
	    try {
	        Optional<Order> searchResult = orderSer.findByOrderIdAndCustomerId(Long.parseLong(orderId), tmp.customerId);
	        if (searchResult.isPresent()) {
	            model.addAttribute("orders", Collections.singletonList(searchResult.get()));
	        } else 
	        {
	            redirect.addFlashAttribute("error", "Order not found");
	            return "redirect:/ecom/user/order-history";
	        }
	    } catch (NumberFormatException e) {
	        redirect.addFlashAttribute("error", "Invalid Order ID");
	        return "redirect:/ecom/user/order-history";
	    }
	    
	    model.addAttribute("customer", tmp);
	    return "/customer/order-history";
	}
	
	@GetMapping("/user/edit-profile")
	public String editProfile(HttpSession session, Model model)
	{
		Customer tmp = isSessionAvailable(session);
		if(tmp == null)
			return "redirect:/ecom/user/user-login";
		else
		{
			model.addAttribute("customerTmp", tmp);
			model.addAttribute("customer", new Customer());
			return "/customer/edit-profile";
		}
	}
	
	private Order processOrderForSingleShop(long shopId, long customerId, List<CartItem> shopItems, double totalAmount) {
	    Optional<Shop> shop = shopSer.findShopById(shopId);
	    Optional<Customer> customer = cusSer.findCustomerById(customerId);

	    if (shop.isEmpty() || customer.isEmpty()) return null;


	    Order order = new Order();
	    order.setShop(shop.get());
	    order.setCustomer(customer.get());
	    order.setTotalAmount(totalAmount);
	    order.setOrderStatus(OrderStatus.PENDING);
	    order.setOrderedDate(LocalDateTime.now());
	    order.setDueDate(LocalDateTime.now().plusDays(7));

	    List<OrderDetail> orderDetails = new ArrayList<>();
	    for (CartItem item : shopItems) {
	        Product product = item.getProduct();
	        ProductShop productShop = productShopService.findProductByProductIdAndShopId(shopId, product.getProduct_id());

	        if (!product.getCategory().getCategoryName().equalsIgnoreCase("foods and drinks")) {
	            productShop.setStockQuantity(productShop.getStockQuantity() - item.getQuantity());
	            productShopService.save(productShop);
	        }

	        orderDetails.add(new OrderDetail(order, product, item.getQuantity()));
	    }

	    order.setOrderDetails(orderDetails);
	    return orderSer.saveOrder(order);
	}
	
	public List<Order> processOrdersForAllShops(long customerId, Cart cart) {
	    List<Order> orders = new ArrayList<>();
	    
	    Map<Long, List<CartItem>> itemsByShop = cart.getCartItems().values().stream()
	        .collect(Collectors.groupingBy(CartItem::getShopId));


	    for (Map.Entry<Long, List<CartItem>> entry : itemsByShop.entrySet()) 
	    {
	        long shopId = entry.getKey();
	        List<CartItem> shopItems = entry.getValue();

	        double total = shopItems.stream()
	            .mapToDouble(item -> item.getProduct().getUnitPrice() * item.getQuantity())
	            .sum();

	        // Create order for this shop
	        Order order = processOrderForSingleShop(shopId, customerId, shopItems, total);
	        if (order != null) orders.add(order);
	    }

	    return orders;
	}
	
	private Customer isSessionAvailable(HttpSession session) 
	{
	    Object attribute = session.getAttribute("loggedCustomer");
	    if (attribute instanceof Optional<?> optional && optional.isPresent() && optional.get() instanceof Customer customer) 
	    {
	        return customer;
	    }
	    return null;
	}
}
