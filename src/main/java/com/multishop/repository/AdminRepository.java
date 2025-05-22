package com.multishop.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multishop.model.Admin;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>
{
	public Optional<Admin> findByEmailAndPassword(String email, String password);
	public List<Admin> findByfirstName(String firstName);
	public List<Admin> findBylastName(String lastName);
	public Optional<Admin> findByEmail(String email);
}
