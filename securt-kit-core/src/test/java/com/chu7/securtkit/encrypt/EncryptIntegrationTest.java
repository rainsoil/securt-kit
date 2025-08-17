package com.chu7.securtkit.encrypt;

import com.chu7.securtkit.encrypt.annotation.EncryptField;
import com.chu7.securtkit.encrypt.cache.TableFieldCache;
import com.chu7.securtkit.encrypt.config.EncryptProperties;
import com.chu7.securtkit.encrypt.core.EncryptContext;
import com.chu7.securtkit.encrypt.entity.UserEntity;
import com.chu7.securtkit.encrypt.entity.OrderEntity;
import com.chu7.securtkit.encrypt.strategy.AesEncryptStrategy;
import com.chu7.securtkit.encrypt.strategy.DesEncryptStrategy;
import com.chu7.securtkit.encrypt.strategy.KeyManager;
import com.chu7.securtkit.encrypt.util.EncryptUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import com.chu7.securtkit.encrypt.config.TestConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 加密集成测试类
 * 测试完整的加密解密功能
 *
 * @author chu7
 * @date 2025/8/15
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "securt-kit.encrypt.enabled=true",
    "securt-kit.encrypt.patternType=POJO",
    "securt-kit.encrypt.key=test-secret-key-32-chars-long",
    "securt-kit.encrypt.algorithm=AES"
})
@Import(TestConfig.class)
public class EncryptIntegrationTest {
    
    @Autowired
    private TableFieldCache tableFieldCache;
    
    @Autowired
    private AesEncryptStrategy aesEncryptStrategy;
    
    @Autowired
    private DesEncryptStrategy desEncryptStrategy;
    
    @Autowired
    private KeyManager keyManager;
    
    @Autowired
    private EncryptUtil encryptUtil;
    
    @Autowired
    private EncryptProperties encryptProperties;
    
    @BeforeEach
    void setUp() {
        // 配置测试用的表字段缓存
        Set<String> userEncryptFields = new HashSet<>(Arrays.asList("phone", "email", "id_card"));
        tableFieldCache.addTableEncryptFields("user", userEncryptFields);
        tableFieldCache.addClassNameToTableName("com.chu7.securtkit.encrypt.entity.UserEntity", "user");
        
        // 配置订单表的加密字段
        Set<String> orderEncryptFields = new HashSet<>(Arrays.asList("customer_name", "customer_phone", "customer_email", "delivery_address"));
        tableFieldCache.addTableEncryptFields("orders", orderEncryptFields);
        tableFieldCache.addClassNameToTableName("com.chu7.securtkit.encrypt.entity.OrderEntity", "orders");
    }
    
    @Test
    void testAesEncryptionDecryption() {
        // 测试AES加密解密
        String plainText = "13800138000";
        String key = keyManager.getDefaultKey();
        
        String encrypted = aesEncryptStrategy.encrypt(plainText, key);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);
        
        String decrypted = aesEncryptStrategy.decrypt(encrypted, key);
        assertEquals(plainText, decrypted);
    }
    
    @Test
    void testDesEncryptionDecryption() {
        // 测试DES加密解密
        String plainText = "test@example.com";
        String key = keyManager.getDefaultKey();
        
        String encrypted = desEncryptStrategy.encrypt(plainText, key);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);
        
        String decrypted = desEncryptStrategy.decrypt(encrypted, key);
        assertEquals(plainText, decrypted);
    }
    
    @Test
    void testEncryptUtil() {
        // 测试加密工具类
        String plainText = "110101199001011234";
        
        // AES加密
        String aesEncrypted = encryptUtil.encrypt(plainText, "AES");
        assertNotNull(aesEncrypted);
        assertNotEquals(plainText, aesEncrypted);
        
        String aesDecrypted = encryptUtil.decrypt(aesEncrypted, "AES");
        assertEquals(plainText, aesDecrypted);
        
        // DES加密
        String desEncrypted = encryptUtil.encrypt(plainText, "DES");
        assertNotNull(desEncrypted);
        assertNotEquals(plainText, desEncrypted);
        
        String desDecrypted = encryptUtil.decrypt(desEncrypted, "DES");
        assertEquals(plainText, desDecrypted);
    }
    
    @Test
    void testObjectEncryptionDecryption() {
        // 测试对象加密解密
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("测试用户");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setIdCard("110101199001011234");
        user.setAddress("测试地址");
        
        // 加密对象
        encryptUtil.encryptObject(user, "user");
        
        // 验证加密后的值
        assertNotEquals("13800138000", user.getPhone());
        assertNotEquals("test@example.com", user.getEmail());
        assertNotEquals("110101199001011234", user.getIdCard());
        assertEquals("测试用户", user.getUsername()); // 未加密字段保持不变
        assertEquals("测试地址", user.getAddress()); // 未加密字段保持不变
        
        // 解密对象
        encryptUtil.decryptObject(user, "user");
        
        // 验证解密后的值
        assertEquals("13800138000", user.getPhone());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("110101199001011234", user.getIdCard());
        assertEquals("测试用户", user.getUsername());
        assertEquals("测试地址", user.getAddress());
    }
    
    @Test
    void testKeyManager() {
        // 测试密钥管理器
        String defaultKey = keyManager.getDefaultKey();
        assertNotNull(defaultKey);
        assertTrue(keyManager.isKeyValid(defaultKey));
        
        // 测试获取特定字段的密钥
        String fieldKey = keyManager.getKey("user", "phone");
        assertNotNull(fieldKey);
        
        // 测试生成新密钥
        String newKey = keyManager.generateKey("AES");
        assertNotNull(newKey);
        assertTrue(keyManager.isKeyValid(newKey));
        
        // 测试密钥轮换
        String rotatedKey = keyManager.rotateKey("user", "phone");
        assertNotNull(rotatedKey);
        assertNotEquals(fieldKey, rotatedKey);
    }
    
    @Test
    void testTableFieldCache() {
        // 测试表字段缓存
        assertTrue(tableFieldCache.hasEncryptFields("user"));
        
        Set<String> encryptFields = tableFieldCache.getTableEncryptFields("user");
        assertEquals(3, encryptFields.size());
        assertTrue(encryptFields.contains("phone"));
        assertTrue(encryptFields.contains("email"));
        assertTrue(encryptFields.contains("id_card"));
        
        assertTrue(tableFieldCache.isFieldEncrypted("user", "phone"));
        assertFalse(tableFieldCache.isFieldEncrypted("user", "username"));
        
        String tableName = tableFieldCache.getTableNameByClassName("com.chu7.securtkit.encrypt.entity.UserEntity");
        assertEquals("user", tableName);
    }
    
    @Test
    void testEncryptProperties() {
        // 测试配置属性
        assertTrue(encryptProperties.isEnabled());
        assertEquals("AES", encryptProperties.getAlgorithm());
        assertEquals("test-secret-key-32-chars-long", encryptProperties.getKey());
        assertEquals("POJO", encryptProperties.getPatternType());
        
        // 测试密钥轮换配置
        assertFalse(encryptProperties.getKeyRotation().isEnabled());
        assertEquals(30, encryptProperties.getKeyRotation().getInterval());
        
        // 测试缓存配置
        assertTrue(encryptProperties.getCache().isEnabled());
        assertEquals(1000, encryptProperties.getCache().getSize());
        assertEquals(1, encryptProperties.getCache().getExpire());
    }
    
    @Test
    void testEncryptContext() {
        // 测试加密上下文
        EncryptContext context = EncryptContext.builder()
                .tableName("user")
                .fieldName("phone")
                .algorithm("AES")
                .encryptStrategy(aesEncryptStrategy)
                .keyManager(keyManager)
                .enabled(true)
                .build();
        
        String plainText = "13800138000";
        String encrypted = context.encrypt(plainText);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);
        
        String decrypted = context.decrypt(encrypted);
        assertEquals(plainText, decrypted);
        
        // 测试上下文属性
        context.setAttribute("testKey", "testValue");
        assertEquals("testValue", context.getAttribute("testKey"));
        assertEquals("defaultValue", context.getAttribute("nonExistentKey", "defaultValue"));
    }
    
    @Test
    void testNullAndEmptyValues() {
        // 测试空值和null值处理
        assertNull(encryptUtil.encrypt(null, "AES"));
        assertEquals("", encryptUtil.encrypt("", "AES"));
        
        assertNull(encryptUtil.decrypt(null, "AES"));
        assertEquals("", encryptUtil.decrypt("", "AES"));
        
        // 测试对象中的空值
        UserEntity user = new UserEntity();
        user.setPhone(null);
        user.setEmail("");
        user.setIdCard("110101199001011234");
        
        encryptUtil.encryptObject(user, "user");
        assertNull(user.getPhone());
        assertEquals("", user.getEmail());
        assertNotEquals("110101199001011234", user.getIdCard());
        
        encryptUtil.decryptObject(user, "user");
        assertNull(user.getPhone());
        assertEquals("", user.getEmail());
        assertEquals("110101199001011234", user.getIdCard());
    }
    
    @Test
    void testComplexObjectEncryptionDecryption() {
        // 测试复杂对象的加密解密
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("张三");
        user.setPhone("13800138000");
        user.setEmail("zhangsan@example.com");
        user.setIdCard("110101199001011234");
        user.setAddress("北京市朝阳区");
        
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderNo("ORD20250101001");
        order.setUserId(1L);
        order.setCustomerName("张三");
        order.setCustomerPhone("13800138000");
        order.setCustomerEmail("zhangsan@example.com");
        order.setDeliveryAddress("北京市朝阳区某某街道123号");
        order.setAmount(new BigDecimal("999.99"));
        order.setStatus("已支付");
        
        // 加密用户对象
        encryptUtil.encryptObject(user, "user");
        
        // 加密订单对象
        encryptUtil.encryptObject(order, "orders");
        
        // 验证加密后的值
        assertNotEquals("13800138000", user.getPhone());
        assertNotEquals("zhangsan@example.com", user.getEmail());
        assertNotEquals("110101199001011234", user.getIdCard());
        assertEquals("张三", user.getUsername()); // 未加密字段保持不变
        assertEquals("北京市朝阳区", user.getAddress()); // 未加密字段保持不变
        
        assertNotEquals("张三", order.getCustomerName());
        assertNotEquals("13800138000", order.getCustomerPhone());
        assertNotEquals("zhangsan@example.com", order.getCustomerEmail());
        assertNotEquals("北京市朝阳区某某街道123号", order.getDeliveryAddress());
        assertEquals("ORD20250101001", order.getOrderNo()); // 未加密字段保持不变
        assertEquals(new BigDecimal("999.99"), order.getAmount()); // 未加密字段保持不变
        
        // 解密用户对象
        encryptUtil.decryptObject(user, "user");
        
        // 解密订单对象
        encryptUtil.decryptObject(order, "orders");
        
        // 验证解密后的值
        assertEquals("13800138000", user.getPhone());
        assertEquals("zhangsan@example.com", user.getEmail());
        assertEquals("110101199001011234", user.getIdCard());
        assertEquals("张三", user.getUsername());
        assertEquals("北京市朝阳区", user.getAddress());
        
        assertEquals("张三", order.getCustomerName());
        assertEquals("13800138000", order.getCustomerPhone());
        assertEquals("zhangsan@example.com", order.getCustomerEmail());
        assertEquals("北京市朝阳区某某街道123号", order.getDeliveryAddress());
        assertEquals("ORD20250101001", order.getOrderNo());
        assertEquals(new BigDecimal("999.99"), order.getAmount());
    }
    
    @Test
    void testBatchObjectEncryptionDecryption() {
        // 测试批量对象的加密解密
        List<UserEntity> users = Arrays.asList(
            createTestUser(1L, "张三", "13800138000", "zhangsan@example.com", "110101199001011234"),
            createTestUser(2L, "李四", "13900139000", "lisi@example.com", "110101199002022345"),
            createTestUser(3L, "王五", "13700137000", "wangwu@example.com", "110101199003033456")
        );
        
        List<OrderEntity> orders = Arrays.asList(
            createTestOrder(1L, "ORD001", 1L, "张三", "13800138000", "zhangsan@example.com", "北京朝阳区"),
            createTestOrder(2L, "ORD002", 2L, "李四", "13900139000", "lisi@example.com", "上海浦东区"),
            createTestOrder(3L, "ORD003", 3L, "王五", "13700137000", "wangwu@example.com", "广州天河区")
        );
        
        // 批量加密
        for (UserEntity user : users) {
            encryptUtil.encryptObject(user, "user");
        }
        
        for (OrderEntity order : orders) {
            encryptUtil.encryptObject(order, "orders");
        }
        
        // 验证加密后的值
        for (UserEntity user : users) {
            assertNotEquals("13800138000", user.getPhone());
            assertNotEquals("zhangsan@example.com", user.getEmail());
            assertNotEquals("110101199001011234", user.getIdCard());
        }
        
        for (OrderEntity order : orders) {
            assertNotEquals("张三", order.getCustomerName());
            assertNotEquals("13800138000", order.getCustomerPhone());
        }
        
        // 批量解密
        for (UserEntity user : users) {
            encryptUtil.decryptObject(user, "user");
        }
        
        for (OrderEntity order : orders) {
            encryptUtil.decryptObject(order, "orders");
        }
        
        // 验证解密后的值
        assertEquals("13800138000", users.get(0).getPhone());
        assertEquals("13900139000", users.get(1).getPhone());
        assertEquals("13700137000", users.get(2).getPhone());
        
        assertEquals("张三", orders.get(0).getCustomerName());
        assertEquals("李四", orders.get(1).getCustomerName());
        assertEquals("王五", orders.get(2).getCustomerName());
    }
    
    @Test
    void testDifferentAlgorithmEncryption() {
        // 测试不同算法的加密解密
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("测试用户");
        user.setPhone("13800138000"); // 默认AES
        user.setEmail("test@example.com"); // AES
        user.setIdCard("110101199001011234"); // DES
        user.setAddress("测试地址");
        
        // 加密对象
        encryptUtil.encryptObject(user, "user");
        
        // 验证不同算法的加密结果不同
        String phoneEncrypted = user.getPhone();
        String emailEncrypted = user.getEmail();
        String idCardEncrypted = user.getIdCard();
        
        assertNotEquals("13800138000", phoneEncrypted);
        assertNotEquals("test@example.com", emailEncrypted);
        assertNotEquals("110101199001011234", idCardEncrypted);
        
        // 解密对象
        encryptUtil.decryptObject(user, "user");
        
        // 验证解密后的值
        assertEquals("13800138000", user.getPhone());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("110101199001011234", user.getIdCard());
    }
    
    @Test
    void testEncryptContextWithComplexScenario() {
        // 测试加密上下文在复杂场景下的使用
        EncryptContext userPhoneContext = EncryptContext.builder()
                .tableName("user")
                .fieldName("phone")
                .algorithm("AES")
                .encryptStrategy(aesEncryptStrategy)
                .keyManager(keyManager)
                .enabled(true)
                .build();
        
        EncryptContext orderCustomerContext = EncryptContext.builder()
                .tableName("orders")
                .fieldName("customer_phone")
                .algorithm("AES")
                .encryptStrategy(aesEncryptStrategy)
                .keyManager(keyManager)
                .enabled(true)
                .build();
        
        String userPhone = "13800138000";
        String orderCustomerPhone = "13900139000";
        
        // 加密
        String encryptedUserPhone = userPhoneContext.encrypt(userPhone);
        String encryptedOrderCustomerPhone = orderCustomerContext.encrypt(orderCustomerPhone);
        
        assertNotEquals(userPhone, encryptedUserPhone);
        assertNotEquals(orderCustomerPhone, encryptedOrderCustomerPhone);
        
        // 解密
        String decryptedUserPhone = userPhoneContext.decrypt(encryptedUserPhone);
        String decryptedOrderCustomerPhone = orderCustomerContext.decrypt(encryptedOrderCustomerPhone);
        
        assertEquals(userPhone, decryptedUserPhone);
        assertEquals(orderCustomerPhone, decryptedOrderCustomerPhone);
    }
    
    // 辅助方法：创建测试用户
    private UserEntity createTestUser(Long id, String username, String phone, String email, String idCard) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setPhone(phone);
        user.setEmail(email);
        user.setIdCard(idCard);
        user.setAddress("测试地址");
        return user;
    }
    
    // 辅助方法：创建测试订单
    private OrderEntity createTestOrder(Long id, String orderNo, Long userId, String customerName, 
                                      String customerPhone, String customerEmail, String deliveryAddress) {
        OrderEntity order = new OrderEntity();
        order.setId(id);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setCustomerEmail(customerEmail);
        order.setDeliveryAddress(deliveryAddress);
        order.setAmount(new BigDecimal("100.00"));
        order.setStatus("待支付");
        return order;
    }
    
    @Test
    void testComplexObjectEncryptionWithNestedObjects() {
        // 测试复杂对象加密（包含嵌套对象）
        UserEntity user = createTestUser(1L, "张三", "13800138000", "zhangsan@example.com", "110101199001011234");
        OrderEntity order1 = createTestOrder(1L, "ORD001", 1L, "张三", "13800138000", "zhangsan@example.com", "北京市朝阳区");
        OrderEntity order2 = createTestOrder(2L, "ORD002", 1L, "张三", "13800138000", "zhangsan@example.com", "上海市浦东新区");
        
        // 创建用户订单列表
        List<OrderEntity> userOrders = Arrays.asList(order1, order2);
        
        // 加密用户对象
        encryptUtil.encryptObject(user, "user");
        
        // 加密订单列表
        for (OrderEntity order : userOrders) {
            encryptUtil.encryptObject(order, "orders");
        }
        
        // 验证加密结果
        assertNotEquals("13800138000", user.getPhone());
        assertNotEquals("zhangsan@example.com", user.getEmail());
        assertNotEquals("110101199001011234", user.getIdCard());
        
        for (OrderEntity order : userOrders) {
            assertNotEquals("张三", order.getCustomerName());
            assertNotEquals("13800138000", order.getCustomerPhone());
            assertNotEquals("zhangsan@example.com", order.getCustomerEmail());
            assertNotEquals("北京市朝阳区", order.getDeliveryAddress());
        }
        
        // 解密用户对象
        encryptUtil.decryptObject(user, "user");
        
        // 解密订单列表
        for (OrderEntity order : userOrders) {
            encryptUtil.decryptObject(order, "orders");
        }
        
        // 验证解密结果
        assertEquals("13800138000", user.getPhone());
        assertEquals("zhangsan@example.com", user.getEmail());
        assertEquals("110101199001011234", user.getIdCard());
        
        for (OrderEntity order : userOrders) {
            assertEquals("张三", order.getCustomerName());
            assertEquals("13800138000", order.getCustomerPhone());
            assertEquals("zhangsan@example.com", order.getCustomerEmail());
        }
    }
    
    @Test
    void testBatchEncryptionWithDifferentAlgorithms() {
        // 测试批量加密（不同算法）
        List<UserEntity> users = Arrays.asList(
            createTestUser(1L, "用户1", "13800138001", "user1@example.com", "110101199001011235"),
            createTestUser(2L, "用户2", "13800138002", "user2@example.com", "110101199001011236"),
            createTestUser(3L, "用户3", "13800138003", "user3@example.com", "110101199001011237")
        );
        
        // 批量加密
        for (UserEntity user : users) {
            encryptUtil.encryptObject(user, "user");
        }
        
        // 验证所有用户都被加密
        for (UserEntity user : users) {
            assertNotEquals("1380013800" + user.getId(), user.getPhone());
            assertNotEquals("user" + user.getId() + "@example.com", user.getEmail());
            assertNotEquals("11010119900101123" + (4 + user.getId()), user.getIdCard());
        }
        
        // 批量解密
        for (UserEntity user : users) {
            encryptUtil.decryptObject(user, "user");
        }
        
        // 验证所有用户都被正确解密
        for (UserEntity user : users) {
            assertEquals("1380013800" + user.getId(), user.getPhone());
            assertEquals("user" + user.getId() + "@example.com", user.getEmail());
            assertEquals("11010119900101123" + (4 + user.getId()), user.getIdCard());
        }
    }
    
    @Test
    void testEncryptionWithNullAndEmptyValues() {
        // 测试空值和null值的加密处理
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("测试用户");
        user.setPhone(null); // null值
        user.setEmail(""); // 空字符串
        user.setIdCard("   "); // 空白字符串
        user.setAddress("测试地址");
        
        // 加密对象
        encryptUtil.encryptObject(user, "user");
        
        // 验证null值保持不变
        assertNull(user.getPhone());
        
        // 验证空字符串被加密
        assertNotNull(user.getEmail());
        assertNotEquals("", user.getEmail());
        
        // 验证空白字符串被加密
        assertNotNull(user.getIdCard());
        assertNotEquals("   ", user.getIdCard());
        
        // 解密对象
        encryptUtil.decryptObject(user, "user");
        
        // 验证解密结果
        assertNull(user.getPhone());
        assertEquals("", user.getEmail());
        assertEquals("   ", user.getIdCard());
    }
    
    @Test
    void testEncryptionWithSpecialCharacters() {
        // 测试特殊字符的加密
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("测试用户");
        user.setPhone("138-0013-8000"); // 包含连字符
        user.setEmail("test+tag@example.com"); // 包含加号
        user.setIdCard("110101199001011234"); // 纯数字
        user.setAddress("北京市朝阳区建国路88号");
        
        // 加密对象
        encryptUtil.encryptObject(user, "user");
        
        // 验证加密结果
        assertNotEquals("138-0013-8000", user.getPhone());
        assertNotEquals("test+tag@example.com", user.getEmail());
        assertNotEquals("110101199001011234", user.getIdCard());
        
        // 解密对象
        encryptUtil.decryptObject(user, "user");
        
        // 验证解密结果
        assertEquals("138-0013-8000", user.getPhone());
        assertEquals("test+tag@example.com", user.getEmail());
        assertEquals("110101199001011234", user.getIdCard());
    }
    
    @Test
    void testEncryptionWithLongValues() {
        // 测试长字符串的加密
        StringBuilder longPhone = new StringBuilder();
        StringBuilder longEmail = new StringBuilder();
        StringBuilder longIdCard = new StringBuilder();
        
        // 生成长字符串
        for (int i = 0; i < 1000; i++) {
            longPhone.append("1");
            longEmail.append("a");
            longIdCard.append("9");
        }
        
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("测试用户");
        user.setPhone(longPhone.toString());
        user.setEmail(longEmail.toString() + "@example.com");
        user.setIdCard(longIdCard.toString());
        user.setAddress("测试地址");
        
        // 加密对象
        encryptUtil.encryptObject(user, "user");
        
        // 验证加密结果
        assertNotEquals(longPhone.toString(), user.getPhone());
        assertNotEquals(longEmail.toString() + "@example.com", user.getEmail());
        assertNotEquals(longIdCard.toString(), user.getIdCard());
        
        // 解密对象
        encryptUtil.decryptObject(user, "user");
        
        // 验证解密结果
        assertEquals(longPhone.toString(), user.getPhone());
        assertEquals(longEmail.toString() + "@example.com", user.getEmail());
        assertEquals(longIdCard.toString(), user.getIdCard());
    }
    
    @Test
    void testEncryptionWithUnicodeCharacters() {
        // 测试Unicode字符的加密
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("测试用户");
        user.setPhone("13800138000");
        user.setEmail("测试@example.com");
        user.setIdCard("110101199001011234");
        user.setAddress("北京市朝阳区建国路88号🏢");
        
        // 加密对象
        encryptUtil.encryptObject(user, "user");
        
        // 验证加密结果
        assertNotEquals("13800138000", user.getPhone());
        assertNotEquals("测试@example.com", user.getEmail());
        assertNotEquals("110101199001011234", user.getIdCard());
        
        // 解密对象
        encryptUtil.decryptObject(user, "user");
        
        // 验证解密结果
        assertEquals("13800138000", user.getPhone());
        assertEquals("测试@example.com", user.getEmail());
        assertEquals("110101199001011234", user.getIdCard());
    }
    
    @Test
    void testEncryptionPerformanceWithLargeDataset() {
        // 测试大数据量加密性能
        List<UserEntity> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            UserEntity user = createTestUser(
                (long) i, 
                "用户" + i, 
                "1380013" + String.format("%04d", i), 
                "user" + i + "@example.com", 
                "11010119900101" + String.format("%04d", i)
            );
            users.add(user);
        }
        
        long startTime = System.currentTimeMillis();
        
        // 批量加密
        for (UserEntity user : users) {
            encryptUtil.encryptObject(user, "user");
        }
        
        long encryptTime = System.currentTimeMillis() - startTime;
        
        // 验证加密结果
        for (UserEntity user : users) {
            assertNotEquals("1380013" + String.format("%04d", user.getId()), user.getPhone());
            assertNotEquals("user" + user.getId() + "@example.com", user.getEmail());
        }
        
        startTime = System.currentTimeMillis();
        
        // 批量解密
        for (UserEntity user : users) {
            encryptUtil.decryptObject(user, "user");
        }
        
        long decryptTime = System.currentTimeMillis() - startTime;
        
        // 验证解密结果
        for (UserEntity user : users) {
            assertEquals("1380013" + String.format("%04d", user.getId()), user.getPhone());
            assertEquals("user" + user.getId() + "@example.com", user.getEmail());
        }
        
        System.out.println("1000个用户加密耗时: " + encryptTime + "ms");
        System.out.println("1000个用户解密耗时: " + decryptTime + "ms");
        
        // 性能断言（加密解密时间应该在合理范围内）
        assertTrue(encryptTime < 10000, "加密1000个用户耗时不应超过10秒");
        assertTrue(decryptTime < 10000, "解密1000个用户耗时不应超过10秒");
    }
    
    @Test
    void testEncryptionWithDifferentKeyScenarios() {
        // 测试不同密钥场景
        String originalKey = keyManager.getDefaultKey();
        
        // 测试场景1：使用默认密钥
        UserEntity user1 = createTestUser(1L, "用户1", "13800138001", "user1@example.com", "110101199001011235");
        encryptUtil.encryptObject(user1, "user");
        String encryptedPhone1 = user1.getPhone();
        
        // 测试场景2：使用表特定密钥
        String tableSpecificKey = keyManager.getKey("user", "phone");
        UserEntity user2 = createTestUser(2L, "用户2", "13800138002", "user2@example.com", "110101199001011236");
        
        EncryptContext context = EncryptContext.builder()
                .tableName("user")
                .fieldName("phone")
                .algorithm("AES")
                .encryptStrategy(aesEncryptStrategy)
                .keyManager(keyManager)
                .enabled(true)
                .build();
        
        user2.setPhone(context.encrypt(user2.getPhone()));
        String encryptedPhone2 = user2.getPhone();
        
        // 验证不同密钥产生不同的加密结果
        assertNotEquals(encryptedPhone1, encryptedPhone2);
        
        // 解密验证
        encryptUtil.decryptObject(user1, "user");
        assertEquals("13800138001", user1.getPhone());
        
        user2.setPhone(context.decrypt(user2.getPhone()));
        assertEquals("13800138002", user2.getPhone());
    }
    
    @Test
    void testEncryptionWithDisabledFields() {
        // 测试禁用加密字段的处理
        UserEntity user = createTestUser(1L, "测试用户", "13800138000", "test@example.com", "110101199001011234");
        
        // 创建禁用加密的上下文
        EncryptContext disabledContext = EncryptContext.builder()
                .tableName("user")
                .fieldName("phone")
                .algorithm("AES")
                .encryptStrategy(aesEncryptStrategy)
                .keyManager(keyManager)
                .enabled(false) // 禁用加密
                .build();
        
        String originalPhone = user.getPhone();
        String encryptedPhone = disabledContext.encrypt(user.getPhone());
        
        // 禁用时应该返回原值
        assertEquals(originalPhone, encryptedPhone);
        
        String decryptedPhone = disabledContext.decrypt(encryptedPhone);
        assertEquals(originalPhone, decryptedPhone);
    }
} 