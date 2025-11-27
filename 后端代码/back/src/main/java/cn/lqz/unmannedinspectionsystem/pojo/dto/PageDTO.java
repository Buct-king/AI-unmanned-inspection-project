package cn.lqz.unmannedinspectionsystem.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private Integer pageNo;

    private Integer pageSize;
}
