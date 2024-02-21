package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    @Autowired
    private LabelRepository lr;

    @Autowired
    private LabelMapper lm;

    public List<LabelDTO> getAll() {
        return lr.findAll().stream().map(lm::map).toList();
    }

    public LabelDTO findById(Long id) {
        var label = lr.findById(id).orElseThrow();
        return lm.map(label);
    }

    public LabelDTO create(LabelCreateDTO dto) {
        var label = lm.map(dto);
        lr.save(label);
        return lm.map(label);
    }

    public LabelDTO update(LabelUpdateDTO dto, Long id) {
        var label = lr.findById(id).orElseThrow();
        lm.update(dto, label);
        lr.save(label);
        return lm.map(label);
    }

    public void delete(Long id) {
        lr.deleteById(id);
    }
}
