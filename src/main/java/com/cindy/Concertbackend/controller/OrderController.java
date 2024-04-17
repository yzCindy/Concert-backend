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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cindy.Concertbackend.request.OrderRequest;
import com.cindy.Concertbackend.response.ManageOrderResponse;
import com.cindy.Concertbackend.response.UserOrderResponse;
import com.cindy.Concertbackend.response.CancelledOrderResponse;
import com.cindy.Concertbackend.response.OrderResponse;
import com.cindy.Concertbackend.service.OrderService;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** 新增訂單 */
    @PostMapping
    public ResponseEntity<OrderResponse> order(
            @RequestHeader("Authorization") String auth,
            @RequestBody OrderRequest request) {

        return orderService.addOrder(request, auth);
    }

    /** 管理者查詢擁有的訂單 */
    @GetMapping("/manage")
    public ResponseEntity<ManageOrderResponse> selectAllOrder(
            @RequestHeader("Authorization") String auth,
            @RequestParam Integer page,
            @RequestParam Integer size
            ) {
        return orderService.findAllOrder(auth,page,size);
    }

    /** 使用者查詢擁有的訂單 */
    @GetMapping("/info")
    public ResponseEntity<UserOrderResponse> selectUserOrder(
            @RequestHeader("Authorization") String auth ,
            @RequestParam Integer page,
            @RequestParam Integer size
            ) {
        return orderService.findUserOrder(auth,page,size);
    }

    /** 是否取消訂單 */
    @PutMapping("/modify")
    public ResponseEntity<CancelledOrderResponse> changeOrder(
            @RequestHeader("Authorization") String auth,
            @RequestParam Integer orderId) {
        return orderService.updateOrder(auth, orderId);

    }
}