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
@Table(name = "concerts")
public class Concerts {

    /** 演唱會ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 使用者ID */
    @Column(name = "fk_userId")
    private Integer userId;

    /** 活動名稱 */
    private String concertName;

    /** 活動時間 */
    private Date concertTime;

    /** 活動資訊 */
    private String information;

    /** 活動地址 */
    private String address;

    /** 活動銷售時間 */
    private Date saleTime;

    /** 單張票價 */
    private Integer price;

    /** 預計銷售票數 */
    private Integer saleQuantity;

    /** 剩餘票數 */
    private Integer remaingQuantity;

    /** 圖片類型 */
    private String contentType;

    /** 圖片類型 */
    private String image;

    /** 活動狀態
     *   0上架中 / 1 下架中 /2 已售完/3 已刪除
     */
    private Integer status;

    /** 演唱會創建時間 */
    private Date createdAt;

}
