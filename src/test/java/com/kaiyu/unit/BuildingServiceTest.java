package com.kaiyu.unit;

import com.kaiyu.dao.BuildingDao;
import com.kaiyu.entity.Building;
import com.kaiyu.model.ResponsePage;
import com.kaiyu.service.BuildingService;
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
 * 宿舍楼管理模块测试类
 */
class BuildingServiceTest {

    @Mock
    private BuildingDao buildingDao;

    @InjectMocks
    private BuildingService buildingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试用例：获取所有宿舍楼信息
     * 验证：
     * 1. 正确调用DAO层方法
     * 2. 返回正确的宿舍楼列表
     */
    @Test
    void getBuildingAll_Success() {
        // Arrange
        List<Building> expectedBuildings = Arrays.asList(
            createBuilding(1, "男生宿舍1号楼", 1),
            createBuilding(2, "女生宿舍1号楼", 2)
        );
        when(buildingDao.getBuildingAll()).thenReturn(expectedBuildings);

        // Act
        List<Building> result = buildingService.getBuildingAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("男生宿舍1号楼", result.get(0).getName());
        assertEquals("女生宿舍1号楼", result.get(1).getName());
        verify(buildingDao).getBuildingAll();
    }

    /**
     * 测试用例：分页查询宿舍楼信息
     * 验证：
     * 1. 正确处理分页参数
     * 2. 正确处理查询条件
     * 3. 返回正确的分页结果
     */
    @Test
    void getBuildingPage_Success() {
        // Arrange
        String buildingName = "1号楼";
        List<Building> buildings = Arrays.asList(
            createBuilding(1, "男生宿舍1号楼", 1),
            createBuilding(2, "女生宿舍1号楼", 2)
        );
        when(buildingDao.getBuildingByName(buildingName)).thenReturn(buildings);

        // Act
        ResponsePage result = buildingService.getBuildingPage(1, 10, buildingName);

        // Assert
        assertNotNull(result);
        assertEquals(2, ((List<Building>)result.getData()).size());
        verify(buildingDao).getBuildingByName(buildingName);
    }

    /**
     * 测试用例：保存新宿舍楼信息
     * 验证：
     * 1. 正确调用DAO层保存方法
     * 2. 返回正确的保存结果
     */
    @Test
    void saveBuilding_Success() {
        // Arrange
        Building building = createBuilding(null, "新宿舍楼", 1);
        when(buildingDao.saveBuilding(building)).thenReturn(1);

        // Act
        int result = buildingService.saveBuilding(building);

        // Assert
        assertEquals(1, result);
        verify(buildingDao).saveBuilding(building);
    }

    /**
     * 测试用例：编辑宿舍楼信息
     * 验证：
     * 1. 正确调用DAO层更新方法
     * 2. 返回正确的更新结果
     */
    @Test
    void editBuilding_Success() {
        // Arrange
        Building building = createBuilding(1, "修改后的宿舍楼", 1);
        when(buildingDao.editBuilding(building)).thenReturn(1);

        // Act
        int result = buildingService.editBuilding(building);

        // Assert
        assertEquals(1, result);
        verify(buildingDao).editBuilding(building);
    }

    /**
     * 测试用例：批量删除宿舍楼
     * 验证：
     * 1. 正确处理删除列表
     * 2. 正确调���DAO层删除方法
     * 3. 返回正确的删除结果
     */
    @Test
    void deleteBuildings_Success() {
        // Arrange
        List<Building> buildings = Arrays.asList(
            createBuilding(1, "宿舍楼1", 1),
            createBuilding(2, "宿舍楼2", 1)
        );
        List<Integer> ids = Arrays.asList(1, 2);
        when(buildingDao.deleteBuildings(ids)).thenReturn(2);

        // Act
        int result = buildingService.deleteBuildings(buildings);

        // Assert
        assertEquals(2, result);
        verify(buildingDao).deleteBuildings(ids);
    }


    /**
     * 辅助方法：创建测试用Building对象
     */
    private Building createBuilding(Integer id, String name, Integer buildingType) {
        Building building = new Building();
        building.setId(id);
        building.setName(name);
        building.setBuildingType(buildingType);
        building.setRemark("测试备注");
        return building;
    }
} 