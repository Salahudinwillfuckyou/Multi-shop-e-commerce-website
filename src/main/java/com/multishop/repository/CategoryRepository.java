package com.multishop.repository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.multishop.model.Category;;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>
{
	public Category findByCategoryName(String name);
	
	@Query(value = "SELECT * FROM category WHERE is_deleted = false LIMIT 6", nativeQuery = true)
    List<Category> findTopSixCategories();
	
	@Query("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId")
	Long countProductsByCategory(@Param("categoryId") long categoryId);
	
		@Query("SELECT c.categoryName, SUM(od.soldQuantity) " +
	           "FROM OrderDetail od " +
	           "JOIN od.product p " +
	           "JOIN p.category c " +
	           "GROUP BY c.categoryName")
	    List<Object[]> findCategorySalesRaw();

	    // Convert raw data to a Map<String, Integer>
	    default Map<String, Integer> findCategorySales() 
	    {
	        List<Object[]> rawData = findCategorySalesRaw();
	        Map<String, Integer> result = new LinkedHashMap<>();
	        for (Object[] row : rawData) 
	        {
	        	System.out.println((String) row[0]);
	            String categoryName = (String) row[0];
	            Long totalQuantity = (Long) row[1]; // SUM returns Long
	            result.put(categoryName, totalQuantity.intValue());
	        }
	        return result;
	    }
}
