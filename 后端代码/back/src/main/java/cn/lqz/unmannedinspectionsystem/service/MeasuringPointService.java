package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.dto.MeasuringPointPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.MeasuringPointDetailVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;

public interface MeasuringPointService {
    /**
     * 分页查询测点记录
     * @param measuringPointPageQueryDTO
     * @return
     */
    PageResultVO loadMeasuringPointList(MeasuringPointPageQueryDTO measuringPointPageQueryDTO);

    /**
     * 获取测点图片
     * @param mpId
     */
    String loadImage(Long mpId);

    /**
     * 获取测点详情
     * @param mpId
     * @return
     */
    MeasuringPointDetailVO getMeasuringPointDetail(Long mpId);
}
