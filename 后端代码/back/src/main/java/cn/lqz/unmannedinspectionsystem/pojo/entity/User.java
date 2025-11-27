package cn.lqz.unmannedinspectionsystem.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 名称
     */
    private String name;
    /**
     * 密码
     */
    private String password;
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
}
