package cn.lqz.unmannedinspectionsystem.dto;

import lombok.Data;

/**
 * YOLO检测请求DTO
 */
@Data
public class YoloDetectionRequest {
    /**
     * 模型文件名
     */
    private String modelName;
    
    /**
     * base64编码的图片数据
     */
    private String imageBase64;
    
    /**
     * 置信度阈值
     */
    private Double confThreshold = 0.25;
    
    /**
     * IOU阈值
     */
    private Double iouThreshold = 0.45;
    
    /**
     * 只检测这些类别（可选）
     */
    private java.util.List<String> target_class_names;
} 