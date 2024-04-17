package com.cindy.Concertbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cindy.Concertbackend.request.RegisterRequest;
import com.cindy.Concertbackend.request.UpdateUserRequest;
import com.cindy.Concertbackend.request.LoginRequest;
import com.cindy.Concertbackend.response.UpdateUserResponse;
import com.cindy.Concertbackend.response.UserInfoResponse;
import com.cindy.Concertbackend.response.LoginResponse;
import com.cindy.Concertbackend.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /** POST:驗證token */
    @PostMapping("/validate")
    public ResponseEntity<LoginResponse> validateToken(
            @RequestHeader("Authorization") String auth) {
        return userService.validateToken(auth);
    }

    /** POST:登入 */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @RequestBody LoginRequest userLoginRequest) {
        return userService.loginUser(userLoginRequest);
    }

    /** POST:註冊 */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registerUser(
            @RequestBody RegisterRequest user) {
        return userService.createUser(user);
    }

    
    /** GET:取得使用者資訊 */
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> selectUserInfo(
            @RequestHeader("Authorization") String token) {
        return userService.selectUser(token);
    }

    
    /** PUT:修改使用者資訊 */
    @PutMapping("/modify")
    public ResponseEntity<UpdateUserResponse> changeUserDetail(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateUserRequest user) {
        return userService.updateUserDetail(token, user);
    }

}
