package me.ervin.yang.service;

import me.ervin.yang.entity.User;
import me.ervin.yang.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhiyingyang
 * @version 2018-05-14 10:30
 */
@Service
public class UserService {

    @Resource
    private UserRepository userRepository;


    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
//        return
        userRepository.saveAndFlush(user);
        throw new RuntimeException("test transaction");
    }

    public User saveWithoutTransaction(User user) {
        return save(user);
    }


    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
