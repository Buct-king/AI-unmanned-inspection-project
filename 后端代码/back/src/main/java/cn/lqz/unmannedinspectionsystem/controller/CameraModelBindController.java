package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.config.FlaskConfig;
import cn.lqz.unmannedinspectionsystem.pojo.entity.CameraModelBind;
import cn.lqz.unmannedinspectionsystem.service.CameraModelBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/camera_model_bind")
public class CameraModelBindController {
    @Autowired
    private CameraModelBindService cameraModelBindService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FlaskConfig flaskConfig;

    @GetMapping("/list")
    public List<CameraModelBind> list() {
        return cameraModelBindService.selectAll();
    }

    @PostMapping("/add")
    public int add(@RequestBody CameraModelBind cameraModelBind) {
        return cameraModelBindService.insert(cameraModelBind);
    }

    @PutMapping("/update")
    public int update(@RequestBody CameraModelBind cameraModelBind) {
        return cameraModelBindService.update(cameraModelBind);
    }

    @DeleteMapping("/delete/{id}")
    public int delete(@PathVariable Long id) {
        return cameraModelBindService.deleteById(id);
    }

    @GetMapping("/{id}")
    public CameraModelBind getById(@PathVariable Long id) {
        return cameraModelBindService.selectById(id);
    }

    // 检测接口：查库后转发给 Flask
    @PostMapping("/detect/{id}")
    public ResponseEntity<String> detect(@PathVariable Long id) {
        CameraModelBind camera = cameraModelBindService.selectById(id);
        if (camera == null) {
            return ResponseEntity.badRequest().body("摄像头不存在");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("camera_source", camera.getCameraSource());
        params.put("model", camera.getModelName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
        String flaskUrl = flaskConfig.getFlask2BaseUrl() + "/api/camera/start";
        ResponseEntity<String> flaskResp = restTemplate.postForEntity(flaskUrl, entity, String.class);
        return ResponseEntity.status(flaskResp.getStatusCode()).body(flaskResp.getBody());
    }
} 