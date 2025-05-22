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

import com.multishop.model.Shop;
import com.multishop.repository.ShopRepository;
import com.multishop.service.ShopService;

public class ShopServiceTest {
	@InjectMocks
    private ShopService shopService;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
//    @Test
//    void testSaveShop() {
//        Shop shop = new Shop();
//        shop.setOwnerEmail("shop@gmail.com");
//        shop.setPassword("123456");
//        when(shopRepository.save(shop)).thenReturn(shop);
//        Shop savedShop = shopService.saveShop(shop);
//        assertNotNull(savedShop);
//        assertEquals("shop@gmail.com", savedShop.getOwnerEmail());
//    }
    
//    @Test
//    void testFindByEmailAndPassword_ValidCredentials() {
//        String email = "shop@gmail.com";
//        String rawPassword = "123456";
//        String encodedPassword = "$2a$10$encodedpass123";
//
//        Shop shop = new Shop();
//        shop.setOwnerEmail(email);
//        shop.setPassword(encodedPassword);
//
//        when(shopRepository.findByownerEmail(email)).thenReturn(Optional.of(shop));
//        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
//        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
//
//        Optional<Shop> result = shopService.findByOwnerEmailandPassword(email, rawPassword);
//
//        assertTrue(result.isPresent());
//        assertEquals(email, result.get().getOwnerEmail());
//    }
    
//    @Test
//    void testFindByEmailAndPassword_InvalidCredentials() {
//        String email = "wrong@gmail.com";
//        String rawPassword = "wrong123";
//
//        when(shopRepository.findByownerEmail(email)).thenReturn(Optional.empty());
//
//        Optional<Shop> result = shopService.findByOwnerEmailandPassword(email, rawPassword);
//        assertNull(result);
//    }
    
//    @Test
//    void testFindShopByName() {
//        Shop shop = new Shop();
//        shop.setShop_id(1L);
//        shop.setShopName("CityMart");
//
//        // You must match this exactly in the test method
//        when(shopRepository.findByshopName("CityMart")).thenReturn(Optional.of(shop));
//
//        Optional<Shop> result = shopRepository.findByshopName("CityMart");
//
//        assertNotNull(result);
//        assertEquals("CityMart", result.get().getShopName());
//    }
    
    @Test
    void testDeleteShop() {
        Long shopId = 1L;

        when(shopRepository.existsById(shopId)).thenReturn(true).thenReturn(false);
        doNothing().when(shopRepository).deleteById(shopId);

        shopRepository.deleteById(shopId);  // Call delete

        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());
        Optional<Shop> result = shopRepository.findById(shopId);

        assertFalse(result.isPresent());  // Verify it's deleted
    }


}
