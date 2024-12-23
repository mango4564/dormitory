package com.kaiyu.unit;

import com.kaiyu.dao.UserDao;
import com.kaiyu.dao.UserRoleDao;
import com.kaiyu.entity.Role;
import com.kaiyu.entity.User;
import com.kaiyu.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 */
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserRoleDao userRoleDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试用例：成功加载用户信息
     * 验证：
     * 1. 能够正确加载用户基本信息
     * 2. 能够正确加载用户角色信息
     * 3. 返回的用户信息完整且正确
     */
    @Test
    void loadUserByUsername_Success() {
        // Arrange
        String username = "testUser";
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(username);
        List<Role> roles = new ArrayList<>();
        Role adminRole = new Role();
        adminRole.setId(1);
        adminRole.setName("ROLE_ADMIN");
        roles.add(adminRole);

        when(userDao.findByUsername(username)).thenReturn(mockUser);
        when(userDao.getUserRolesById(1)).thenReturn(roles);

        // Act
        UserDetails result = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userDao).findByUsername(username);
        verify(userDao).getUserRolesById(1);
    }

    /**
     * 测试用例：加载不存在的用户
     * 验证：
     * 1. 当用户不存在时抛出UsernameNotFoundException异常
     */
    @Test
    void loadUserByUsername_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        when(userDao.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> 
            userService.loadUserByUsername(username)
        );
    }

    /**
     * 测试用例：成功注册新用户
     * 验证：
     * 1. 用户名不存在时可以注册
     * 2. 密码正确加密
     * 3. 用户信息成功保存到数据库
     */
    @Test
    void userReg_Success() {
        // Arrange
        String username = "newUser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        
        when(userDao.findByUsername(username)).thenReturn(null);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userDao.saveUser(username, encodedPassword)).thenReturn(1);

        // Act
        int result = userService.userReg(username, password);

        // Assert
        assertEquals(1, result);
        verify(passwordEncoder).encode(password);
        verify(userDao).saveUser(username, encodedPassword);
    }

    /**
     * 测试用例：注册已存在的用户名
     * 验证：
     * 1. 用户名已存在时返回错误
     * 2. 不进行密码加密
     * 3. 不执行保存操作
     */
    @Test
    void userReg_UserExists() {
        // Arrange
        String username = "existingUser";
        String password = "password";
        
        when(userDao.findByUsername(username)).thenReturn(new User());

        // Act
        int result = userService.userReg(username, password);

        // Assert
        assertEquals(-1, result);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userDao, never()).saveUser(anyString(), anyString());
    }

    /**
     * 测试用例：成功更新用户密码
     * 验证：
     * 1. 旧密码验证正确
     * 2. 新密码正确加密
     * 3. 密码更新成功
     */
    @Test
    void updateUserPassword_Success() {
        // Arrange
        String oldPassword = "oldPass";
        String newPassword = "newPass";
        Integer userId = 1;
        User user = new User();
        user.setPassword("encodedOldPass");

        when(userDao.findUserById(userId)).thenReturn(user);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPass");
        when(userDao.updatePassword(userId, "encodedNewPass")).thenReturn(1);

        // Act
        boolean result = userService.updateUserPassword(oldPassword, newPassword, userId);

        // Assert
        assertTrue(result);
        verify(userDao).updatePassword(userId, "encodedNewPass");
    }

    /**
     * 测试用例：更新密码时旧密码错误
     * 验证：
     * 1. 旧密码验证失败时返回错误
     * 2. 不执行密码更新操作
     */
    @Test
    void updateUserPassword_WrongOldPassword() {
        // Arrange
        String oldPassword = "wrongPass";
        String newPassword = "newPass";
        Integer userId = 1;
        User user = new User();
        user.setPassword("encodedOldPass");

        when(userDao.findUserById(userId)).thenReturn(user);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

        // Act
        boolean result = userService.updateUserPassword(oldPassword, newPassword, userId);

        // Assert
        assertFalse(result);
        verify(userDao, never()).updatePassword(any(), any());
    }

    /**
     * 测试用例：成功更新用户角色
     * 验证：
     * 1. 删除用户原有角色
     * 2. 成功添加新角色
     * 3. 返回成功结果
     */
    @Test
    void updateUserRole_Success() {
        // Arrange
        Integer userId = 1;
        Integer[] roleIds = {1, 2};
        
        when(userRoleDao.addRole(userId, roleIds)).thenReturn(2);

        // Act
        boolean result = userService.updateUserRole(userId, roleIds);

        // Assert
        assertTrue(result);
        verify(userRoleDao).deleteByUserId(userId);
        verify(userRoleDao).addRole(userId, roleIds);
    }

    /**
     * 测试用例：更新用户角色失败
     * 验证：
     * 1. 删除原有角色成功
     * 2. 新角色添加不完整
     * 3. 返回失败结果
     */
    @Test
    void updateUserRole_Failure() {
        // Arrange
        Integer userId = 1;
        Integer[] roleIds = {1, 2};
        
        when(userRoleDao.addRole(userId, roleIds)).thenReturn(1); // 只有一个角色添加成功

        // Act
        boolean result = userService.updateUserRole(userId, roleIds);

        // Assert
        assertFalse(result);
        verify(userRoleDao).deleteByUserId(userId);
        verify(userRoleDao).addRole(userId, roleIds);
    }
} 