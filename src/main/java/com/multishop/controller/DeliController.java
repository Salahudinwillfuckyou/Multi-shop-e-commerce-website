package com.multishop.controller;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.multishop.enums.OrderStatus;
import com.multishop.model.Delivery_Person;
import com.multishop.model.Order;
import com.multishop.model.Shop;
import com.multishop.service.DeliveryService;
import com.multishop.service.OrderService;
import com.multishop.service.ShopService;
import com.multishop.service.StorageService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ecom")
public class DeliController 
{
	@Autowired
	private DeliveryService deliSer;
	@Autowired
	private OrderService orderSer;
	@Autowired
	private ShopService shopSer;
	@Autowired
	private StorageService storageSer;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final String deliProfile = "deli-profiles";
	
	@GetMapping("/deli")
	public String index()
	{
		return "redirect:/ecom/deli/login";
	}
	
	@GetMapping("/deli/login")
	public String loginDeli(HttpSession session) {
		session.invalidate();
		return "/delivery/login_deli";
	}
	
	@GetMapping("/deli/register")
	public String registerDeli(Model model) {
		List<Shop> shops = shopSer.findAllRecord();
		model.addAttribute("shops", shops);
		model.addAttribute("delivery", new Delivery_Person());
		return "/delivery/register_deli";
	}
	
	@PostMapping("/deli/register-deli")
	public String registerDeli(@ModelAttribute("delivery") Delivery_Person deli, Model model, @RequestParam("file") MultipartFile file){
	    if (deliSer.findByEmail(deli.getEmail()).isEmpty()) {
	    	if (!file.isEmpty()) {
		        String imagePath = storageSer.saveFile(file, deliProfile); 
		        deli.setProfileImage(imagePath); // Save the file path, not the file itself
		    } else {
		        deli.setProfileImage("/Images/uploads/deli-profiles/deli-default-profile.webp");
		    }
	    	deli.setPassword(passwordEncoder.encode(deli.getPassword()));
	        deliSer.saveDelivery(deli);
	        return "redirect:/ecom/deli/login";
	    }
	    model.addAttribute("errorMessage", "Your email already exists. Try again with a different email.");
	    return "/delivery/register_deli";
	}
	
	@GetMapping("/deli/login-deli")
	public String loginDelivery(@RequestParam("email")String em, Model model, HttpSession session,@RequestParam("password") String pass) {
		Optional<Delivery_Person> deli = deliSer.findByEmailAndPassword(em, pass);
		if(!deli.isEmpty() && deli.get().shop != null) {
			session.setAttribute("loggedDeli", deli.get());
        	return "redirect:/ecom/deli/index";
		} else {
			model.addAttribute("errorMessage", "Your credentials are wrong!. Please Try again.");
			return "redirect:/ecom/deli/login";
		}
	}
	
	@GetMapping("/deli/index")
	public String deliIndex(HttpSession session, Model model)
	{
		Delivery_Person deli = (Delivery_Person) session.getAttribute("loggedDeli");
		if(deli == null)
			return "redirect:/ecom/deli/login";
		List<Order> orders = orderSer.findOrdersByDeliveryPerson(deli.deliveryPersonId);
		List<Order> completedOrders = new ArrayList<Order>();
		List<Order> pendingOrders = new ArrayList<Order>();
		List<Order> processingOrders = new ArrayList<Order>();
		for(Order o : orders)
		{
			if(o.orderStatus == OrderStatus.DELIVERED)
			{
				completedOrders.add(o);
			}
			else if( o.orderStatus == OrderStatus.IN_TRANSIT)
			{
				processingOrders.add(o);
			}
			else
			{
				pendingOrders.add(o);
			}
		}
		model.addAttribute("deli", deli);
		model.addAttribute("processings", processingOrders);
		model.addAttribute("pendings", pendingOrders);
		model.addAttribute("completed", completedOrders);
		model.addAttribute("earning", formatNumber(getDailyEarning(completedOrders)));
		return "/delivery/home_deli";
	}
	
	@GetMapping("/deli/pendings")
	public String pendings(HttpSession session, Model model)
	{
		Delivery_Person deli = (Delivery_Person)session.getAttribute("loggedDeli");
		if(deli == null)
			return "redirect:/ecom/deli/login";
		model.addAttribute("orders", orderSer.findOrdersByDeliIdAndPendingStatus(deli.deliveryPersonId, OrderStatus.PENDING));
		model.addAttribute("deli", deli);
		return "/delivery/pendings";
	}
	
	@GetMapping("/deli/accept-order/{orderId}")
	public String accceptOrder(HttpSession session, Model model, @PathVariable("orderId") long orderId)
	{
		Delivery_Person deli = (Delivery_Person)session.getAttribute("loggedDeli");
		Order order = orderSer.findOrderById(orderId).get();
		order.orderStatus = OrderStatus.IN_TRANSIT;
		orderSer.saveOrder(order);
		model.addAttribute("orders", orderSer.findOrdersByDeliIdAndPendingStatus(deli.deliveryPersonId, OrderStatus.IN_TRANSIT));
		model.addAttribute("deli", deli);
		return "/delivery/transits";
	}
	
	@GetMapping("/deli/transits")
	public String transits(HttpSession session, Model model)
	{
		Delivery_Person deli = (Delivery_Person)session.getAttribute("loggedDeli");
		if(deli == null)
			return "redirect:/ecom/deli/login";
		model.addAttribute("orders", orderSer.findOrdersByDeliIdAndPendingStatus(deli.deliveryPersonId, OrderStatus.IN_TRANSIT));
		model.addAttribute("deli", deli);
		return "/delivery/transits";
	}
	
	@GetMapping("/deli/complete-order/{orderId}")
	public String completeOrder(HttpSession session, Model model, @PathVariable("orderId") long orderId)
	{
		Delivery_Person deli = (Delivery_Person)session.getAttribute("loggedDeli");
		Order order = orderSer.findOrderById(orderId).get();
		order.deliveredDate = LocalDateTime.now();
		order.orderStatus = OrderStatus.DELIVERED;
		orderSer.saveOrder(order);
		model.addAttribute("orders", orderSer.findOrdersByDeliIdAndPendingStatus(deli.deliveryPersonId, OrderStatus.DELIVERED));
		model.addAttribute("deli", deli);
		return "/delivery/delivered-orders";
	}
	
	@GetMapping("/deli/delivered-orders")
	public String deliveredOrders(HttpSession session, Model model)
	{
		Delivery_Person deli = (Delivery_Person)session.getAttribute("loggedDeli");
		if(deli == null)
			return "redirect:/ecom/deli/login";
		model.addAttribute("orders", orderSer.findOrdersByDeliIdAndPendingStatus(deli.deliveryPersonId, OrderStatus.DELIVERED));
		model.addAttribute("deli", deli);
		return "/delivery/delivered-orders";
	}
	
	@GetMapping("/deli/view-order-detail/{orderId}")
	public String getOrderDetail(@PathVariable String orderId, Model model, HttpSession session) 
	{
		Delivery_Person tmp = (Delivery_Person) session.getAttribute("loggedDeli");
		if(tmp == null)
			return "redirect:/ecom/deli/login";
		Order order = orderSer.findOrderById(Long.parseLong(orderId)).get();
		model.addAttribute("order", order);
		model.addAttribute("deli", tmp);
		return "/delivery/order-detail";
    }
	
	@GetMapping("/deli/edit-profile")
	public String editProfile(HttpSession session, Model model)
	{
		Delivery_Person deli = (Delivery_Person) session.getAttribute("loggedDeli");
		if(deli == null)
			return "redirect:/ecom/deli/login";
		model.addAttribute("deliTmp", deli);
		model.addAttribute("deli", new Delivery_Person());
		return "/delivery/edit-profile";
	}
	@PostMapping("/deli/update_deli/{deliId}")
	public String updateDeli(@PathVariable("deliId") long deliId, Model model, HttpSession session,@RequestParam("file") MultipartFile file,
	                        @ModelAttribute("deliTmp") Delivery_Person deliTmp ) {
	    Optional<Delivery_Person> tmp = deliSer.findDeliveryById(deliId);
	    if (tmp.isEmpty())
	        return "redirect:/ecom/deli/login";
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
	    }
	    return "redirect:/ecom/deli/edit-profile";
	}
	
	@GetMapping("/deli/view-shop/{shopId}")
	public String viewShop(HttpSession session, Model model, @PathVariable("shopId") long shopId)
	{
		Delivery_Person deli = (Delivery_Person) session.getAttribute("loggedDeli");
		if(deli == null)
			return "redirect:/ecom/deli/login";
		model.addAttribute("deli", deli);
		model.addAttribute("shops", getVerifiedShops(shopSer.findAllRecord()));
		model.addAttribute("shop", shopSer.findShopById(shopId).get());
		return "/delivery/view-shop";
	}
	
	@GetMapping("/deli/leave-shop")
	public String leaveShop(HttpSession session, Model model)
	{
		Delivery_Person deli = (Delivery_Person) session.getAttribute("loggedDeli");
		if(deli == null)
			return "redirect:/ecom/deli/login";
		deli.shop = null;
		deliSer.saveDelivery(deli);
		return "redirect:/ecom/deli/register";
	}
	
	private double getDailyEarning(List<Order> completedOrders)
	{
		double earning = 0;
		for (Order o : completedOrders) {
		    if (o.deliveredDate != null && o.deliveredDate.equals(LocalDateTime.now())) {
		        earning += 2000;
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
}
