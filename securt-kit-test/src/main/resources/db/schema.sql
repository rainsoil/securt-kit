-- 创建测试用户表
DROP TABLE IF EXISTS test_user;

CREATE TABLE test_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(500) NOT NULL COMMENT '密码(MD5加密)',
    phone VARCHAR(500) COMMENT '手机号(DES加密)',
    email VARCHAR(1000) COMMENT '邮箱(AES加密)',
    id_card VARCHAR(500) COMMENT '身份证号(Base64编码)',
    age INT COMMENT '年龄',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用户表';

-- 创建索引
CREATE INDEX idx_username ON test_user(username);
CREATE INDEX idx_phone ON test_user(phone);
CREATE INDEX idx_email ON test_user(email);

-- 创建用户详情表
DROP TABLE IF EXISTS user_profile;

CREATE TABLE user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '详情ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    real_name VARCHAR(500) COMMENT '真实姓名(AES加密)',
    id_card VARCHAR(500) COMMENT '身份证号(Base64加密)',
    gender VARCHAR(10) COMMENT '性别',
    occupation VARCHAR(100) COMMENT '职业',
    address VARCHAR(200) COMMENT '地址',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户详情表';

-- 创建用户详情表索引
CREATE INDEX idx_profile_user_id ON user_profile(user_id);
CREATE INDEX idx_real_name ON user_profile(real_name);
CREATE INDEX idx_id_card ON user_profile(id_card);

-- 创建订单表
DROP TABLE IF EXISTS user_order;

CREATE TABLE user_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    customer_name VARCHAR(500) COMMENT '客户姓名(AES加密)',
    customer_phone VARCHAR(500) COMMENT '客户电话(Base64加密)',
    amount DECIMAL(10,2) COMMENT '订单金额',
    status VARCHAR(20) COMMENT '订单状态',
    product_name VARCHAR(200) COMMENT '商品名称',
    quantity INT COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建订单表索引
CREATE INDEX idx_order_user_id ON user_order(user_id);
CREATE INDEX idx_order_no ON user_order(order_no);
CREATE INDEX idx_customer_name ON user_order(customer_name);
CREATE INDEX idx_customer_phone ON user_order(customer_phone);
CREATE INDEX idx_status ON user_order(status);
CREATE INDEX idx_create_time ON user_order(create_time);