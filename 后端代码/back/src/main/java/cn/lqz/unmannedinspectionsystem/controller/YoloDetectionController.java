package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.dto.YoloDetectionRequest;
import cn.lqz.unmannedinspectionsystem.dto.YoloDetectionResponse;
import cn.lqz.unmannedinspectionsystem.dto.YoloVideoDetectionRequest;
import cn.lqz.unmannedinspectionsystem.dto.YoloVideoDetectionResponse;
import cn.lqz.unmannedinspectionsystem.dto.ModelUploadResponse;
import cn.lqz.unmannedinspectionsystem.service.YoloDetectionService;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * YOLO检测控制器
 */
@RestController
@RequestMapping("/yolo")
@Slf4j
public class YoloDetectionController {

    @Autowired
    private YoloDetectionService yoloDetectionService;

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseVO<String> health() {
        ResponseVO<String> responseVO = new ResponseVO<>();
        responseVO.setCode(200);
        responseVO.setInfo("操作成功");
        responseVO.setData("YOLO检测服务正常运行");
        return responseVO;
    }

    /**
     * 检测base64图片
     *
     * @param request 检测请求
     * @return 检测结果
     */
    @PostMapping("/detect")
    public ResponseVO<YoloDetectionResponse> detectImage(@RequestBody YoloDetectionRequest request) {
        try {
            log.info("收到检测请求，模型: {}", request.getModelName());
            
            YoloDetectionResponse response = yoloDetectionService.detectBase64Image(request);
            
            ResponseVO<YoloDetectionResponse> responseVO = new ResponseVO<>();
            if (response.getSuccess()) {
                responseVO.setCode(200);
                responseVO.setInfo("操作成功");
                responseVO.setData(response);
            } else {
                responseVO.setCode(500);
                responseVO.setInfo(response.getError());
                responseVO.setData(null);
            }
            return responseVO;
        } catch (Exception e) {
            log.error("检测图片时发生错误", e);
            ResponseVO<YoloDetectionResponse> responseVO = new ResponseVO<>();
            responseVO.setCode(500);
            responseVO.setInfo("检测失败: " + e.getMessage());
            responseVO.setData(null);
            return responseVO;
        }
    }

    /**
     * 获取模型列表
     *
     * @return 模型列表
     */
    @GetMapping("/models")
    public ResponseVO<Map<String, Object>> getModelList() {
        try {
            Map<String, Object> result = yoloDetectionService.getModelList();
            
            ResponseVO<Map<String, Object>> responseVO = new ResponseVO<>();
            if (result.containsKey("error")) {
                responseVO.setCode(500);
                responseVO.setInfo((String) result.get("error"));
                responseVO.setData(null);
            } else {
                responseVO.setCode(200);
                responseVO.setInfo("操作成功");
                responseVO.setData(result);
            }
            return responseVO;
        } catch (Exception e) {
            log.error("获取模型列表时发生错误", e);
            ResponseVO<Map<String, Object>> responseVO = new ResponseVO<>();
            responseVO.setCode(500);
            responseVO.setInfo("获取模型列表失败: " + e.getMessage());
            responseVO.setData(null);
            return responseVO;
        }
    }

    /**
     * 获取模型状态
     *
     * @return 模型状态
     */
    @GetMapping("/model/status")
    public ResponseVO<Map<String, Object>> getModelStatus() {
        try {
            Map<String, Object> result = yoloDetectionService.getModelStatus();
            
            ResponseVO<Map<String, Object>> responseVO = new ResponseVO<>();
            if (result.containsKey("error")) {
                responseVO.setCode(500);
                responseVO.setInfo((String) result.get("error"));
                responseVO.setData(null);
            } else {
                responseVO.setCode(200);
                responseVO.setInfo("操作成功");
                responseVO.setData(result);
            }
            return responseVO;
        } catch (Exception e) {
            log.error("获取模型状态时发生错误", e);
            ResponseVO<Map<String, Object>> responseVO = new ResponseVO<>();
            responseVO.setCode(500);
            responseVO.setInfo("获取模型状态失败: " + e.getMessage());
            responseVO.setData(null);
            return responseVO;
        }
    }

    /**
     * 加载模型
     *
     * @param modelName 模型名称
     * @return 加载结果
     */
    @PostMapping("/model/load")
    public ResponseVO<Map<String, Object>> loadModel(@RequestParam String modelName) {
        try {
            log.info("加载模型: {}", modelName);
            Map<String, Object> result = yoloDetectionService.loadModel(modelName);
            ResponseVO<Map<String, Object>> responseVO = new ResponseVO<>();
            if (result.containsKey("error")) {
                responseVO.setCode(500);
                responseVO.setInfo((String) result.get("error"));
                responseVO.setData(null);
            } else {
                responseVO.setCode(200);
                responseVO.setInfo("操作成功");
                responseVO.setData(result);
                if (result.containsKey("class_names")) {
                    responseVO.setInfo(responseVO.getInfo() + ",class_names:" + result.get("class_names"));
                }
            }
            return responseVO;
        } catch (Exception e) {
            log.error("加载模型时发生错误", e);
            ResponseVO<Map<String, Object>> responseVO = new ResponseVO<>();
            responseVO.setCode(500);
            responseVO.setInfo("加载模型失败: " + e.getMessage());
            responseVO.setData(null);
            return responseVO;
        }
    }

    /**
     * 检测base64视频
     *
     * @param request 视频检测请求
     * @return 视频检测结果
     */
    @PostMapping("/detect/video")
    public ResponseVO<YoloVideoDetectionResponse> detectVideo(@RequestBody YoloVideoDetectionRequest request) {
        try {
            log.info("收到视频检测请求，模型: {}，frameInterval: {}", request.getModelName(), request.getFrameInterval());
            
            YoloVideoDetectionResponse response = yoloDetectionService.detectBase64Video(request);
            
            ResponseVO<YoloVideoDetectionResponse> responseVO = new ResponseVO<>();
            if (response.getSuccess()) {
                responseVO.setCode(200);
                responseVO.setInfo("操作成功");
                responseVO.setData(response);
            } else {
                responseVO.setCode(500);
                responseVO.setInfo(response.getError());
                responseVO.setData(null);
            }
            return responseVO;
        } catch (Exception e) {
            log.error("检测视频时发生错误", e);
            ResponseVO<YoloVideoDetectionResponse> responseVO = new ResponseVO<>();
            responseVO.setCode(500);
            responseVO.setInfo("视频检测失败: " + e.getMessage());
            responseVO.setData(null);
            return responseVO;
        }
    }

    /**
     * 上传模型文件
     *
     * @param modelFile 模型文件
     * @return 上传结果
     */
    @PostMapping("/upload/model")
    public ResponseVO<ModelUploadResponse> uploadModel(@RequestParam("modelFile") MultipartFile modelFile,
                                                  @RequestParam(value = "title", required = false) String title,
                                                  @RequestParam(value = "description", required = false) String description) {
        try {
            log.info("收到模型文件上传请求: {}", modelFile.getOriginalFilename());
            ModelUploadResponse response = yoloDetectionService.uploadModel(modelFile, title, description);
            ResponseVO<ModelUploadResponse> responseVO = new ResponseVO<>();
            if (response.getSuccess()) {
                responseVO.setCode(200);
                responseVO.setInfo("操作成功");
                responseVO.setData(response);
            } else {
                responseVO.setCode(500);
                responseVO.setInfo(response.getError());
                responseVO.setData(null);
            }
            return responseVO;
        } catch (Exception e) {
            log.error("上传模型文件时发生错误", e);
            ResponseVO<ModelUploadResponse> responseVO = new ResponseVO<>();
            responseVO.setCode(500);
            responseVO.setInfo("上传模型文件失败: " + e.getMessage());
            responseVO.setData(null);
            return responseVO;
        }
    }
} 