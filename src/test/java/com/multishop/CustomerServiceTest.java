package com.multishop;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.multishop.model.Customer;
import com.multishop.repository.CustomerRepository;
import com.multishop.service.CustomerService;

public class CustomerServiceTest 
{
	@InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
//    @Test
//    void testSaveCustomer() {
//        Customer customer = new Customer();
//        customer.setEmail("john@gmail.com");
//        customer.setPassword("123456");
//        when(customerRepository.save(customer)).thenReturn(customer);
//        Customer savedCustomer = customerService.saveCustomer(customer);
//        assertNotNull(savedCustomer);
//        assertEquals("john@gmail.com", savedCustomer.getEmail());
//    }
    
//    @Test
//    void testFindByEmailAndPassword_ValidCredentials() {
//        String email = "john@gmail.com";
//        String rawPassword = "123456";
//        String encodedPassword = "$2a$10$encodedpass123";
//
//        Customer customer = new Customer();
//        customer.setEmail(email);
//        customer.setPassword(encodedPassword);
//
//        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
//        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
//        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
//
//        Optional<Customer> result = customerService.findByEmailandPassword(email, rawPassword);
//
//        assertTrue(result.isPresent());
//        assertEquals(email, result.get().getEmail());
//    }
    
//    @Test
//    void testFindByEmailAndPassword_InvalidCredentials() {
//        String email = "wrong@gmail.com";
//        String rawPassword = "wrong123";
//
//        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        Optional<Customer> result = customerService.findByEmailandPassword(email, rawPassword);
//        assertNull(result);
//    }
    
//    @Test
//    void testFindCustomersByFirstName() {
//        Customer customer1 = new Customer();
//        customer1.setFirstName("John");
//
//        Customer customer2 = new Customer();
//        customer2.setFirstName("John");
//
//        when(customerRepository.findByfirstName("John")).thenReturn(List.of(customer1, customer2));
//
//        List<Customer> result = customerRepository.findByfirstName("John");
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("John", result.get(0).getFirstName());
//    }
    
//    @Test
//    void testFindCustomersByLastName() {
//        Customer customer = new Customer();
//        customer.setLastName("Smith");
//
//        when(customerRepository.findBylastName("Smith")).thenReturn(List.of(customer));
//
//        List<Customer> result = customerRepository.findBylastName("Smith");
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("Smith", result.get(0).getLastName());
//    }
    
    @Test
    void testDeleteCustomer() {
        Long id = 1L;

        // Assume customer exists
        when(customerRepository.existsById(id)).thenReturn(true).thenReturn(false);

        customerRepository.deleteById(id);

        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        
        Optional<Customer> deleted = customerRepository.findById(id);
        assertFalse(deleted.isPresent());
    }

}
