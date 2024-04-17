package com.cindy.Concertbackend.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.cindy.Concertbackend.model.Concerts;
import com.cindy.Concertbackend.model.Order;
import com.cindy.Concertbackend.model.User;
import com.cindy.Concertbackend.repository.ConcertRepository;
import com.cindy.Concertbackend.repository.OrderRepository;
import com.cindy.Concertbackend.repository.UserRepository;
import com.cindy.Concertbackend.request.OrderRequest;
import com.cindy.Concertbackend.response.ManageOrderResponse;
import com.cindy.Concertbackend.response.UserOrderResponse;
import com.cindy.Concertbackend.response.CancelledOrderResponse;
import com.cindy.Concertbackend.response.OrderAndConcert;
import com.cindy.Concertbackend.response.OrderResponse;
import com.cindy.Concertbackend.response.LoginResponse;
import com.cindy.Concertbackend.util.JWTUtil;

import io.jsonwebtoken.JwtException;

@Service
public class OrderService {

    @Autowired
    private JWTUtil jwt;

    @Autowired
    private OrderRepository orderDao;

    @Autowired
    private ConcertRepository concertDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userDao;

    /** 新增訂單 */
    // 此Transaction，避免幻影讀，使讀到的數字與資料庫一致
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { NullPointerException.class })
    public ResponseEntity<OrderResponse> addOrder(OrderRequest request, String auth) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                Concerts concert = concertDao.findById(request.getConcertId()).orElse(null);
                if (concert != null) {
                    if ( request.getOrderQuantity() <= concert.getRemaingQuantity() ) {
                        // 對剩餘票量的修改(前端已經擋下剩餘數量本來就是0的狀況)
                        concert.setRemaingQuantity(concert.getRemaingQuantity() - request.getOrderQuantity());
                        if (concert.getRemaingQuantity() == 0) {
                            concert.setStatus(2);
                        }
                        // 新增訂單資訊
                        Order newOrder = Order.builder()
                                .userId(user.getId())
                                .concertId(concert.getId())
                                .quantity(request.getOrderQuantity())
                                .totalPrice(request.getOrderQuantity() * concert.getPrice())
                                .isCancelled(false)
                                .createdAt(new Date())
                                .build();
                        orderDao.save(newOrder);
                        concertDao.save(concert);
                        OrderResponse response = OrderResponse.builder().status("ok").message("訂購成功").build();
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    } else {
                        OrderResponse response = OrderResponse.builder().status("error").message("剩餘票量不足，請重新選擇購買數量")
                                .build();
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }
                OrderResponse response = OrderResponse.builder().status("error").message("此節目不存在，請重新確認").build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            OrderResponse response = OrderResponse.builder().status("error").message("尚未登入，請重新登入").build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (JwtException e) {
            OrderResponse response = OrderResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            OrderResponse response = OrderResponse.builder().message("其他例外訊息：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    /** 查詢使用者擁有的訂單 */
    @Transactional(readOnly = true, rollbackFor = { NullPointerException.class })
    public ResponseEntity<UserOrderResponse> findUserOrder(String auth, int page, int size) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                // 查詢使用者全部訂單數
                long totalOrders = orderDao.countByUserId(user.getId());
                // 計算分頁
                int totalPages = (int) Math.ceil((double) totalOrders / size);

                // 查詢使用者擁有的訂單
                Pageable pageable = PageRequest.of(page, size);
                Page<Order> newpage = orderDao.findByUserId(user.getId(), pageable);
                List<OrderAndConcert> responseList = new ArrayList<>();
                if (newpage != null) {
                    for (Order order : newpage) {
                        System.out.println("每一個" + order);
                        Concerts concert = concertDao.findById(order.getConcertId()).orElse(null);
                        OrderAndConcert data = OrderAndConcert.builder()
                                .orderId(order.getId())
                                .concertId(order.getConcertId())
                                .quantity(order.getQuantity())
                                .totalPrice(order.getTotalPrice())
                                .isCancelled(order.getIsCancelled())
                                .createdAt(order.getCreatedAt())
                                .concertName(concert.getConcertName())
                                .concertTime(concert.getConcertTime())
                                .address(concert.getAddress())
                                .price(concert.getPrice())
                                .build();
                        responseList.add(data);
                    }
                    UserOrderResponse response = UserOrderResponse
                            .builder()
                            .list(responseList)
                            .totalPages(totalPages)
                            .totalData(totalOrders)
                            .status("ok")
                            .message("查詢成功")
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                UserOrderResponse response = UserOrderResponse.builder()
                        .status("error")
                        .message("尚無訂單")
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (JwtException e) {
            UserOrderResponse response = UserOrderResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        UserOrderResponse response = UserOrderResponse.builder()
                .status("error")
                .message("使用者錯誤，請重新登入")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /** 查詢管理者擁有的訂單 */
    @Transactional(readOnly = true, rollbackFor = { NullPointerException.class })
    public ResponseEntity<ManageOrderResponse> findAllOrder(String auth, int page, int size) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                // 查詢管理者全部訂單數
                long totalOrders = orderDao.countByManagerId(user.getId());
                // 計算分頁
                int totalPages = (int) Math.ceil((double) totalOrders / size);
                // 查詢管理者擁有的訂單
                Pageable pageable = PageRequest.of(page, size);
                Page<Order> list = orderDao.findByManagerId(user.getId(), pageable);
                List<OrderAndConcert> responseList = new ArrayList<>();
                if (list != null) {
                    for (Order order : list) {
                        Concerts concert = concertDao.findById(order.getConcertId()).orElse(null);
                        OrderAndConcert data = OrderAndConcert.builder()
                                .orderId(order.getId())
                                .concertId(order.getConcertId())
                                .quantity(order.getQuantity())
                                .totalPrice(order.getTotalPrice())
                                .isCancelled(order.getIsCancelled())
                                .createdAt(order.getCreatedAt())
                                .concertName(concert.getConcertName())
                                .concertTime(concert.getConcertTime())
                                .address(concert.getAddress())
                                .price(concert.getPrice())
                                .build();
                        responseList.add(data);
                    }
                    ManageOrderResponse response = ManageOrderResponse.builder()
                            .list(responseList)
                            .totalPages(totalPages)
                            .totalData(totalOrders)
                            .status("ok")
                            .message("查詢成功")
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                ManageOrderResponse response = ManageOrderResponse.builder()
                        .status("error")
                        .message("尚無訂單")
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (JwtException e) {
            ManageOrderResponse response = ManageOrderResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        ManageOrderResponse response = ManageOrderResponse.builder()
                .status("error")
                .message("使用者錯誤，請重新登入")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /** 取消訂單 */
    @Transactional(rollbackFor = { NullPointerException.class })
    public ResponseEntity<CancelledOrderResponse> updateOrder(String auth, Integer orderId) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                Order order = orderDao.findById(orderId).orElse(null);
                if (order != null) {
                    order.setIsCancelled(true);
                    Concerts orderConcert = concertDao.findById(order.getConcertId()).orElse(null);
                    orderConcert.setRemaingQuantity(orderConcert.getRemaingQuantity() + order.getQuantity());
                    if(orderConcert.getRemaingQuantity() !=0){
                        orderConcert.setStatus(0);
                    }
                    orderDao.save(order);
                    CancelledOrderResponse response = CancelledOrderResponse.builder().status("ok").message("取消成功")
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                CancelledOrderResponse response = CancelledOrderResponse.builder().status("error").message("訂單不存在")
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (JwtException e) {
            CancelledOrderResponse response = CancelledOrderResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        CancelledOrderResponse response = CancelledOrderResponse.builder().status("ok").message("使用者錯誤，請重新登入").build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
