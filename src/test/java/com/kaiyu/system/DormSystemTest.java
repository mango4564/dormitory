package com.kaiyu.system;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DormSystemTest {
    
    private static WebDriver driver;
    private static WebDriverWait wait;
    
    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.edge.driver", "src/test/java/resources/msedgedriver.exe");
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        
        // 执行登录
        loginAsAdmin();
    }
    
    private static void loginAsAdmin() {
        driver.get("http://localhost:8080/login");
        
        WebElement usernameInput = driver.findElement(By.cssSelector("input[placeholder='请输入用��名']"));
        usernameInput.sendKeys("admin");
        
        WebElement passwordInput = driver.findElement(By.cssSelector("input[placeholder='请输入密码']"));
        passwordInput.sendKeys("123456");
        
        WebElement codeInput = driver.findElement(By.cssSelector("input[placeholder='点击图片更换验证码']"));
        codeInput.sendKeys("1234");
        
        WebElement loginButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        loginButton.click();
        
        wait.until(ExpectedConditions.urlContains("home"));
    }
    
    @Test
    @Order(1)
    public void testAddDorm() {
        // 导航到宿舍管理页面
        WebElement dormMenu = driver.findElement(By.xpath("//span[contains(text(),'宿舍管理')]"));
        dormMenu.click();
        
        // 点击添加按钮
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.el-button--primary")));
        addButton.click();
        
        // 填写宿舍信息
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder='请输入宿舍号']")));
        nameInput.sendKeys("A101");
        
        // 选择所属宿舍楼
        WebElement buildingSelect = driver.findElement(By.cssSelector("input[placeholder='请选择所属宿舍楼']"));
        buildingSelect.click();
        WebElement buildingOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(@class,'el-select-dropdown__item')]")));
        buildingOption.click();
        
        // 设置最大容纳人数
        WebElement maxNumInput = driver.findElement(By.cssSelector("input[placeholder='请输入最大容纳人数']"));
        maxNumInput.sendKeys("4");
        
        WebElement remarkInput = driver.findElement(By.cssSelector("input[placeholder='请输入备注']"));
        remarkInput.sendKeys("系统测试用例");
        
        // 提交表单
        WebElement submitButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        submitButton.click();
        
        // 验证提示信息
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("el-message")));
        assertTrue(message.getText().contains("添加成功"));
    }
    
    @Test
    @Order(2)
    public void testSearchDorm() {
        // 输入搜索条件
        WebElement searchInput = driver.findElement(By.cssSelector("input[placeholder='请输入宿舍号']"));
        searchInput.clear();
        searchInput.sendKeys("A101");
        
        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        searchButton.click();
        
        // 等待搜索结果加载
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".el-table__body")));
        
        // 验证搜索结果
        WebElement tableRow = driver.findElement(By.cssSelector(".el-table__row"));
        assertTrue(tableRow.getText().contains("A101"));
    }
    
    @Test
    @Order(3)
    public void testEditDorm() {
        // 点击编辑按钮
        WebElement editButton = driver.findElement(By.cssSelector("button.el-button--warning"));
        editButton.click();
        
        // 修改宿舍信息
        WebElement maxNumInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder='请输入最大容纳人数']")));
        maxNumInput.clear();
        maxNumInput.sendKeys("6");
        
        WebElement remarkInput = driver.findElement(By.cssSelector("input[placeholder='请输入备注']"));
        remarkInput.clear();
        remarkInput.sendKeys("系统测试用例-已修改");
        
        // 提交修改
        WebElement submitButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        submitButton.click();
        
        // 验证提示信息
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("el-message")));
        assertTrue(message.getText().contains("修改成功"));
    }
    
    @Test
    @Order(4)
    public void testDeleteDorm() {
        // 选择要删除的记录
        WebElement checkbox = driver.findElement(By.cssSelector(".el-table__row .el-checkbox__inner"));
        checkbox.click();
        
        // 点击删除按钮
        WebElement deleteButton = driver.findElement(By.cssSelector("button.el-button--danger"));
        deleteButton.click();
        
        // 确认删除
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".el-message-box__btns .el-button--primary")));
        confirmButton.click();
        
        // 验证提示信息
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("el-message")));
        assertTrue(message.getText().contains("删除成功"));
    }
    
    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 