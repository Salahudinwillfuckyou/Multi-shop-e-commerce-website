package com.multishop.model;

import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Admin
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long adminId;
	
	@Nullable
	@Column(columnDefinition = "VARCHAR(30)")
	public String firstName;
	
	@Nullable
	@Column(columnDefinition = "VARCHAR(30)")
	public String lastName;
	
	@Column(columnDefinition = "VARCHAR(50)")
	public String email;
	
	@Nullable
	@Column(columnDefinition = "VARCHAR(15)")
	public String phoneNumber;
	
	@Column(columnDefinition = "VARCHAR(200)")
	public String password;
	
	public Admin(String firstName, String lastName, String email,String phoneNumber, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}
	
	public Admin() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Admin(long adminId, String firstName, String lastName, String email, String phoneNumber, String password) {
		super();
		this.adminId = adminId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}

	public long getAdminId() {
		return adminId;
	}

	public void setAdminId(long adminId) {
		this.adminId = adminId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
