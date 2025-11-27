package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.pojo.entity.ModelPt;
import cn.lqz.unmannedinspectionsystem.mapper.ModelPtMapper;
import cn.lqz.unmannedinspectionsystem.service.ModelPtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ModelPtServiceImpl implements ModelPtService {
    @Autowired
    private ModelPtMapper modelPtMapper;

    @Override
    public void insert(ModelPt modelPt) {
        modelPtMapper.insert(modelPt);
    }

    @Override
    public void update(ModelPt modelPt) {
        modelPtMapper.update(modelPt);
    }

    @Override
    public List<ModelPt> listAll() {
        return modelPtMapper.selectAll();
    }

    @Override
    public ModelPt getByFileName(String fileName) {
        return modelPtMapper.selectByFileName(fileName);
    }

    @Override
    public void syncWithModelDir(String modelDirPath) {
        File modelDir = new File(modelDirPath);
        File[] files = modelDir.listFiles((dir, name) -> name.endsWith(".pt"));
        Set<String> filePtSet = new HashSet<>();
        if (files != null) {
            for (File f : files) filePtSet.add(f.getName());
        }
        List<ModelPt> dbList = modelPtMapper.selectAll();
        Set<String> dbPtSet = dbList.stream().map(ModelPt::getFileName).collect(Collectors.toSet());
        // 数据库有但文件夹没有的，删除
        for (ModelPt pt : dbList) {
            if (!filePtSet.contains(pt.getFileName())) {
                modelPtMapper.deleteByFileName(pt.getFileName());
            }
        }
        // 文件夹有但数据库没有的，插入
        for (String file : filePtSet) {
            if (!dbPtSet.contains(file)) {
                ModelPt pt = new ModelPt();
                pt.setFileName(file);
                pt.setTitle("未知模型");
                pt.setDescription("暂无描述");
                pt.setStatus(1);
                modelPtMapper.insert(pt);
            }
        }
    }
} 