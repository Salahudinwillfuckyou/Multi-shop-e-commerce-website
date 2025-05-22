package com.multishop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.multishop.model.Delivery_Person;
import com.multishop.repository.DeliRepository;

@Service
public class DeliveryService 
{
	@Autowired
	private DeliRepository deliRep;
	@Autowired
	private PasswordEncoder passwordEncoder;
	public Optional<Delivery_Person> findDeliveryById(long id) {
		Optional<Delivery_Person> deli= deliRep.findById(id);
		return deli;
	}
	public Delivery_Person saveDelivery(Delivery_Person delivery) {
		return deliRep.save(delivery);
	}
	public List<Delivery_Person> findAllRecord() {
		return deliRep.findAll();
	}
	public Optional<Delivery_Person> findByEmailAndPassword(String em, String pass) {
		if(deliRep.findByEmail(em).isPresent() && authenticate(pass, passwordEncoder.encode(pass)))
			return deliRep.findByEmail(em);
		return null;
	}
	public Optional<Delivery_Person> findByEmail(String email) {
		return deliRep.findByEmail(email);
	}
	public Page<Delivery_Person> findAllDeli(Pageable pageable) {
        return deliRep.findByIsDeletedFalse(pageable);
    }
	public Page<Delivery_Person> findDeliByShopId(Pageable pageable, long shopId) {
        return deliRep.findByIsDeletedFalseAndShop_ShopId(pageable, shopId);
    }
	public Page<Delivery_Person> findDeliByName(Pageable pageable, String name) {
		return deliRep.findDeliveryPersonByName(pageable, name);
	}
	public Page<Delivery_Person> findDeliByNameAndShopId(Pageable pageable, String name, long shopId) {
		return deliRep.findDeliveryPersonByNameAndShopId(pageable, name, shopId);
	}
	public Long isChildExists(long id) {
		return deliRep.countOrdersByDeliveryPersonId(id);
	}
	private boolean authenticate(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
}

