package me.ervin.yang.service;

import me.ervin.yang.entity.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

/**
 * @author zhiyingyang
 * @version 2018-05-14 10:37
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder
public class UserServiceTest {

    private Random random = new Random();
    private User user;

    @Resource
    private UserService userService;

    @Before
    public void setup() {
        String name = RandomStringUtils.randomAlphabetic(10);
        Integer age = random.nextInt(100);
        user = new User(name, age);
    }

    @Test
    public void save() throws Exception {

        try {
            try {
                userService.save(user);
            } catch (Exception e) {
                //...
            }

            List<User> all = userService.findAll();
            Assert.assertTrue(CollectionUtils.isEmpty(all));
        } finally {
            userService.deleteAll();
        }
    }

    @Test
    public void saveWithoutTransaction() throws Exception {
        try {
            try {
                userService.saveWithoutTransaction(user);
            } catch (Exception e) {
                //...
            }

            List<User> all = userService.findAll();
            Assert.assertTrue(CollectionUtils.isEmpty(all));
        } finally {
            userService.deleteAll();
        }
    }
}