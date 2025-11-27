package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.vo.DeviceStatusCountVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.DeviceService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;
    @PostMapping("/countStatus")
    public ResponseVO<DeviceStatusCountVO> countStatus() {
        return ResponseUtils.success(deviceService.countStatus());
    }

    @PostMapping("/countType")
    public ResponseVO<Map<String,Integer>> countType(){
        return ResponseUtils.success(deviceService.countType());
    }
}
