package com.cindy.Concertbackend.model;

import java.util.Date;

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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "[user]")
public class User {

    /** 使用者ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 使用者帳號 */
    private String email;

    /** 使用者密碼 */
    private String password;

    /** 權限層級 0 user/ 1 manager */
    private Integer level;

    /** 使用者全名 */
    private String name;

    /** 使用者電話 */
    private String phone;

    /** 使用者地址 */
    private String address;

    /** 使用者創建日期 */
    private Date createdAt;

}
