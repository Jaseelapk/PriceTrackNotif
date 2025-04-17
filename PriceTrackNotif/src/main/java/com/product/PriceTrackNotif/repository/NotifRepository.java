package com.product.PriceTrackNotif.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.PriceTrackNotif.model.Notification;


/**
 * Repository interface for accessing  Notification entities from the database.
 * 
 * Extends JpaRepository to provide standard CRUD operations and custom queries.
 */
@Repository
public interface NotifRepository extends JpaRepository<Notification, Integer>{
	List<Notification> findByStatus(String status);
	
	

}