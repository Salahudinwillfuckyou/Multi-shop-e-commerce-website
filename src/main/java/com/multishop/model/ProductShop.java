package com.multishop.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;

@Entity
@Table(name = "Product_Shop")
public class ProductShop 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long productShopId;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "fk_shop_id", referencedColumnName = "shopId", nullable = false)
    public Shop shop;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "fk_product_id", referencedColumnName = "productId", nullable = false)
    public Product product;
    
    public int stockQuantity;

	public ProductShop(Shop shop, Product product, int stockQuantity) {
		super();
		this.shop = shop;
		this.product = product;
		this.stockQuantity = stockQuantity;
	}

	public ProductShop() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getProductShopId() {
		return productShopId;
	}

	public void setProductShopId(long productShopId) {
		this.productShopId = productShopId;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(int stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
    
    
}
