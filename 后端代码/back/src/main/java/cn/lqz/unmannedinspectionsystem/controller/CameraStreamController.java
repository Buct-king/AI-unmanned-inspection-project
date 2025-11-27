package cn.lqz.unmannedinspectionsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import cn.lqz.unmannedinspectionsystem.dto.AutoDetectRtspDTO;
import cn.lqz.unmannedinspectionsystem.config.FlaskConfig;

@RestController
@RequestMapping("/camera")
public class CameraStreamController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FlaskConfig flaskConfig;

    @GetMapping("/stream")
    public void proxyCameraStream(
            @RequestParam(value = "camera_idx", required = false) String cameraIdx,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "frameInterval", required = false) Integer frameInterval,
            HttpServletResponse response) {
        StringBuilder flaskUrl = new StringBuilder(flaskConfig.getFlask2BaseUrl() + "/api/camera/stream");
        boolean hasParam = false;
        if (cameraIdx != null && !cameraIdx.isEmpty()) {
            flaskUrl.append("?camera_idx=").append(cameraIdx);
            hasParam = true;
        }
        if (model != null && !model.isEmpty()) {
            flaskUrl.append(hasParam ? "&" : "?").append("model=").append(model);
            hasParam = true;
        }
        if (frameInterval != null) {
            flaskUrl.append(hasParam ? "&" : "?").append("frame_interval=").append(frameInterval);
        }
        response.setContentType("multipart/x-mixed-replace; boundary=frame");
        try (
            java.io.InputStream is = new java.net.URL(flaskUrl.toString()).openStream();
            java.io.OutputStream os = response.getOutputStream()
        ) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                os.flush();
            }
        } catch (Exception e) {
            // 只设置状态码，不输出JSON
            response.setStatus(500);
        }
    }

    // 代理启动检测
    @PostMapping("/start")
    public ResponseEntity<String> startCamera(@RequestBody Map<String, Object> params) {
        String url = flaskConfig.getFlask2BaseUrl() + "/api/camera/start";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 兼容前端传frameInterval，转为frame_interval
        if (params.containsKey("frameInterval")) {
            Object val = params.remove("frameInterval");
            params.put("frame_interval", val);
        }
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    // 代理停止检测
    @PostMapping("/stop")
    public ResponseEntity<String> stopCamera(@RequestBody Map<String, Object> params) {
        String url = flaskConfig.getFlask2BaseUrl() + "/api/camera/stop";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    // 代理获取检测状态
    @GetMapping("/status")
    public ResponseEntity<String> cameraStatus() {
        String url = flaskConfig.getFlask2BaseUrl() + "/api/camera/status";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping("/list")
    public ResponseEntity<String> listCameras() {
        String url = flaskConfig.getFlask2BaseUrl() + "/api/camera/list";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    // 自动检测海康RTSP流（ONVIF）
    @PostMapping("/auto_detect_rtsp")
    public ResponseEntity<String> autoDetectRtsp(@Valid @RequestBody AutoDetectRtspDTO params) {
        String url = flaskConfig.getFlask2BaseUrl() + "/api/camera/auto_detect_rtsp";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> paramMap = Map.of(
            "ip", params.getIp(),
            "port", params.getPort(),
            "user", params.getUser(),
            "pwd", params.getPwd()
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paramMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    /**
     * 停止摄像头流（显式释放摄像头资源）
     * 
     * 前端关闭页面、切换流、或不再需要流时，主动调用本接口，彻底释放后端摄像头采集/推流资源。
     * 实际释放由 Flask /api/camera/stream/stop 实现。
     * 
     * @param params 需包含 camera_idx 或唯一标识
     * @return Flask 返回内容
     */
    @PostMapping("/stream/stop")
    public ResponseEntity<String> stopCameraStream(@RequestBody Map<String, Object> params) {
        String url = flaskConfig.getFlask2BaseUrl() + "/api/camera/stream/stop";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
} 