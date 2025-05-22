package com.multishop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multishop.model.Category;
import com.multishop.model.Product;
import com.multishop.repository.ProductRepository;

@Service
public class ProductService 
{
	@Autowired
	private ProductRepository prodRep;
	public Product findProductById(long id) {
		Product product = prodRep.findById(id).get();
		return product;
	}
	public Product saveProd(Product prod) {
		prodRep.save(prod);
		return prod;
	}
	public List<Product> findAllRecord() {
		return prodRep.findAll();
	}
	public Optional<Product> findByProductName(String name) {
		return prodRep.findByproductName(name);
	}
	public List<Product> findProductsByProductName(String name) {
		return prodRep.findByProductNameContaining(name);
	}
	public List<Product> findByCategory(Category category) {
		return prodRep.findBycategory(category);
	}
	public List<Product> findByShopId(long shop_id) {
		// TODO Auto-generated method stub
		return prodRep.findProductsByShopId(shop_id);
	}
	public List<Product> searchProducts(String query, Long categoryId, Long shopId) {
        return prodRep.searchProducts(query, categoryId, shopId);
    }
	public long getChild(long productId) {
		return prodRep.countOrderDetailsByProductId(productId);
	}
	public List<Product> searchProductsInShop(Long shopId, String query) {
	    return prodRep.findByShopIdAndSearchQuery(shopId, query.toLowerCase());
	}
}
