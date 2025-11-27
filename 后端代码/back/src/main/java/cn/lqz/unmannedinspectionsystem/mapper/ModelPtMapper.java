package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.pojo.entity.ModelPt;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ModelPtMapper {
    void insert(ModelPt modelPt);
    void update(ModelPt modelPt);
    List<ModelPt> selectAll();
    ModelPt selectByFileName(String fileName);
    void deleteByFileName(String fileName);
} 