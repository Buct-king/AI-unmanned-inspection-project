package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.dto.YoloDetectionRequest;
import cn.lqz.unmannedinspectionsystem.dto.YoloDetectionResponse;
import cn.lqz.unmannedinspectionsystem.dto.YoloVideoDetectionRequest;
import cn.lqz.unmannedinspectionsystem.dto.YoloVideoDetectionResponse;
import cn.lqz.unmannedinspectionsystem.dto.ModelUploadResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import cn.lqz.unmannedinspectionsystem.config.FlaskConfig;
import cn.lqz.unmannedinspectionsystem.service.ModelPtService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * YOLO检测服务
 */
@Service
@Slf4j
public class YoloDetectionService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlaskConfig flaskConfig;

    @Autowired
    private ModelPtService modelPtService;

    /**
     * 检测base64图片
     *
     * @param request 检测请求
     * @return 检测响应
     */
    public YoloDetectionResponse detectBase64Image(YoloDetectionRequest request) {
        try {
            log.info("开始检测图片，模型: {}", request.getModelName());

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model_name", request.getModelName());
            requestBody.put("image_base64", request.getImageBase64());
            requestBody.put("conf_threshold", request.getConfThreshold());
            requestBody.put("iou_threshold", request.getIouThreshold());
            if (request.getTarget_class_names() != null) {
                requestBody.put("target_class_names", request.getTarget_class_names());
            }

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建请求实体
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 调用Flask API
            String url = flaskConfig.getFlaskBaseUrl() + "/api/detect_base64";
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // 解析响应
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
                return convertToYoloDetectionResponse(responseMap);
            } else {
                log.error("Flask API调用失败，状态码: {}", response.getStatusCode());
                return createErrorResponse("Flask API调用失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("检测图片时发生错误", e);
            return createErrorResponse("检测失败: " + e.getMessage());
        }
    }

    /**
     * 获取模型列表
     *
     * @return 模型列表
     */
    public Map<String, Object> getModelList() {
        try {
            String url = flaskConfig.getFlaskBaseUrl() + "/api/list_models";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return objectMapper.readValue(response.getBody(), Map.class);
            } else {
                log.error("获取模型列表失败，状态码: {}", response.getStatusCode());
                return Map.of("error", "获取模型列表失败");
            }
        } catch (Exception e) {
            log.error("获取模型列表时发生错误", e);
            return Map.of("error", "获取模型列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取模型状态
     *
     * @return 模型状态
     */
    public Map<String, Object> getModelStatus() {
        try {
            String url = flaskConfig.getFlaskBaseUrl() + "/api/model_status";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return objectMapper.readValue(response.getBody(), Map.class);
            } else {
                log.error("获取模型状态失败，状态码: {}", response.getStatusCode());
                return Map.of("error", "获取模型状态失败");
            }
        } catch (Exception e) {
            log.error("获取模型状态时发生错误", e);
            return Map.of("error", "获取模型状态失败: " + e.getMessage());
        }
    }

    /**
     * 加载模型
     *
     * @param modelName 模型名称
     * @return 加载结果
     */
    public Map<String, Object> loadModel(String modelName) {
        try {
            Map<String, Object> requestBody = Map.of("model_name", modelName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = flaskConfig.getFlaskBaseUrl() + "/api/load_model";
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return objectMapper.readValue(response.getBody(), Map.class);
            } else {
                log.error("加载模型失败，状态码: {}", response.getStatusCode());
                return Map.of("error", "加载模型失败");
            }
        } catch (Exception e) {
            log.error("加载模型时发生错误", e);
            return Map.of("error", "加载模型失败: " + e.getMessage());
        }
    }

    /**
     * 转换Flask响应为YoloDetectionResponse
     */
    private YoloDetectionResponse convertToYoloDetectionResponse(Map<String, Object> responseMap) {
        YoloDetectionResponse response = new YoloDetectionResponse();
        
        if (responseMap.containsKey("error")) {
            response.setSuccess(false);
            response.setError((String) responseMap.get("error"));
            return response;
        }

        response.setSuccess(true);
        response.setTotalDetections((Integer) responseMap.get("total_detections"));
        response.setAnnotatedImageBase64((String) responseMap.get("annotated_image_base64"));
        
        // 转换检测结果
        if (responseMap.containsKey("detections")) {
            @SuppressWarnings("unchecked")
            var detections = (List<Map<String, Object>>) responseMap.get("detections");
            response.setDetections(detections.stream()
                    .map(this::convertToDetectionResult)
                    .toList());
        }

        // 转换检测信息
        if (responseMap.containsKey("detection_info")) {
            @SuppressWarnings("unchecked")
            var detectionInfo = (Map<String, Object>) responseMap.get("detection_info");
            response.setDetectionInfo(convertToDetectionInfo(detectionInfo));
        }

        return response;
    }

    /**
     * 转换检测结果
     */
    private YoloDetectionResponse.DetectionResult convertToDetectionResult(Map<String, Object> detectionMap) {
        YoloDetectionResponse.DetectionResult result = new YoloDetectionResponse.DetectionResult();
        
        @SuppressWarnings("unchecked")
        var bbox = (List<Integer>) detectionMap.get("bbox");
        result.setBbox(bbox);
        result.setConfidence((Double) detectionMap.get("confidence"));
        result.setClassId((Integer) detectionMap.get("class"));
        result.setClassName((String) detectionMap.get("class_name"));
        
        return result;
    }

    /**
     * 转换检测信息
     */
    private YoloDetectionResponse.DetectionInfo convertToDetectionInfo(Map<String, Object> infoMap) {
        YoloDetectionResponse.DetectionInfo info = new YoloDetectionResponse.DetectionInfo();
        info.setModelName((String) infoMap.get("model_name"));
        info.setDetectionTime((String) infoMap.get("detection_time"));
        info.setConfThreshold((Double) infoMap.get("conf_threshold"));
        info.setIouThreshold((Double) infoMap.get("iou_threshold"));
        return info;
    }

    /**
     * 创建错误响应
     */
    private YoloDetectionResponse createErrorResponse(String errorMessage) {
        YoloDetectionResponse response = new YoloDetectionResponse();
        response.setSuccess(false);
        response.setError(errorMessage);
        return response;
    }

    /**
     * 检测base64视频
     *
     * @param request 视频检测请求
     * @return 视频检测响应
     */
    public YoloVideoDetectionResponse detectBase64Video(YoloVideoDetectionRequest request) {
        try {
            log.info("开始检测视频，模型: {}", request.getModelName());

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model_name", request.getModelName());
            requestBody.put("video_base64", request.getVideoBase64());
            requestBody.put("conf_threshold", request.getConfThreshold());
            requestBody.put("iou_threshold", request.getIouThreshold());
            requestBody.put("frame_interval", request.getFrameInterval() != null ? request.getFrameInterval() : 1);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 创建请求实体
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 调用Flask API
            String url = flaskConfig.getFlaskBaseUrl() + "/api/detect_video_base64";
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // 解析响应
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
                return convertToYoloVideoDetectionResponse(responseMap);
            } else {
                log.error("Flask视频检测API调用失败，状态码: {}", response.getStatusCode());
                return createVideoErrorResponse("Flask视频检测API调用失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("检测视频时发生错误", e);
            return createVideoErrorResponse("视频检测失败: " + e.getMessage());
        }
    }

    /**
     * 转换Flask响应为YoloVideoDetectionResponse
     */
    private YoloVideoDetectionResponse convertToYoloVideoDetectionResponse(Map<String, Object> responseMap) {
        YoloVideoDetectionResponse response = new YoloVideoDetectionResponse();
        
        if (responseMap.containsKey("error")) {
            response.setSuccess(false);
            response.setError((String) responseMap.get("error"));
            return response;
        }

        response.setSuccess(true);
        response.setTotalFrames((Integer) responseMap.get("total_frames"));
        response.setTotalDetections((Integer) responseMap.get("total_detections"));
        response.setProcessedVideoBase64((String) responseMap.get("video_base64"));
        
        // 转换视频信息
        if (responseMap.containsKey("video_info")) {
            @SuppressWarnings("unchecked")
            var videoInfoMap = (Map<String, Object>) responseMap.get("video_info");
            response.setVideoInfo(convertToVideoInfo(videoInfoMap));
        }

        return response;
    }

    /**
     * 转换视频信息
     */
    private YoloVideoDetectionResponse.VideoInfo convertToVideoInfo(Map<String, Object> videoInfoMap) {
        YoloVideoDetectionResponse.VideoInfo videoInfo = new YoloVideoDetectionResponse.VideoInfo();
        videoInfo.setFps((Integer) videoInfoMap.get("fps"));
        videoInfo.setWidth((Integer) videoInfoMap.get("width"));
        videoInfo.setHeight((Integer) videoInfoMap.get("height"));
        videoInfo.setDuration((Double) videoInfoMap.get("duration"));
        return videoInfo;
    }

    /**
     * 创建视频检测错误响应
     */
    private YoloVideoDetectionResponse createVideoErrorResponse(String errorMessage) {
        YoloVideoDetectionResponse response = new YoloVideoDetectionResponse();
        response.setSuccess(false);
        response.setError(errorMessage);
        return response;
    }

    /**
     * 上传模型文件到Flask
     *
     * @param modelFile 模型文件
     * @return 上传结果
     */
    public ModelUploadResponse uploadModel(MultipartFile modelFile, String title, String description) {
        try {
            log.info("开始上传模型文件: {}", modelFile.getOriginalFilename());
            String filename = modelFile.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".pt")) {
                return createModelUploadErrorResponse("只支持.pt格式的模型文件");
            }
            if (modelFile.getSize() > 100 * 1024 * 1024) {
                return createModelUploadErrorResponse("文件大小不能超过100MB");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("model_file", new ByteArrayResource(modelFile.getBytes()) {
                @Override
                public String getFilename() {
                    return filename;
                }
            });
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            String url = flaskConfig.getFlaskBaseUrl() + "/api/upload_model";
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
                ModelUploadResponse uploadResponse = convertToModelUploadResponse(responseMap);
                // 上传成功后自动同步数据库，带 title/description
                String backPath = System.getProperty("user.dir");
                String yoloModelDir = new java.io.File(backPath).getParent() + "/yolov5-master/model";
                // 先同步所有pt
                modelPtService.syncWithModelDir(yoloModelDir);
                // 再补充/更新本次上传的pt的title/description
                if (filename != null) {
                    var pt = modelPtService.getByFileName(filename);
                    if (pt != null) {
                        pt.setTitle(title != null ? title : "未知模型");
                        pt.setDescription(description != null ? description : "暂无描述");
                        modelPtService.update(pt);
                    }
                }
                return uploadResponse;
            } else {
                log.error("Flask模型上传API调用失败，状态码: {}", response.getStatusCode());
                return createModelUploadErrorResponse("Flask模型上传API调用失败: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("上传模型文件时发生错误", e);
            return createModelUploadErrorResponse("上传模型文件失败: " + e.getMessage());
        }
    }

    /**
     * 转换Flask响应为ModelUploadResponse
     */
    private ModelUploadResponse convertToModelUploadResponse(Map<String, Object> responseMap) {
        ModelUploadResponse response = new ModelUploadResponse();
        
        if (responseMap.containsKey("error")) {
            response.setSuccess(false);
            response.setError((String) responseMap.get("error"));
            return response;
        }

        response.setSuccess(true);
        response.setMessage((String) responseMap.get("message"));
        
        // 转换模型信息
        if (responseMap.containsKey("model_info")) {
            @SuppressWarnings("unchecked")
            var modelInfoMap = (Map<String, Object>) responseMap.get("model_info");
            response.setModelInfo(convertToModelInfo(modelInfoMap));
        }

        return response;
    }

    /**
     * 转换模型信息
     */
    private ModelUploadResponse.ModelInfo convertToModelInfo(Map<String, Object> modelInfoMap) {
        ModelUploadResponse.ModelInfo modelInfo = new ModelUploadResponse.ModelInfo();
        modelInfo.setFilename((String) modelInfoMap.get("filename"));
        modelInfo.setSizeMb((Double) modelInfoMap.get("size_mb"));
        modelInfo.setPath((String) modelInfoMap.get("path"));
        modelInfo.setUploadTime((String) modelInfoMap.get("upload_time"));
        return modelInfo;
    }

    /**
     * 创建模型上传错误响应
     */
    private ModelUploadResponse createModelUploadErrorResponse(String errorMessage) {
        ModelUploadResponse response = new ModelUploadResponse();
        response.setSuccess(false);
        response.setError(errorMessage);
        return response;
    }
} 