package org.secKill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SecKillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.Date;
import java.util.List;

/**
 * @author: ligang
 * date: 2018/2/6
 * time: 13:48
 * 配置Spring和junit整合,junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junitspring配置文件的位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {
    //注入Dao
    @Autowired
    private SecKillDao secKillDao;

    /**
     *  Preparing: SELECT seckill.seckill_id,seckill.name,seckill.number,seckill.start_time,seckill.end_time,seckill.create_time
     *  FROM seckill
     *  WHERE seckill.seckill_id = ?;
     * @throws Exception
     */
    @Test
    public void queryById() throws Exception {
        long id=1000L;
        Seckill seckill = secKillDao.queryById(id);
        System.out.println(seckill);
    }


    @Test
    public void reduceNumber() throws Exception {
        Date killTime=new Date();
        int updateNumber= secKillDao.reduceNumber(1000L, killTime);
        System.out.println("updateNumber="+updateNumber);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void queryAll() throws Exception {
        List<Seckill> seckills = secKillDao.queryAll(2, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }
    }

}