package org.seckill.entity;

import java.util.Date;

/**
 * @author: ligang
 * date: 2018/2/1
 * time: 10:18
 */
public class SuccessKilled {
    private long seckillId;

    private long userPhone;
    /**
     * 状态标识:-1:无效,0:成功,1:已付款,2:已发货
     */
    private int state;

    private Date createTime;

    /**
     * 有一个变通,
     * 秒杀成功后,需要我们拿到一个SecKill的实体.
     *
     * 多对一的复合属性.
     * 一个秒杀Seckill成功对应多个SuccessKilled实体.
     * 需要写出get,set方法,方便数据的存取
     */

    private Seckill seckill;

    public Seckill getSeckill() {
        return seckill;
    }

    public SuccessKilled setSeckill(Seckill seckill) {
        this.seckill = seckill;
        return this;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }

    public long getSeckillId() {
        return seckillId;
    }

    public SuccessKilled setSeckillId(long seckillId) {
        this.seckillId = seckillId;
        return this;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public SuccessKilled setUserPhone(long userPhone) {
        this.userPhone = userPhone;
        return this;
    }

    public int getState() {
        return state;
    }

    public SuccessKilled setState(int state) {
        this.state = state;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public SuccessKilled setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}
