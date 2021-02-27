-- 用户表
CREATE TABLE `business-core`.`t_user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` VARCHAR(45) NOT NULL COMMENT '用户名',
  `password` VARCHAR(45) NOT NULL COMMENT '密码，加盐MD5',
  `level` VARCHAR(45) NOT NULL DEFAULT '100' COMMENT '100 普通用户 200 VIP',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `status` VARCHAR(45) NULL DEFAULT '100' COMMENT '100 正常 200封禁',
  PRIMARY KEY (`id`));


-- 商品表
CREATE TABLE `business-core`.`t_commodity` (
  `id` INT NOT NULL COMMENT '商品id',
  `name` VARCHAR(45) NOT NULL COMMENT '名称',
  `code` VARCHAR(45) NOT NULL COMMENT '商品码',
  `type` VARCHAR(45) NOT NULL COMMENT '分类',
  `pic` VARCHAR(45) NULL COMMENT '图片链接',
  `stock` INT NULL DEFAULT 0 COMMENT '库存',
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NULL,
  `status` VARCHAR(45) NOT NULL DEFAULT '100' COMMENT '100 正常 200 下架 300 删除',
  PRIMARY KEY (`id`));


-- 订单表
CREATE TABLE `business-core`.`t_order` (
  `id` INT NOT NULL,
  `u_id` INT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NULL,
  `order_status` VARCHAR(45) NOT NULL DEFAULT '100' COMMENT '订单状态 100 正常 200 删除',
  `pay_status` VARCHAR(45) NOT NULL DEFAULT '100' COMMENT '付款状态 100 待付款 200 已付款',
  `pay_time` DATETIME NULL COMMENT '付款时间',
  `delivery_status` VARCHAR(45) NOT NULL DEFAULT '100' COMMENT '配送状态：100 待配送 200 配送中 300 配送完成',
  `delivery_time` DATETIME NULL COMMENT '配送时间',
  PRIMARY KEY (`id`));


-- 订单商品关联表
CREATE TABLE `business-core`.`t_order_co_rel` (
  `id` INT NOT NULL,
  `order_id` INT NOT NULL COMMENT '订单id',
  `co_id` INT NOT NULL COMMENT '商品id',
  PRIMARY KEY (`id`));



