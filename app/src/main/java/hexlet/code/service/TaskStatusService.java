package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository tsr;

    @Autowired
    private TaskStatusMapper tsm;

    public List<TaskStatusDTO> getAll() {
        var statuses = tsr.findAll();
        return statuses.stream().map(tsm::map).toList();
    }

    public TaskStatusDTO create(TaskStatusCreateDTO data) {
        var status = tsm.map(data);
        tsr.save(status);
        return tsm.map(status);
    }

    public TaskStatusDTO findById(Long id) {
        var status = tsr.findById(id)
                .orElseThrow();
        return tsm.map(status);
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO data, Long id) {
        var status = tsr.findById(id)
                .orElseThrow();
        tsm.update(data, status);
        tsr.save(status);
        return tsm.map(status);
    }

    public void delete(Long id) {
        tsr.deleteById(id);
    }
}
