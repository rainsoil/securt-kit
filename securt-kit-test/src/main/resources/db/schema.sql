-- 创建测试用户表
DROP TABLE IF EXISTS test_user;

CREATE TABLE test_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(MD5加密)',
    phone VARCHAR(255) COMMENT '手机号(DES加密)',
    email VARCHAR(255) COMMENT '邮箱(AES加密)',
    id_card VARCHAR(255) COMMENT '身份证号(Base64编码)',
    age INT COMMENT '年龄',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用户表';

-- 创建索引
CREATE INDEX idx_username ON test_user(username);
CREATE INDEX idx_phone ON test_user(phone);
CREATE INDEX idx_email ON test_user(email);