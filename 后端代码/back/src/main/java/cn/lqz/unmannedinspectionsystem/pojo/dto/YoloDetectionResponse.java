/*
 * YOLO检测响应DTO
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-07-05 23:55:50
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-05 23:58:57
 * @Description: YOLO检测响应数据传输对象
 */
package cn.lqz.unmannedinspectionsystem.pojo.dto;

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
     * 检测到的目标列表
     */
    private List<Detection> detections;
    
    /**
     * 检测到的目标总数
     */
    private Integer totalDetections;
    
    /**
     * 图片信息
     */
    private ImageInfo imageInfo;
    
    /**
     * 检测目标信息
     */
    @Data
    public static class Detection {
        /**
         * 边界框坐标 [x1, y1, x2, y2]
         */
        private List<Double> bbox;
        
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
     * 图片信息
     */
    @Data
    public static class ImageInfo {
        /**
         * 文件名
         */
        private String filename;
        
        /**
         * 文件路径
         */
        private String path;
        
        /**
         * 检测时间
         */
        private String detectionTime;
    }
} 