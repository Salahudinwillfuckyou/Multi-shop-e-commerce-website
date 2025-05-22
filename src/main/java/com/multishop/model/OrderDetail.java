package com.multishop.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderDetail 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long orderDetailId;
	
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name="fk_order_id", referencedColumnName = "orderId", nullable=false)
	public Order order;
	
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name="fk_product_id", referencedColumnName = "productId", nullable=false)
	public Product product;
	
	public int soldQuantity;

	public OrderDetail(Order order, Product product, int soldQuantity) {
		super();
		this.order = order;
		this.product = product;
		this.soldQuantity = soldQuantity;
	}

	public OrderDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getSoldQuantity() {
		return soldQuantity;
	}

	public void setSoldQuantity(int soldQuantity) {
		this.soldQuantity = soldQuantity;
	}

}
