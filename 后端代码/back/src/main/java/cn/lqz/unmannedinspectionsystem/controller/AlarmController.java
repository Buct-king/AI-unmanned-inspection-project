package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.annotations.PageQueryAutoFill;
import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmStatusCountVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.AlarmService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    /**
     * 加载报警数据列表
     * @param alarmPageQueryDTO
     * @return
     */
    @PostMapping("/loadList")
    @PageQueryAutoFill
    public ResponseVO<PageResultVO> loadAlarmList(AlarmPageQueryDTO alarmPageQueryDTO){
        return ResponseUtils.success(alarmService.loadAlarmList(alarmPageQueryDTO));
    }

    /**
     * 改变单条报警记录的状态
     * @param alarmId
     * @return
     */
    @PostMapping("/changeToProcessed")
    public ResponseVO changeToProcessed(Long alarmId){
        alarmService.changeToProcessed(alarmId);
        return ResponseUtils.success();
    }

    /**
     * 统计报警记录的处置状态
     * @return
     */
    @PostMapping("/countStatus")
    public ResponseVO<AlarmStatusCountVO> countStatus(){
        return ResponseUtils.success(alarmService.countStatus());
    }

    /**
     * 统计报警记录的类型
     * @return
     */
    @PostMapping("/countType")
    public ResponseVO<Map<String,Long>> countType(){
        return ResponseUtils.success(alarmService.countType());
    }

    /**
     * 根据月份统计报警记录
     * @return
     */
    @PostMapping("/countAlarmTimeByMonth")
    public ResponseVO<Map<Integer,Long>> countAlarmTimeByMonth(){
        return ResponseUtils.success(alarmService.countAlarmTimeByMonth());
    }

    /**
     * 根据月份统计已处理报警记录
     * @return
     */
    @PostMapping("/countProcessedTimeByMonth")
    public ResponseVO<Map<Integer,Long>> countProcessedTimeByMonth(){
        return ResponseUtils.success(alarmService.countProcessedTimeByMonth());
    }


}
