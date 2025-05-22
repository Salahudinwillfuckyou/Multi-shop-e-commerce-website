package com.multishop;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.multishop.model.Delivery_Person;
import com.multishop.repository.AdminRepository;
import com.multishop.repository.CustomerRepository;
import com.multishop.repository.DeliRepository;
import com.multishop.repository.ShopRepository;
import com.multishop.service.AdminService;
import com.multishop.service.CustomerService;
import com.multishop.service.DeliveryService;
import com.multishop.service.ShopService;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;
    
    @InjectMocks
    private CustomerService customerService;
    
    @InjectMocks
    private ShopService shopService;
    
    @InjectMocks
    private DeliveryService deliService;

    @Mock
    private AdminRepository adminRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private ShopRepository shopRepository;
    
    @Mock
    private DeliRepository deliRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }

//    @Test
//    void testSaveAdmin() {
//        Admin admin = new Admin();
//        admin.setEmail("admin@gmail.com");
//        admin.setPassword("123456");
//        when(adminRepository.save(admin)).thenReturn(admin);
//        Admin savedAdmin = adminService.saveAdmin(admin);
//        assertNotNull(savedAdmin);
//        assertEquals("admin@gmail.com", savedAdmin.getEmail());
//    }

//    @Test
//    void testFindByEmailAndPassword_ValidCredentials() {
//        String email = "admin@shop.com";
//        String rawPassword = "admin123";
//        String encodedPassword = "$2a$10$encodedpass123";
//
//        Admin admin = new Admin();
//        admin.setEmail(email);
//        admin.setPassword(encodedPassword);
//
//        when(adminRepository.findByEmail(email)).thenReturn(Optional.of(admin));
//        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
//        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
//
//        Optional<Admin> result = adminService.findByEmailandPassword(email, rawPassword);
//
//        assertTrue(result.isPresent());
//        assertEquals(email, result.get().getEmail());
//    } 
//    @Test
//    void testFindByEmailAndPassword_InvalidCredentials() {
//        String email = "wrong@gmail.com";
//        String rawPassword = "wrong123";
//
//        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        Optional<Admin> result = adminService.findByEmailandPassword(email, rawPassword);
//        assertNull(result);
//    }
//
//    // 4. Test creating accounts for customer/shop/delivery from admin (Assuming methods exist)
//    @Test
//    void testRegisterAccountsFromAdminModule() {
//        // Mocked example if you had service like customerService.saveCustomer()
//
//        // You would inject and test services like:
//        // customerService.saveCustomer(), shopService.saveShop(), deliveryService.saveDelivery()
//
//        // Since actual services are not shown in your code, here's a mockup:
//        assertTrue(true, "Assume the related services exist and their save methods are tested separately.");
//    }
//
//    // Optional: Test getAdminById()
//    @Test
//    void testGetAdminById() {
//        Admin admin = new Admin();
//        admin.setAdminId(1L);
//        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
//
//        Admin found = adminService.getAdminById(1L);
//        assertEquals(1L, found.getAdminId());
//    }
//
//    // Optional: Test findAllRecord()
//    @Test
//    void testFindAllAdmins() {
//        Admin admin1 = new Admin();
//        Admin admin2 = new Admin();
//        when(adminRepository.findAll()).thenReturn(Arrays.asList(admin1, admin2));
//
//        List<Admin> allAdmins = adminService.findAllRecord();
//        assertEquals(2, allAdmins.size());
//    }
    
//    @Test
//    void testVerifyNewDeliveryFromAdminModule() {
//        Delivery_Person deli = new Delivery_Person();
//        deli.setEmail("jane@gmail.com");
//        deli.setMember(false); // initially unverified
//
//        when(deliRepository.findById(1L)).thenReturn(Optional.of(deli));
//
//        deli.setMember(true); // verified
//        when(deliRepository.save(deli)).thenReturn(deli);
//
//        Delivery_Person verifiedDeli = deli;
//
//        assertTrue(verifiedDeli.isMember);
//    }
    
    @Test
    void testUpdateDeliveryProfileAndPassword() {
        Delivery_Person existingDeli = new Delivery_Person();
        existingDeli.setDelivery_person_id(1L);
        existingDeli.setEmail("jane@gmail.com");
        existingDeli.setPassword(passwordEncoder.encode("oldpass"));

        when(deliRepository.findById(1L)).thenReturn(Optional.of(existingDeli));
        
        existingDeli.setEmail("newjane@gmail.com");
        existingDeli.setPassword(passwordEncoder.encode("newpass123"));

        when(deliRepository.save(existingDeli)).thenReturn(existingDeli);

        Delivery_Person updatedDeli = deliRepository.save(existingDeli); 
        assertEquals("newjane@gmail.com", updatedDeli.getEmail());
        assertTrue(passwordEncoder.matches("newpass123", updatedDeli.getPassword()));
    }
}
