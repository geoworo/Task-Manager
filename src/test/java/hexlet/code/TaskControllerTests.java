package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static hexlet.code.Generator.generateTask;
import static hexlet.code.Generator.generateStatus;
import static hexlet.code.Generator.generateUser;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private TaskStatus tstatus;
    private User user;
    private Task task;
    private JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        user = generateUser();
        userRepository.save(user);
        token = jwt().jwt(builder -> builder.subject(user.getEmail()));

        tstatus = generateStatus();
        taskStatusRepository.save(tstatus);
        task = generateTask();

        task.setAssignee(user);
        task.setTaskStatus(tstatus);
    }

    @AfterEach
    public void clean() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        taskRepository.save(task);
        var result = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        taskRepository.save(task);
        var result = mockMvc.perform(get("/api/tasks/" + task.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(task.getName()),
                v -> v.node("content").isEqualTo(task.getDescription())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = taskMapper.map(task);
        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isCreated());
        var resultTask = taskRepository.findByName(task.getName()).orElseThrow();
        assertNotNull(resultTask);
        assertThat(resultTask.getName()).isEqualTo(dto.getTitle());
        assertThat(resultTask.getDescription()).isEqualTo(dto.getContent());
    }

    @Test
    public void testUpdate() throws Exception {
        taskRepository.save(task);
        var dto = new TaskUpdateDTO();
        dto.setTitle(JsonNullable.of("name"));
        dto.setContent(JsonNullable.of("description"));
        var request = put("/api/tasks/" + task.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isOk());
        var resultTask = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(resultTask.getName()).isEqualTo(dto.getTitle().get());
        assertThat(resultTask.getDescription()).isEqualTo(dto.getContent().get());
    }

    @Test
    public void testDelete() throws Exception {
        taskRepository.save(task);
        mockMvc.perform(delete("/api/tasks/" + task.getId()).with(token))
                .andExpect(status().isNoContent());
        assertThat(taskRepository.existsById(task.getId())).isEqualTo(false);
    }
}
