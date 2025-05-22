package com.multishop.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.multishop.model.Delivery_Person;

@Repository
public interface DeliRepository extends JpaRepository<Delivery_Person, Long> 
{
	public Optional<Delivery_Person> findByEmailAndPassword(String email, String password);
	public Optional<Delivery_Person> findByEmail(String email);
	
    @NonNull
    public Page<Delivery_Person> findByIsDeletedFalse(@NonNull Pageable pageable);
    
    @NonNull
    public Page<Delivery_Person> findByIsDeletedFalseAndShop_ShopId(@NonNull Pageable pageable, long shopId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.delivery.deliveryPersonId = :deliveryPersonId")
    Long countOrdersByDeliveryPersonId(@Param("deliveryPersonId") long deliveryPersonId);

    @Query("SELECT d FROM Delivery_Person d " +
            "WHERE d.isDeleted = false " +
            "AND LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Delivery_Person> findDeliveryPersonByName(Pageable pageable, @Param("name") String name);
    
    @Query("SELECT d FROM Delivery_Person d " +
            "WHERE d.isDeleted = false " +
            "AND LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))" + 
            "AND d.shop.shopId = :shopId" )
    Page<Delivery_Person> findDeliveryPersonByNameAndShopId(Pageable pageable, @Param("name") String name, @Param("shopId") long shopId);
}
