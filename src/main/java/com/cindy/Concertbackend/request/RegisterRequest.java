package com.cindy.Concertbackend.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**註冊request */
public class RegisterRequest {
      private String email;
      private String password;
      private Integer level;
      private String name;
      private String phone;
      private String address;
}
