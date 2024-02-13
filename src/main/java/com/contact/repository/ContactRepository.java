package com.contact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	//method for finding the contact list of particular user
	//Method for pagination 
	
	//Current Page
	//Contact per page -5
	
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pageable);
	
	
}
