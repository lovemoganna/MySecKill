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

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}





















