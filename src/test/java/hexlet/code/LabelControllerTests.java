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
    private MockMvc mm;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository lr;

    @Autowired
    private UserRepository ur;

    @Autowired
    private LabelMapper lm;

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
        lr.deleteAll();
        ur.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        lr.save(label);
        var result = mm.perform(get("/api/labels").with(token)).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        lr.save(label);
        var request = mm.perform(get("/api/labels/" + label.getId()).with(token))
                .andExpect(status().isOk()).andReturn();
        var body = request.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(label.getName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = lm.map(label);
        var request = mm.perform(post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated());
        var resultLabel = lr.findByName(label.getName()).get();
        assertNotNull(resultLabel);
        assertThat(resultLabel.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        lr.save(label);
        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("name"));
        var request = put("/api/labels/" + label.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mm.perform(request).andExpect(status().isOk());
        var resultLabel = lr.findById(label.getId()).get();
        assertThat(resultLabel.getName()).isEqualTo(dto.getName().get());
    }

    @Test
    public void testDelete() throws Exception {
        lr.save(label);
        mm.perform(delete("/api/labels/" + label.getId()).with(token)).andExpect(status().isNoContent());
        assertThat(lr.existsById(label.getId())).isEqualTo(false);
    }
}
