
package com.multishop.repository;

import static org.springframework.data.jpa.domain.Specification.where;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.multishop.model.Category;
import com.multishop.model.Product;
import com.multishop.model.ProductShop;
import com.multishop.model.Shop;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>
{
    public Optional<Product> findByproductName(String name);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :name, '%'))")
    public List<Product> findByProductNameContaining(@Param("name") String name);
    
    public List<Product> findBycategory(Category category);
    @Query("SELECT p FROM Product p JOIN p.productShopList ps WHERE ps.shop.shopId = :shopId")
    List<Product> findProductsByShopId(@Param("shopId") long shopId);
    
    @Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.product.productId = :productId")
    public long countOrderDetailsByProductId(@Param("productId") long productId);
    
    
    default List<Product> searchProducts(String query, Long categoryId, Long shopId) {
        Specification<Product> spec = where(null);
        
        if (query != null && !query.isEmpty()) {
            spec = spec.and(withSearchQuery(query));
        }
        
        if (categoryId != null) {
            spec = spec.and(withCategory(categoryId));
        }
        
        if (shopId != null) {
            spec = spec.and(withShop(shopId));
        }
        
        return findAll(spec);
    }

    static Specification<Product> withSearchQuery(String query) {
        return (root, cq, cb) -> {
            if (query == null || query.isEmpty()) return null;
            return cb.or(
                cb.like(cb.lower(root.get("productName")), "%" + query.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("description")), "%" + query.toLowerCase() + "%")
            );
        };
    }

    static Specification<Product> withCategory(Long categoryId) {
        return (root, cq, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("categoryId"), categoryId);
        };
    }

    @SuppressWarnings("null")
	static Specification<Product> withShop(Long shopId) {
        return (root, cq, cb) -> {
            if (shopId == null) return null;
            
            Join<Product, ProductShop> productShopJoin = root.join("productShopList", JoinType.INNER);
            Join<ProductShop, Shop> shopJoin = productShopJoin.join("shop", JoinType.INNER);
            
            cq.distinct(true);
            return cb.equal(shopJoin.get("shopId"), shopId);
        };
    }
    
    @Query("SELECT p FROM Product p " +
    	       "JOIN ProductShop ps ON p.productId = ps.product.productId " +
    	       "WHERE ps.shop.shopId = :shopId " +
    	       "AND (LOWER(p.productName) LIKE %:query% OR LOWER(p.description) LIKE %:query%)")
    List<Product> findByShopIdAndSearchQuery(@Param("shopId") long shopId, 
    	                                        @Param("query") String query);
}
