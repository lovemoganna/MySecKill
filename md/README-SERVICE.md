service层开发
==

## 1. 思考DAO层做的事

创建数据库,编写接口,书写mapper.xml(SQL),

配置mybatis-config.xml,整合spring-dao.xml.

总的来说就是接口设计和SQL编写.

代码和SQL进行分离,方便Review.

DAO拼接等逻辑在SERVICE层完成.

DAO层也成为数据访问层,也就是对mysql等远程系统的操作.

## 2.service层包分类

dto --数据传输层,关于web和service的数据传递.

entity --业务实体的存放.

exception --秒杀结束,秒杀存货不足.

enum --枚举类

### 1.service层接口的设计
```androiddatabinding
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
     * @param seckillId
     * @return 展示单个秒杀记录
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
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @Return SeckillException
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, SeckillClosedException, RepatKillException;
}

```
### 2.数据传输数据的封装(dto)

#### 1.Exposer-暴露秒杀信息的的封装类
```androiddatabinding
package org.seckill.dto;

import java.util.Date;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 10:46
 * 暴露秒杀地址DTO
 */
public class Exposer {
    //是否开启秒杀
    public boolean exposed;

    //MD5加密
    private String md5;

    //商品ID
    private long seckillId;

    //系统当前时间(毫秒),方便用户浏览器控制服务器的时间.
    private Date now;

    //秒杀开始时间
    private Date start;

    //秒杀结束时间
    private Date end;

    /**
     * 不同的构造方法方便对象的初始化.
     * 秒杀成功用到的构造方法
     * @param exposed
     * @param md5
     * @param seckillId
     */
    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    /**
     * 不符合条件,用到的构造方法
     * @param exposed
     * @param seckillId
     * @param now
     * @param start
     * @param end
     */
    public Exposer(boolean exposed,long seckillId,Date now, Date start, Date end) {
        this.exposed=exposed;
        this.seckillId=seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    /**
     * 秒杀开始之前,需要对秒杀地址隐藏,加密.
     * @param exposed
     * @param seckillId
     */
    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public boolean isExposed() {
        return exposed;
    }

    public Exposer setExposed(boolean exposed) {
        this.exposed = exposed;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public Exposer setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public Exposer setSeckillId(long seckillId) {
        this.seckillId = seckillId;
        return this;
    }

    public Date getNow() {
        return now;
    }

    public Exposer setNow(Date now) {
        this.now = now;
        return this;
    }

    public Date getStart() {
        return start;
    }

    public Exposer setStart(Date start) {
        this.start = start;
        return this;
    }

    public Date getEnd() {
        return end;
    }

    public Exposer setEnd(Date end) {
        this.end = end;
        return this;
    }
}
```
#### 2.SeckillExecution--秒杀执行结果的封装类
```androiddatabinding
package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 15:13
 * 秒杀执行结果
 */
public class SeckillExecution {

    private long seckillId;
    //秒杀执行结果状态
    private int state;
    //状态表示
    private String stateInfo;
    //秒杀成功对象
    private SuccessKilled successKilled;

    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
    }

    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public SeckillExecution setSeckillId(long seckillId) {
        this.seckillId = seckillId;
        return this;
    }

    public int getState() {
        return state;
    }

    public SeckillExecution setState(int state) {
        this.state = state;
        return this;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public SeckillExecution setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
        return this;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public SeckillExecution setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
        return this;
    }
}
```
## 3.秒杀接口的实现--Impl
```androiddatabinding
package org.seckill.service.impl;

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
import org.springframework.util.DigestUtils;

import java.util.Date;
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
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws SeckillClosedException
     * @throws RepatKillException
     */
    @Override
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
            //减库存
            int updateCount = secKillDao.reduceNumber(seckillId, killTime);
            //更新数<0,说明减库存失败,没有更新到记录
            if (updateCount < 0) {
                //秒杀过期
                throw new SeckillClosedException("Seckill is closed!");
            }
            //写明细
            int insertState = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //唯一的验证标准就是验证 秒杀商品的ID和用户手机号.
            //之前秒杀成功,state=1.再次秒杀同一seckillId的商品,他就会秒杀不成功了,因为我们设置的是insert ignore,插入就会忽略,insertState返回的就是0.
            if (insertState <= 0) {
                //重复秒杀
                throw new RepatKillException("Seckill repeted!");
            } else {
                //秒杀成功
                SuccessKilled successKilled = successKilledDao.querySuccessKilledWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS, successKilled);
                //TODO
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
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常都要转换为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
```       
#### 1.数据字典的形成

根据条件判断需要抛出的stateInfo提示信息,直接在里面写,可用性很差,所以我们采用枚举类的方式来规范statInfo的分配.

枚举类SeckillStatEnum
```androiddatabinding
/**
 * 使用枚举类表述常量数据字典
 */
public enum SeckillStatEnum {
    SUCCESS(1,"秒杀成功!"),
    END(0,"秒杀结束!"),
    REPEAT_KILL(-1,"重复秒杀!"),
    INNER_ERROR(-2,"系统异常!"),
    DATA_REWRITE(-3,"数据篡改!");
    
    private int state;
    private String stateInfo;

    SeckillStatEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    //此静态方法迭代所有的类型
    public static SeckillStatEnum stateOf(int index) {
        for (SeckillStatEnum state : values()) {
            //values()用来拿到所有类型
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}

```
基于以前的构造方法改造成含有枚举的表示方法.

秒杀成功用到的构造方法:
改造前
```androiddatabinding
   public SeckillExecution(long seckillId, int state, String stateInfo, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = state;
        this.stateInfo = stateInfo;
        this.successKilled = successKilled;
    }
```
改造后
```androiddatabinding
 public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
         this.seckillId = seckillId;
         this.state = statEnum.getState();
         this.stateInfo = statEnum.getStateInfo();
     }
 
     public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
         this.seckillId = seckillId;
         this.state = statEnum.getState();
         this.stateInfo = statEnum.getStateInfo();
         this.successKilled = successKilled;
     }
```

#### 2.异常的处理--Exception
##### 1.秒杀异常
```androiddatabinding
package org.seckill.exception;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 15:42
 * 后两个异常都属于秒杀异常,所以继承此类即可.
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
##### 2.重复秒杀异常
```androiddatabinding
package org.seckill.exception;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 15:18
 * 重复秒杀异常(运行期异常)
 * java的异常一般分为运行期异常和编译期异常
 * spring声明式事务只接收运行期异常事务回滚策略.抛出非声明式异常,spring不会对其进行事务回滚.
 */
public class RepatKillException extends SeckillException{
    public RepatKillException(String message) {
        super(message);
    }
    public RepatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
##### 3.秒杀关闭异常
```androiddatabinding
package org.seckill.exception;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 15:39
 * 秒杀时间关闭异常
 */
public class SeckillClosedException extends SeckillException {
    /**
     * 运行期异常的继承,但是他们都属于秒杀异常
     * @param message
     */
    public SeckillClosedException(String message) {
        super(message);
    }

    public SeckillClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
## 4.使用Spring托管SERVICE依赖理论
### 1.SpringIOC的概念

IOC-依赖注入

SpringIOC的过程会创建一个对象工厂,还会进行依赖管理,最终给一个一致的访问接口.

SuccessKillSERVICE完整的实例如下:

![](http://upload-images.jianshu.io/upload_images/7505161-63c952d8ab503399.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**使用IOC的好处:**

1. 对象创建统一托管
2. 规范的声明周期管理
3. 灵活的依赖注入
4. 一致的获取对象(默认是单例)

SpringIOC的注入方式和场景

三种方式实现如下

![](http://upload-images.jianshu.io/upload_images/7505161-7337c3d038b7ae0e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我们的IOC使用:

```androiddatabinding
XML配置
package-scan(包扫描)
Annotation注解
```

### 2.springIOC的注解用法

1. 先进行包扫描

在spring-service.xml里面输入
```androiddatabinding
<!--扫描Service包下所有使用注解的类型-->
    <context:component-scan base-package="org.seckill.service"/>
```
2. 注解部分

- @Component  如果你不知道该写@Service/@Controller的时候,@Component就是一个比较笼统的Spring容器在一个组件实例.
- @Service
- @Controller
- dao里面的所有内容都会通过mapper.xml文件初始化放进Spring容器中,然后spring容器中回去Dao的实例,注入到相关的ervice下面
- @Autowired(自动注入,就不用自己new实现类了) / @Resource / @Inject 注入方式

### 3.spring的声明式事务

声明式事务就是不关心事务的开启或者提交.而是交给第三方框架来实现的.解脱事务代码.

1.执行事务的步骤
```
开启事务
修改SQL-1(更新/增加/删除)
修改SQL-2
修改SQL-3
提交或者回滚
```
2.声明式事务的使用方式
```
ProxyFactoryBean + XML  ----  早期的使用方式(2.0)
tx:advice+aop命名空间    ----  一次配置永久生效(使用最多的方式,不太关心事务是如何操作的)
注解@Transaction        -----  注解控制(推荐使用,对我们有利)
```
3.事务方法嵌套

声明式事务独有的概念
```androiddatabinding
传播行为 --- spring默认是`propagation_required`,
意思就是说当有一个新的事务加入中来,会直接加入到已经存在的事务,
如果有事务存在就会直接加入到原有的事务当中,如果没有就会创建一个新的事务

```
4.什么时候回滚事务
```androiddatabinding
1. 抛出运行期异常(RuntimeException)可以执行回滚,非运行期异常可能不会全部回滚.
2. 小心不当的try-catch,要是你使用try-catch包括一个有异常的程序,spring就会感知不到它会出现异常.
```


- 声明式事务的配置:
```androiddatabinding
 <!--2.配置事务管理器,我们使用的是jdbc的事务管理器-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!--注入数据库连接池-->
        <property name="dataSource" ref="dataSource"/>

    </bean>
    
    <!--3.配置基于声明式注解的声明式事务
        当你输入tx:annotation-driven的时候,它会自动的把相关的schema加上
        默认使用注解来管理事务行为
    -->
    <tx:annotation-driven transaction-manager="transactionManager"/>
```

- @Transaction的使用
```androiddatabinding

 使用注解控制事务方法的优点:
      1. 开发团队达成一致的约定,明确标注事务方法的编程风格
      2. 保证事务方法的执行时间尽可能短,不要穿插其他的网络操作,比如:RPC/HTTP请求.或者剥离到事务方法外部:就是把它们写到方法的上一层.
      3. 不是所有的方法都使用事务,如:只有一条修改操作,只读操作不需要事务控制.(mysql的行级锁有涉及到)
```

### 4.service测试

[slf4j接口的实现logback官网配置文件](https://logback.qos.ch/manual/configuration.html)

日志logback.xml的配置

```androiddatabinding
<?xml version="1.0" encoding="UTF-8" ?>
<!--打印到控制台,默认级别是debug,时间,线程和日志的格式-->
<configuration debug="true">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are  by default assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    
    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

测试类:
```androiddatabinding
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
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    //使用日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;
    @Test
    public void getAllSeckill() throws Exception {
        List<Seckill> list = seckillService.getAllSeckill();
        logger.info("list ={},",list);

    }

    @Test
    public void getBySeckillId() throws Exception {
        Seckill seckill = seckillService.getBySeckillId(1000L);
        logger.info("list ={},",seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        Exposer exposer = seckillService.exportSeckillUrl(1000L);
        logger.info("exposer ={},",exposer);
        /**
         *  exposer =Exposer{exposed=true, md5='d592364bb958482949d97e04131f4b2e', seckillId=1000, now=null, start=null, end=null},
         */
    }

    @Test
    public void executeSeckill() throws Exception {
        long id =1000L;
        long phone= 1245564659;
        String md5="d592364bb958482949d97e04131f4b2e";
         try {
             SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
             logger.info("seckillExecution:" + seckillExecution);
         }
         catch(RepatKillException e){
             logger.error(e.getMessage());
         }
         catch (SeckillClosedException e){
             logger.error(e.getMessage());
         }
        /**
         * 再一次执行秒杀会出现运行期异常.
         * org.seckill.exception.RepatKillException: Seckill repeted!
         */
    }
    @Test
    public void testSeckillLogic() throws Exception{
        long id =1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer ={},",exposer);
        if(exposer.isExposed()){
            //开始执行秒杀
            long phone= 1245564359;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("seckillExecution:" + seckillExecution);
            }
            catch(RepatKillException e){
                logger.error(e.getMessage());
            }
            catch (SeckillClosedException e){
                logger.error(e.getMessage());
            }
        }
        else{
            logger.warn("exposer={}",exposer);
        }
    }
//id=1001 的测试 seckillExecution:SeckillExecution{seckillId=1001, state=1, stateInfo='秒杀成功!', successKilled=SuccessKilled{seckillId=1001, userPhone=1245564359, state=0, createTime=Tue Feb 20 13:19:46 CST 2018}}
//id=1000 的测试 21:22:24.838 [main] WARN  o.secKill.service.SeckillServiceTest - exposer=Exposer{exposed=false, md5='null', seckillId=1000, now=Tue Feb 20 21:22:24 CST 2018, start=Tue Feb 20 12:41:15 CST 2018, end=Tue Feb 20 12:41:15 CST 2018}
}
```