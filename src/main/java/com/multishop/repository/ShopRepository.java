package com.multishop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.multishop.model.Category;
import com.multishop.model.Shop;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    public Optional<Shop> findByshopName(String shopName);

    public Optional<Shop> findByaddress(String address);

    public Optional<Shop> findByownerEmail(String email);

    public Optional<Shop> findByownerEmailAndPassword(String email, String password);

    @NonNull
    public Page<Shop> findByIsDeletedFalse(@NonNull Pageable pageable);

    public Page<Shop> findShopByShopName(Pageable pageable, String name);

    @Query("SELECT s FROM Shop s LEFT JOIN s.orders o WHERE s.isDeleted = false GROUP BY s ORDER BY COUNT(o) DESC LIMIT 4")
    List<Shop> findTopThreeShopsByOrders();

    @Query("SELECT s FROM Shop s WHERE LOWER(s.shopName) LIKE LOWER(CONCAT('%', :shopName, '%')) AND s.isDeleted = false")
    List<Shop> findShopsByName(@NonNull String shopName);

    @Query("SELECT DISTINCT ps.product.category FROM Shop s JOIN s.productShopList ps WHERE s.shopId = :shopId AND s.isDeleted = false")
    List<Category> findCategoriesByShopId(@Param("shopId") long shopId);
}

