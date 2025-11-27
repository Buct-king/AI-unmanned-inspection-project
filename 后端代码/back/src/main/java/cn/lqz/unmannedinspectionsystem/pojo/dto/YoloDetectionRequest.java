package cn.lqz.unmannedinspectionsystem.pojo.dto;

import lombok.Data;

/**
 * YOLO检测请求DTO
 */
@Data
public class YoloDetectionRequest {
    
    /**
     * 模型文件名（.pt文件）
     */
    private String modelName;
    
    /**
     * Base64编码的图片数据
     */
    private String imageBase64;
    
    /**
     * 置信度阈值
     */
    private Double confidenceThreshold = 0.25;
    
    /**
     * IOU阈值
     */
    private Double iouThreshold = 0.45;
} 