package leonardo.ezio.personal.job.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:00
 */
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRegisterJob {

}
