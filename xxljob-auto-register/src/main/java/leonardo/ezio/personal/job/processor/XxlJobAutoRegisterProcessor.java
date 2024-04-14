package leonardo.ezio.personal.job.processor;

import com.xxl.job.core.handler.annotation.XxlJob;
import leonardo.ezio.personal.job.annotation.AutoRegisterJob;
import leonardo.ezio.personal.job.annotation.JobInfo;
import leonardo.ezio.personal.job.config.XxlJobExecutorConfiguration;
import leonardo.ezio.personal.job.entity.Job;
import leonardo.ezio.personal.job.helper.XxlJobAdminHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:00
 */
@Component
@DependsOn("jobAdminHelper")
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

        Set<Job> jobSet = new HashSet<>();
        xxlJobBeans.forEach((k, v) -> {
            Method[] methods = v.getClass().getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(JobInfo.class) && method.isAnnotationPresent(XxlJob.class)) {
                    JobInfo jobInfoAnnotation = method.getAnnotation(JobInfo.class);
                    XxlJob xxlJobAnnotation = method.getAnnotation(XxlJob.class);
                    Job job = getJob(jobInfoAnnotation, xxlJobAnnotation);
                    jobSet.add(job);
                }
            }
        });

        if (!CollectionUtils.isEmpty(jobSet)){
            LOGGER.info("Begin Add Job ! Job Info : {}", jobSet);
            XxlJobAdminHelper xxlJobAdminHelper = applicationContext.getBean(XxlJobAdminHelper.class);
            XxlJobExecutorConfiguration xxlJobExecutorConfiguration = applicationContext.getBean(XxlJobExecutorConfiguration.class);
            xxlJobAdminHelper.registerJobGroup(xxlJobExecutorConfiguration.getAppName(), xxlJobExecutorConfiguration.getAppDesc());
            xxlJobAdminHelper.addJob(jobSet);
        }
    }

    private Job getJob(JobInfo jobInfoAnnotation, XxlJob xxlJobAnnotation) {
        Job job = new Job();

        job.setJobCron(jobInfoAnnotation.cron());
        job.setExecutorBlockStrategy(jobInfoAnnotation.blockStrategy().getValue());
        job.setExecutorTimeout(jobInfoAnnotation.timeout());
        job.setExecutorFailRetryCount(jobInfoAnnotation.maxRetryCount());
        job.setJobDesc(jobInfoAnnotation.desc());
        job.setAutoStart(jobInfoAnnotation.autoStart());

        job.setExecutorHandler(xxlJobAnnotation.value());

        return job;
    }

}
