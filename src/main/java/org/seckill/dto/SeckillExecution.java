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

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
