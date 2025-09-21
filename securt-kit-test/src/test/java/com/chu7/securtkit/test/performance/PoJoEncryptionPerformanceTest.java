package com.chu7.securtkit.test.performance;

import com.chu7.securtkit.encryptor.pojo.*;
import com.chu7.securtkit.config.properties.EncryptorProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * POJO加密性能测试
 * 测试各种加密策略的性能表现
 *
 * @author chu7
 * @date 2024/7/9 14:06
 */
@ExtendWith(MockitoExtension.class)
public class PoJoEncryptionPerformanceTest {

    @Mock
    private EncryptorProperties encryptorProperties;


    private DefaultPoJoFieldEncryptorPattern defaultEncryptor;
    private AesPoJoFieldEncryptorStrategy aesEncryptor;
    private Base64PoJoFieldEncryptorStrategy base64Encryptor;
    private Md5PoJoFieldEncryptorStrategy md5Encryptor;

    private static final int TEST_ITERATIONS = 1000;
    private static final int CONCURRENT_THREADS = 10;
    private static final String TEST_DATA = "This is a test string for performance testing with various encryption algorithms.";

    @BeforeEach
    public void setUp() {
        // 设置模拟对象
        when(encryptorProperties.getSecretKey()).thenReturn("1234567890123456"); // 16位密钥用于AES

        // 初始化加密器
        defaultEncryptor = new DefaultPoJoFieldEncryptorPattern(encryptorProperties);
        aesEncryptor = new AesPoJoFieldEncryptorStrategy(encryptorProperties);
        base64Encryptor = new Base64PoJoFieldEncryptorStrategy();
        md5Encryptor = new Md5PoJoFieldEncryptorStrategy();
    }

    /**
     * 测试默认DES加密器的性能
     */
    @Test
    public void testDefaultEncryptorPerformance() {
        long startTime = System.currentTimeMillis();
        
        List<String> encryptedResults = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String encrypted = defaultEncryptor.encryption(TEST_DATA);
            encryptedResults.add(encrypted);
        }
        
        long encryptionTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        for (String encrypted : encryptedResults) {
            String decrypted = defaultEncryptor.decryption(encrypted);
            assertEquals(TEST_DATA, decrypted);
        }
        
        long decryptionTime = System.currentTimeMillis() - startTime;
        
        System.out.println("Default DES Encryptor Performance:");
        System.out.println("Encryption time: " + encryptionTime + "ms for " + TEST_ITERATIONS + " iterations");
        System.out.println("Decryption time: " + decryptionTime + "ms for " + TEST_ITERATIONS + " iterations");
        System.out.println("Average encryption time: " + (encryptionTime / (double) TEST_ITERATIONS) + "ms per operation");
        System.out.println("Average decryption time: " + (decryptionTime / (double) TEST_ITERATIONS) + "ms per operation");
    }

    /**
     * 测试AES加密器的性能
     */
    @Test
    public void testAesEncryptorPerformance() {
        long startTime = System.currentTimeMillis();
        
        List<String> encryptedResults = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String encrypted = aesEncryptor.encryption(TEST_DATA);
            encryptedResults.add(encrypted);
        }
        
        long encryptionTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        for (String encrypted : encryptedResults) {
            String decrypted = aesEncryptor.decryption(encrypted);
            assertEquals(TEST_DATA, decrypted);
        }
        
        long decryptionTime = System.currentTimeMillis() - startTime;
        
        System.out.println("AES Encryptor Performance:");
        System.out.println("Encryption time: " + encryptionTime + "ms for " + TEST_ITERATIONS + " iterations");
        System.out.println("Decryption time: " + decryptionTime + "ms for " + TEST_ITERATIONS + " iterations");
        System.out.println("Average encryption time: " + (encryptionTime / (double) TEST_ITERATIONS) + "ms per operation");
        System.out.println("Average decryption time: " + (decryptionTime / (double) TEST_ITERATIONS) + "ms per operation");
    }

    /**
     * 测试Base64编码器的性能
     */
    @Test
    public void testBase64EncryptorPerformance() {
        long startTime = System.currentTimeMillis();
        
        List<String> encodedResults = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String encoded = base64Encryptor.encryption(TEST_DATA);
            encodedResults.add(encoded);
        }
        
        long encodingTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        for (String encoded : encodedResults) {
            String decoded = base64Encryptor.decryption(encoded);
            assertEquals(TEST_DATA, decoded);
        }
        
        long decodingTime = System.currentTimeMillis() - startTime;
        
        System.out.println("Base64 Encoder Performance:");
        System.out.println("Encoding time: " + encodingTime + "ms for " + TEST_ITERATIONS + " iterations");
        System.out.println("Decoding time: " + decodingTime + "ms for " + TEST_ITERATIONS + " iterations");
        System.out.println("Average encoding time: " + (encodingTime / (double) TEST_ITERATIONS) + "ms per operation");
        System.out.println("Average decoding time: " + (decodingTime / (double) TEST_ITERATIONS) + "ms per operation");
    }

    /**
     * 测试MD5哈希器的性能
     */
    @Test
    public void testMd5EncryptorPerformance() {
        long startTime = System.currentTimeMillis();
        
        List<String> hashedResults = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String hashed = md5Encryptor.encryption(TEST_DATA);
            hashedResults.add(hashed);
        }
        
        long hashingTime = System.currentTimeMillis() - startTime;
        
        System.out.println("MD5 Hasher Performance:");
        System.out.println("Hashing time: " + hashingTime + "ms for " + TEST_ITERATIONS + " iterations");
        System.out.println("Average hashing time: " + (hashingTime / (double) TEST_ITERATIONS) + "ms per operation");
    }

    /**
     * 测试并发性能
     */
    @Test
    public void testConcurrentPerformance() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        List<Future<Long>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            Future<Long> future = executor.submit(() -> {
                long threadStartTime = System.currentTimeMillis();
                
                for (int j = 0; j < TEST_ITERATIONS / CONCURRENT_THREADS; j++) {
                    String encrypted = defaultEncryptor.encryption(TEST_DATA);
                    String decrypted = defaultEncryptor.decryption(encrypted);
                    assertEquals(TEST_DATA, decrypted);
                }
                
                latch.countDown();
                return System.currentTimeMillis() - threadStartTime;
            });
            futures.add(future);
        }

        latch.await();
        long totalTime = System.currentTimeMillis() - startTime;

        long totalThreadTime = 0;
        for (Future<Long> future : futures) {
            try {
                totalThreadTime += future.get();
            } catch (ExecutionException e) {
                fail("Concurrent execution failed: " + e.getMessage());
            }
        }

        executor.shutdown();

        System.out.println("Concurrent Performance Test:");
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Total thread time: " + totalThreadTime + "ms");
        System.out.println("Average time per thread: " + (totalThreadTime / CONCURRENT_THREADS) + "ms");
        System.out.println("Operations per second: " + (TEST_ITERATIONS * 1000 / totalTime));
    }

    /**
     * 测试不同数据大小的性能
     */
    @Test
    public void testDifferentDataSizes() {
        String[] testDataSizes = {
            "Short",
            "This is a medium length test string for performance testing.",
            "This is a very long test string that contains many characters and should be used to test the performance of encryption algorithms with larger data sizes. " +
            "It includes various types of characters including numbers, letters, symbols, and spaces to provide a comprehensive test case for encryption performance analysis."
        };

        for (String testData : testDataSizes) {
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < TEST_ITERATIONS; i++) {
                String encrypted = defaultEncryptor.encryption(testData);
                String decrypted = defaultEncryptor.decryption(encrypted);
                assertEquals(testData, decrypted);
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            
            System.out.println("Data size: " + testData.length() + " characters");
            System.out.println("Total time: " + totalTime + "ms for " + TEST_ITERATIONS + " iterations");
            System.out.println("Average time per operation: " + (totalTime / (double) TEST_ITERATIONS) + "ms");
            System.out.println("Operations per second: " + (TEST_ITERATIONS * 1000 / totalTime));
            System.out.println("---");
        }
    }

    /**
     * 测试内存使用情况
     */
    @Test
    public void testMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        
        // 强制垃圾回收
        System.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        List<String> encryptedResults = new ArrayList<>();
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String encrypted = defaultEncryptor.encryption(TEST_DATA);
            encryptedResults.add(encrypted);
        }
        
        long afterEncryptionMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = afterEncryptionMemory - initialMemory;
        
        System.out.println("Memory Usage Test:");
        System.out.println("Initial memory: " + (initialMemory / 1024 / 1024) + "MB");
        System.out.println("After encryption memory: " + (afterEncryptionMemory / 1024 / 1024) + "MB");
        System.out.println("Memory used: " + (memoryUsed / 1024 / 1024) + "MB");
        System.out.println("Average memory per encryption: " + (memoryUsed / TEST_ITERATIONS) + " bytes");
        
        // 清理内存
        encryptedResults.clear();
        System.gc();
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Final memory after cleanup: " + (finalMemory / 1024 / 1024) + "MB");
    }
}
