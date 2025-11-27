package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.mapper.WorkshopMapper;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Workshop;
import cn.lqz.unmannedinspectionsystem.service.WorkshopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkshopServiceImpl implements WorkshopService {
    private final WorkshopMapper workshopMapper;

    @Override
    public Map<String, Integer> countDevice() {
        log.info("统计车间设备数目");
        return workshopMapper.selectAll().stream().collect(Collectors.toMap(Workshop::getName,Workshop::getDeviceCount));
    }
}
