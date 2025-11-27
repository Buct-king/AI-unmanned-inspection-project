package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.pojo.entity.CameraModelBind;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CameraModelBindMapper {
    int insert(CameraModelBind cameraModelBind);
    int update(CameraModelBind cameraModelBind);
    int deleteById(Long id);
    CameraModelBind selectById(Long id);
    List<CameraModelBind> selectAll();
    CameraModelBind selectByCameraId(@Param("cameraId") String cameraId);
} 