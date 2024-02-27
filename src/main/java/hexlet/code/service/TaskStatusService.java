package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        var statuses = taskStatusRepository.findAll();
        return statuses.stream().map(taskStatusMapper::map).toList();
    }

    public TaskStatusDTO create(TaskStatusCreateDTO data) {
        var status = taskStatusMapper.map(data);
        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }

    public TaskStatusDTO findById(Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow();
        return taskStatusMapper.map(status);
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO data, Long id) {
        var status = taskStatusRepository.findById(id)
                .orElseThrow();
        taskStatusMapper.update(data, status);
        taskStatusRepository.save(status);
        return taskStatusMapper.map(status);
    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
