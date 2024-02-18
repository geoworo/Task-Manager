package hexlet.code.dto.status;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskStatusCreateDTO {
    @NotNull
    private String name;

    @NotNull
    private String slug;
}
