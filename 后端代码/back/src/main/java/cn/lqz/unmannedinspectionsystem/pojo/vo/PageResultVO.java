package cn.lqz.unmannedinspectionsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页请求结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResultVO {
    /**
     * 当前页号
     */
    private Integer pageNo;
    /**
     * 一页记录数
     */
    private Integer pageSize;
    /**
     * 当前页记录数
     */
    private Long pageTotal;
    /**
     * 结果数据
     */
    private List list;
}
