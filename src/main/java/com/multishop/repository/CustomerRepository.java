package com.multishop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.multishop.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
	public Optional<Customer> findByEmailAndPassword(String email, String password);
	public List<Customer> findByfirstName(String firstName);
	public List<Customer> findBylastName(String lastName);
	public List<Customer> findByAddressOrPhoneNumber(String address, String phone);
	public Optional<Customer> findByEmail(String email);
	
	@NonNull
    public Page<Customer> findByIsDeletedFalse(@NonNull Pageable pageable);
	
    @Query("SELECT c FROM Customer c " +
            "WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :name, '%')) ")
    Page<Customer> findCustomerByName(Pageable pageable, @Param("name") String name);
}
