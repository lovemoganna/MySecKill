package org.seckill.exception;

/**
 * @author: ligang
 * date: 2018/2/7
 * time: 15:18
 * 重复秒杀异常(运行期异常)
 * java的异常一般分为运行期异常和编译期异常
 * spring声明式事务只接收运行期异常事务回滚策略.抛出非声明式异常,spring不会对其进行事务回滚.
 */
public class RepatKillException extends SeckillException{
    public RepatKillException(String message) {
        super(message);
    }
    public RepatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
