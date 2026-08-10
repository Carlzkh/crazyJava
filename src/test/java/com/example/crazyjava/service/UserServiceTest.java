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
}