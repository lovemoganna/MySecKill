-- 数据库初始化脚本
-- 1.创建数据库

CREATE DATABASE seckill;

-- 2.使用数据库
use seckill;

-- 3.创建秒杀数据库
-- 包括存储引擎的选择,具体字段的添加,还有索引的添加

CREATE TABLE seckill(
  `seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` VARCHAR(120) NOT NULL  COMMENT '商品名称',
  `number` int NOT NULL COMMENT '库存数量',
  `start_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀开始时间',
  `end_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀结束时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET='utf8' COMMENT ='秒杀库存表';

-- 查看表的创建计划: show create table seckill/G;

-- 4.初始化数据
INSERT INTO seckill(name,number,start_time,end_time)
VALUES
('1000元秒杀iPad6',100,'2018-1-31 16:31:00','2018-2-01 16:31:00'),
('500元秒杀iPad7',200,'2018-1-31 16:31:00','2018-2-01 16:31:00'),
('300元秒杀小米4',300,'2018-1-31 16:31:00','2018-2-01 16:31:00'),
('200元秒杀红米note',400,'2018-1-31 16:31:00','2018-2-01 16:31:00');

-- 5.秒杀成功明细表
-- 用户登录认证相关的信息
create table success_killed(
  `seckill_id` bigint NOT NULL COMMENT '秒杀商品Id',
  `user_phone` bigint NOT NULL COMMENT '用户手机号',
  `state` tinyint NOT NULL DEFAULT -1 COMMENT '状态标识:-1:无效,0:成功,1:已付款,2:已发货',
  `create_time` TIMESTAMP NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
  PRIMARY KEY (seckill_id,user_phone),/*联合索引*/
  key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='秒杀成功明细表';

-- 连接数据库的控制台
mysql -uroot -p

-- 手写DDL
-- 需要记录每次上线的DDL修改
-- 上线新的版本 V1.1
比如你做了下面的一些操作:

添加新的一列
ALTER TABLE seckill
删除一个索引
drop INDEX idx_crete_time,
添加一个组合索引
add INDEX idx_c_s(start_time.create_time)

-- 上线V1.2
-- DDL
下一个版本,然后再做一些记录.