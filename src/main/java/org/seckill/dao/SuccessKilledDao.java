package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * @author: ligang
 * date: 2018/2/1
 * time: 10:39
 *
 * 1.插入用户购买明细,可过滤重复(之前设置的是联合主键,所以可以帮我们过滤重复)
 * 2.根据id查询Successkilled并携带Seckill实体
 *
 */
public interface SuccessKilledDao {
    /**
     * 插入用户购买明细
     * @param seckillId
     * @param userPhone
     * @return  插入的行数,就是秒杀成功的记录
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

    /**
     * 根据id查询Successkilled并携带Seckill实体
     * @param seckillId
     * @return
     */
    SuccessKilled querySuccessKilledWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}
