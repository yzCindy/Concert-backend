package com.cindy.Concertbackend.response;

import java.util.List;

import com.cindy.Concertbackend.model.Concerts;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
/**管理者查詢可管理節目response */
public class ManageConcertResponse {
    String message;
    List<Concerts> list;
    Integer totalPages;
    Long totalData;
}
