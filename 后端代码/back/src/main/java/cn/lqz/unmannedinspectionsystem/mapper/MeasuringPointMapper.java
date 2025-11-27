package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.annotations.AutoFill;
import cn.lqz.unmannedinspectionsystem.enums.OperationTypeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.dto.MeasuringPointPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.MeasuringPoint;
import cn.lqz.unmannedinspectionsystem.pojo.vo.MeasuringPointDetailVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.MeasuringPointVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MeasuringPointMapper {

    /**
     * 新增
     * @param measuringPoint
     */
    @AutoFill(OperationTypeEnum.INSERT)
    void insert(MeasuringPoint measuringPoint);

    /**
     * 修改
     * @param measuringPoint
     */
    @AutoFill(OperationTypeEnum.UPDATE)
    void update(MeasuringPoint measuringPoint);

    /**
     * 批量查询
     * @param measuringPointPageQueryDTO
     * @return
     */
    Page<MeasuringPointVO> findByStatus(MeasuringPointPageQueryDTO measuringPointPageQueryDTO);

    /**
     * 获取单个测点的详情
     * @param mpId
     * @return
     */
    MeasuringPointDetailVO findMpDetailByMpId(Long mpId);
}
