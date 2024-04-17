package com.cindy.Concertbackend.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "[order]")
public class Order {

    /** 訂單ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 使用者ID */
    @Column(name = "fk_userId")
    private Integer userId;

    /** 演唱會ID */
    @Column(name = "fk_concertId")
    private Integer concertId;

    /** 訂購數量 */
    private Integer quantity;

    /** 總金額 */
    private Integer totalPrice;

    /** 是否取消 */
    private Boolean isCancelled;

    /** 訂單創建日期 */
    private Date createdAt;

}
