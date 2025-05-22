package com.multishop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.multishop.model.Product;
import com.multishop.model.ProductShop;


@Repository
public interface ProductShopRepository extends JpaRepository<ProductShop, Long>
{
    Optional<ProductShop> findByProduct_ProductId(long productId);
    Optional<ProductShop> findByProduct_ProductIdAndShop_ShopId(Long productId, Long shopId);
    boolean existsByProduct_ProductIdAndShop_ShopId(Long productId, Long shopId);
    
    @Query("SELECT COUNT(p) FROM ProductShop p WHERE p.shop.shopId = :shopId")
	Long countProductsByShop(@Param("shopId") long shopId);
    
    @Query("SELECT p FROM Product p WHERE p.productId IN (SELECT ps.product.productId FROM ProductShop ps WHERE ps.shop.shopId = :shopId)")
    List<Product> findByShop_ShopId(@Param("shopId") long shopId);
    
    @Query("SELECT ps FROM ProductShop ps WHERE ps.shop.shopId = :shopId AND ps.product.productId = :productId")
    ProductShop findByShopIdAndProductId(@Param("shopId") long shopId, @Param("productId") long productId);
    
    @Query("SELECT p.category, p FROM Product p " +
    	       "JOIN ProductShop ps ON p.productId = ps.product.productId " +
    	       "WHERE ps.shop.shopId = :shopId " +
    	       "GROUP BY p.category, p")
    public List<Object[]> findProductsGroupedByCategory(@Param("shopId") long shopId);
}
