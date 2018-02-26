## 主要内容

1. 前端交互设计
2. Restful
3. springmvc
4. bootstrap+jquery

## 前端交互设计

1. 产品
2. 前端
3. 后端

![](http://upload-images.jianshu.io/upload_images/7505161-8aad1b29e7b4525e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


详情页流程逻辑

![](http://upload-images.jianshu.io/upload_images/7505161-e6263a73fb5ef628.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## Restful接口设计

### 什么是Restful

```androiddatabinding
REST是英文representational state transfer(表象性状态转变)或者表述性状态转移;
Rest是web服务的一种架构风格;
使用HTTP,URI,XML,JSON,HTML等广泛流行的标准和协议;轻量级,跨平台,跨语言的架构设计;
它是一种设计风格,不是一种标准,是一种思想.

Restful兴起于Rails,是一种优雅的URL表述方式,资源的状态和状态转移.
```

### Rest架构的风格
Rest架构的主要原则
```androiddatabinding
    网络上的所有事物都被抽象为资源

    每个资源都有一个唯一的资源标识符

    同一个资源具有多种表现形式(xml,json等)

    对资源的各种操作不会改变资源标识符

    所有的操作都是无状态的

    符合REST原则的架构方式即可称为RESTful
```

### Restful的示例

```androiddatabinding
GET   /Seckill/list   风格友好
POST /Seckill/execute/{seckillId} 风格不友好 
POST /Seckill/{seckillId}/execute 风格友好

GET /seckill/delete/{id} 风格不友好
GET /seckill/{id}/delete 风格友好

```
### Restful规范

```androiddatabinding
GET     -->查询操作
POST    -->添加/修改操作
PUT     -->修改操作
DELETE  -->删除操作
```
图示:

![](http://img.blog.csdn.net/20170625151347639?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY2hlbnhpYW9jaGFu/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

HTTP状态码:

![](http://img.blog.csdn.net/20170625152145836?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY2hlbnhpYW9jaGFu/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
### springmvc中实现Restful服务

```angular2html
SpringMVC实现restful服务:
SpringMVC原生态的支持了REST风格的架构设计
所涉及到的注解:
--@RequestMapping
--@PathVariable
--@ResponseBody
```

### url的设计

```angular2html
1. /模块/资源/{标示}/集合1/...
良好的表示如下:
    2. /user/{uid}/friends    -->好友列表
    3. /user/{uid}/followers  -->关注者列表
```

**秒杀API的URL设计**
```
GET /seckill/list                   秒杀列表
GET /seckill/{id}/detail            详情页面
GET /seckill/time/now               系统时间
POST /seckill/{id}/exposer          暴露秒杀
POST /seckill/{id}/{md5}/execution  执行秒杀     
```

## SpringMVC的运行流程

### 我们始终围绕着Handler开发,如下:


![](http://upload-images.jianshu.io/upload_images/7505161-a7b951d96de382f4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### SpringMVC的运行流程

```
1.用户发送请求到DispatchServlet

2.DispatchServlet根据请求路径查询具体的Handler

3.HandlerMapping返回一个HandlerExcutionChain给DispatchServlet

　HandlerExcutionChain：Handler和Interceptor集合

4.DispatchServlet调用HandlerAdapter适配器

5.HandlerAdapter调用具体的Handler处理业务

6.Handler处理结束返回一个具体的ModelAndView给适配器

   ModelAndView:model-->数据模型，view-->视图名称

7.适配器将ModelAndView给DispatchServlet

8.DispatchServlet把视图名称给ViewResolver视图解析器

9.ViewResolver返回一个具体的视图给DispatchServlet

10.渲染视图

11.展示给用户

```
图示:

![](http://upload-images.jianshu.io/upload_images/7505161-df43c8d02cff87c3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### Http处理地址映射原理

![](http://upload-images.jianshu.io/upload_images/7505161-df748190bd76bbf0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 注解映射示例
```
@RequestMapping注解:
1. 支持标准的URL
2. Ant风格URL(以?和** 等字符)
3. 带(XXX)占位符的URL

for example:
1. /user/*/creation
匹配 /user/aaa/creation,/user/bbb/creation等URL
2. /user/**/creation 
匹配 /usr/creation,  /user/aaa/bbb/creation等URL
3. /user/{userId}
匹配/user/123,/user/abc等URL
4. /company/{companyId}/user/{userId}/detail
匹配/company/123/user/456/detail等URL
```

## 请求方法细节的处理

包括:
```
1. 请求参数的绑定
2. 请求方式限制
3. 请求转发和重定向
4. 数据模型赋值
5. 返回JSON数据
6. cookie访问
```

### 参数绑定的示例
```
@RequestMapping(value="/{seckillId}/detail",mnethod=RequestMethod.GET)
public String detail(@PathVariable("seckillId")Long seckillId,Model model){
    if(seckillId == null){
        return "redirect:/seckill/list";
    }
    Seckill seckill=SeckillService.getById|(seckillId);
    if(seckill == null){
        return "forward:/seckill/list"; 
    }
    model.addAttribute("seckill",seckill);
    return "detail";//view
}
```

### 返回json数据的示例
```
@RequestMapping(value="/{seckillId}/{md5}/execution",method=RequestMethod.POST,produces={"application/json;charset=UTF-8"})
@ResponseBody
public SeckillResult<Exposer> exposerSeckillURL(@PathVariable("id")long id){
 SeckillResult<Exposer> result;
 try{
    Exposer exposer =...
 }
 catch(Exception e){
    logger.error(e.getMessage(),e);
   result=new SeckillResult<Exposer>(false,e.getMessage());
 }
 return result;
}
```

### Cookie访问的示例
@CookieValue(value="killPhone",required=false)long phone)

如果请求的RequestMapping里面没有包含killPhone的Cookie,SpringMVC框架就会报错,所以要设置成false.具体的判断要在程序里面判断.

```
@RequestMapping(value="/{seckillId}/{md5}/execution",method=RequestMethod.POST)
public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId")long seckillId,@PathVariable("md5")String md5,@CookieValue(value="killPhone",required=false)long phone){
    if(phone ==null ){
        return new SeckillResult<SeckillExecution>(false,"电话未注册");
    }
    SeckillExecution execution=seckillService.executeSeckillByProcedure(seckillId,md5,phone);
    SeckillResult<SeckillExecution> result=new SeckillResult<SeckillExecution>(true,execution);
    return result;
}
``` 

### web.xml的配置
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                      http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1"
         metadata-complete="true">
<!--配置DispactherServlet-->
    <servlet>
        <servlet-name>seckill-dispacther</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!--配置springmvc需要加载的配置文件-->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring/spring-*.xml</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>seckill-dispacther</servlet-name>
        <!--默认匹配所有的请求-->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    
</web-app>

```

### spring-web.xml的配置
```androiddatabinding
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/mvc
http://www.springframework.org/schema/mvc/spring-mvc.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
    <!--配置SpringMVC-->
    <!--1.开启SpringMVC注解模式-->
    <!--简化配置:
        1. 自动注册DefaultAnnotationHandlerMapping,AnnotationMethodHandlerDdapter
        2. 提供一系列:数据绑定,数字和日期.和Format @NumberFormat,@DataTimeFormat,xml,json默认读写支持
    -->
    <mvc:annotation-driven/>
    <!--2.静态资源默认Servlet配置
        1. 加入对静态资源的处理:js,gif,png
        2. 允许使用"/"做整体映射
    -->
    <mvc:default-servlet-handler/>
    <!--3.配置jsp,显示ViewResolver-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value="./jsp"/>
    </bean>
    <!--4.扫描web相关的bean-->
    <context:component-scan base-package="org.seckill.web"/>
</beans>
```

## 书写秒杀的Controller

SeckillController
```androiddatabinding
url的格式:     url:/模块/资源/{id}/细分   /seckill/{id}/list
```

```androiddatabinding
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getList(Model model) {
        List<Seckill> list = seckillService.getAllSeckill();
        model.addAttribute("list", list);
        //list.jsp + model = ModelAndView
        return "list";//WEB-INF/jsp/"list".jsp
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String getDetail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getBySeckillId(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    //ajax  json
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            return new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillResult<Exposer>(false, e.getMessage());
        }
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {
            "application/json; charset=utf-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5, @CookieValue(value = "killPhone", required = false) Long phone) {
        // springmvc valid
        if (phone == null) {
            return new SeckillResult<>(false, "未注册");
        }
        try {
            // 存储过程调用
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepatKillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (SeckillClosedException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }
}

```

## 一些插件的CDN 

[CDN查询地址](http://www.bootcdn.cn/)
```androiddatabinding
jquery-cookie

https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.js

jquery-countdown

https://cdn.bootcss.com/jquery-countdown/2.0.2/jquery.countdown-ar.js
```

**切记**
```androiddatabinding
必须使用这种方法书写
<script src="xxx" type="text/javascript"></script>
如果采用下面的写法,浏览器不会加载资源
<script src="xxx" type="text/javascript"/>
```

## 总结
 
 Bootstrap和JS最费劲
 
 DTO传输数据
 
 注解映射驱动
 
 