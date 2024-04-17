package com.cindy.Concertbackend.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**登入request */
public class LoginRequest {
    String email;
    String password;
}
