package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.pojo.entity.CameraModelBind;
import cn.lqz.unmannedinspectionsystem.mapper.CameraModelBindMapper;
import cn.lqz.unmannedinspectionsystem.service.CameraModelBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CameraModelBindServiceImpl implements CameraModelBindService {
    @Autowired
    private CameraModelBindMapper cameraModelBindMapper;

    @Override
    public int insert(CameraModelBind cameraModelBind) {
        return cameraModelBindMapper.insert(cameraModelBind);
    }

    @Override
    public int update(CameraModelBind cameraModelBind) {
        return cameraModelBindMapper.update(cameraModelBind);
    }

    @Override
    public int deleteById(Long id) {
        return cameraModelBindMapper.deleteById(id);
    }

    @Override
    public CameraModelBind selectById(Long id) {
        return cameraModelBindMapper.selectById(id);
    }

    @Override
    public List<CameraModelBind> selectAll() {
        return cameraModelBindMapper.selectAll();
    }

    @Override
    public CameraModelBind selectByCameraId(String cameraId) {
        return cameraModelBindMapper.selectByCameraId(cameraId);
    }
} 