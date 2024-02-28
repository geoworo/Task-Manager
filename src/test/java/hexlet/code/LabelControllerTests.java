package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelMapper labelMapper;

    private Label label;
    private JwtRequestPostProcessor token;
    private User user;

    @BeforeEach
    public void setUp() {
        label = Generator.generateLabel();
        user = Generator.generateUser();
        token = jwt().jwt(builder -> builder.subject(user.getEmail()));
    }

    @AfterEach
    public void clean() {
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        labelRepository.save(label);
        var result = mockMvc.perform(get("/api/labels").with(token)).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        labelRepository.save(label);
        var request = mockMvc.perform(get("/api/labels/" + label.getId()).with(token))
                .andExpect(status().isOk()).andReturn();
        var body = request.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(label.getName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = labelMapper.map(label);
        var request = mockMvc.perform(post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
        var resultLabel = labelRepository.findByName(label.getName()).orElseThrow();
        assertNotNull(resultLabel);
        assertThat(resultLabel.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        labelRepository.save(label);
        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("name"));
        var request = put("/api/labels/" + label.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isOk());
        var resultLabel = labelRepository.findById(label.getId()).orElseThrow();
        assertThat(resultLabel.getName()).isEqualTo(dto.getName().get());
    }

    @Test
    public void testDelete() throws Exception {
        labelRepository.save(label);
        mockMvc.perform(delete("/api/labels/" + label.getId()).with(token)).andExpect(status().isNoContent());
        assertThat(labelRepository.existsById(label.getId())).isEqualTo(false);
    }
}
