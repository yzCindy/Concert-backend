package com.cindy.Concertbackend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cindy.Concertbackend.model.Concerts;
import com.cindy.Concertbackend.model.User;
import com.cindy.Concertbackend.repository.ConcertRepository;
import com.cindy.Concertbackend.repository.UserRepository;
import com.cindy.Concertbackend.request.ConcerModifyRequest;
import com.cindy.Concertbackend.request.ConcertRequest;
import com.cindy.Concertbackend.response.SearchSaleConcertResponse;
import com.cindy.Concertbackend.response.ManageConcertResponse;
import com.cindy.Concertbackend.response.ConcertSearchResponse;
import com.cindy.Concertbackend.response.CreateConcertResponse;
import com.cindy.Concertbackend.response.ModifyConcertResponse;
import com.cindy.Concertbackend.response.LoginResponse;
import com.cindy.Concertbackend.util.JWTUtil;

import io.jsonwebtoken.JwtException;

@Service
public class ConcertService {

    @Autowired
    private JWTUtil jwt;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userDao;

    @Autowired
    private ConcertRepository concertDao;

    @Autowired
    private ImageService imgService;

    /**
     * 關鍵字查詢所有販售中節目
     * param:
     * 1.使用者輸入的關鍵字
     */
    @Transactional(readOnly = true, rollbackFor = { NullPointerException.class })
    public ResponseEntity<SearchSaleConcertResponse> keyFindOnSaleConcert(String keyWord) {
        // 透過關鍵字進行查詢
        List<Concerts> list = concertDao.findConcertsByKeyWord(keyWord);
        if (list != null && list.size() != 0) {
            List<Concerts> concertList = new ArrayList<>();
            for (Concerts concert : list) {
                try {
                    // 轉換圖片為Base64
                    byte[] imgBytes = imgService.getImage(concert.getImage());
                    String basePhoto = "data:image/" + concert.getContentType() + ";base64,"
                            + Base64.getEncoder().encodeToString(imgBytes);
                    Concerts responseConcert = Concerts
                            .builder()
                            .id(concert.getId())
                            .userId(concert.getUserId())
                            .concertName(concert.getConcertName())
                            .concertTime(concert.getConcertTime())
                            .information(concert.getInformation())
                            .address(concert.getAddress())
                            .saleTime(concert.getSaleTime())
                            .price(concert.getPrice())
                            .saleQuantity(concert.getSaleQuantity())
                            .remaingQuantity(concert.getRemaingQuantity())
                            .contentType(concert.getContentType())
                            .image(basePhoto)
                            .status(concert.getStatus())
                            .build();
                    concertList.add(responseConcert);
                } catch (IOException e) {
                    SearchSaleConcertResponse response = SearchSaleConcertResponse
                            .builder()
                            .message("IOException錯誤訊息:" + e.getMessage())
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                } catch (Exception e) {
                    SearchSaleConcertResponse response = SearchSaleConcertResponse
                            .builder()
                            .message("錯誤訊息:" + e.getMessage())
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            }
            SearchSaleConcertResponse response = SearchSaleConcertResponse
                    .builder()
                    .message("查詢成功")
                    .list(concertList)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        SearchSaleConcertResponse response = SearchSaleConcertResponse.builder()
                .message("尚無資料")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /** 查詢所有販售中節目 */
    @Transactional(readOnly = true, rollbackFor = { NullPointerException.class })
    public ResponseEntity<SearchSaleConcertResponse> findOnSaleConcert() {
        List<Concerts> list = concertDao.findConcertsByTime();
        if (list != null && list.size() != 0) {
            List<Concerts> concertList = new ArrayList<>();
            for (Concerts concert : list) {
                try {
                    byte[] imgBytes = imgService.getImage(concert.getImage());
                    String basePhoto = "data:image/" + concert.getContentType() + ";base64,"
                            + Base64.getEncoder().encodeToString(imgBytes);
                    Concerts responseConcert = Concerts
                            .builder()
                            .id(concert.getId())
                            .userId(concert.getUserId())
                            .concertName(concert.getConcertName())
                            .concertTime(concert.getConcertTime())
                            .information(concert.getInformation())
                            .address(concert.getAddress())
                            .saleTime(concert.getSaleTime())
                            .price(concert.getPrice())
                            .saleQuantity(concert.getSaleQuantity())
                            .remaingQuantity(concert.getRemaingQuantity())
                            .contentType(concert.getContentType())
                            .image(basePhoto)
                            .status(concert.getStatus())
                            .build();
                    concertList.add(responseConcert);
                } catch (IOException e) {
                    SearchSaleConcertResponse response = SearchSaleConcertResponse
                            .builder()
                            .message("錯誤訊息:" + e.getMessage())
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                } catch (Exception e) {
                    SearchSaleConcertResponse response = SearchSaleConcertResponse
                            .builder()
                            .message("錯誤訊息:" + e.getMessage())
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            }
            SearchSaleConcertResponse response = SearchSaleConcertResponse
                    .builder()
                    .message("查詢成功")
                    .list(concertList)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        SearchSaleConcertResponse response = SearchSaleConcertResponse.builder()
                .message("尚無資料")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 刪除節目
     * param:
     * 1.節目id
     * 2.token
     */
    @Transactional(rollbackFor = { NullPointerException.class })
    public ResponseEntity<ModifyConcertResponse> deleteConcert(Integer id, String auth) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                Concerts original = concertDao.findById(id).orElse(null);
                if (original != null) {
                    // 變更為已刪除的狀況
                    original.setStatus(3);
                    concertDao.save(original);
                    ModifyConcertResponse response = ModifyConcertResponse.builder().message("刪除成功").status("ok")
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                ModifyConcertResponse response = ModifyConcertResponse.builder().message("此節目不存在").status("ok").build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (JwtException e) {
            ModifyConcertResponse response = ModifyConcertResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ModifyConcertResponse response = ModifyConcertResponse.builder().message("其他例外訊息：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        ModifyConcertResponse response = ModifyConcertResponse.builder().message("使用者無效，請重新登入").status("ok").build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 修改節目
     * param:
     * 1.節目id
     * 2.token
     */
    @Transactional(rollbackFor = { NullPointerException.class })
    public ResponseEntity<ModifyConcertResponse> modifyConcert(ConcerModifyRequest concert, String auth) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                Concerts original = concertDao.findById(concert.getId()).orElse(null);
                if (original != null) {
                    String imgData = concert.getImage().length() == 0 ? original.getImage() : concert.getImage();
                    Concerts modifyConcerts = Concerts.builder()
                            .id(concert.getId())
                            .userId(concert.getUserId())
                            .concertName(concert.getConcertName())
                            .concertTime(concert.getConcertTime())
                            .information(concert.getInformation())
                            .address(concert.getAddress())
                            .saleTime(concert.getSaleTime())
                            .price(concert.getPrice())
                            .saleQuantity(concert.getSaleQuantity())
                            .remaingQuantity(concert.getRemaingQuantity())
                            .contentType(concert.getContentType())
                            .image(imgData)
                            .status(concert.getStatus())
                            .createdAt(original.getCreatedAt())
                            .build();

                    concertDao.save(modifyConcerts);
                    ModifyConcertResponse response = ModifyConcertResponse.builder().message("修改成功").status("ok")
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);

                }
                ModifyConcertResponse response = ModifyConcertResponse.builder().message("此節目不存在").status("ok").build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (JwtException e) {
            ModifyConcertResponse response = ModifyConcertResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ModifyConcertResponse response = ModifyConcertResponse.builder().message("其他例外訊息：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        ModifyConcertResponse response = ModifyConcertResponse.builder().message("使用者無效，請重新登入").status("ok").build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 增加節目
     * param:
     * 1.節目data
     * 2.token
     */
    @Transactional(rollbackFor = { NullPointerException.class })
    public ResponseEntity<CreateConcertResponse> addConcert(ConcertRequest concert, String auth) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                Concerts newConcerts = Concerts.builder()
                        .userId(user.getId())
                        .concertName(concert.getConcertName())
                        .concertTime(concert.getConcertTime())
                        .information(concert.getInformation())
                        .address(concert.getAddress())
                        .saleTime(concert.getSaleTime())
                        .price(concert.getPrice())
                        .saleQuantity(concert.getSaleQuantity())
                        .remaingQuantity(concert.getRemaingQuantity())
                        .contentType(concert.getContentType())
                        .image(concert.getImage())
                        .status(1)
                        .createdAt(new Date())
                        .build();
                concertDao.save(newConcerts);
                CreateConcertResponse response = CreateConcertResponse.builder().message("新增成功").status("ok").build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (JwtException e) {
            CreateConcertResponse response = CreateConcertResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            CreateConcertResponse response = CreateConcertResponse.builder().message("其他例外訊息：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        CreateConcertResponse response = CreateConcertResponse.builder().message("使用者無效，請重新登入").status("ok").build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 查詢管理的節目
     * param:
     * 1.token
     * 2.page 第幾頁
     * 3.size 每頁需幾個data
     */
    public ResponseEntity<ManageConcertResponse> findConcert(String auth, int page, int size) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            List<Concerts> concertList = new ArrayList<>();
            if (user != null) {
                // 查詢使用者所有節目數量
                long totalConcerts = concertDao.countByEmail(email);
                // 計算分頁
                int totalPages = (int) Math.ceil((double) totalConcerts / size);
                // 查詢使用者擁有的訂單
                Pageable pageable = PageRequest.of(page, size);
                Page<Concerts> concerts = concertDao.findConcertsByEmail(email, pageable);
                if (concerts != null) {
                    for (Concerts concert : concerts) {
                        try {
                            byte[] imgBytes = imgService.getImage(concert.getImage());
                            String basePhoto = "data:image/" + concert.getContentType() + ";base64,"
                                    + Base64.getEncoder().encodeToString(imgBytes);
                            Concerts responseConcert = Concerts
                                    .builder()
                                    .id(concert.getId())
                                    .userId(concert.getUserId())
                                    .concertName(concert.getConcertName())
                                    .concertTime(concert.getConcertTime())
                                    .information(concert.getInformation())
                                    .address(concert.getAddress())
                                    .saleTime(concert.getSaleTime())
                                    .price(concert.getPrice())
                                    .saleQuantity(concert.getSaleQuantity())
                                    .remaingQuantity(concert.getRemaingQuantity())
                                    .contentType(concert.getContentType())
                                    .image(basePhoto)
                                    .status(concert.getStatus())
                                    .build();
                            concertList.add(responseConcert);
                        } catch (IOException e) {
                            ManageConcertResponse response = ManageConcertResponse
                                    .builder()
                                    .message("錯誤訊息:" + e.getMessage())
                                    .build();
                            return ResponseEntity.status(HttpStatus.OK).body(response);
                        }
                    }
                    ManageConcertResponse response = ManageConcertResponse
                            .builder()
                            .message("查詢成功")
                            .totalPages(totalPages)
                            .totalData(totalConcerts)
                            .list(concertList)
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }

                ManageConcertResponse response = ManageConcertResponse
                        .builder()
                        .message("尚無資料")
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (JwtException e) {
            ManageConcertResponse response = ManageConcertResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ManageConcertResponse response = ManageConcertResponse.builder().message("其他例外訊息：" + e.getMessage())
                    .build();
                    System.out.println( e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        ManageConcertResponse response = ManageConcertResponse
                .builder()
                .message("使用者無效，請重新登入")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 查詢單一節目BY concert ID
     * 1.節目id
     * 2.token
     */
    @Transactional(readOnly = true, rollbackFor = { NullPointerException.class })
    public ResponseEntity<ConcertSearchResponse> findConcertById(String auth, Integer id) {
        try {
            ResponseEntity<LoginResponse> authResponse = userService.validateToken(auth);
            String email = jwt.extractEmail(authResponse.getBody().getToken());
            User user = userDao.findByEmail(email);
            if (user != null) {
                Concerts original = concertDao.findById(id).orElse(null);
                if (original != null) {
                    byte[] imgBytes;
                    try {
                        imgBytes = imgService.getImage(original.getImage());
                        String basePhoto = "data:image/" + original.getContentType() + ";base64,"
                                + Base64.getEncoder().encodeToString(imgBytes);
                        Concerts responseConcert = Concerts
                                .builder()
                                .id(original.getId())
                                .userId(original.getUserId())
                                .concertName(original.getConcertName())
                                .concertTime(original.getConcertTime())
                                .information(original.getInformation())
                                .address(original.getAddress())
                                .saleTime(original.getSaleTime())
                                .price(original.getPrice())
                                .saleQuantity(original.getSaleQuantity())
                                .remaingQuantity(original.getRemaingQuantity())
                                .contentType(original.getContentType())
                                .image(basePhoto)
                                .status(original.getStatus())
                                .build();
                        ConcertSearchResponse response = ConcertSearchResponse.builder()
                                .message("查詢成功")
                                .concert(responseConcert)
                                .build();
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    } catch (IOException e) {
                        ConcertSearchResponse response = ConcertSearchResponse.builder()
                                .message("錯誤訊息:" + e.getMessage())
                                .build();
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }
                ConcertSearchResponse response = ConcertSearchResponse.builder()
                        .message("無此節目，請重新查詢")
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (JwtException e) {
            ConcertSearchResponse response = ConcertSearchResponse.builder().message("JWT 相關錯誤：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ConcertSearchResponse response = ConcertSearchResponse.builder().message("其他例外訊息：" + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        ConcertSearchResponse response = ConcertSearchResponse.builder()
                .message("使用者無效，請重新登入")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}