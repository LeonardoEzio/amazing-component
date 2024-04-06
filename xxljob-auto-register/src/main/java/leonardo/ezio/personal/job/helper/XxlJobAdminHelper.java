package leonardo.ezio.personal.job.helper;

import leonardo.ezio.personal.job.entity.Job;
import leonardo.ezio.personal.job.entity.JobGroup;
import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:00
 */
public class XxlJobAdminHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(XxlJobAdminHelper.class);

    /**
     * xxl-job 服务地址
     */
    private String serverUrl;

    /**
     * appName - executor 名称
     */
    private String appName;

    /**
     * xxl-job 登录用户名
     */
    private String userName;

    /**
     * xxl-job 登录密码
     */
    private String password;

    /**
     * xxl-job job info 操作api
     */
    private XxlJobInfoApi jobInfoApi;

    /**
     * 是否登录表示
     */
    private AtomicBoolean isLogin = new AtomicBoolean(false);


    private XxlJobAdminHelper(String serverUrl, String appName, String userName, String password) {
        this.serverUrl = serverUrl;
        this.appName = appName;
        this.userName = userName;
        this.password = password;
        this.jobInfoApi = new XxlJobInfoApi(this.serverUrl);
    }

    public static XxlJobAdminHelper create(String serverUrl, String appName, String userName, String password) {
        return new XxlJobAdminHelper(serverUrl, appName, userName, password);
    }

    /**
     * 登录 xxl-job-amin
     */
    public XxlJobAdminHelper login() {
        this.jobInfoApi.login(this.userName, this.password);
        this.isLogin.set(true);
        return this;
    }

    public void registerJobGroup(String appName,String appDesc){
        JobGroup jobGroup = this.jobInfoApi.findJobGroup(appName);
        if (null == jobGroup){
            this.jobInfoApi.addJobGroup(appName, appDesc);
        }
    }

    /**
     * 添加xxl-job
     *
     * @param jobMap 任务信息 key-job信息 ; value-是否需要自动启动
     */
    public void addJob(Map<Job, Boolean> jobMap) {
        JobGroup jobGroup = this.jobInfoApi.findJobGroup(this.appName);
        if (null == jobGroup) {
            throw new IllegalStateException("Xxl-Job-Executor Not Register!");
        }
        int jobGroupId = jobGroup.getId();
        List<Job> alreadyRegisterJobs = this.jobInfoApi.findJobByGroupId(jobGroupId);
        Set<String> alreadyRegisterJobExecutorSets = alreadyRegisterJobs.stream().map(Job::getExecutorHandler).collect(Collectors.toSet());
        jobMap.forEach((k, v) -> {
            String executorHandler = k.getExecutorHandler();
            if (!alreadyRegisterJobExecutorSets.contains(executorHandler)) {
                long jobId = jobInfoApi.addJob(jobGroupId, k.getJobDesc(), k.getJobCron(), k.getExecutorHandler(), k.getExecutorBlockStrategy(), k.getExecutorTimeout(), k.getExecutorFailRetryCount());
                if (v) {
                    jobInfoApi.startJob(jobId);
                }
                LOGGER.info("============================ Job Add {} Success ============================", StringUtils.isEmpty(k.getJobDesc()) ? k.getExecutorHandler() : k.getJobDesc());
            }
        });
    }

}
