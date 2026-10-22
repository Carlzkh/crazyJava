package com.example.crazyjava.service;

import com.example.crazyjava.entity.User;
import com.example.crazyjava.enums.ResultCodeEnum;
import com.example.crazyjava.exception.BusinessException;
import com.example.crazyjava.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;  // 模拟 Repository，不连真实数据库

    @InjectMocks
    private UserService userService;        // 把模拟的 Repository 注入到 Service 中

    @Test
    void getUserById_shouldReturnUser_whenIdExists() {
        // 1. 准备测试数据（Arrange / Given）
        long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("Alice");

        // 当调用 userRepository.findById(1L) 时，返回上面准备好的 mockUser
        when(userRepository.findById(userId)).thenReturn(mockUser);

        // 2. 执行被测试的方法（Act / When）
        User result = userService.getUserById(userId);

        // 3. 验证结果（Assert / Then）
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Alice", result.getName());

        // 验证 repository.findById 确实被调用了一次
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_shouldThrowBusinessException_whenUserNotFound() {
        // 1. 准备测试数据：模拟 Repository 返回 null
        long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(null);

        // 2. 执行并验证异常（Act + Assert）
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserById(userId);
        });

        // 3. 验证异常信息是否正确
        assertEquals(ResultCodeEnum.USER_NOT_FOUND.getCode(), exception.getCode());
        assertEquals(ResultCodeEnum.USER_NOT_FOUND.getMsg(), exception.getMsg());
    }
    @Test
    void createUser_shouldThrow_whenAgeUnder18() {
        User user = new User();
        user.setName("小明");
        user.setAge(16);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.createUser(user));

        assertEquals(ResultCodeEnum.USER_AGE_TOO_YOUNG.getCode(), exception.getCode());
        verify(userRepository, never()).save(any());
    }
    @Test
    void createUser_shouldSave_whenAgeIsAdult() {
        User user = new User();
        user.setName("张三");
        user.setAge(20);

        User result = userService.createUser(user);

        assertSame(user, result);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void deleteUserById_shouldDelete_whenUserExists() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById_(userId)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(user);
        when(userRepository.deleteById(userId)).thenReturn(1);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
    @Test
    void setUserById_shouldUpdate_whenUserExists() {
        long userId = 1L;
        User existing = new User();
        existing.setId(userId);
        existing.setName("旧名字");
        existing.setAge(20);

        User incoming = new User();
        incoming.setName("新名字");
        incoming.setAge(30);

        when(userRepository.findById(userId)).thenReturn(existing);
        when(userRepository.setById(existing)).thenReturn(1);

        User result = userService.setUserById(userId, incoming);

        assertEquals("新名字", result.getName());
        assertEquals(30, result.getAge());
        verify(userRepository, times(1)).setById(existing);
    }
}