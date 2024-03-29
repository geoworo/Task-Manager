package hexlet.code.specification;

import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamsDTO params) {
        return withAssigneeId(params.getAssigneeId())
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()))
                .and(withTitleCont(params.getTitleCont()));
    }

    public Specification<Task> withAssigneeId(Long id) {
        return (root, query, cb) -> id == null
                ? cb.conjunction()
                : cb.equal(root.get("assignee").get("id"), id);
    }

    public Specification<Task> withLabelId(Long id) {
        return (root, query, cb) -> id == null
                ? cb.conjunction()
                : cb.equal(root.get("labels").get("id"), id);
    }

    public Specification<Task> withStatus(String status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("taskStatus").get("slug"), status);
    }

    public Specification<Task> withTitleCont(String content) {
        return (root, query, cb) -> content == null
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + content.toLowerCase() + "%");
    }
}
