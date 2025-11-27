/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-07-06 16:14:13
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-06 16:22:59
 * @FilePath: yolo_test/back/src/main/java/cn/lqz/unmannedinspectionsystem/dto/ModelUploadResponse.java
 * @Description: 模型上传响应DTO
 */
package cn.lqz.unmannedinspectionsystem.dto;

import lombok.Data;

/**
 * 模型上传响应DTO
 */
@Data
public class ModelUploadResponse {
    /**
     * 上传是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 成功消息
     */
    private String message;
    
    /**
     * 模型信息
     */
    private ModelInfo modelInfo;
    
    /**
     * 模型信息内部类
     */
    @Data
    public static class ModelInfo {
        /**
         * 文件名
         */
        private String filename;
        
        /**
         * 文件大小(MB)
         */
        private Double sizeMb;
        
        /**
         * 文件路径
         */
        private String path;
        
        /**
         * 上传时间
         */
        private String uploadTime;
    }
} 