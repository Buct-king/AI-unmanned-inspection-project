package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.annotations.AutoFill;
import cn.lqz.unmannedinspectionsystem.enums.OperationTypeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeviceMapper {
    /**
     * 插入
     * @param device
     */
    @AutoFill(OperationTypeEnum.INSERT)
    void insert(Device device);

    /**
     * 修改
     * @param device
     */
    @AutoFill(OperationTypeEnum.UPDATE)
    void update(Device device);

    /**
     * 根据状态统计
     * @param status
     * @return
     */
    @Select("select count(*) from device where run_status=#{status}")
    Integer countByStatus(Integer status);

    /**
     * 统计设备数目
     * @return
     */
    @Select("select count(*) from device")
    Integer count();

    @Select("select * from device")
    List<Device> selectAll();
}
