package com.cindy.Concertbackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**查詢會員資料response */
public class UserInfoResponse {
    String email;
    String name;
    String phone;
    String address;
    String message;
}
