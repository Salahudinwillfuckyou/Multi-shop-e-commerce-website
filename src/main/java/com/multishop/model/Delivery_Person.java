 package com.multishop.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.lang.Nullable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@SuppressWarnings("deprecation")
@Entity
@Where(clause = "is_deleted = false")
public class Delivery_Person 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long deliveryPersonId;
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
	public boolean isMember;
	@Nullable
	@Column(columnDefinition = "VARCHAR(100)")
	public String address;
	@Column(columnDefinition = "VARCHAR(200)")
	public String password;
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name="fk_shop_id", referencedColumnName = "shopId", nullable = true)
	public Shop shop;
	@OneToMany(mappedBy="delivery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<Order> assignedOrders = new ArrayList<Order>();
	@Column(name = "is_deleted", nullable = false)
    public boolean isDeleted;
	
	public Delivery_Person(String firstName, String lastName, String email, String phoneNumber,
			String address, String password,Shop shop, List<Order> assignedOrders) {
		super();
		this.shop = shop;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.password = password;
		this.assignedOrders = assignedOrders;
	}
	
	public Delivery_Person() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public long getDelivery_person_id() {
		return deliveryPersonId;
	}

	public void setDelivery_person_id(long delivery_person_id) {
		this.deliveryPersonId = delivery_person_id;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
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

	public long getDeliveryPersonId() {
		return deliveryPersonId;
	}

	public void setDeliveryPersonId(long deliveryPersonId) {
		this.deliveryPersonId = deliveryPersonId;
	}

	public List<Order> getAssignedOrders() {
		return assignedOrders;
	}

	public void setAssignedOrders(List<Order> assignedOrders) {
		this.assignedOrders = assignedOrders;
	}
	
	public boolean getMember() {
        return isMember;
    }

    // Setter method
    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }
}
