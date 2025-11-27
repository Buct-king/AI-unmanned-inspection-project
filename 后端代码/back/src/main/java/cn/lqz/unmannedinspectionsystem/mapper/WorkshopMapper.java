package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.annotations.AutoFill;
import cn.lqz.unmannedinspectionsystem.enums.OperationTypeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Device;
import cn.lqz.unmannedinspectionsystem.pojo.entity.Workshop;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkshopMapper {

    /**
     * 新增
     * @param workshop
     */
    @AutoFill(OperationTypeEnum.INSERT)
    void insert(Workshop workshop);

    /**
     * 修改
     * @param workshop
     */
    @AutoFill(OperationTypeEnum.UPDATE)
    void update(Workshop workshop);

    @Select("select * from workshop")
    List<Workshop> selectAll();
}
