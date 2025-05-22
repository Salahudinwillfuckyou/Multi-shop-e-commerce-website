package com.multishop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.multishop.model.Customer;
import com.multishop.repository.CustomerRepository;

@Service
public class CustomerService 
{
	@Autowired 
	private CustomerRepository cusRep;
	@Autowired
	private PasswordEncoder passwordEncoder;
	public Optional<Customer> findCustomerById(long id) {
		Optional<Customer> customer = cusRep.findById(id);
		return customer;
	}
	public Customer saveCustomer(Customer customer) {
		return cusRep.save(customer);
	}
	public Optional<Customer> findByEmailandPassword(String em, String pass) {
		if(cusRep.findByEmail(em).isPresent() && authenticate(pass, passwordEncoder.encode(pass)))
			return cusRep.findByEmail(em);
		return null;
	}
	public List<Customer> findAllRecord() {
		return cusRep.findAll();
	}
	public Optional<Customer> findByEmail(String email) {
	   return cusRep.findByEmail(email);
	}
	public Page<Customer> findAllCustomer(Pageable pageable) {
        return cusRep.findByIsDeletedFalse(pageable);
    }
	public Page<Customer> findCustomerByName(Pageable pageable, String name) {
		return cusRep.findCustomerByName(pageable, name);
	}
	private boolean authenticate(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
}

