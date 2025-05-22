package com.multishop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.multishop.model.Product;
import com.multishop.model.ProductShop;
import com.multishop.model.Shop;
import com.multishop.repository.ProductRepository;
import com.multishop.repository.ProductShopRepository;
import com.multishop.repository.ShopRepository;
import com.multishop.service.ProductService;
import com.multishop.service.ProductShopService;
import com.multishop.service.ShopService;

public class ProductServiceTest 
{
	@InjectMocks
    private ProductService productService;
	
	@InjectMocks
	private ProductShopService productShopService;
	
	@InjectMocks
	private ShopService shopService;

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductShopRepository productShopRepository;
    
    @Mock
    private ShopRepository shopRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
//    @Test
//    void testFindAllProducts() {
//        List<Product> productList = List.of(new Product(), new Product());
//        when(productRepository.findAll()).thenReturn(productList);
//        
//        List<Product> result = productService.findAllRecord();
//        assertEquals(2, result.size());
//    }
    
//    @Test
//    void testAddProductFromShop() {
//        Product product = new Product();
//        product.setProductName("New Product");
//        product.setUnitPrice(100.0);
//        
//        when(productRepository.save(product)).thenReturn(product);
//        
//        Product saved = productService.saveProd(product);
//        assertNotNull(saved);
//        assertEquals("New Product", saved.getProductName());
//    }
    
//    @Test
//    void testFindAllProductsByShopId() {
//    	
//        Long shopId = 1L;
//        Shop shop = new Shop();
//        shop.setShop_id(shopId);
//        Product p1 = new Product(); ProductShop ps1 = new ProductShop(); ps1.setProduct(p1); ps1.setShop(shop);
//        Product p2 = new Product(); ProductShop ps2 = new ProductShop(); ps2.setProduct(p2); ps2.setShop(shop);
//        
//        when(productShopRepository.findByShop_ShopId(shopId)).thenReturn(List.of(p1, p2));
//        
//        List<Product> result = productShopService.findByShopId(shopId);
//        assertEquals(2, result.size());
//        assertNotNull(result);
//    }
    
//    @Test
//    void testUpdateProductInfo() {
//        Product product = new Product();
//        product.setProduct_id(1L);
//        product.setProductName("Old Name");
//        
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        
//        product.setProductName("Updated Name");
//        when(productRepository.save(product)).thenReturn(product);
//        
//        Product updated = productService.saveProd(product);
//        assertEquals("Updated Name", updated.getProductName());
//    }
    
    @Test
    void testUpdateStockQuantity() {
        // Arrange
        Product product = new Product();
        product.setProduct_id(1L);

        Shop shop = new Shop();
        shop.setShop_id(1L);

        ProductShop ps = new ProductShop();
        ps.setProduct(product);
        ps.setShop(shop);
        ps.setStockQuantity(50);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productShopRepository.save(ps)).thenReturn(ps); 

        ProductShop updated = productShopService.save(ps); 

        assertNotNull(updated);
        assertEquals(50, updated.getStockQuantity());
    }
    
//    @Test
//    void testDeleteProduct() {
//        Long productId = 1L;
//        doNothing().when(productRepository).deleteById(productId);
//        
//        productRepository.deleteById(productId);
//        
//        when(productRepository.findById(productId)).thenReturn(Optional.empty());
//        Optional<Product> result = productRepository.findById(productId);
//        assertFalse(result.isPresent());
//    }


}
