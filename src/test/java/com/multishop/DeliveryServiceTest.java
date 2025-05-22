package com.multishop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.multishop.model.Delivery_Person;
import com.multishop.repository.DeliRepository;
import com.multishop.service.DeliveryService;

public class DeliveryServiceTest 
{
	@InjectMocks
    private DeliveryService deliService;

    @Mock
    private DeliRepository deliRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
//    @Test
//    void testSaveDeli() {
//        Delivery_Person deli = new Delivery_Person();
//        deli.setEmail("jane@gmail.com");
//        deli.setPassword("123456");
//        when(deliRepository.save(deli)).thenReturn(deli);
//        Delivery_Person savedDeli = deliService.saveDelivery(deli);
//        assertNotNull(savedDeli);
//        assertEquals("jane@gmail.com", savedDeli.getEmail());
//    }
    
//    @Test
//    void testFindByEmailAndPassword_ValidCredentials() {
//        String email = "jane@gmail.com";
//        String rawPassword = "123456";
//        String encodedPassword = "$2a$10$encodedpass123";
//
//        Delivery_Person deli = new Delivery_Person();
//        deli.setEmail(email);
//        deli.setPassword(encodedPassword);
//
//        when(deliRepository.findByEmail(email)).thenReturn(Optional.of(deli));
//        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
//        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
//
//        Optional<Delivery_Person> result = deliService.findByEmailAndPassword(email, rawPassword);
//
//        assertTrue(result.isPresent());
//        assertEquals(email, result.get().getEmail());
//    }
    
//    @Test
//    void testFindByEmailAndPassword_InvalidCredentials() {
//        String email = "wrong@gmail.com";
//        String rawPassword = "wrong123";
//
//        when(deliRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        Optional<Delivery_Person> result = deliService.findByEmailAndPassword(email, rawPassword);
//        assertNull(result);
//    }
    
//    @Test
//    void testFindAllDeliveryPersons() {
//        List<Delivery_Person> mockList = List.of(new Delivery_Person(), new Delivery_Person(), new Delivery_Person());
//        when(deliRepository.findAll()).thenReturn(mockList);
//
//        List<Delivery_Person> result = deliService.findAllRecord();
//        assertNotNull(result);
//        assertEquals(3, result.size());
//    }
    
//    @Test
//    void testFindDeliByNameWithPagination() {
//        Pageable pageable = PageRequest.of(0, 2); // First page, 2 per page
//
//        Delivery_Person dp1 = new Delivery_Person();
//        dp1.setFirstName("Aung");
//
//        Delivery_Person dp2 = new Delivery_Person();
//        dp2.setFirstName("Aung");
//
//        Page<Delivery_Person> page = new PageImpl<>(List.of(dp1, dp2));
//
//        when(deliRepository.findDeliveryPersonByName(pageable, "Aung")).thenReturn(page);
//
//        Page<Delivery_Person> result = deliService.findDeliByName(pageable, "Aung");
//
//        assertNotNull(result);
//        assertEquals(2, result.getContent().size());
//        assertEquals("Aung", result.getContent().get(0).getFirstName());
//    }
    
    @Test
    void testDeleteDeliveryPerson() {
        Long dpId = 1L;
        when(deliRepository.existsById(dpId)).thenReturn(true).thenReturn(false);
        doNothing().when(deliRepository).deleteById(dpId);

        deliRepository.deleteById(dpId); // perform deletion

        when(deliRepository.findById(dpId)).thenReturn(Optional.empty());
        Optional<Delivery_Person> result = deliRepository.findById(dpId);

        assertFalse(result.isPresent());
    }
}
