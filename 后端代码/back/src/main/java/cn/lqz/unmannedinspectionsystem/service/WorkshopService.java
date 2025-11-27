package cn.lqz.unmannedinspectionsystem.service;

import java.util.Map;

public interface WorkshopService {
    /**
     * 统计每个车间设备数
     * @return
     */
    Map<String,Integer> countDevice();
}
