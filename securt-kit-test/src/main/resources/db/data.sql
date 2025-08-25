-- 插入测试用户数据
INSERT INTO user (username, phone, email, id_card, age, address) VALUES
('张三', '13800138001', 'zhangsan@example.com', '110101199001011234', '30', '北京市朝阳区'),
('李四', '13800138002', 'lisi@example.com', '110101199002022345', '25', '上海市浦东新区'),
('王五', '13800138003', 'wangwu@example.com', '110101199003033456', '35', '广州市天河区'),
('赵六', '13800138004', 'zhaoliu@example.com', '110101199004044567', '28', '深圳市南山区');

-- 插入测试订单数据
INSERT INTO orders (order_no, user_id, customer_name, customer_phone, customer_email, delivery_address, amount, status) VALUES
('ORD202401001', 1, '张三', '13800138001', 'zhangsan@example.com', '北京市朝阳区某某街道123号', 299.99, 'PAID'),
('ORD202401002', 2, '李四', '13800138002', 'lisi@example.com', '上海市浦东新区某某路456号', 599.99, 'SHIPPED'),
('ORD202401003', 3, '王五', '13800138003', 'wangwu@example.com', '广州市天河区某某大道789号', 899.99, 'DELIVERED'),
('ORD202401004', 4, '赵六', '13800138004', 'zhaoliu@example.com', '深圳市南山区某某小区101号', 399.99, 'PAID');

-- 插入系统配置数据（不加密）
INSERT INTO system_config (config_key, config_value, description) VALUES
('system.name', 'SecurtKit测试系统', '系统名称'),
('system.version', '1.0.0', '系统版本'),
('encrypt.enabled', 'true', '是否启用加密'),
('test.mode', 'true', '测试模式'); 