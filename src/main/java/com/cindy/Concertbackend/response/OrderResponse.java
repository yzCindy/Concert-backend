package com.cindy.Concertbackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/** 創建訂單response*/
public class OrderResponse {
    String message;
    String status;
}
