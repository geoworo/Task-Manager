package hexlet.code.app.dto;

import org.openapitools.jackson.nullable.JsonNullable;

public class UserUpdateDTO {
    private JsonNullable<String> email;
    private JsonNullable<String> password;
}
