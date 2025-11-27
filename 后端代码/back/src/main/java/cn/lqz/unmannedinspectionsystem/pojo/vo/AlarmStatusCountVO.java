package cn.lqz.unmannedinspectionsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmStatusCountVO {
    /**
     * 未处理状态数
     */
    private Long unprocessedCount;
    /**
     * 已处理状态数
     */
    private Long processedCount;
}
