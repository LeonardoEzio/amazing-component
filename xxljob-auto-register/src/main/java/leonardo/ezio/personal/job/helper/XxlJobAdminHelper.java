package leonardo.ezio.personal.job.helper;

import leonardo.ezio.personal.job.entity.JobGroup;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:00
 */
public class XxlJobAdminHelper {

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
     * 任务分组信息
     */
    private List<JobGroup> jobGroups;

    /**
     * 是否登录表示
     */
    private AtomicBoolean isLogin = new AtomicBoolean(false);


    private XxlJobAdminHelper(String serverUrl, String appName, String userName, String password) {
        this.serverUrl = serverUrl;
        this.appName = appName;
        this.userName = userName;
        this.password = password;
        this.jobInfoApi = new XxlJobInfoApi(serverUrl);
    }

    public static XxlJobAdminHelper create(String serverUrl, String appName, String userName, String password) {
        return new XxlJobAdminHelper(serverUrl, appName, userName, password);
    }


}
