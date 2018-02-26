package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepatKillException;
import org.seckill.exception.SeckillClosedException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 10:39
 * 业务接口:站在"使用者"角度设计接口
 * 三个方面:方法定义粒度,参数,返回类型(return 类型/异常)
 */
public interface SeckillService {
    /**
     * 展示所有秒杀记录
     *
     * @return
     */
    List<Seckill> getAllSeckill();

    /**
     * 展示单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getBySeckillId(long seckillId);

    /**
     * 行为接口
     * 秒杀开启时输出秒杀接口地址,
     * 否则输出系统时间和秒杀时间.
     * @param seckillId
     * @return Exposer
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作,需要根据商品Id和用户名来执行操作,
     * 同时对用户的url来源渠道做一次验证.即和之前秒杀开启前的MD5值作比较.
     * @param seckillId
     * @param userPhone
     * @param md5
     * @Return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, SeckillClosedException, RepatKillException;

    /**
     * 主要是完成mysql的存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);

}
