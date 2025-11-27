package cn.lqz.unmannedinspectionsystem.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenDTO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 名称
     */
    private String name;
    /**
     * 电话号码
     */
    private String phoneNumber;
    /**
     * 状态，0禁用，1启用
     */
    private Integer status;
    /**
     * 角色：0用户，1管理员
     */
    private Integer roleType;
    /**
     * token
     */
    private String token;
}
