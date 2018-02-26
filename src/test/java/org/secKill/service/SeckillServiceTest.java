package org.secKill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepatKillException;
import org.seckill.exception.SeckillClosedException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.lang.annotation.Repeatable;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author: ligang
 * date: 2018/2/20
 * time: 18:12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    //使用日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getAllSeckill() throws Exception {
        List<Seckill> list = seckillService.getAllSeckill();
        logger.info("list ={},", list);

    }

    @Test
    public void getBySeckillId() throws Exception {
        Seckill seckill = seckillService.getBySeckillId(1000L);
        logger.info("list ={},", seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        Exposer exposer = seckillService.exportSeckillUrl(1000L);
        logger.info("exposer ={},", exposer);
        /**
         *  exposer =Exposer{exposed=true, md5='d592364bb958482949d97e04131f4b2e', seckillId=1000, now=null, start=null, end=null},
         */
    }

    @Test
    public void executeSeckill() throws Exception {
        long id = 1000L;
        long phone = 1245564659;
        String md5 = "d592364bb958482949d97e04131f4b2e";
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
            logger.info("seckillExecution:" + seckillExecution);
        } catch (RepatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillClosedException e) {
            logger.error(e.getMessage());
        }
        /**
         * 再一次执行秒杀会出现运行期异常.
         * org.seckill.exception.RepatKillException: Seckill repeted!
         */
    }

    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer ={},", exposer);
        if (exposer.isExposed()) {
            //开始执行秒杀
            long phone = 1245564359;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("seckillExecution:" + seckillExecution);
            } catch (RepatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillClosedException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.warn("exposer={}", exposer);
        }
    }
//id=1001 的测试 seckillExecution:SeckillExecution{seckillId=1001, state=1, stateInfo='秒杀成功!', successKilled=SuccessKilled{seckillId=1001, userPhone=1245564359, state=0, createTime=Tue Feb 20 13:19:46 CST 2018}}
//id=1000 的测试 21:22:24.838 [main] WARN  o.secKill.service.SeckillServiceTest - exposer=Exposer{exposed=false, md5='null', seckillId=1000, now=Tue Feb 20 21:22:24 CST 2018, start=Tue Feb 20 12:41:15 CST 2018, end=Tue Feb 20 12:41:15 CST 2018}

    /**
     * 测试mysql存储过程
     */
    @Test
    public void executeSeckillByProcedure() {
        long id = 1003;
        long phone = 1358963569;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            //开始执行秒杀
            String md5 = exposer.getMd5();
            if (md5 != null) {
                SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(id, phone, md5);
                logger.info(seckillExecution.getStateInfo());
            }
        }
    }
}