package cn.lqz.unmannedinspectionsystem.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户密码修改DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordDTO {
    /**
     * 用户id
     */
    @NotNull
    private Long userId;
    /**
     * 旧密码
     */
    @NotEmpty
    private String oldPassword;
    /**
     * 新密码
     */
    @NotEmpty
    private String newPassword;
}
