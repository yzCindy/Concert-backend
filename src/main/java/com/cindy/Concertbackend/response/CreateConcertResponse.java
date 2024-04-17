package com.cindy.Concertbackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**創建節目response */
public class CreateConcertResponse {
    String message;
    String status;
}
