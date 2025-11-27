package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.entity.CameraModelBind;
import java.util.List;

public interface CameraModelBindService {
    int insert(CameraModelBind cameraModelBind);
    int update(CameraModelBind cameraModelBind);
    int deleteById(Long id);
    CameraModelBind selectById(Long id);
    List<CameraModelBind> selectAll();
    CameraModelBind selectByCameraId(String cameraId);
} 