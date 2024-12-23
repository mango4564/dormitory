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
public class LoginSystemTest {
    
    private static WebDriver driver;
    private static WebDriverWait wait;
    
    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.edge.driver", "src/test/java/resources/msedgedriver.exe");
        driver = new EdgeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }
    
    @Test
    @Order(1)
    public void testLoginPageElements() {
        driver.get("http://localhost:8080/#/");
        
        // 验证登录页面标题
        WebElement loginTitle = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("loginTitle")));
        assertEquals("系统登录", loginTitle.getText());
        
        // 验证用户名输入框
        WebElement usernameInput = driver.findElement(By.cssSelector("input[placeholder='请输入用户名']"));
        assertTrue(usernameInput.isDisplayed());
        
        // 验证密码输入框
        WebElement passwordInput = driver.findElement(By.cssSelector("input[placeholder='请输入密码']"));
        assertTrue(passwordInput.isDisplayed());
        
        // 验证验证码输入框
        WebElement codeInput = driver.findElement(By.cssSelector("input[placeholder='点击图片更换验证码']"));
        assertTrue(codeInput.isDisplayed());
        
        // 验证验证码图片
        WebElement verifyCodeImage = driver.findElement(By.cssSelector("img[src*='verifyCode']"));
        assertTrue(verifyCodeImage.isDisplayed());
        
        // 验证登录按钮
        WebElement loginButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        assertEquals("登录", loginButton.getText());
    }
    
    @Test
    @Order(2)
    public void testLoginProcess() {
        // 输入登录信息
        WebElement usernameInput = driver.findElement(By.cssSelector("input[placeholder='请输入用户名']"));
        usernameInput.sendKeys("admin");
        
        WebElement passwordInput = driver.findElement(By.cssSelector("input[placeholder='请输入密码']"));
        passwordInput.sendKeys("123456");
        
        WebElement codeInput = driver.findElement(By.cssSelector("input[placeholder='点击图片更换验证码']"));
        codeInput.sendKeys("1234"); // 注意：实际验证码需要从图片中识别
        
        // 点击登录按钮
        WebElement loginButton = driver.findElement(By.cssSelector("button.el-button--primary"));
        loginButton.click();
        
        // 等待主页加载
        wait.until(ExpectedConditions.urlContains("home"));
    }
    
    @Test
    @Order(3)
    public void testHomePageElements() {
        // 验证系统标题
        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("title")));
        assertTrue(title.getText().contains("宿舍管理系统"));
        
        // 验证用户信息下拉菜单
        WebElement userDropdown = driver.findElement(By.className("el-dropdown-link"));
        assertTrue(userDropdown.isDisplayed());
        
        // 验证导航菜单项
        assertTrue(driver.findElement(By.linkText("学生管理")).isDisplayed());
        assertTrue(driver.findElement(By.linkText("楼层管理")).isDisplayed());
        assertTrue(driver.findElement(By.linkText("宿舍管理")).isDisplayed());
        assertTrue(driver.findElement(By.linkText("用户管理")).isDisplayed());
        assertTrue(driver.findElement(By.linkText("系统管理")).isDisplayed());
    }
    
    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 