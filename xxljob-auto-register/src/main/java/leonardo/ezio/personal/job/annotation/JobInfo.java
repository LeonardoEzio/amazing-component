package leonardo.ezio.personal.job.annotation;

import leonardo.ezio.personal.job.constant.BlockStrategy;

import java.lang.annotation.*;

/**
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:00
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JobInfo {

    /**
     * 周期表达式
     * */
    String corn();

    /**
     * 阻塞策略
     * */
    BlockStrategy blockStrategy() default BlockStrategy.SERIAL_EXECUTION;

    /**
     * 超时时间
     * */
    long timeout() default 0L;

    /**
     * 重试次数
     * */
    int maxRetryCount() default 0;

    /**
     * 任务描述
     * */
    String desc();


}
