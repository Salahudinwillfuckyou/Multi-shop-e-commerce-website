package com.multishop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.multishop.model.Admin;
import com.multishop.repository.AdminRepository;

@Service
public class AdminService 
{
	@Autowired
	private AdminRepository admRep;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public Admin getAdminById(long id) {
		return admRep.findById(id).get();
	}
	
	public Admin saveAdmin(Admin adm) {
		return admRep.save(adm);
	}

	public Optional<Admin> findByEmailandPassword(String em, String pass) {
		if(admRep.findByEmail(em).isPresent() && authenticate(pass, passwordEncoder.encode(pass)))
		{
			return admRep.findByEmail(em);
		}
		return null;
	}

	public List<Admin> findAllRecord() {
		// TODO Auto-generated method stub
		return admRep.findAll();
	}
	
	public Optional<Admin> findByEmail(String email) {
	   return admRep.findByEmail(email);
	}
	
	private boolean authenticate(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
}

