package cn.lqz.unmannedinspectionsystem.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private String password;
}
