package com.multishop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.multishop.model.Category;
import com.multishop.model.Shop;
import com.multishop.repository.ShopRepository;

@Service
public class ShopService 
{
	@Autowired
	private ShopRepository shopRep;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public Optional<Shop> findShopById(long id) {
		Optional<Shop> shop = shopRep.findById(id);
		return shop;
	}
	public Shop saveShop(Shop shop) {
		return shopRep.save(shop);
	}
	public List<Shop> findAllRecord() {
		return shopRep.findAll();
	}
	public Page<Shop> findAllShops(Pageable pageable) {
        return shopRep.findByIsDeletedFalse(pageable);
    }
	public Page<Shop> findShopByName(Pageable pageable, String shopName) {
		return shopRep.findShopByShopName(pageable, shopName);
	}
	
	public Optional<Shop> findByOwnerEmailandPassword(String em, String pass) {
		if(shopRep.findByownerEmail(em).isPresent() && authenticate(pass, passwordEncoder.encode(pass)))
			return shopRep.findByownerEmail(em);
		return null;
	}
	public Optional<Shop> findByownerEmail(String email) {
	   return shopRep.findByownerEmail(email);
	}
	public List<Shop> findTopThreeShopsByOrders() {
		return shopRep.findTopThreeShopsByOrders();
	}
	public List<Shop> findShopsByName(String name) {
		return shopRep.findShopsByName(name);
	}
	public List<Category> findCategoriesByShopId(long shopId) {
		return shopRep.findCategoriesByShopId(shopId);
	}
	private boolean authenticate(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
}
