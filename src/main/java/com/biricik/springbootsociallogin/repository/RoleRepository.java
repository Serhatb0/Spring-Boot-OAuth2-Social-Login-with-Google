package com.biricik.springbootsociallogin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biricik.springbootsociallogin.model.ERole;
import com.biricik.springbootsociallogin.model.Role;

public interface RoleRepository extends JpaRepository<Role,Long>{

	
	Optional<Role> findByName(ERole name);
}
