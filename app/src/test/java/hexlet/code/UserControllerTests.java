package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.instancio.Instancio;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mm;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper um;

    @Autowired
    private UserRepository ur;

    @Autowired
    private Faker faker;

    private User user;

    @BeforeEach
    public void setUp() {
        user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getPassword), () -> faker.internet().password())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .create();
    }

    @Test
    public void testShow() throws Exception {
        ur.save(user);
        var request = get("/api/users/" + user.getId());
        var result = mm.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                r -> r.node("firstName").isEqualTo(user.getFirstName()),
                r -> r.node("email").isEqualTo(user.getEmail())
        );
    }

    @Test
    public void testIndex() throws Exception {
        ur.save(user);
        var result = mm.perform(get("/api/users")).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = um.map(user);
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mm.perform(request).andExpect(status().isCreated());
        var userRes = ur.findById(user.getId()).get();
        assertThat(userRes.getFirstName()).isEqualTo(dto.getFirstName());
    }

    @Test
    public void testUpdate() throws Exception {
        ur.save(user);
        var dto = new UserUpdateDTO();
        String email = faker.internet().emailAddress();
        dto.setEmail(JsonNullable.of(email));
        var request = put("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mm.perform(request).andExpect(status().isOk());
        var userRes = ur.findById(user.getId()).get();
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    public void testDelete() throws Exception {
        ur.save(user);
        var request = delete("/api/users/" + user.getId());
        mm.perform(request).andExpect(status().isNoContent());
        assertThat(ur.existsById(user.getId())).isEqualTo(false);
    }
}
