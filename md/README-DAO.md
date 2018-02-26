DAO层开发
==

## 1.数据库设计编码


数据库设计工作:
```androiddatabinding
1.创建数据库seckill
2.使用数据库seckill

3.创建秒杀数据库存表:
    3.1.存储引擎的选用.
    3.2.具体字段的编辑.
    3.3.索引的创建.
4.初始化数据(插入几组数据)  

5.创建用户明细表


秒杀成功明细表里面的主键不使用自增的主键,

而是采用联合主键

同一个用户只可能对同一个库存内的商品做秒杀,
所以设计为
PRIMARY_KEY(seckill_id,user_phone),
```
详情如下:
**!!! 千万注意:表的字段,比如:`seckill_id`里面是tab上面的那个符号,不要弄错了.我使的是5.7版本,还有就是创建时间戳的时候,需要给它设置一个默认值**

### 1.创建秒杀数据库
```androiddatabinding
CREATE TABLE seckill(
  `seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` VARCHAR(120) NOT NULL  COMMENT '商品名称',
  `number` int NOT NULL COMMENT '库存数量',
  `start_time` TIMESTAMP not null ON UPDATE CURRENT_TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀开始时间',
  `end_time` TIMESTAMP not null ON UPDATE CURRENT_TIMESTAMP  DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀结束时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  key idx_start_time(start_time),
  key idx_end_time(end_time),
  key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT ='秒杀库存表';
```
查看表的详细计划:show create table seckill\G;

### 2.插入几组数据
```androiddatabinding
INSERT INTO seckill(name,number,start_time,end_time)
VALUES
('1000元秒杀iPad6',100,'2018-1-31 16:31:00','2018-2-16 16:31:00'),
('500元秒杀iPad7',200,'2018-1-31 16:31:00','2018-2-16 16:31:00'),
('300元秒杀小米4',300,'2018-1-31 16:31:00','2018-2-16 16:31:00'),
('200元秒杀红米note',400,'2018-1-31 16:31:00','2018-2-16 16:31:00');
```         
### 3.创建用户明细表
```androiddatabinding
create table success_killed(
  `seckill_id` bigint NOT NULL COMMENT '秒杀商品Id',
  `user_phone` bigint NOT NULL COMMENT '用户手机号',
  `state` tinyint NOT NULL DEFAULT -1 COMMENT '状态标识:-1:无效,0:成功,1:已付款,2:已发货',
  `create_time` TIMESTAMP NOT NULL DEFAULT current_timestamp COMMENT '创建时间',
  PRIMARY KEY (seckill_id,user_phone),/*联合索引*/
  key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='秒杀成功明细表';

```
现在发现IDEA里面用`show CREATE TABLE success_killed;`不用加上\G;

一般其他的控制台要加上\G;即:`show CREATE TABLE success_killed\G;`

DAO相关接口编码
==

## 1.DAO层接口的设计

总的看来,我们的接口需要减少库存和增加明细的操作.

但是还要更加细致

其中,要注意的是,SuccessSeckilled是一个复合实体,里面有一个实体是Seckill,属于多对一的关系.

也就是同样的商品秒杀成功的话,可能有多个明细.

### 1.SeckillDao接口的设计

1. 根据商品ID和秒杀时间减少库存

2. 根据商品Id秒杀商品

3. 根据偏移量查询秒杀商品列表
```androiddatabinding
package org.seckill.dao;

import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * @author: ligang
 * date: 2018/2/1
 * time: 10:31
 */
public interface SeckillDao {
    /**
     * 减库存
     * @param secKillId
     * @param killTime
     * @return 如果影响行数>1,表示更新的记录行数(如果返回0,说明这条语句没有更新成功.)
     */
    int reduceNumber(long secKillId, Date killTime);

    /**
     * 根据Id秒杀商品
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param limit 取多少条记录
     * @param offet 偏移量
     * @return 商品列表
     * 会用到一个SQL的链接,因为是符合查询(查询秒杀明细(查询秒杀商品))
     */
    List<Seckill> queryAll(int offet,int limit);
}
```
### 2.SuccessKilledDao接口的设计
1. 插入用户秒杀明细

2. 查询携带秒杀商品的明细
```androiddatabinding
package org.seckill.dao;

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
     * @param secKillId
     * @param userPhone
     * @return  插入的行数,就是秒杀成功的记录
     */
    int insertSuccessKilled(long secKillId,long userPhone);

    /**
     * 根据id查询Successkilled并携带Seckill实体
     * @param secKillId
     * @return
     */
    SuccessKilled querySuccessKilledWithSeckill(long secKillId);
}

```

基于MyBatis实现DAO
==

mybatis与hibernate其实都是针对对象关系的映射框架.

把数据库中的东西映射到对象

反过来就把对象中的东西映射到数据库当中

图示:

![](http://upload-images.jianshu.io/upload_images/7505161-946d004c773772cb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 1.mybatis的特点

参数+SQL=Entity/List

### 1.SQL写在的位置

1. XML提供SQL(推荐)

2, 注解提供SQL

### 2.实现DAO接口:

1. Mapper自动实现DAO接口(推荐)
    我知道有一种是通过MyEclipse实现接口的方式,用起来挺爽的.但是不利于我们对设计接口的理解.
    
    关注点:SQL如何编写,如何去设计DAO接口.节省了很多需要维护的程序.所有的实现都是mybatis自动完成.
2. API编程的方式实现DAO接口
    你可能会遗漏一些东西...影响工作效率

## 2.mybatis实现DAO编程

### 1.mybatis官方文档地址
[Mybatis官方链接](http://www.mybatis.org/mybatis-3/zh/index.html)

驼峰命名转换:不用关心列名到属性名的转换了.

配置文件如下:

#### 1.mybatis-config.xml
```androiddatabinding
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--1.配置全局属性-->
<configuration>
    <settings>
        <!--使用JDBC的getGenerateKeys 获取数据库自增主键值,默认是false-->
        <setting name="useGeneratekeys" value="true"/>
        <!--使用列别名替换别名,默认是true
            select name as title from table
            列名name取得的列别名是title,mybatis会自动识别
        -->
        <setting name="useColumnLable" value="true"/>
        <!--开启驼峰命名转换:Table(create_time)转为 Entity(createtime)-->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>
</configuration>
```

### 2.配置文件mapper.xml 的书写

```androiddatabinding
1.引入方法内部的参数
就跟HQL里里面的? 有类似之处
#{xxx}  xxx是方法里面的参数

2.mybatis配置的xml文件内部不能识别的符号的改写方法
>= 
配置文件中不允许有>=符号的出现,允许下面这种写法:
<![CDATA[ <= ]]>
为了防止冲突,就是告诉 <= 不是xml的语法

3.resultType--实体类名 parameterType-参数类型

多个参数并不用加上parameterType

4.解决主键冲突的话,可能出现错误:

需要在insert into 中间加上ignore,即:insert ignore into 这样重复插入的话,就会插入不成功.返回插入的数=0

5.解决mybatis把结果映射到表中同时映射表内部的实体问题

可以利用内连接(inner join)来解决这个问题.

select * 
from s s1
inner join e e1 on s1.id = e1.id 
where s1.id =#{Id}

```
**Mapper下的两个映射文件:**

#### 1.SeckillDao.xml
````androiddatabinding
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="org.seckill.dao.SecKillDao">
    <!--目的:为DAO接口方法提供SQL语句配置-->
    <!--id就是方法名,看你的方法要执行什么操作了-->
    <update id="reduceNumber">
        <!--具体的SQL-->
        UPDATE seckill
        SET number = number-1
        WHERE seckill_id=#{seckillId}
        AND start_time <![CDATA[ <= ]]> #{killTime}
        AND end_time >= #{killTime}
        AND number > 0;
    </update>

    <select id="queryById" resultType="Seckill" parameterType="long">
        SELECT *
        FROM seckill
        WHERE seckill_id=#{seckillId}
    </select>

    <select id="queryAll" resultType="Seckill">
        SELECT *
        FROM seckill
        ORDER BY create_time DESC
        limit #{offset},#{limit}
    </select>
</mapper>
````
#### 2.SuccessKilledDao.xml
```androiddatabinding
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="org.seckill.dao.SuccessKilledDao">
    <insert id="insertSuccessKilled">
        <!--如果主键冲突,报错-->
        insert ignore into success_killed (seckill_id, user_phone,state)
        values (#{seckillId},#{userPhone},0);
    </insert>
    <select id="querySuccessKilledWithSeckill" resultType="SuccessKilled">
        <!--根据id查询Successkilled并携带Seckill实体-->
        <!--如何告诉MyBatis把结果映射到SuccessKilled同时映射SecKill属性-->
        <!--最重要的原因就是:可以自由的控制SQL-->
        SELECT
        sk.seckill_id,
        sk.user_phone,
        sk.create_time,
        sk.state,
        s.seckill_id "seckill.seckill_id",
        s.name "seckill.name",
        s.number "seckill.number",
        s.start_time "seckill.start_time",
        s.end_time "seckill.end_time",
        s.create_time "seckill.create_time"
        FROM success_killed sk
        INNER JOIN seckill s ON sk.seckill_id=s.seckill_id
        WHERE sk.seckill_id=#{seckillId} and sk.user_phone=#{userPhone};
    </select>
</mapper>
```

Mybatis整合Spring理论
==
## 1.目标

```androiddatabinding
1.更少的编码
    只写接口,不写实现类
    接口本身就能说明很多事情
    
    比如:Seckill queryById(long secKillID);
        1.参数 --long
        2.结果集 --SecKill
        3.行为: query
        根据上面的就可以写SQL,配置mapper.xml就可以了.
    
2.更少的配置
        1.别名
               就比如:从resultType="Scekill"来说吧,本来他应该写更长的名字才对,即resultType="org.seckill.dao.SecKillDao"
               但是可以简写的原因就是,mybatis帮我们实现了包扫描,即Package Scan
        2.配置扫描
              1.
               <mapper resource="mapper/SeckillDao.xml"/>               
               <mapper resource="mapper/SuccessKilledDao.xml"/>               
                   ......
                当有很多这样的配置文件的话,我们会很费劲的添加,但mybatis有一个自动扫描配置文件的功能.   
              2.dao实现
              一般就是<bean id = "xxxDao" class ="xxx.xxx.dao"/>的形式配置交给Spring容器管理.
              要是有很多这样的配置文件的时候,我们就需要些很多这样的配置.
              
              mybatis可以自动实现DAO接口,统一叫Mapper,效率非常高,
              
              但是不利于初学者的学习.我之前做的电商项目就是DAO用mybatis自动生成的文件.
              因为出来一堆mapper,就是dao,和一堆映射文件xxxmapper.xml.
              
              自动注入spring容器.
               
                            
3.足够的灵活性
        1.自由定制SQL语句
        2.自由传递传参
        3.结果集自动赋值

自由的传递参数,自由的返回实体的类型

```

**XML提供SQL,DAO接口Mapper.**

这种方式很好

mybatis整合spring编码
==

## 1.spring整合mybatis官方文档
[Spring4.1.7官方版本](https://docs.spring.io/spring/docs/4.1.7.RELEASE/spring-framework-reference/pdf/)

取它的容器头部.
````androiddatabinding
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="..." class="...">
    <!-- collaborators and configuration for this bean go here -->
    </bean>
    <bean id="..." class="...">
    <!-- collaborators and configuration for this bean go here -->
    </bean>
<!-- more bean definitions go here -->
</beans>
````

连接池的配置,

c3p0连接池的配置:comboolPoolDataSource
除了驱动类,URL,user,password之外.还有一些私有配置

`maxPoolSize`,每一个数据库中池子的最大数:,默认是15.
`minPoolSize`,默认是3.

`autoCommitOnClose`

连接池的Connection调用Close的时候,本质上是把连接对象放到池子当中,放到池子的过程当中,c3p0连接池要做一些清理工作.

当close连接的时候,不要commit

`checkoutTimeOut`
当maxPoolSize连接满的时候,等待连接的间隔时间,c3p0默认是0,无线等待.

`acquireRetryAttempts`
获取连接失败重试次数


`<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>`
后边的那个value值就是sqlSessionFactory的名字.

当MapperScannerConfigure启动的时候,可能会出现jdbc.properties里面的东西还没有被加载,不能拿到SQLSessionFactory里面的参数.

当用到mybatis的时候,才会去找对应的SqlSessionFactory.

#### 1.spring-dao.xml配置
````androiddatabinding
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <!--配置整合mybatis过程-->
    <!--1:配置数据库相关参数
        properties的属性: ${url}
    -->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <!--配置连接池属性-->
        <property name="driverClass" value="${jdbc.driver}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
        <!--2:c3p0连接池的私有属性-->
        <property name="maxPoolSize" value="30"/>
        <property name="minPoolSize" value="10"/>
        <!--关闭连接后不自动commit-->
        <property name="autoCommitOnClose" value="false"/>
        <!--获取连接超时时间-->
        <!--property name="checkoutTimeout" value="8000"/-->
        <!--当获取连接失败重试次数-->
        <property name="acquireRetryAttempts" value="2"/>
    </bean>

    <!--3.配置SQLSessionFactory对象-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!--注入数据库连接池-->
        <property name="dataSource" ref="dataSource"/>
        <!--配置mybatis全局配置文件:mybatis-config.xml-->
        <property name="configLocation" value="classpath:mybatis-config.xml"/>
        <!--扫描Entity包 使用别名 org.seckill.entity.Seckill同一的转换为Seckill使用
            有多个包的时候,可以分开写:
            value="org.seckill.entity;org.seckill.entity2"
        -->
        <property name="typeAliasesPackage" value="org.seckill.entity"/>
        <!--扫描SQL配置文件:mapper需要的xml文件-->
        <property name="mapperLocations" value="classpath:mapper/*.xml"/>
    </bean>

    <!--4.配置扫描DAO接口包,动态实现Dao接口,并注入到Spring容器中.
           可以看出这个类专门是扫描mapper的,而在mybatis中mapper就相当于dao
    -->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--注入SQLSessionFactory-->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <!--给出需要扫描Dao接口包-->
        <property name="basePackage" value="org.seckill.dao"/>
    </bean>
</beans>
````

单元测试
==

spring 与junit整合
0. ctrl+shift+t创建单元测试类

1. RunWith(SpringJunit4ClassRunner.class)//junit启动时加载springIOC容器.

2. @Resource不管用,改用@Autowired

3. QueryAll方法测试遇到下面的**绑定参数**异常
```androiddatabinding
org.apache.ibatis.binding.BindingException: Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
```
需要加上@Param("实际形参")
````androiddatabinding
List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
````

SeckillDaoTest测试:
```androiddatabinding
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
```
SuccessKilledTest测试:
```androiddatabinding
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
```
