SecSkillDemo
==


## 1.涉及的内容:

1.Mysql

表设计
SQL技巧
事务和行级锁

2.Mybatis

dao层的设计和开发

mybatis的合理使用

mybatis与Spring整合

3.Spring

Spring IOC整合Service

声明式事务运用

4.SpringMVC

Restful接口设计和使用

框架运作流程

Controller开发技巧

5.前端

交互设计

Bootstrap

JQuery,Ajax

6.高并发

优化,及优化思路.

## 2.创建maven项目

mvn命令创建项目

```
mvn archetype:generate  -DarchetypeCatalog=internal -DgroupId=org.seckill -DartifactId=secKill -DarchetypeArtifactId=maven-archetype-webapp
``` 

[创建项目更加详细的链接](http://blog.csdn.net/smilecall/article/details/54345702)

值得注意的是,webapp下面的web.xml过时了不能使用,

我们可以拿tomcat8里面的webapp/examples下面的web.xml的表头来操作:

```androiddatabinding
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                      http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1"
         metadata-complete="true">
</web-app>
```
## 3.pom.xml的配置

```androiddatabinding
1.junit3使用的是编程式的测试,junit4使用的是声明式(注解)的测试
2.java中常用的日志:slf4j,log4j,logback,common-logging
       sfl4j:是规范/接口
       
       日志实现:log4j,logback,common-logging
       使用:slf4j + logback
3.数据库的相关组件
4.mybatis的相关组件
5.spring相关组件

```
pom.xml
````androiddatabinding
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.seckill</groupId>
  <artifactId>secKill</artifactId>
  <packaging>war</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>secKill Maven Webapp</name>
  <url>http://maven.apache.org</url>
  <dependencies>
    <!--0.使用junit4,采用声明注解方式测试-->
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>

    <!--1.日志选用slf4j和logback-->
    <!--日志使用slf4j-->
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>1.7.12</version>
    </dependency>

    <!--实现了logback核心的功能-->
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-core</artifactId>
      <version>1.1.1</version>
    </dependency>

    <!--实现slf4j接口并整合-->
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.1.1</version>
    </dependency>

    <!--2.数据库相关的依赖-->
    <!--数据库驱动-->
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <scope>runtime</scope>
      <version>5.1.35</version>
    </dependency>

    <!--数据库连接池c3p0-->
    <dependency>
      <groupId>c3p0</groupId>
      <artifactId>c3p0</artifactId>
      <version>0.9.1.2</version>
    </dependency>

    <!--3.dao框架:mybatis依赖-->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.3.0</version>
    </dependency>

    <!--mybatis自身实现的spring整合依赖-->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis-spring</artifactId>
      <version>1.2.3</version>
    </dependency>

    <!--4.Service Web相关依赖-->
    <dependency>
      <groupId>taglibs</groupId>
      <artifactId>standard</artifactId>
      <version>1.1.2</version>
    </dependency>

    <dependency>
      <groupId>jstl</groupId>
      <artifactId>jstl</artifactId>
      <version>1.2</version>
    </dependency>

    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.5.4</version>
    </dependency>

    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>javax.servlet-api</artifactId>
      <version>3.1.0</version>
    </dependency>

    <!--5.Spring依赖-->

    <!--5.1.spring核心依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-core</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>

    <!--5.2 spring的IOC-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-beans</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>

    <!--5.3 spring的包扫描.IOC拓展用到的依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>

    <!--5.4 spring Dao层的依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>

    <!--5.5 spring Transaction的依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-tx</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>

    <!--5.6 spring Web的依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>

    <!--5.6 spring Test的相关依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-test</artifactId>
      <version>4.1.7.RELEASE</version>
    </dependency>

  </dependencies>
  <build>



    <finalName>secKill</finalName>
  </build>
</project>

````

## 4.秒杀业务的分析

![](http://upload-images.jianshu.io/upload_images/7505161-5d7a0f57c7897ae1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可知,秒杀业务的核心是对库存的处理.

用户针对库存业务分析:

![](http://upload-images.jianshu.io/upload_images/7505161-5befd8c4cfdc0171.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

什么是购买行为?

记录秒杀成功信息

![](http://upload-images.jianshu.io/upload_images/7505161-e4c7fac05c5fb225.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

如果没有事务存在,可能会出现:
```androiddatabinding

1. 减库存没有购买明细,

2. 记录了明细没有减库存,

3. 超卖/少卖
```
故障责任...

数据落地:

mysql  VS nosql

nosql对事务的支持不尽如意,但是对高性能,高可用支持非常棒.

事务机制仍然是目前最可靠的落地方案.mysql内置的事务机制很可靠.

## 5.秒杀业务的难点

![](http://upload-images.jianshu.io/upload_images/7505161-3419dc842b5f4b19.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

难点在与竞争.

**反映在mysql中是事务和行级锁.**

我们做的事务流程:

````androiddatabinding
Start transaction 

Update 库存数量(竞争发生在这一块)

Insert购买明细

Commit
````

![](http://upload-images.jianshu.io/upload_images/7505161-42ef80fb34c43437.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可见当所有人秒杀一件商品时,执行同一个SQL语句,由于采用的是行级锁,所以每次只能执行一条SQL语句.

现在看来,秒杀的难点就是如何**高效的**处理竞争了.

## 6.秒杀功能

````androiddatabinding
1. 秒杀接口暴露
2. 执行秒杀
3. 相关查询
````
代码开发

````androiddatabinding
1. DAO设计编码
   包括数据库的表设计,DAO的接口,mybatis如何去实现DAO.
2.Service设计编码
    Spring管理Service,声明式事务去标注方法是事务操作,简化事务控制
3.Web设计编码    
````

