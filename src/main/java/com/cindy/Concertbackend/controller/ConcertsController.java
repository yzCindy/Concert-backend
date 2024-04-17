package com.cindy.Concertbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cindy.Concertbackend.request.ConcerModifyRequest;
import com.cindy.Concertbackend.request.ConcertRequest;
import com.cindy.Concertbackend.response.SearchSaleConcertResponse;
import com.cindy.Concertbackend.response.ManageConcertResponse;
import com.cindy.Concertbackend.response.ConcertSearchResponse;
import com.cindy.Concertbackend.response.CreateConcertResponse;
import com.cindy.Concertbackend.response.ImageResponse;
import com.cindy.Concertbackend.response.ModifyConcertResponse;
import com.cindy.Concertbackend.service.ConcertService;
import com.cindy.Concertbackend.service.ImageService;

@RestController
@CrossOrigin
@RequestMapping("/concert")
public class ConcertsController {

    private final static Logger log = LoggerFactory.getLogger(ConcertsController.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private ConcertService concertService;

    /**
     * 新增照片
     */
    @PostMapping(path = "/img", consumes = "multipart/form-data")
    public ResponseEntity<ImageResponse> saveImg(
        @RequestParam(name = "image") MultipartFile photo) {
        if (photo != null) {
            return imageService.addImg(photo);
        }
        ImageResponse response = ImageResponse
                .builder()
                .message("未上傳照片，請重新上傳")
                .status("error")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 新增節目
     */
    @PostMapping("/create")
    public ResponseEntity<CreateConcertResponse> saveConcert(
            @RequestHeader("Authorization") String auth,
            @RequestBody ConcertRequest concert) {
        return concertService.addConcert(concert, auth);
    }

    /**
     * 修改節目
     */
    @PutMapping("/modify")
    public ResponseEntity<ModifyConcertResponse> changeConcert(
            @RequestHeader("Authorization") String auth,
            @RequestBody ConcerModifyRequest concert) {
        return concertService.modifyConcert(concert, auth);
    }

    /**
     * 刪除節目
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ModifyConcertResponse> deleteConcert(
            @RequestHeader("Authorization") String auth,
            @RequestParam Integer id) {
        return concertService.deleteConcert(id, auth);
    }

    /**
     * 查詢可管理的節目
     */
    @GetMapping("/manage")
    public ResponseEntity<ManageConcertResponse> selectConcert(
            @RequestHeader("Authorization") String auth,
            @RequestParam Integer page,
            @RequestParam Integer size) {

        return concertService.findConcert(auth, page, size);
    }

    /**
     * 查詢節目BY ID
     */
    @GetMapping
    public ResponseEntity<ConcertSearchResponse> selectConcertById(
            @RequestHeader("Authorization") String auth,
            @RequestParam Integer id) {
                log.info("查詢id={}",id);
        return concertService.findConcertById(auth, id);
    }

    /**
     * 查詢全部可購買節目
     */
    @GetMapping("/search")
    public ResponseEntity<SearchSaleConcertResponse> selectOnSaleConcert() {
        return concertService.findOnSaleConcert();
    }

    /**
     * 關鍵字查詢全部可購買節目
     */
    @GetMapping("/searchKey")
    public ResponseEntity<SearchSaleConcertResponse> keySelectOnSaleConcert(
            @RequestParam String keyword) {
        return concertService.keyFindOnSaleConcert(keyword);
    }

}
