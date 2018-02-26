package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: ligang
 * date: 2018/2/1
 * time: 10:31
 * 需要进行2个操作:减库存和显示明细
 */
public interface SecKillDao {
    /**
     * 减库存
     *
     * @param seckillId
     * @param killTime
     * @return 如果影响行数>1,表示更新的记录行数(如果返回0,说明这条语句没有更新成功.)
     * SQL:
     * update s
     * from seckill s
     * where s.number = s.number -1;
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据Id秒杀商品
     *
     * @param seckillId
     * @return SQL:
     * select *
     * from seckill s
     * where s.seckillId=?
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     *
     * @param limit  取多少条记录
     * @param offset 偏移量
     * @return 商品列表
     * 会用到一个SQL的链接,因为是符合查询(查询秒杀明细(查询秒杀商品))
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String,Object> paramMap);
}