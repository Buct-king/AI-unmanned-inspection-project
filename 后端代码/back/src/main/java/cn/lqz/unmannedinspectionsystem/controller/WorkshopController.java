package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.WorkshopService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/workshop")
@RequiredArgsConstructor
public class WorkshopController {
    private final WorkshopService workshopService;

    @PostMapping("/countDevice")
    public ResponseVO<Map<String,Integer>> countDevice(){
        return ResponseUtils.success(workshopService.countDevice());
    }
}
