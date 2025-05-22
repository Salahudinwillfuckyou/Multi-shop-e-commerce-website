package com.multishop.model;

import java.io.Serializable;

public class CartItem implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Product product;
    public int quantity;
    public long shopId;
    
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public CartItem(Product product, int quantity, long shopId) {
		super();
		this.product = product;
		this.quantity = quantity;
		this.shopId= shopId;
	}
	public long getShopId() {
		return shopId;
	}
	public void setShopId(long shopId) {
		this.shopId = shopId;
	}
	
	
	
	
    
    
}
