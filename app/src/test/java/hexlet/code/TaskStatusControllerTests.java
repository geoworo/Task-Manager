package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTests {
    @Autowired
    private MockMvc mm;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusMapper tsm;

    @Autowired
    private TaskStatusRepository tsr;

    @Autowired
    private Faker faker;

    private TaskStatus ts;

    @BeforeEach
    public void setUp() {
        ts = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(Select.field(TaskStatus::getName), () -> faker.lorem().word())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.lorem().word())
                .create();
    }

    @Test
    public void testIndex() throws Exception {
        tsr.save(ts);
        var result = mm.perform(get("/api/task_statuses"))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        tsr.save(ts);
        var result = mm.perform(get("/api/task_statuses/" + ts.getId()))
                .andExpect(status().isOk())
                .andReturn();
        var body =  result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(ts.getName()),
                v -> v.node("slug").isEqualTo(ts.getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = tsm.map(ts);
        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mm.perform(request)
                .andExpect(status().isCreated());
        var tstatus = tsr.findBySlug(dto.getSlug()).get();
        assertNotNull(tstatus);
        assertThat(tstatus.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        tsr.save(ts);
        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("name"));
        var request = put("/api/task_statuses/" + ts.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mm.perform(request)
                .andExpect(status().isOk());
        var tstatus = tsr.findById(ts.getId()).get();
        assertThat(tstatus.getName()).isEqualTo(dto.getName());
    }

    @Test
    public void testDelete() throws Exception {
        tsr.save(ts);
        mm.perform(delete("/api/task_statuses/" + ts.getId()))
                .andExpect(status().isNoContent());
        assertThat(tsr.existsById(ts.getId())).isEqualTo(false);
    }
}
