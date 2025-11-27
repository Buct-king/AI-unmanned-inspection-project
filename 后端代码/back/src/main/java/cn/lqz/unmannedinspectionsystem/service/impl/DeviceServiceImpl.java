package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.AlarmStatusEnum;
import cn.lqz.unmannedinspectionsystem.enums.DeviceRunStatusEnum;
import cn.lqz.unmannedinspectionsystem.enums.DeviceTypeEnum;
import cn.lqz.unmannedinspectionsystem.mapper.AlarmMapper;
import cn.lqz.unmannedinspectionsystem.mapper.DeviceMapper;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Device;
import cn.lqz.unmannedinspectionsystem.pojo.vo.DeviceStatusCountVO;
import cn.lqz.unmannedinspectionsystem.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {
    private final DeviceMapper deviceMapper;
    private final AlarmMapper alarmMapper;
    /**
     * 统计设备状态数
     * @return
     */
    @Override
    public DeviceStatusCountVO countStatus() {
        log.info("统计设备状态数");
        // 总数
        Integer totalCount = deviceMapper.count();
        // 运行中设备数
        Integer runningCount = deviceMapper.countByStatus(DeviceRunStatusEnum.RUNNING.getStatus());
        // 报警设备数
        Integer alarmCount = alarmMapper.countDeviceByStatus(AlarmStatusEnum.UNPROCESSED.getStatus());
        return new DeviceStatusCountVO(runningCount,alarmCount,runningCount/totalCount,alarmCount/totalCount);
    }

    /**
     * 统计每种设备的数目
     * @return
     */
    @Override
    public Map<String, Integer> countType() {
        log.info("统计每种设备的数目");
        List<Device> deviceList = deviceMapper.selectAll();
        Map<String,Integer> typeCountMap = new HashMap<>();
        for (Device device : deviceList) {
            String deviceType = DeviceTypeEnum.getByType(device.getDeviceType()).getDescription();
            typeCountMap.put(deviceType,typeCountMap.getOrDefault(deviceType,0)+1);
        }
        return typeCountMap;
    }
}
