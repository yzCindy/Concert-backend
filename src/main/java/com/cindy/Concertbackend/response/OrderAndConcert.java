package com.cindy.Concertbackend.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/** 訂單與演唱會資訊VO*/
public class OrderAndConcert {
    private Integer orderId;
    private Integer concertId;
    private Integer quantity;
    private Integer totalPrice;
    private Boolean isCancelled;
    private Date createdAt;
    private String concertName;
    private Date concertTime;
    private String address;
    private Integer price;
}
