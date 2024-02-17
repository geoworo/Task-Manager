package hexlet.code.component;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final CustomUserDetailsService userService;

    @Autowired
    private final TaskStatusRepository tsr;

    @Autowired
    private final UserRepository ur;

    @Override
    public void run(ApplicationArguments args) {
        var statuses = createTaskStatuses();
        if (ur.findByEmail("hexlet@example.com").isEmpty()) {
            userService.createUser(createAdmin());
        }
        for (var status: statuses) {
            if (tsr.findBySlug(status.getSlug()).isEmpty()) {
                tsr.save(status);
            }
        }
    }

    private static User createAdmin() {
        User admin = new User();
        admin.setEmail("hexlet@example.com");
        admin.setPasswordDigest("qwerty");
        return admin;
    }

    private static List<TaskStatus> createTaskStatuses() {
        TaskStatus draft = new TaskStatus();
        draft.setName("Draft");
        draft.setSlug("draft");
        TaskStatus toReview = new TaskStatus();
        toReview.setName("To Review");
        toReview.setSlug("to_review");
        TaskStatus toBeFixed = new TaskStatus();
        toBeFixed.setName("To Be Fixed");
        toBeFixed.setSlug("to_be_fixed");
        TaskStatus toPublish = new TaskStatus();
        toPublish.setName("To Publish");
        toPublish.setSlug("to_publish");
        TaskStatus published = new TaskStatus();
        published.setName("Published");
        published.setSlug("published");
        return List.of(draft, toReview, toBeFixed, toPublish, published);
    }
}
