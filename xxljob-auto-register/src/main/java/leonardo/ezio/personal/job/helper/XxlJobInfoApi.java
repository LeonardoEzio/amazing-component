package leonardo.ezio.personal.job.helper;

/**
 * xxl-job 任务信息操作api
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:03
 */
public class XxlJobInfoApi {

    /**
     * xxl-job 服务端地址
     * */
    private String xxlJobServerUrl;

    /**
     * 登录cookie
     * */
    private String cookie;

    public XxlJobInfoApi(String xxlJobServerUrl) {
        this.xxlJobServerUrl = xxlJobServerUrl;
    }


}
