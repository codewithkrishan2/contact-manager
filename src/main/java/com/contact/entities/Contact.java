package com.contact.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

//@Data
@Setter
@Getter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
@Entity
@Table(name = "CONTACT")
public class Contact {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer cId;
	private String name;
	private String secondName;
	private String work;
	private String email;
	private String phone;
	private String image;
	@Column(length = 5000)
	private String description;
	
	@ManyToOne
	private User user;
	
}
