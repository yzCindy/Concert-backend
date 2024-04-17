package com.cindy.Concertbackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**修改會員資料response */
public class UpdateUserResponse {
    String message;
    String status;
    String name;
}
