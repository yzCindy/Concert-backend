package com.cindy.Concertbackend.response;

import java.util.List;

import com.cindy.Concertbackend.model.Concerts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**查詢可販售的節目response */
public class SearchSaleConcertResponse {
    String message;
    List<Concerts> list;
}
