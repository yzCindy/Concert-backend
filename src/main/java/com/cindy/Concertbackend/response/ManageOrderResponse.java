package com.cindy.Concertbackend.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**查詢管理者擁有的訂單response */
public class ManageOrderResponse {
    String message;
    String status;
    List<OrderAndConcert> list;
    Integer totalPages;
    Long totalData;
}
