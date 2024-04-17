package com.cindy.Concertbackend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cindy.Concertbackend.model.User;


@Repository
public interface UserRepository extends JpaRepository<User,Integer>  {
 
    /**查詢會員資料 */
    public User findByEmail(String email);
  
} 