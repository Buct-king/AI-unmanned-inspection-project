package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.entity.ModelPt;
import cn.lqz.unmannedinspectionsystem.service.ModelPtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/model_pt")
public class ModelPtController {
    @Autowired
    private ModelPtService modelPtService;

    @GetMapping("/list")
    public List<ModelPt> listAll() {
        return modelPtService.listAll();
    }

    @GetMapping("/{fileName}")
    public ModelPt getByFileName(@PathVariable String fileName) {
        return modelPtService.getByFileName(fileName);
    }

    @PostMapping("/activate")
    public String activate(@RequestParam("fileName") String fileName) {
        ModelPt modelPt = modelPtService.getByFileName(fileName);
        if (modelPt != null) {
            modelPt.setStatus(1);
            modelPtService.update(modelPt);
            return "activated";
        }
        return "not found";
    }

    @PostMapping("/sync")
    public Map<String, Object> syncModelPt() {
        // 修正同步目录为back同级的yolov5-master/model
        String backPath = System.getProperty("user.dir");
        String yoloModelDir = new java.io.File(backPath).getParent() + "/yolov5-master/model";
        modelPtService.syncWithModelDir(yoloModelDir);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "同步完成");
        return result;
    }
} 