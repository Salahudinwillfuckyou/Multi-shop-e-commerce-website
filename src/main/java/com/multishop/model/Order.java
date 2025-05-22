package com.multishop.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.lang.Nullable;

import com.multishop.converters.OrderStatusConverter;
import com.multishop.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "orders")
@Where(clause = "is_deleted = false")
public class Order 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long orderId;
	@ManyToOne
	@JoinColumn(name="fk_customer_id", referencedColumnName = "customerId", nullable=false)
    public Customer customer;
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "fk_shop_id", referencedColumnName = "shopId", nullable = false)
	public Shop shop;
	@ManyToOne
	@JoinColumn(name="fk_delivery_person_id", referencedColumnName = "deliveryPersonId", nullable=true)
	public Delivery_Person delivery;
	public LocalDateTime orderedDate;
	@Nullable
	public LocalDateTime dueDate;
	@Nullable
	public LocalDateTime deliveredDate;
	@Convert(converter = OrderStatusConverter.class) // Use the custom converter
	public OrderStatus orderStatus;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
	public double totalAmount;
	@Column(name = "is_deleted", nullable = false)
    public boolean isDeleted;

	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Order(Customer customer, Shop shop, Delivery_Person delivery, LocalDateTime orderedDate,
			LocalDateTime dueDate, LocalDateTime deliveredDate, OrderStatus orderStatus, List<OrderDetail> orderDetails,
			double totalAmount) {
		super();
		this.customer = customer;
		this.shop = shop;
		this.delivery = delivery;
		this.orderedDate = orderedDate;
		this.dueDate = dueDate;
		this.deliveredDate = deliveredDate;
		this.orderStatus = orderStatus;
		this.orderDetails = orderDetails;
		this.totalAmount = totalAmount;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getOrder_id() {
		return orderId;
	}

	public void setOrder_id(long order_id) {
		this.orderId = order_id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Delivery_Person getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery_Person delivery) {
		this.delivery = delivery;
	}

	public LocalDateTime getOrderedDate() {
		return orderedDate;
	}

	public void setOrderedDate(LocalDateTime orderedDate) {
		this.orderedDate = orderedDate;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public LocalDateTime getDeliveredDate() {
		return deliveredDate;
	}

	public void setDeliveredDate(LocalDateTime deliveredDate) {
		this.deliveredDate = deliveredDate;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
}
