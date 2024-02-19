package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository tr;

    @Autowired
    private TaskMapper tm;

    public List<TaskDTO> getAll() {
        return tr.findAll().stream().map(tm::map).toList();
    }

    public TaskDTO findById(Long id) {
        var task = tr.findById(id).orElseThrow();
        return tm.map(task);
    }

    public TaskDTO create(TaskCreateDTO data) {
        var task = tm.map(data);
        tr.save(task);
        return tm.map(task);
    }

    public TaskDTO update(TaskUpdateDTO data, Long id) {
        var task = tr.findById(id).orElseThrow();
        tm.update(data, task);
        tr.save(task);
        return tm.map(task);
    }

    public void delete(Long id) {
        tr.deleteById(id);
    }
}
