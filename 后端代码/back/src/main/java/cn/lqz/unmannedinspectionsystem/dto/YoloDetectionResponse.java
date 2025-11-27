package cn.lqz.unmannedinspectionsystem.dto;

import lombok.Data;
import java.util.List;

/**
 * YOLO检测响应DTO
 */
@Data
public class YoloDetectionResponse {
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 检测结果列表
     */
    private List<DetectionResult> detections;
    
    /**
     * 总检测数量
     */
    private Integer totalDetections;
    
    /**
     * 标注后的图片base64数据
     */
    private String annotatedImageBase64;
    
    /**
     * 原始图片尺寸
     */
    private List<Integer> originalImageShape;
    
    /**
     * 检测信息
     */
    private DetectionInfo detectionInfo;
    
    /**
     * 检测结果
     */
    @Data
    public static class DetectionResult {
        /**
         * 边界框坐标 [x1, y1, x2, y2]
         */
        private List<Integer> bbox;
        
        /**
         * 置信度
         */
        private Double confidence;
        
        /**
         * 类别ID
         */
        private Integer classId;
        
        /**
         * 类别名称
         */
        private String className;
    }
    
    /**
     * 检测信息
     */
    @Data
    public static class DetectionInfo {
        /**
         * 模型名称
         */
        private String modelName;
        
        /**
         * 检测时间
         */
        private String detectionTime;
        
        /**
         * 置信度阈值
         */
        private Double confThreshold;
        
        /**
         * IOU阈值
         */
        private Double iouThreshold;
    }
} 