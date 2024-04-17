package com.cindy.Concertbackend.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**查詢使用者訂單response */
public class UserOrderResponse {
    String message;
    String status;
    List<OrderAndConcert> list;
    Integer totalPages;
    Long totalData;
}
