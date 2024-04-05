package leonardo.ezio.personal.job.processor;

import com.xxl.job.core.handler.annotation.XxlJob;
import leonardo.ezio.personal.job.annotation.AutoRegisterJob;
import leonardo.ezio.personal.job.annotation.JobInfo;
import leonardo.ezio.personal.job.entity.Job;
import leonardo.ezio.personal.job.helper.XxlJobAdminHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:00
 */
@Component
@ConditionalOnBean({XxlJobAdminHelper.class})
public class XxlJobAutoRegisterProcessor implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(XxlJobAutoRegisterProcessor.class);

    private ApplicationContext applicationContext;

    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> xxlJobBeans = applicationContext.getBeansWithAnnotation(AutoRegisterJob.class);

        Map<Job, Boolean> jobBooleanMap = new HashMap<>();
        xxlJobBeans.forEach((k, v) -> {
            Method[] methods = v.getClass().getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(JobInfo.class) && method.isAnnotationPresent(XxlJob.class)) {
                    JobInfo jobInfoAnnotation = method.getAnnotation(JobInfo.class);
                    XxlJob xxlJobAnnotation = method.getAnnotation(XxlJob.class);
                    Job job = getJob(jobInfoAnnotation, xxlJobAnnotation);
                    jobBooleanMap.put(job, jobInfoAnnotation.autoStart());
                }
            }
        });

        LOGGER.info("Begin Add Job ! Job Info : {}", jobBooleanMap);
        XxlJobAdminHelper xxlJobAdminHelper = applicationContext.getBean(XxlJobAdminHelper.class);
        xxlJobAdminHelper.addJob(jobBooleanMap);
    }

    private Job getJob(JobInfo jobInfoAnnotation, XxlJob xxlJobAnnotation) {
        Job job = new Job();

        job.setJobCron(jobInfoAnnotation.corn());
        job.setExecutorBlockStrategy(jobInfoAnnotation.blockStrategy().getValue());
        job.setExecutorTimeout(jobInfoAnnotation.timeout());
        job.setExecutorFailRetryCount(jobInfoAnnotation.maxRetryCount());
        job.setJobDesc(job.getJobDesc());

        job.setExecutorHandler(xxlJobAnnotation.value());

        return job;
    }

}
