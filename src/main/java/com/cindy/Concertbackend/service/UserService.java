package com.cindy.Concertbackend.service;

import java.util.Date;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.transaction.annotation.Transactional;

import com.cindy.Concertbackend.model.User;
import com.cindy.Concertbackend.repository.UserRepository;
import com.cindy.Concertbackend.request.RegisterRequest;
import com.cindy.Concertbackend.request.UpdateUserRequest;
import com.cindy.Concertbackend.request.LoginRequest;
import com.cindy.Concertbackend.response.UpdateUserResponse;
import com.cindy.Concertbackend.response.UserInfoResponse;
import com.cindy.Concertbackend.response.LoginResponse;
import com.cindy.Concertbackend.util.JWTUtil;

import io.jsonwebtoken.JwtException;

@Service
public class UserService {

    @Autowired
    private UserRepository userDao;

    @Autowired
    private JWTUtil jwt;

    /** 驗證token */
    @Transactional(readOnly = true, rollbackFor = { NullPointerException.class })
    public ResponseEntity<LoginResponse> validateToken(String auth) {
        String token = auth.substring(6);
        System.out.println(token);
        String email;
        try {
            email = jwt.extractEmail(token);
            User original = userDao.findByEmail(email);
            if (original != null && !jwt.isTokenExpired(token)) {
                LoginResponse response = LoginResponse.builder()
                        .token(token)
                        .level(original.getLevel())
                        .name(original.getName())
                        .message("驗證成功")
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            LoginResponse response = LoginResponse.builder().message("驗證失敗，請重新登入").build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (JwtException e) {
            // 主要避免JWT的錯誤
            LoginResponse response = LoginResponse.builder().message("JWT 相關錯誤：" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            LoginResponse response = LoginResponse.builder().message("其他例外訊息：" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    /**
     * 會員登入
     */
    @Transactional(readOnly = true, rollbackFor = { NullPointerException.class })
    public ResponseEntity<LoginResponse> loginUser(LoginRequest loginData) {
        try {
            // 判斷使用者是否存在
            User original = userDao.findByEmail(loginData.getEmail());
            if (original == null) {
                LoginResponse response = LoginResponse.builder().message("此帳號尚未註冊").build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                // 透過BCrypt方法比對與資料庫加密的密碼是否一致
                if (BCrypt.checkpw(loginData.getPassword(), original.getPassword())) {
                    // 產生token
                    String token = jwt.generateToken(original);
                    LoginResponse response = LoginResponse.builder()
                            .token(token)
                            .level(original.getLevel())
                            .name(original.getName())
                            .message("登入成功")
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                } else {
                    LoginResponse response = LoginResponse.builder().message("帳號密碼輸入錯誤請重新登入").build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            }
        } catch (JwtException e) {
            LoginResponse response = LoginResponse.builder().message("JWT 相關錯誤：" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            LoginResponse response = LoginResponse.builder().message("其他例外訊息：" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    /**
     * 創建會員
     */
    @Transactional(rollbackFor = { NullPointerException.class },timeout = 2)
    public ResponseEntity<LoginResponse> createUser(RegisterRequest user) {
        try {
            // Thread.sleep(6000);
            // 判斷是否已經註冊過
            if (userDao.findByEmail(user.getEmail()) != null) {
                LoginResponse response = LoginResponse.builder().message("帳號已註冊，請重新登入").build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            // 透過BCrypt對密碼進行加密
            String hashPwd = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            User registerData = User.builder()
                    .email(user.getEmail())
                    .password(hashPwd)
                    .level(user.getLevel())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .address(user.getAddress())
                    .createdAt(new Date())
                    .build();
            // 儲存至資料庫
            userDao.save(registerData);
            // 產生token
            String token = jwt.generateToken(registerData);
            LoginResponse response = LoginResponse.builder()
                    .level(user.getLevel())
                    .token(token)
                    .name(user.getName())
                    .message("註冊成功")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (TransactionTimedOutException  e) {
            LoginResponse response = LoginResponse.builder().message("事務超時：" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (DataAccessException e) {
            LoginResponse response = LoginResponse.builder().message("資料庫相關錯誤：" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            LoginResponse response = LoginResponse.builder().message("其他錯誤訊息" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    /** 查詢會員資料 */
    @Transactional(readOnly = true, rollbackFor = { NullPointerException.class })
    public ResponseEntity<UserInfoResponse> selectUser(String auth) {
        try {
            ResponseEntity<LoginResponse> authResponse = this.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                UserInfoResponse response = UserInfoResponse.builder()
                        .email(user.getEmail())
                        .name(user.getName())
                        .phone(user.getPhone())
                        .address(user.getAddress())
                        .message("查詢成功")
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            UserInfoResponse response = UserInfoResponse.builder().message("尚未登入，請重新登入").build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (JwtException e) {
            UserInfoResponse response = UserInfoResponse.builder().message("JWT 相關錯誤：" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            UserInfoResponse response = UserInfoResponse.builder().message("其他例外訊息：" + e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    /** 修改會員資料 */
    @Transactional(rollbackFor = { NullPointerException.class })
    public ResponseEntity<UpdateUserResponse> updateUserDetail(String auth, UpdateUserRequest user) {
        String token = auth.substring(6);
        System.out.println(token);
        String email;
        try {
            email = jwt.extractEmail(token);
            User originalUser = userDao.findByEmail(email);
            if (originalUser != null) {
                User newUserData = User.builder()
                        .id(originalUser.getId())
                        .email(email)
                        .password(originalUser.getPassword())
                        .level(originalUser.getLevel())
                        .name(user.getName())
                        .address(user.getAddress())
                        .phone(user.getPhone())
                        .createdAt(originalUser.getCreatedAt())
                        .build();
                userDao.save(newUserData);
                UpdateUserResponse reponse = UpdateUserResponse.builder().status("ok").message("修改成功")
                        .name(newUserData.getName()).build();
                return ResponseEntity.status(HttpStatus.OK).body(reponse);
            }
            UpdateUserResponse reponse = UpdateUserResponse.builder().status("error").message("使用者驗證失敗，請重新登入")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(reponse);
        } catch (JwtException e) {
            UpdateUserResponse response = UpdateUserResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            UpdateUserResponse response = UpdateUserResponse.builder().message("其他例外訊息：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

}
