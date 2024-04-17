package com.cindy.Concertbackend.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**圖片上傳response */
public class ImageResponse {
    String imgName;
    String message;
    String status;

}
