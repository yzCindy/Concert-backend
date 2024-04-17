package com.cindy.Concertbackend.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**修改會員資訊request */
public class UpdateUserRequest {
    private String name;
    private String phone;
    private String address;
}
