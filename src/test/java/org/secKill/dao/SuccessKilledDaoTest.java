package org.secKill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author: ligang
 * date: 2018/2/6
 * time: 22:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() throws Exception {
        long id=1000L;
        long phone=13784832725L;
        int updateSuccessedKilled = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println(updateSuccessedKilled);
    }

    /**
     * Preparing:
     * SELECT sk.seckill_id, sk.create_time, sk.state, sk.user_phone, s.seckill_id "seckill.seckill_id", s.name "seckill_name", s.number "seckill_number", s.start_time "seckill_start_time", s.end_time "seckill_end_time", s.create_time "seckill_create_time"
     * FROM success_killed sk
     * INNER JOIN seckill s ON sk.seckill_id = s.seckill_id
     * WHERE sk.seckill_id = ? AND sk.user_phone = ?;
     * @throws Exception
     */
    @Test
    public void querySuccessKilledWithSeckill() throws Exception {
        long id=1001L;
        long phone=15733207536L;
        SuccessKilled successKilled = successKilledDao.querySuccessKilledWithSeckill(id,phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}