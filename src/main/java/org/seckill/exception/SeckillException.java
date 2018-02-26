package org.seckill.exception;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 15:42
 * 前两个异常都属于秒杀异常,所以继承此类即可.
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
