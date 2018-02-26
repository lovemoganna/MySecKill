package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SecKillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepatKillException;
import org.seckill.exception.SeckillClosedException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 16:28
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    /**
     * 使用slf4j的日志
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SecKillDao secKillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    /**
     * 与MD5结合的混淆的字符串.
     */
    private final String hx = "aaskg8has$%^&@i1564I^$&*@$!";

    @Override
    public List<Seckill> getAllSeckill() {
        return secKillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getBySeckillId(long seckillId) {
        return secKillDao.queryById(seckillId);
    }

    /**
     * 判断秒杀时间是否开始了
     *
     * @param seckillId
     * @return
     */
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = secKillDao.queryById(seckillId);

        //系统当前时间
        Date nowTime = new Date();
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();

        //1.如果seckill为null,不暴露地址.调用Exposer里面的初始化方法就可以了.
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        //2.如果秒杀时间不符合,也不能进行秒杀.
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime, startTime, endTime);
        }

        //3.md5转换特定字符串的过程,是不可逆的.
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    /**
     * 生成MD5字符串
     *
     * @param seckillId
     * @return
     */
    private String getMd5(long seckillId) {
        String base = seckillId + "/" + hx;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 执行秒杀的实现,主要校验MD5来实现秒杀
     * <p>
     * 使用注解控制事务方法的优点:
     * 1. 开发团队达成一致的约定,明确标注事务方法的编程风格
     * 2. 保证事务方法的执行时间尽可能短,不要穿插其他的网络操作,比如:RPC/HTTP请求.或者玻璃到事务方法外部:就是把它们写到上一层方法
     * 3. 不是所有的方法都使用事务,如:只有一条修改操作,只读操作不需要事务控制.(mysql的行级锁有涉及到)
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws SeckillClosedException
     * @throws RepatKillException
     */
    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, SeckillClosedException, RepatKillException {

        //1.md5匹配不上,系统出现异常
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("Seckill data rewite! 你可能使用了重复秒杀的插件 !=QAQ=!");
        }

        /**
         * 2. md5匹配成功,
         * 执行秒杀逻辑:
         *  --减库存
         *  --写明细
         */
        Date killTime = new Date();
        try {
            //写明细
            int insertState = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //唯一的验证标准就是验证 秒杀商品的ID和用户手机号.
            //之前秒杀成功,state=1.再次秒杀同一seckillId的商品,他就会秒杀不成功了,因为我们设置的是insert ignore,插入就会忽略,insertState返回的就是0.
            if (insertState <= 0) {
                //重复秒杀
                throw new RepatKillException("Seckill repeted!");
            } else {
                //减库存,热点商品竞争发生在这个地方
                int updateCount = secKillDao.reduceNumber(seckillId, killTime);
                //更新数<0,说明减库存失败,没有更新到记录
                if (updateCount <= 0) {
                    //没有更新到记录,秒杀结束,rollback
                    throw new SeckillClosedException("Seckill is closed!");
                } else {
                    //秒杀成功,commit
                    SuccessKilled successKilled = successKilledDao.querySuccessKilledWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        }
        //这些异常的抛出有次序.我们要友好一些,要知道抛出的是哪个部分的异常.
        //重复秒杀的异常
        catch (RepatKillException e1) {
            throw e1;
        }
        //秒杀时间过期的异常
        catch (SeckillClosedException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常都要转换为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    /**
     * 使用Mysql存储过程执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        //校验MD5
        if(md5 == null || !md5.equals(getMd5(seckillId)) ){
            return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime  = new Date();
        HashMap<String, Object> map = new HashMap<>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程,result被赋值
        try {
            secKillDao.killByProcedure(map);
            //获取Result
            Integer result = MapUtils.getInteger(map, "result", -2);

            if(result == 1 ){
                SuccessKilled successKilled = successKilledDao.querySuccessKilledWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilled);
            }else{
                //根据Result去拿我们的秒杀状态.
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }
}
