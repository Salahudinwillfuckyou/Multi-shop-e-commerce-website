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
public class Shop 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long shopId;
	@Nullable
	public String shopImage;
	public String shopName;
	public String ownerEmail;
	@Nullable
	public String contactNumber;
	@Nullable
	public String address;
	@Column(columnDefinition = "VARCHAR(200)")
	public String password;
	public boolean status;
	@Nullable
	public String description;
	public boolean isMember;
	@OneToMany(mappedBy="shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<Delivery_Person> deliveryPersonList = new ArrayList<Delivery_Person>();
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<ProductShop> productShopList = new ArrayList<ProductShop>();
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<Order> orders = new ArrayList<Order>();
	@Column(name = "is_deleted", nullable = false)
    public boolean isDeleted;
	
	public Shop() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Shop(String shopName, String ownerEmail, String contactNumber, String address, String password) {
		super();
		this.shopName = shopName;
		this.ownerEmail = ownerEmail;
		this.contactNumber = contactNumber;
		this.address = address;
		this.password = password;
		this.status = true;
	}
	
	@PrePersist
	protected void onPrePersist() 
	{
		this.status = true;
    }
	
	public long getShop_id() {
		return shopId;
	}

	public void setShop_id(long shop_id) {
		this.shopId = shop_id;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public String getShopImage() {
		return shopImage;
	}

	public void setShopImage(String shopImage) {
		this.shopImage = shopImage;
	}

	public String getDescription() {
		return description;
	}

	public boolean getMember() {
        return isMember;
    }

    // Setter method
    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ProductShop> getProductShopList() {
		return productShopList;
	}

	public void setProductShopList(List<ProductShop> productShopList) {
		this.productShopList = productShopList;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean isAvailable) {
		this.status = isAvailable;
	}

	public List<Delivery_Person> getDeliveryPersonList() {
		return deliveryPersonList;
	}

	public void setDeliveryPersonList(List<Delivery_Person> deliveryPersonList) {
		this.deliveryPersonList = deliveryPersonList;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
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
	
	
}
