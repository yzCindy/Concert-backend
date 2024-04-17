package com.cindy.Concertbackend.request;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**修改節目request */
public class ConcerModifyRequest {
    private Integer id;
    private Integer userId;
    private String concertName;
    private Date concertTime;
    private String information;
    private String address;
    private Date saleTime;
    private Integer price;
    private Integer saleQuantity;
    private Integer remaingQuantity;
    private String contentType;
    private String image;
    /** 活動狀態
     *   0上架中 / 1 下架中 /2 已售完/3 已刪除
     */
    private Integer status;
}
