package com.multishop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multishop.model.Product;
import com.multishop.model.ProductShop;
import com.multishop.model.Shop;
import com.multishop.repository.ProductShopRepository;

@Service
public class ProductShopService 
{
	@Autowired
	private ProductShopRepository prodShopRep;
	public ProductShop addProductToShop(Product product, Shop shop) {
        ProductShop productShop = new ProductShop(shop, product, 0);
        productShop.setProduct(product);
        productShop.setShop(shop);
        productShop.setStockQuantity(0);
        return prodShopRep.save(productShop);
    } 
	public List<Product> findByShopId(long shopId) {
		return prodShopRep.findByShop_ShopId(shopId);
	}
	
	public ProductShop findProductByProductIdAndShopId(long shopId, long prodId) {
		return prodShopRep.findByShopIdAndProductId(shopId, prodId);
	}
	public List<Object[]> findProductsGroupedByCategory(long shopId) {
		return prodShopRep.findProductsGroupedByCategory(shopId);
	}
	public ProductShop save(ProductShop prodShop) {
		return prodShopRep.save(prodShop);
	}
	public Optional<ProductShop> findProductShopByProductId(long id) {
		return prodShopRep.findByProduct_ProductId(id);
	}
	public Long isChildExists(long shopId) {
		return prodShopRep.countProductsByShop(shopId);
	}
	public ProductShop findByProductAndShop(Product p, Shop s) {
		return prodShopRep.findByShopIdAndProductId(p.productId, s.shopId);
	}
}
