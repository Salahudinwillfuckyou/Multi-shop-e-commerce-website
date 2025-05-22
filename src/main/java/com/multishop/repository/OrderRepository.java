package com.multishop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.multishop.enums.OrderStatus;
import com.multishop.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>
{
	@Query("SELECT o FROM Order o WHERE o.delivery.deliveryPersonId = :deliveryPersonId")
    List<Order> findOrdersByDeliveryPersonId(@Param("deliveryPersonId") Long deliveryPersonId);
	@Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId")
	List<Order> findOrdersByCustomerId(@Param("customerId") long customerId);
	@Query("SELECT o FROM Order o WHERE o.shop.shopId = :shopId")
	List<Order> findOrdersByShopId(@Param("shopId") long shopId);
	@Query("SELECT o From Order o WHERE o.orderStatus = :orderStatus")
	List<Order> filterByStatus(@Param("orderStatus") OrderStatus status);
	@Query("SELECT o FROM Order o WHERE " +
		       "o.delivery.deliveryPersonId = :deliveryPersonId AND " +
		       "o.orderStatus = :status")
		List<Order> findOrdersByDeliIdAndPendingStatus(@Param("deliveryPersonId") long deliId, @Param("status") OrderStatus status);
	
	@Query("SELECT o FROM Order o WHERE " +
		       "(:startDate IS NULL OR o.orderedDate >= :startDate) AND " +
		       "(:endDate IS NULL OR o.orderedDate <= :endDate) AND " +
		       "(:status IS NULL OR o.orderStatus = :status) AND " +
		       "(:shopId IS NULL OR o.shop.shopId = :shopId)")
		List<Order> findByFiltersByShopId(
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate,
		    @Param("status") OrderStatus status,
		    @Param("shopId") long shopId);
	
	@Query("SELECT o FROM Order o WHERE " +
		       "(:startDate IS NULL OR o.orderedDate >= :startDate) AND " +
		       "(:endDate IS NULL OR o.orderedDate <= :endDate) AND " +
		       "(:status IS NULL OR o.orderStatus = :status) AND " +
		       "(:customerId IS NULL OR o.customer.customerId = :customerId)")
		List<Order> findByFiltersByCustomerId(
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate,
		    @Param("status") OrderStatus status,
		    @Param("customerId") long customerId);
	
	@Query("SELECT o FROM Order o WHERE " +
		       "(:startDate IS NULL OR o.orderedDate >= :startDate) AND " +
		       "(:endDate IS NULL OR o.orderedDate <= :endDate) AND " +
		       "(:status IS NULL OR o.orderStatus = :status)")
		List<Order> filtersByDatesAndStatus(
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate,
		    @Param("status") OrderStatus status);
	
	@Query("SELECT o FROM Order o WHERE " +
		       "(:startDate IS NULL OR o.orderedDate >= :startDate) AND " +
		       "(:endDate IS NULL OR o.orderedDate <= :endDate) AND " +
		       "(:shopId IS NULL OR o.shop.shopId = :shopId)")
		List<Order> filterByDatesByShopId(
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate,
		    @Param("shopId") long shopId);

	
	@Query("SELECT o FROM Order o WHERE " +
		       "(:startDate IS NULL OR o.orderedDate >= :startDate) AND " +
		       "(:endDate IS NULL OR o.orderedDate <= :endDate) AND " +
		       "(:customerId IS NULL OR o.customer.customerId = :customerId)")
		List<Order> filterByDatesByCustomerId(
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate,
		    @Param("customerId") long customerId);
	
	@Query("SELECT o FROM Order o WHERE " +
		       "(:startDate IS NULL OR o.orderedDate >= :startDate) AND " +
		       "(:endDate IS NULL OR o.orderedDate <= :endDate)")
		List<Order> filterByDates(
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate);
	
	@Query("SELECT o FROM Order o WHERE o.orderId = :orderId AND o.shop.shopId = :shopId")
	Optional<Order> findByOrderIdAndShopId(@Param("orderId") long orderId, @Param("shopId") long shopId);
	
	@Query("SELECT o FROM Order o WHERE o.orderId = :orderId AND o.customer.customerId = :customerId")
	Optional<Order> findByOrderIdAndCustomerId(@Param("orderId") long orderId, @Param("customerId") long customerId);
	
	List<Order> findTop4ByOrderByOrderedDateDesc();
	
	@Query("SELECT MONTH(o.orderedDate), SUM(od.soldQuantity * od.product.unitPrice) " +
	           "FROM Order o JOIN o.orderDetails od " +
	           "GROUP BY MONTH(o.orderedDate) " +
	           "ORDER BY MONTH(o.orderedDate)")
	    List<Object[]> findMonthlySales();
		
}
