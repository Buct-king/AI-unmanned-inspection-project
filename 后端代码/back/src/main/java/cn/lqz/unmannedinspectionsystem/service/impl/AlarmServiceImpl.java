package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.AlarmStatusEnum;
import cn.lqz.unmannedinspectionsystem.enums.MeasuringPointStatusEnum;
import cn.lqz.unmannedinspectionsystem.mapper.AlarmMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Alarm;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmStatusCountVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.AlarmVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.service.AlarmService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmMapper alarmMapper;
    /**
     * 报警记录分页查询
     * @param alarmPageQueryDTO
     * @return
     */
    @Override
    public PageResultVO loadAlarmList(AlarmPageQueryDTO alarmPageQueryDTO) {
        log.info("报警记录分页查询：{}",alarmPageQueryDTO);
        PageHelper.startPage(alarmPageQueryDTO.getPageNo(), alarmPageQueryDTO.getPageSize());
        Page<AlarmVO> alarmPage =  alarmMapper.findBatch(alarmPageQueryDTO);
        // 根据报警类型int值填入报警类型String值，异常测点的状态就是报警的类型
        for (AlarmVO alarmVO : alarmPage) {
            MeasuringPointStatusEnum mpse = MeasuringPointStatusEnum.getByStatus(alarmVO.getType());
            alarmVO.setTypeString(mpse==null?"无":mpse.getDescription());
        }
        return new PageResultVO(alarmPageQueryDTO.getPageNo(), alarmPageQueryDTO.getPageSize(), alarmPage.getTotal(), alarmPage.getResult());
    }

    /**
     * 标记已处理
     * @param alarmId
     */
    @Override
    public void changeToProcessed(Long alarmId) {
        log.info("报警记录：标记已处理：{}",alarmId);
        Alarm alarm = new Alarm();
        alarm.setAlarmId(alarmId);
        alarm.setStatus(AlarmStatusEnum.PROCESSED.getStatus());
        alarm.setProcessedTime(LocalDateTime.now());
        alarmMapper.update(alarm);
    }

    /**
     * 统计处理状态
     * @return
     */
    @Override
    public AlarmStatusCountVO countStatus() {
        log.info("统计报警记录处理状态");
        Long unprocessedCount = alarmMapper.countByStatus(AlarmStatusEnum.UNPROCESSED.getStatus());
        Long processedCount = alarmMapper.countByStatus(AlarmStatusEnum.PROCESSED.getStatus());
        return new AlarmStatusCountVO(unprocessedCount,processedCount);
    }

    /**
     * 统计类型
     * @return
     */
    @Override
    public Map<String, Long> countType() {
        log.info("统计报警记录的类型");
        List<Alarm> alarmList = alarmMapper.selectAll();
        Map<String,Long> alarmTypeMap = new HashMap<>();
        for (Alarm alarm : alarmList) {
            String typeName = MeasuringPointStatusEnum.getByStatus(alarm.getType()).getDescription();
            alarmTypeMap.put(typeName,alarmTypeMap.getOrDefault(typeName,0L)+1);
        }
        return alarmTypeMap;
    }

    /**
     * 根据月份统计报警记录
     * @return
     */
    @Override
    public Map<Integer, Long> countAlarmTimeByMonth() {
        log.info("统计近5个月报警记录");
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        LinkedHashMap<Integer,Long> monthCountMap = new LinkedHashMap<>();
        for(int i=4;i>=0;i--){
            monthCountMap.put(now.getMonthValue()-i,0L);
        }
        // 查表
        for (Integer month : monthCountMap.keySet()) {
            // 去年 or 今年
            Integer year = month>now.getMonthValue()? now.getYear()-1:now.getYear();
            LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(year, month,  1, 23, 59, 59, 999999999)
                    .with(TemporalAdjusters.lastDayOfMonth());
            Long count = alarmMapper.countByAlarmTime(startTime,endTime);
            monthCountMap.put(month,count);
        }
        return monthCountMap;
    }

    /**
     * 根据月份统计已处理报警记录
     * @return
     */
    public Map<Integer,Long> countProcessedTimeByMonth(){
        log.info("统计近5个月已处理报警记录");
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        LinkedHashMap<Integer,Long> monthCountMap = new LinkedHashMap<>();
        for(int i=4;i>=0;i--){
            monthCountMap.put(now.getMonthValue()-i,0L);
        }
        // 查表
        for (Integer month : monthCountMap.keySet()) {
            // 去年 or 今年
            Integer year = month>now.getMonthValue()? now.getYear()-1:now.getYear();
            LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(year, month,  1, 23, 59, 59, 999999999)
                    .with(TemporalAdjusters.lastDayOfMonth());
            Long count = alarmMapper.countByProcessedTime(startTime,endTime);
            monthCountMap.put(month,count);
        }
        return monthCountMap;
    }
}
