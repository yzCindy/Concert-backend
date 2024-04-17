package com.cindy.Concertbackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**修改節目response */
public class ModifyConcertResponse {
    String message;
    String status;
}
