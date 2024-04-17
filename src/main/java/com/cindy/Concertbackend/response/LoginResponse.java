package com.cindy.Concertbackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**註冊登入response */
public class LoginResponse {
    String token;
    Integer level;
    String name;
    String message;
}
