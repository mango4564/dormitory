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
public class StudentSystemTest {
    
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
        
        WebElement usernameInput = driver.findElement(By.cssSelector("input[placeholder='请输入��户名']"));
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
    public void testAddStudent() {
        // 导航到学生管理页面
        WebElement studentMenu = driver.findElement(By.xpath("//span[contains(text(),'学生管理')]"));
        studentMenu.click();
        
        // 点击添加按钮
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.el-button--primary")));
        addButton.click();
        
        // 填写学生信息
        WebElement studentNoInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder='请输入学号']")));
        studentNoInput.sendKeys("2021001");
        
        WebElement nameInput = driver.findElement(By.cssSelector("input[placeholder='请输入姓名']"));
        nameInput.sendKeys("测试学生");
        
        // 选择性别
        WebElement sexSelect = driver.findElement(By.cssSelector("input[placeholder='请选择性别']"));
        sexSelect.click();
        WebElement maleOption = driver.findElement(By.xpath("//span[contains(text(),'男')]"));
        maleOption.click();
        
        // 选择宿舍
        WebElement dormSelect = driver.findElement(By.cssSelector("input[placeholder='请选择宿舍']"));
        dormSelect.click();
        WebElement dormOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(@class,'el-select-dropdown__item')]")));
        dormOption.click();
        
        // 提交表单
        WebElement submitButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        submitButton.click();
        
        // 验证提示信息
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("el-message")));
        assertTrue(message.getText().contains("添加成功"));
    }
    
    @Test
    @Order(2)
    public void testSearchStudent() {
        // 输入搜索条件
        WebElement searchInput = driver.findElement(By.cssSelector("input[placeholder='请输入姓名']"));
        searchInput.clear();
        searchInput.sendKeys("测试学生");
        
        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        searchButton.click();
        
        // 等待搜索结果加载
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".el-table__body")));
        
        // 验证搜索结果
        WebElement tableRow = driver.findElement(By.cssSelector(".el-table__row"));
        assertTrue(tableRow.getText().contains("测试学生"));
        assertTrue(tableRow.getText().contains("2021001"));
    }
    
    @Test
    @Order(3)
    public void testEditStudent() {
        // 点击编辑按钮
        WebElement editButton = driver.findElement(By.cssSelector("button.el-button--warning"));
        editButton.click();
        
        // 修改学生信息
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder='请输入姓名']")));
        nameInput.clear();
        nameInput.sendKeys("测试学生(已修改)");
        
        // 选择新宿舍
        WebElement dormSelect = driver.findElement(By.cssSelector("input[placeholder='请选择宿舍']"));
        dormSelect.click();
        WebElement dormOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[contains(@class,'el-select-dropdown__item')][2]")));
        dormOption.click();
        
        // 提交修改
        WebElement submitButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        submitButton.click();
        
        // 验证提示信息
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("el-message")));
        assertTrue(message.getText().contains("修改成功"));
    }
    
    @Test
    @Order(4)
    public void testDeleteStudent() {
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
        assertTrue(message.getText().contains("���除成功"));
    }
    
    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 