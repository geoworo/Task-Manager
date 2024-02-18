package hexlet.code.dto.status;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter

public class TaskStatusUpdateDTO {
    private JsonNullable<String> name;
}
