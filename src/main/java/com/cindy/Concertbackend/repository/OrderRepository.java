package com.cindy.Concertbackend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cindy.Concertbackend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

        /** 查詢管理者的訂單 */
        @Query("SELECT o FROM Order o " +
                        "LEFT JOIN Concerts c ON c.id = o.concertId " +
                        "LEFT JOIN User u ON u.id = o.userId " +
                        "WHERE c.userId = :id ORDER BY o.createdAt DESC")
        public Page<Order> findByManagerId(@Param("id") Integer id, Pageable pageable);

        /** 查詢管理者總訂單數 */
        @Query("SELECT COUNT(o.id) FROM Order o " +
                        "LEFT JOIN Concerts c ON c.id = o.concertId " +
                        "LEFT JOIN User u ON u.id = o.userId " +
                        "WHERE c.userId = :id")
        public Long countByManagerId(Integer id);

        /** 查詢使用者者的訂單 */
        @Query("SELECT o FROM Order o " +
                        "LEFT JOIN Concerts c ON c.id = o.concertId " +
                        "LEFT JOIN User u ON u.id = o.userId " +
                        "WHERE o.userId = :id ORDER BY o.createdAt DESC")
        public Page<Order> findByUserId(@Param("id") Integer id, Pageable pageable);

        /** 查詢使用者總訂單數 */
        @Query("SELECT COUNT(o.id) FROM Order o " +
                        "LEFT JOIN Concerts c ON c.id = o.concertId " +
                        "LEFT JOIN User u ON u.id = o.userId " +
                        "WHERE o.userId = :id ")
        public Long countByUserId(Integer id);
}
