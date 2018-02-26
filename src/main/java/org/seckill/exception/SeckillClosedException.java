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
