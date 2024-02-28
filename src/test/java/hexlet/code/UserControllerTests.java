package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private Faker faker = new Faker();

    private User user;

    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        user = Generator.generateUser();
        token = jwt().jwt(builder -> builder.subject(user.getEmail()));
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(user);
        var request = get("/api/users/" + user.getId()).with(token);
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                r -> r.node("firstName").isEqualTo(user.getFirstName()),
                r -> r.node("email").isEqualTo(user.getEmail())
        );
    }

    @Test
    public void testIndex() throws Exception {
        userRepository.save(user);
        var result = mockMvc.perform(get("/api/users").with(jwt())).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = new UserCreateDTO();
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isCreated());
        var userRes = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(userRes.getFirstName()).isEqualTo(dto.getFirstName());
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(user);
        var dto = new UserUpdateDTO();
        String email = faker.internet().emailAddress();
        dto.setEmail(JsonNullable.of(email));
        var request = put("/api/users/" + user.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isOk());
        var userRes = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userRes.getEmail()).isEqualTo(email);
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(user);
        var request = delete("/api/users/" + user.getId()).with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());
        assertThat(userRepository.existsById(user.getId())).isEqualTo(false);
    }
}
