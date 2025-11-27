package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.dto.DetectDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.DetectService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/detect")
@RequiredArgsConstructor
public class DetectController {
    private final DetectService detectService;

    @PostMapping("/detect")
    public ResponseVO<String> imageUpload(@Valid DetectDTO detectDTO){
        return ResponseUtils.success(detectService.detect(detectDTO));
    }
}
