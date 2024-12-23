package com.kaiyu.unit;

import com.kaiyu.dao.StudentDao;
import com.kaiyu.entity.Dorm;
import com.kaiyu.entity.Student;
import com.kaiyu.model.ResponsePage;
import com.kaiyu.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 学生管理模块测试类
 */
class StudentServiceTest {

    @Mock
    private StudentDao studentDao;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试用例：分页查询学生信息
     * 验证：
     * 1. 正确处理分页参数
     * 2. 正确处理查询条件（学生姓名）
     * 3. 返回正确的分页结果
     */
    @Test
    void getStudentByName_Success() {
        // Arrange
        String studentName = "张三";
        List<Student> students = Arrays.asList(
            createStudent(1, "2021001", "张三", 1, 1),
            createStudent(2, "2021002", "张三丰", 1, 1)
        );
        when(studentDao.getStudentByName(studentName)).thenReturn(students);

        // Act
        ResponsePage result = studentService.getStudentByName(1, 10, studentName);

        // Assert
        assertNotNull(result);
        assertEquals(2, ((List<Student>)result.getData()).size());
        verify(studentDao).getStudentByName(studentName);
    }

    /**
     * 测试用例：查询不存在的学生
     * 验证：
     * 1. 查询条件无匹配结果时的处理
     * 2. 返回空列表而不是null
     */
    @Test
    void getStudentByName_NoResult() {
        // Arrange
        String studentName = "不存在的学生";
        when(studentDao.getStudentByName(studentName)).thenReturn(new ArrayList<>());

        // Act
        ResponsePage result = studentService.getStudentByName(1, 10, studentName);

        // Assert
        assertNotNull(result);
        assertEquals(0, ((List<Student>)result.getData()).size());
        verify(studentDao).getStudentByName(studentName);
    }

    /**
     * 测试用例：保存新学生信息
     * 验证：
     * 1. 正确调用DAO层保存方法
     * 2. 返回正确的保存结果
     */
    @Test
    void saveStudent_Success() {
        // Arrange
        Student student = createStudent(null, "2021003", "李四", 1, 1);
        when(studentDao.saveStudent(student)).thenReturn(1);

        // Act
        int result = studentService.saveStudent(student);

        // Assert
        assertEquals(1, result);
        verify(studentDao).saveStudent(student);
    }

    /**
     * 测试用例：编辑学生信息
     * 验证：
     * 1. 正确调用DAO层更新方法
     * 2. 返回正确的更新结果
     */
    @Test
    void editStudent_Success() {
        // Arrange
        Student student = createStudent(1, "2021001", "张三", 1, 2);
        when(studentDao.editStudent(student)).thenReturn(1);

        // Act
        int result = studentService.editStudent(student);

        // Assert
        assertEquals(1, result);
        verify(studentDao).editStudent(student);
    }

    /**
     * 测试用例：批量删除学生
     * 验证：
     * 1. 正确处理删除列表
     * 2. 正确调用DAO层删除方法
     * 3. 返回正确的删除结果
     */
    @Test
    void deleteStudents_Success() {
        // Arrange
        List<Student> students = Arrays.asList(
            createStudent(1, "2021001", "张三", 1, 1),
            createStudent(2, "2021002", "李四", 1, 1)
        );
        List<Integer> ids = Arrays.asList(1, 2);
        when(studentDao.deleteStudents(ids)).thenReturn(2);

        // Act
        int result = studentService.deleteStudents(students);

        // Assert
        assertEquals(2, result);
        verify(studentDao).deleteStudents(ids);
    }


    /**
     * 辅助方法：创建测试用Student对象
     */
    private Student createStudent(Integer id, String studentNo, String studentName, Integer sex, Integer dormId) {
        Student student = new Student();
        student.setId(id);
        student.setStudentNo(studentNo);
        student.setStudentName(studentName);
        student.setSex(sex);
        student.setDormId(dormId);
        
        // 设置关联的Dorm对象
        Dorm dorm = new Dorm();
        dorm.setId(dormId);
        dorm.setName("101");
        dorm.setMaxNum(4);
        student.setDorm(dorm);
        
        return student;
    }
} 