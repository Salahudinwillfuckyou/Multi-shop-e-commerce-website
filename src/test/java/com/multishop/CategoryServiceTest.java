package com.multishop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.multishop.model.Category;
import com.multishop.repository.CategoryRepository;
import com.multishop.service.CategoryService;
public class CategoryServiceTest 
{
	@InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
//    @Test
//    void testAddCategory() {
//        Category category = new Category();
//        category.setCategoryName("Electronics");
//
//        when(categoryRepository.save(category)).thenReturn(category);
//
//        Category saved = categoryService.saveCategory(category);
//
//        assertNotNull(saved);
//        assertEquals("Electronics", saved.getCategoryName());
//    }
    
//    @Test
//    void testUpdateCategory() {
//        Category category = new Category();
//        category.setCategoryId(1L);
//        category.setCategoryName("Old Name");
//
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//
//        category.setCategoryName("New Name");
//
//        when(categoryRepository.save(category)).thenReturn(category);
//
//        Category updated = categoryService.saveCategory(category);
//
//        assertEquals("New Name", updated.getCategoryName());
//    }
    
//    @Test
//    void testDeleteCategory() {
//        Long catId = 1L;
//        doNothing().when(categoryRepository).deleteById(catId);
//        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());
//
//        categoryRepository.deleteById(catId);
//
//        Optional<Category> result = categoryRepository.findById(catId);
//        assertFalse(result.isPresent());
//    }

    @Test
    void testFindAllCategories() {
        List<Category> categories = List.of(new Category(), new Category(), new Category());

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAllRecord();

        assertNotNull(result);
        assertEquals(3, result.size());
    }
}
