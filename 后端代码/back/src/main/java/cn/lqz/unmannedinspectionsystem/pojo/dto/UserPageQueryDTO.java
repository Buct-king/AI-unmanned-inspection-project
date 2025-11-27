package cn.lqz.unmannedinspectionsystem.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPageQueryDTO extends PageDTO{
    private Long userId;
    private String name;
    private String phoneNumber;
    private Integer roleType;
    private Integer status;
}
