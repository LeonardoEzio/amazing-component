package leonardo.ezio.personal.job.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import io.netty.util.internal.StringUtil;
import leonardo.ezio.personal.job.helper.XxlJobAdminHelper;
import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * xxl-job 执行器配置类
 *
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-05 23:14
 */
@Configuration
@ConditionalOnExpression("${xxl.job.enable:false}")
public class XxlJobExecutorConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(XxlJobExecutorConfiguration.class);

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.executor.appName}")
    private String appName;

    @Value("${xxl.job.executor.desc:${xxl.job.executor.appName}}")
    private String appDesc;

    @Value("${xxl.job.executor.ip:}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private int port;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.executor.logPath}")
    private String logPath;

    @Value("${xxl.job.executor.logRetentionDays}")
    private int logRetentionDays;

    @Value("${xxl.job.userName:admin}")
    private String userName;

    @Value("${xxl.job.password:admin}")
    private String password;

    public XxlJobExecutorConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean
    public XxlJobSpringExecutor xxlJobExecutor() {
        LOGGER.info("Xxl-Job-Executor Config...................");
        XxlJobSpringExecutor xxlJobExecutor = new XxlJobSpringExecutor();
        xxlJobExecutor.setAdminAddresses(this.adminAddresses);
        xxlJobExecutor.setAppname(this.appName);
        xxlJobExecutor.setIp(this.ip);
        xxlJobExecutor.setPort(this.port);
        xxlJobExecutor.setAccessToken(this.accessToken);
        xxlJobExecutor.setLogPath(this.logPath);
        xxlJobExecutor.setLogRetentionDays(this.logRetentionDays);
        return xxlJobExecutor;
    }

    @Bean
    @DependsOn("xxlJobExecutor")
    public XxlJobAdminHelper jobAdminHelper() {
        LOGGER.info("Begin Create Xll-Job AdminHelper...................");
        if (!StringUtils.isEmpty(this.adminAddresses) && !StringUtils.isEmpty(this.appName)
                && !StringUtils.isEmpty(this.userName) && !StringUtils.isEmpty(this.password)) {
            try {
                XxlJobAdminHelper xxlJobAdminHelper = XxlJobAdminHelper.create(this.adminAddresses, this.appName, this.userName, this.password);
                xxlJobAdminHelper.login();
                return xxlJobAdminHelper;
            } catch (Exception e) {
                LOGGER.error("Xxl Job Admin Help Init Exception : ", e);
                throw e;
            }
        } else {
            throw new IllegalStateException("XxlJobAdminHelper Init Failed ! Please Check AdminAddress、AppName、UserName、Password Config.");
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getAppDesc() {
        return appDesc;
    }
}
