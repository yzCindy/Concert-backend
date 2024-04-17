package com.cindy.Concertbackend.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cindy.Concertbackend.response.ImageResponse;

@Service
public class ImageService {

    /**
     * 照片存放目錄
     */
    @Value("${product.image.directory}")
    private String imageDirectory;

    /**
     * 存放照片至指定目錄
     */
    public ResponseEntity<ImageResponse> addImg(MultipartFile img) {
        String originalName = img.getOriginalFilename();
        String filetype = originalName.substring(originalName.lastIndexOf("."));
        // 創建檔案名稱
        String imageName = UUID.randomUUID().toString() + filetype;
        // 創建資料夾
        File directory = new File(imageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 將圖片存入指定目錄
        try {
            img.transferTo(new File(directory, imageName));
        } catch (IllegalStateException e) {
            ImageResponse response = ImageResponse
                    .builder()
                    .message("IllegalStateException錯誤訊息:" + e.getMessage())
                    .status("error")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            ImageResponse response = ImageResponse
                    .builder()
                    .message("IOException錯誤訊息:" + e.getMessage())
                    .status("error")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            ImageResponse response = ImageResponse
                    .builder()
                    .message("錯誤訊息:" + e.getMessage())
                    .status("error")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        ImageResponse response = ImageResponse
                .builder()
                //還傳照片名稱
                .imgName(imageName)
                .message("新增照片成功")
                .status("ok")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 查詢照片
     * param:
     * 照片名稱
     * return:
     * byte[]
     */
    public byte[] getImage(String imageName) throws IOException {
        // 透過存放路徑創建檔案物件（File）
        File imageFile = new File(imageDirectory + "/" + imageName);
        BufferedImage image = ImageIO.read(imageFile);
        // 取得檔案副檔名
        String fileExtension = imageName.substring(imageName.lastIndexOf(".") + 1);
        String formatName = getFormatNameForImageExtension(fileExtension);
        // 新增ByteArrayOutputStream物件，裝寫入的圖片資料
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);

        return baos.toByteArray();
    }

    /**
     * 根據檔案副檔名來決定圖片的格式
     */
    private String getFormatNameForImageExtension(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "jpeg":
            case "jpg":
                return "jpeg";
            case "png":
                return "png";
            default:
                return "jpeg"; // 或者根據需求返回預設格式
        }
    }
}
