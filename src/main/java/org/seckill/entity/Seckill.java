package org.seckill.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: ligang
 * date: 2018/2/1
 * time: 10:09
 */

/**
 * 秒杀实体类
 */
public class Seckill  {

    private long seckillId;

    private String name;

    private Integer number;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    @Override
    public String toString() {
        return "Seckill{" +
                "seckillId=" + seckillId +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                '}';
    }

    public long getseckillId() {
        return seckillId;
    }

    public Seckill setseckillId(long seckillId) {
        this.seckillId = seckillId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Seckill setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public Seckill setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Seckill setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Seckill setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Seckill setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}
