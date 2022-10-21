package com.biricik.springbootsociallogin.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email") })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String password;
	private String email;
	private String imageUrl;
	
	@Column(nullable = false)
	private Boolean emailVerified =false;
	
	
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;
	
	private String providerId;
	
	
	
	
	public User(String username, String password, String email) {
	
		this.username = username;
		this.password = password;
		this.email = email;
	}




	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="user_roles" ,joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	Set<Role> roles = new HashSet<>();
	
	
}
