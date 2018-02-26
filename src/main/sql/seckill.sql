-- 秒杀执行存储过程
DELIMITER $$ -- console ;转换为 $$
-- 定义存储过程
-- 参数:in 输入参数;out 输出参数
-- row_count(): 返回上一条修改类型sql(delete,insert,update)的影响行数.
-- row_count(): 0:未修改的数据; >0:表示修改的行数; <0:SQL错误/未执行的SQL

-- 主要是在存储过程中完成秒杀的事务
CREATE PROCEDURE `seckill`.`execute_seckill`
  (in v_seckill_id bigint,in v_phone bigint,
    in v_kill_time timestamp,out r_result int)
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    -- 开始事务
    start TRANSACTION ;
    -- 插入明细
    insert ignore into success_killed
      (seckill_id,user_phone,create_time)
    values(v_seckill_id,v_phone,v_kill_time);
    --  查询上一条修改类型SQL的影响行数
    select row_count() into insert_count;
    -- 没有修改数据
    if(insert_count =0 ) THEN
    ROLLBACK ;
    set r_result = -1 ;
    -- SQL错误/未执行SQL
    ELSEIF(insert_count < 0) THEN
    ROLLBACK ;
    set r_result = -2 ;
    -- 写入明细成功,执行减库存操作
    ELSE
        update seckill
        set number = number - 1
        where seckill_id = v_seckill_id
          AND number > 0
          AND end_time > v_kill_time
          AND start_time < v_kill_time;
    END IF;

    --  查询上一条修改类型SQL的影响行数
    select row_count() into insert_count;
    IF (insert_count = 0 ) THEN
      ROLLBACK ;
      set r_result = 0;
      -- sql执行出错/等待行级锁超时
        ELSEIF (insert_count < 0 ) THEN
            ROLLBACK ;
            set r_result = -2;
        ELSE
            COMMIT ;
            set r_result = 1;
        END If;
    END IF;
  END;
$$
-- 存储过程定义结束
-- 把插入购买明细和更新库存的操作放入到了存储过程里面,只需调用存储过程即可
DELIMITER ;
-- 定义一个变量是-3
set @r_result = -3;
-- 执行存储过程
call execute_seckill(1003,13784832739,now(),@r_result);

--  获取结果
select @r_result;

-- 存储过程
-- 1. 存储过程优化:事务行级锁持有的时间.
-- 2. 不要过度的依赖存储过程.
-- 3. 简单的逻辑,可以应用存储过程.
-- 4. 秒杀同一个商品可以达到600Mps

