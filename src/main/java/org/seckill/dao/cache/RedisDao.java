package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author: ligang
 * date: 2018/2/24
 * time: 9:37
 * 缓存优化
 * get from cache
 * if null
 * getdb
 * else
 * put cache
 * locgoin
 */
public class RedisDao {
    private JedisPool jedisPool;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 传入地址&&端口
     *
     * @param ip
     * @param port
     */
    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    /**
     * 通过类的字节码对象创建,
     * 通过类的反射可以拿到类有什么属性,哪些方法.
     * RunTimeSchema就是基于类的属性去做一个模式.
     * 创建对象的时候,会根据模式赋予相应的值.
     */
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
    /**
     * 取出Seckill
     *
     * @param seckillId
     * @return
     */
    public Seckill getSeckill(long seckillId) {
        //Redis缓存的逻辑:先拿到对象,判断对象是否存在,将其反序列化成对象.
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                //前缀+值的模式
                String key = "seckill:" + seckillId;
                //但并没有实现内部序列化操作
                //get-->byte[]-->反序列化-->Object[Seckill]
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    //就将这个字节数组利用protostuff序列化
                    //创建一个空对象
                    Seckill seckill = schema.newMessage();
                    //按照schema把数据传输到空对象里面去
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    //seckill被反序列化,空间压缩到原生jdk处理的十分之一,压缩速度快,节省了CPU.
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * 往里面防止Seckill对象
     *
     * @param seckill
     * @return
     */
    public String putSeckill(Seckill seckill) {
        //set Object[Seckill] --> 序列化-->byte[] 
        Jedis jedis = jedisPool.getResource();
        try {
            try {
                String key="seckill:"+seckill.getseckillId();
                //将其转为字节数组,里面内置了一个缓存器,如果当前对象特别大,会有一个缓冲的过程.
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //经测试,这个字节数组的大小为51
                //超时缓存
                int timeout =60 * 60;
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

}
