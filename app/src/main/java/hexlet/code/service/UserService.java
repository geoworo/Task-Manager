package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hexlet.code.repository.UserRepository;
import hexlet.code.mapper.UserMapper;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository ur;

    @Autowired
    private UserMapper um;

    public List<UserDTO> getAll() {
        var users = ur.findAll();
        return users.stream().map(um::map).toList();
    }

    public UserDTO findById(Long id) {
        var user = ur.findById(id).orElseThrow();
        return um.map(user);
    }

    public UserDTO create(UserCreateDTO data) {
        var user = um.map(data);
        ur.save(user);
        return um.map(user);
    }

    public UserDTO update(UserUpdateDTO data, Long id) {
        var user = ur.findById(id).orElseThrow();
        um.update(data, user);
        ur.save(user);
        return um.map(user);
    }

    public void delete(Long id) {
        ur.deleteById(id);
    }
}
