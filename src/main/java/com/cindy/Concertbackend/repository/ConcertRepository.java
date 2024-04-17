package com.cindy.Concertbackend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cindy.Concertbackend.model.Concerts;

@Repository
public interface ConcertRepository extends JpaRepository<Concerts, Integer> {

    /** 查詢管理者的節目 */

    // JPQL:@Query("SELECT c  FROM Concerts c LEFT JOIN User u ON c.userId = u. id WHERE u.email = :email and c.status != 3 Order by c.concertTime desc")
    // public Page<Concerts> findConcertsByEmail(@Param("email") String email, Pageable pageable);

    //Native SQL
    @Query(nativeQuery = true , value ="SELECT c.*, u.id as user_id FROM [concerts] as c LEFT JOIN [user] as u ON c.[fk_userId] = u.[id] WHERE u.email = :email and c.status != 3 Order by c.concertTime desc")
    public Page<Concerts> findConcertsByEmail(@Param("email") String email, Pageable pageable);
    

    /** 查詢管理者節目總數 (分頁使用) */
    @Query("SELECT COUNT(c.id)  FROM Concerts c LEFT JOIN User u ON c.userId = u. id WHERE u.email = :email and c.status != 3 ")
    public Long countByEmail(@Param("email") String email);

    /** 查詢銷售中的節目 */
    @Query("SELECT c FROM Concerts c WHERE c.concertTime > CURRENT_TIMESTAMP and c.status != 1 and c.status != 3 order by c.concertTime asc ")
    public List<Concerts> findConcertsByTime();

    /** 依關鍵字查詢節目 */
    @Query("SELECT c FROM Concerts c WHERE c.concertTime > CURRENT_TIMESTAMP and c.status != 1 and c.status != 3  and c.concertName LIKE  %:keyWord% order by c.concertTime asc ")
    public List<Concerts> findConcertsByKeyWord(@Param("keyWord") String keyWord);

}
