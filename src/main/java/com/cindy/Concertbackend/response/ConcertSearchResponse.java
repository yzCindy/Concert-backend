package com.cindy.Concertbackend.response;

import com.cindy.Concertbackend.model.Concerts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**查詢單一節目response */
public class ConcertSearchResponse {
        String message;
        Concerts concert;
}
