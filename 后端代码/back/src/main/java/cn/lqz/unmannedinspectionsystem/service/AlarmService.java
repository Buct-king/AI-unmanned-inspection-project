package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmStatusCountVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;

import java.util.Map;

public interface AlarmService {
    /**
     * 报警记录分页查询
     * @param alarmPageQueryDTO
     * @return
     */
    PageResultVO loadAlarmList(AlarmPageQueryDTO alarmPageQueryDTO);

    /**
     * 标记已处理
     * @param alarmId
     */
    void changeToProcessed(Long alarmId);

    /**
     * 统计处理状态
     * @return
     */
    AlarmStatusCountVO countStatus();

    /**
     * 统计报警记录的类型
     * @return
     */
    Map<String,Long> countType();

    /**
     * 根据月份统计报警记录
     * @return
     */
    Map<Integer,Long> countAlarmTimeByMonth();
    /**
     * 根据月份统计已处理报警记录
     * @return
     */
    Map<Integer,Long> countProcessedTimeByMonth();
}
