package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.vo.DeviceStatusCountVO;

import java.util.Map;

public interface DeviceService {
    /**
     * 统计设备数
     * @return
     */
    DeviceStatusCountVO countStatus();

    /**
     * 统计每种设备的数目
     * @return
     */
    Map<String,Integer> countType();
}
