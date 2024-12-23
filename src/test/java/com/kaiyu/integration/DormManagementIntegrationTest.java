package com.kaiyu.integration;

import com.kaiyu.entity.Building;
import com.kaiyu.entity.Dorm;
import com.kaiyu.entity.Student;
import com.kaiyu.model.ResponsePage;
import com.kaiyu.service.BuildingService;
import com.kaiyu.service.DormService;
import com.kaiyu.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 宿舍管理系统集成测试类
 * 测试多个模块之间的交互
 */
@SpringBootTest
@ActiveProfiles("test")
class DormManagementIntegrationTest {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private DormService dormService;

    @Autowired
    private StudentService studentService;

    /**
     * 集成测试：完整的宿舍分配流程
     * 测试场景：
     * 1. 创建新宿舍楼
     * 2. 在新宿舍楼中创建宿舍
     * 3. 分配学生到新宿舍
     * 4. 查询验证分配结果
     */
    @Test
    @Transactional
    void testCompleteDormAllocationProcess() {
        // 1. 创建宿舍楼
        Building building = createBuilding("男生宿舍A栋", 1);
        int buildingResult = buildingService.saveBuilding(building);
        assertTrue(buildingResult > 0, "宿舍楼创建失败");

        // 获取所有宿舍楼，验证新建的宿舍楼
        List<Building> buildings = buildingService.getBuildingAll();
        assertFalse(buildings.isEmpty(), "宿舍楼列表不应为空");
        Building savedBuilding = buildings.stream()
                .filter(b -> b.getName().equals("男生宿舍A栋"))
                .findFirst()
                .orElse(null);
        assertNotNull(savedBuilding, "未找到新创建的宿舍楼");

        // 2. 创建宿舍
        Dorm dorm = createDorm("101", savedBuilding.getId(), 4);
        int dormResult = dormService.saveDorm(dorm);
        assertTrue(dormResult > 0, "宿舍创建失败");

        // 查询验证新建的宿舍
        ResponsePage dormPage = dormService.getDormPage(1, 10, "101");
        assertNotNull(dormPage.getData(), "宿舍查询结果不应为空");
        List<Dorm> dorms = (List<Dorm>) dormPage.getData();
        assertFalse(dorms.isEmpty(), "宿���列表不应为空");
        Dorm savedDorm = dorms.get(0);
        assertEquals("101", savedDorm.getName(), "宿舍号不匹配");

        // 3. 创建并分配学生到宿舍
        Student student = createStudent("2021001", "张三", 1, savedDorm.getId());
        int studentResult = studentService.saveStudent(student);
        assertTrue(studentResult > 0, "学生创建失败");

        // 4. 查询验证学生分配结果
        ResponsePage studentPage = studentService.getStudentByName(1, 10, "张三");
        assertNotNull(studentPage.getData(), "学生查询结果不应为空");
        List<Student> students = (List<Student>) studentPage.getData();
        assertFalse(students.isEmpty(), "学生列表不应为空");
        Student savedStudent = students.get(0);
        assertEquals(savedDorm.getId(), savedStudent.getDormId(), "学生宿舍分配不匹配");
        assertEquals("张三", savedStudent.getStudentName(), "学生姓名不匹配");
    }

    /**
     * 集成测试：宿舍调整流程
     * 测试场景：
     * 1. 查询现有宿舍和学生
     * 2. 调整学生宿舍分配
     * 3. 验证调整结果
     */
    @Test
    @Transactional
    void testDormReallocationProcess() {
        // 1. 创建初始数据
        // 创建两个宿舍楼
        Building building1 = createBuilding("男生宿舍B栋", 1);
        Building building2 = createBuilding("男生宿舍C栋", 1);
        buildingService.saveBuilding(building1);
        buildingService.saveBuilding(building2);

        // 在两个宿舍楼各创建一个宿舍
        Dorm dorm1 = createDorm("201", building1.getId(), 4);
        Dorm dorm2 = createDorm("202", building2.getId(), 4);
        dormService.saveDorm(dorm1);
        dormService.saveDorm(dorm2);

        // 创建学生并分配到第一个宿舍
        Student student = createStudent("2021002", "李四", 1, dorm1.getId());
        studentService.saveStudent(student);

        // 2. 调整学生宿舍分配
        student.setDormId(dorm2.getId());
        int updateResult = studentService.editStudent(student);
        assertTrue(updateResult > 0, "学生宿舍调整失败");

        // 3. 验证调整结果
        ResponsePage studentPage = studentService.getStudentByName(1, 10, "李四");
        List<Student> students = (List<Student>) studentPage.getData();
        assertFalse(students.isEmpty(), "未找到学生信息");
        Student updatedStudent = students.get(0);
        assertEquals(dorm2.getId(), updatedStudent.getDormId(), "学生宿舍调整结果不匹配");
    }

    /**
     * 集成测试：宿舍楼容量管理
     * ��试场景：
     * 1. 创建限定容量的宿舍
     * 2. 分配学生直到达到容量限制
     * 3. 验证容量限制
     */
    @Test
    @Transactional
    void testDormCapacityManagement() {
        // 1. 创建宿舍楼和宿舍
        Building building = createBuilding("男生宿舍D栋", 1);
        buildingService.saveBuilding(building);

        // 创建一个2人宿舍
        Dorm dorm = createDorm("301", building.getId(), 2);
        dormService.saveDorm(dorm);

        // 2. 分配学生
        Student student1 = createStudent("2021003", "王五", 1, dorm.getId());
        Student student2 = createStudent("2021004", "赵六", 1, dorm.getId());
        
        int result1 = studentService.saveStudent(student1);
        int result2 = studentService.saveStudent(student2);
        
        assertTrue(result1 > 0, "第一个学生分配失败");
        assertTrue(result2 > 0, "第二个学生分配失败");

        // 3. 验证宿舍学生数量
        ResponsePage studentPage = studentService.getStudentByName(1, 10, "");
        List<Student> students = (List<Student>) studentPage.getData();
        long dormStudentCount = students.stream()
                .filter(s -> s.getDormId().equals(dorm.getId()))
                .count();
        assertEquals(2, dormStudentCount, "宿舍��生数量不匹配");
    }

    // 辅助方法：创建测试用Building对象
    private Building createBuilding(String name, Integer buildingType) {
        Building building = new Building();
        building.setName(name);
        building.setBuildingType(buildingType);
        building.setRemark("测试用宿舍楼");
        return building;
    }

    // 辅助方法：创建测试用Dorm对象
    private Dorm createDorm(String name, Integer buildingId, Integer maxNum) {
        Dorm dorm = new Dorm();
        dorm.setName(name);
        dorm.setBuildingId(buildingId);
        dorm.setMaxNum(maxNum);
        dorm.setRemark("测试用宿舍");
        return dorm;
    }

    // 辅助方法：创建测试用Student对象
    private Student createStudent(String studentNo, String studentName, Integer sex, Integer dormId) {
        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setStudentName(studentName);
        student.setSex(sex);
        student.setDormId(dormId);
        return student;
    }
} 