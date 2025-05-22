package com.multishop.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;
import org.springframework.lang.Nullable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@SuppressWarnings("deprecation")
@Entity
@Where(clause = "is_deleted = false")
public class Customer 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long customerId;
	@Nullable
	public String profileImage;
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
	@Nullable
	@Column(columnDefinition = "VARCHAR(100)")
	public String address;
	@Column(columnDefinition = "VARCHAR(200)")
	public String password;
	public boolean isMember;
	@Column(name = "is_deleted", nullable = false)
    public boolean isDeleted;
	@OneToMany(mappedBy="customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<Order> orders = new ArrayList<Order>();
	
	public Customer(String firstName, String lastName, String email, String phoneNumber, String address,
			String password, List<Order> orders, boolean isMember) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.password = password;
		this.orders = orders;
		this.isMember = isMember;
	}
	
	@PrePersist
    public void prePersist() {
        if (isMember == true) {
            isMember = false; // Default all customers to be members unless explicitly set otherwise.
        }
    }
	
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public boolean getMember() {
        return isMember;
    }

    // Setter method
    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }

	public long getCustomer_id() {
		return customerId;
	}
	public void setCustomer_id(long customer_id) {
		this.customerId = customer_id;
	}
	public String getFirstName() {
		return firstName;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	
}
