package com.cindy.Concertbackend.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**創建訂單request */
public class OrderRequest {
    Integer concertId ;
    Integer orderQuantity;
    
}
