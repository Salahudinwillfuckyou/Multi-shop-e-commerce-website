package com.multishop.service;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multishop.enums.OrderStatus;
import com.multishop.model.Order;
import com.multishop.repository.OrderRepository;

@Service
public class OrderService 
{
	@Autowired
	private OrderRepository orderRep;
	public Optional<Order> findOrderById(long orderid) { return orderRep.findById(orderid); }
	public Order saveOrder(Order order) { return orderRep.save(order); }
	public List<Order> findAllRecord() { return orderRep.findAll(); }
	public List<Order> findOrdersByDeliveryPerson(long id) { return orderRep.findOrdersByDeliveryPersonId(id); }
	public List<Order> findOrdersByCustomerId(long id) { return orderRep.findOrdersByCustomerId(id); }
	public List<Order> findOrdersByShopId(long id) { return orderRep.findOrdersByShopId(id); }
	public Optional<Order> findByOrderIdAndShopId(long orderId, long shopId) { return orderRep.findByOrderIdAndShopId(orderId, shopId); }
	public Optional<Order> findByOrderIdAndCustomerId(long orderId, long customerId) {
		return orderRep.findByOrderIdAndCustomerId(orderId, customerId);
	}
	public List<Order> getFilteredOrdersByShopId(LocalDateTime startDate, 
            LocalDateTime endDate, 
            OrderStatus status,
            long shopId) 
	{
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) 
			throw new IllegalArgumentException("Start date cannot be after end date");
		return orderRep.findByFiltersByShopId(startDate, endDate, status, shopId);
	}
	public List<Order> getFilteredOrdersByCustomerId(LocalDateTime startDate, 
            LocalDateTime endDate, 
            OrderStatus status,
            long customerId) 
	{
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) 
			throw new IllegalArgumentException("Start date cannot be after end date");
		return orderRep.findByFiltersByCustomerId(startDate, endDate, status, customerId);
	}
	public List<Order> filterByDatesByShopId(LocalDateTime startDate, 
            LocalDateTime endDate, long shopId)
	{
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) 
			throw new IllegalArgumentException("Start date cannot be after end date");
		return orderRep.filterByDatesByShopId(startDate, endDate, shopId);
	}
	public List<Order> filterByDatesByCustomerId(LocalDateTime startDate, 
            LocalDateTime endDate, long customerId)
	{
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) 
			throw new IllegalArgumentException("Start date cannot be after end date");
		return orderRep.filterByDatesByCustomerId(startDate, endDate, customerId);
	}
	public List<Order> filterOrdersByDates(LocalDateTime startDate, 
            LocalDateTime endDate)
	{
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) 
			throw new IllegalArgumentException("Start date cannot be after end date");
		return orderRep.filterByDates(startDate, endDate);
	}
	public List<Order> filterOrdersByDatesAndStatus(LocalDateTime startDate, 
            LocalDateTime endDate, OrderStatus status)
	{
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) 
			throw new IllegalArgumentException("Start date cannot be after end date");
		return orderRep.filtersByDatesAndStatus(startDate, endDate, status);
	}
	public List<Order> findOrdersByDeliIdAndPendingStatus(long deliId, OrderStatus status) {
		return orderRep.findOrdersByDeliIdAndPendingStatus(deliId, status);
	}
	public List<Order> getRecentOrders() { return orderRep.findTop4ByOrderByOrderedDateDesc(); }
	public Map<String, Double> getMonthlySales() {
        List<Object[]> rawData = orderRep.findMonthlySales();
        Map<String, Double> result = new LinkedHashMap<>();
        
        for (Object[] row : rawData) {
            int monthNumber = (int) row[0];
            Double sales = (Double) row[1];
            result.put(monthToAbbreviation(monthNumber), sales);
        }
        
        return result;
    }
	public List<Order> filterByStatus(OrderStatus status)
	{
		return orderRep.filterByStatus(status);
	}
	private String monthToAbbreviation(int month) {
        return new DateFormatSymbols().getShortMonths()[month - 1];
    }
}
