package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.entity.ModelPt;
import java.util.List;

public interface ModelPtService {
    void insert(ModelPt modelPt);
    void update(ModelPt modelPt);
    List<ModelPt> listAll();
    ModelPt getByFileName(String fileName);
    void syncWithModelDir(String modelDirPath);
} 