package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.dto.MeasuringPointPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.MeasuringPointDetailVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.MeasuringPointService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mp")
@RequiredArgsConstructor
@Validated
public class MeasuringPointController {
    private final MeasuringPointService measuringPointService;

    /**
     * 测点分页列表
     * @param measuringPointPageQueryDTO
     * @return
     */
    @PostMapping("/loadList")
    public ResponseVO<PageResultVO> loadMeasuringPointList(MeasuringPointPageQueryDTO measuringPointPageQueryDTO){
        return ResponseUtils.success(measuringPointService.loadMeasuringPointList(measuringPointPageQueryDTO));
    }

    /**
     * 加载测点图片
     * @param mpId
     * @return
     */
    @PostMapping("/loadImage")
    public ResponseVO<String> loadMeasuringPointImage(@NotNull  Long mpId){
        return ResponseUtils.success(measuringPointService.loadImage(mpId));
    }

    /**
     * 获取测点详情
     * @param mpId
     * @return
     */
    @PostMapping("/getDetail")
    public ResponseVO<MeasuringPointDetailVO> getMeasuringPointDetail(@NotNull Long mpId){
        return ResponseUtils.success(measuringPointService.getMeasuringPointDetail(mpId));
    }
}
