package hexlet.code.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {
    @NotNull
    private String email;

    private String firstName;
    private String lastName;

    @NotNull
    private String password;
}
