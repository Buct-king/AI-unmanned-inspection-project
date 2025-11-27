package cn.lqz.unmannedinspectionsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long userId;
    private String name;
    private String phoneNumber;
    private Integer roleType;
    private Integer status;
}
