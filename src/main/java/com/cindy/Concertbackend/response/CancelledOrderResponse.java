package com.cindy.Concertbackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**取消訂單response */
public class CancelledOrderResponse {
    String message;
    String status;
}
