package cn.lqz.unmannedinspectionsystem.dto;

import lombok.Data;
import java.util.List;

/**
 * YOLO视频检测响应DTO
 */
@Data
public class YoloVideoDetectionResponse {
    /**
     * 检测是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 总帧数
     */
    private Integer totalFrames;
    
    /**
     * 总检测数
     */
    private Integer totalDetections;
    
    /**
     * 处理后的视频base64编码
     */
    private String processedVideoBase64;
    
    /**
     * 视频信息
     */
    private VideoInfo videoInfo;
    
    /**
     * 视频信息内部类
     */
    @Data
    public static class VideoInfo {
        /**
         * 帧率
         */
        private Integer fps;
        
        /**
         * 宽度
         */
        private Integer width;
        
        /**
         * 高度
         */
        private Integer height;
        
        /**
         * 时长（秒）
         */
        private Double duration;
    }
} 