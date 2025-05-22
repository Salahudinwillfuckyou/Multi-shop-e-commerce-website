package com.multishop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.multishop.model.Order;
import com.multishop.repository.DeliRepository;
import com.multishop.repository.OrderRepository;
import com.multishop.service.DeliveryService;
import com.multishop.service.OrderService;

public class OrderServiceTest 
{
	@InjectMocks
	private OrderService orderService;
	
	@InjectMocks
	private DeliveryService deliService;
	
	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private DeliRepository deliRepository;
	
	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
	
//	@Test
//	void testFindAllOrders() {
//	    List<Order> orders = Arrays.asList(new Order(), new Order());
//	    when(orderRepository.findAll()).thenReturn(orders);
//
//	    List<Order> result = orderService.findAllRecord();
//
//	    assertNotNull(result);
//	    assertEquals(2, result.size());
//	}
	
//	@Test
//	void testFilterOrdersByDate() {
//		LocalDateTime startDate = LocalDateTime.of(2025, 4, 1, 0, 0);
//	    LocalDateTime endDate = LocalDateTime.of(2025, 4, 13, 23, 59);
//	    List<Order> orders = Arrays.asList(new Order());
//	    when(orderService.filterOrdersByDates(startDate, endDate)).thenReturn(orders);
//
//	    List<Order> result = orderService.filterOrdersByDates(startDate, endDate);
//
//	    assertNotNull(result);
//	    assertEquals(1, result.size());
//	}
	
//	@Test
//	void testFilterOrdersByStatus() {
//	    List<Order> orders = Arrays.asList(new Order());
//	    when(orderService.filterByStatus(OrderStatus.PENDING)).thenReturn(orders);
//
//	    List<Order> result = orderService.filterByStatus(OrderStatus.PENDING);
//
//	    assertNotNull(result);
//	    assertEquals(1, result.size());
//	}
	
//	@Test
//	void testFindOrderById() {
//	    Order order = new Order();
//	    order.setOrder_id(1L);
//	    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//	    Order result = orderService.findOrderById(1L).get();
//
//	    assertNotNull(result);
//	    assertEquals(1L, result.getOrder_id());
//	}
	
//	@Test
//	void testCreateNewOrderWithUniqueId() {
//	    Order order = new Order();
//	    order.setOrder_id(1L);
//	    when(orderRepository.save(any(Order.class))).thenReturn(order);
//
//	    Order result = orderService.saveOrder(order);
//
//	    assertNotNull(result);
//	    assertNotNull(result.getOrder_id());
//	}
	
//	@Test
//	void testAssignOrderToDeliveryPerson() {
//	    Order order = new Order();
//	    order.setOrder_id(1L);
//	    Delivery_Person dp = new Delivery_Person();
//	    dp.setDelivery_person_id(100L);
//	    order.setDelivery(dp);
//
//	    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//	    when(deliRepository.findById(100L)).thenReturn(Optional.of(dp));
//	    when(orderRepository.save(any(Order.class))).thenReturn(order);
//
//	    System.out.println(order.delivery);
//	    System.out.println(order.getDelivery().getDelivery_person_id());
//	    assertNotNull(order.getDelivery());
//	    assertEquals(100L, order.getDelivery().getDelivery_person_id());
//	}
	
//	@Test
//	void testAcceptOrderByDeliveryPerson() {
//	    Order order = new Order();
//	    order.setOrderStatus(OrderStatus.PENDING);
//
//	    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//	    order.setOrderStatus(OrderStatus.IN_TRANSIT);
//	    when(orderRepository.save(order)).thenReturn(order);
//
//	    assertEquals(OrderStatus.IN_TRANSIT, order.getOrderStatus());
//	}
	
	
//	@Test
//	void testViewOrderDetail() {
//	    Order order = new Order();
//	    order.setOrder_id(1L);
//
//	    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//	    Order result = orderService.findOrderById(1L).get();
//
//	    assertNotNull(result);
//	    assertEquals(1L, result.getOrder_id());
//	}
	
//	@Test
//	void testFindOrdersByShopId() {
//	    Long shopId = 10L;
//	    List<Order> orders = List.of(new Order(), new Order());
//
//	    when(orderService.findOrdersByShopId(shopId)).thenReturn(orders);
//
//	    List<Order> result = orderService.findOrdersByShopId(shopId);
//
//	    assertEquals(2, result.size());
//	}
	
//	@Test
//	void testFindOrdersByCustomerId() {
//	    Long customerId = 5L;
//	    List<Order> orders = List.of(new Order());
//
//	    when(orderRepository.findOrdersByCustomerId(customerId)).thenReturn(orders);
//
//	    List<Order> result = orderService.findOrdersByCustomerId(customerId);
//
//	    assertEquals(1, result.size());
//	}
	
	@Test
	void testFindOrdersByDeliveryPersonId() {
	    Long deliveryId = 20L;
	    List<Order> orders = List.of(new Order());

	    when(orderRepository.findOrdersByDeliveryPersonId(deliveryId)).thenReturn(orders);

	    List<Order> result = orderService.findOrdersByDeliveryPerson(deliveryId);

	    assertEquals(1, result.size());
	}


}
