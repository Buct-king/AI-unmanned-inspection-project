package cn.lqz.unmannedinspectionsystem.dto;

import lombok.Data;

/**
 * YOLO视频检测请求DTO
 */
@Data
public class YoloVideoDetectionRequest {
    /**
     * 模型文件名
     */
    private String modelName;
    
    /**
     * base64编码的视频数据
     */
    private String videoBase64;
    
    /**
     * 置信度阈值
     */
    private Double confThreshold = 0.25;
    
    /**
     * IOU阈值
     */
    private Double iouThreshold = 0.45;
    
    /**
     * 跳帧间隔（每多少帧检测一次，默认1）
     */
    private Integer frameInterval = 1;
} 