package com.kaiyu.unit;

import com.kaiyu.dao.DormDao;
import com.kaiyu.entity.Building;
import com.kaiyu.entity.Dorm;
import com.kaiyu.model.ResponsePage;
import com.kaiyu.service.DormService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 宿舍管理模块测试类
 */
class DormServiceTest {

    @Mock
    private DormDao dormDao;

    @InjectMocks
    private DormService dormService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试用例：获取所有宿舍信息
     * 验证：
     * 1. 正确调用DAO层方法
     * 2. 返回正确的宿舍列表
     */
    @Test
    void getDormAll_Success() {
        // Arrange
        List<Dorm> expectedDorms = Arrays.asList(
            createDorm(1, "101", 1, 4),
            createDorm(2, "102", 1, 4)
        );
        when(dormDao.getDormAll()).thenReturn(expectedDorms);

        // Act
        List<Dorm> result = dormService.getDormAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("101", result.get(0).getName());
        assertEquals("102", result.get(1).getName());
        verify(dormDao).getDormAll();
    }

    /**
     * 测试用例：分页查询宿舍信息
     * 验证：
     * 1. 正确处理分页参数
     * 2. 正确处理查询条件
     * 3. 返回正确的分页结果
     */
    @Test
    void getDormPage_Success() {
        // Arrange
        String dormName = "101";
        List<Dorm> dorms = Arrays.asList(
            createDorm(1, "101", 1, 4),
            createDorm(2, "102", 1, 4)
        );
        when(dormDao.getDormByName(dormName)).thenReturn(dorms);

        // Act
        ResponsePage result = dormService.getDormPage(1, 10, dormName);

        // Assert
        assertNotNull(result);
        assertEquals(2, ((List<Dorm>)result.getData()).size());
        verify(dormDao).getDormByName(dormName);
    }

    /**
     * 测试用例：保存新宿舍信息
     * 验证：
     * 1. 正确调用DAO层保存方法
     * 2. 返回正确的保存结果
     */
    @Test
    void saveDorm_Success() {
        // Arrange
        Dorm dorm = createDorm(null, "103", 1, 4);
        when(dormDao.saveDorm(dorm)).thenReturn(1);

        // Act
        int result = dormService.saveDorm(dorm);

        // Assert
        assertEquals(1, result);
        verify(dormDao).saveDorm(dorm);
    }

    /**
     * 测试用例：编辑宿舍信息
     * 验证：
     * 1. 正确调用DAO层更新方法
     * 2. 返回正确的更新结果
     */
    @Test
    void editDorm_Success() {
        // Arrange
        Dorm dorm = createDorm(1, "101", 1, 6);
        when(dormDao.editDorm(dorm)).thenReturn(1);

        // Act
        int result = dormService.editDorm(dorm);

        // Assert
        assertEquals(1, result);
        verify(dormDao).editDorm(dorm);
    }

    /**
     * 测试用例：批量删除宿舍
     * 验证：
     * 1. 正确处理删除列表
     * 2. 正确调用DAO层删除方法
     * 3. 返回正确的删除结果
     */
    @Test
    void deleteDorms_Success() {
        // Arrange
        List<Dorm> dorms = Arrays.asList(
            createDorm(1, "101", 1, 4),
            createDorm(2, "102", 1, 4)
        );
        List<Integer> ids = Arrays.asList(1, 2);
        when(dormDao.deleteDorms(ids)).thenReturn(2);

        // Act
        int result = dormService.deleteDorms(dorms);

        // Assert
        assertEquals(2, result);
        verify(dormDao).deleteDorms(ids);
    }


    /**
     * 辅助方法：创建测试用Dorm对象
     */
    private Dorm createDorm(Integer id, String name, Integer buildingId, Integer maxNum) {
        Dorm dorm = new Dorm();
        dorm.setId(id);
        dorm.setName(name);
        dorm.setBuildingId(buildingId);
        dorm.setMaxNum(maxNum);
        dorm.setRemark("测试备注");
        
        // 设置关联的Building对象
        Building building = new Building();
        building.setId(buildingId);
        building.setName("测试宿舍楼");
        building.setBuildingType(1);
        dorm.setBuilding(building);
        
        return dorm;
    }
} 