package leonardo.ezio.personal.job.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import groovyjarjarantlr4.v4.parse.v3TreeGrammarException;
import jakarta.annotation.Nullable;
import leonardo.ezio.personal.job.constant.BlockStrategy;
import leonardo.ezio.personal.job.entity.Job;
import leonardo.ezio.personal.job.entity.JobGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.HttpCookie;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xxl-job 任务信息操作api
 *
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:03
 */
public class XxlJobInfoApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(XxlJobInfoApi.class);

    /**
     * xxl-job 服务端地址
     */
    private final String xxlJobServerUrl;

    /**
     * 登录cookie
     */
    private String cookie;

    public XxlJobInfoApi(String xxlJobServerUrl) {
        this.xxlJobServerUrl = xxlJobServerUrl;
    }


    /**
     * 登录至xxl-job-admin
     *
     * @param userName 用户名
     * @param password 密码
     */
    public void login(String userName, String password) {
        Map<String, Object> loginParam = new HashMap<>();
        loginParam.put("userName", userName);
        loginParam.put("password", password);
        try {
            HttpResponse loginResponse = HttpRequest.post(this.xxlJobServerUrl + "/login").form(loginParam).execute();
            if (loginResponse.isOk() && loginResponse.getStatus() == HttpStatus.HTTP_OK) {
                List<HttpCookie> cookies = loginResponse.getCookies();
                if (cookies != null && !cookies.isEmpty()) {
                    StringBuilder cookieBuilder = new StringBuilder();
                    cookies.forEach(c -> {
                        cookieBuilder.append(c.toString());
                    });
                    this.cookie = cookieBuilder.toString();
                    return;
                }
            }

            throw new IllegalStateException("Xll-Job-Admin Login Failed !");
        } catch (Exception e) {
            LOGGER.error("Xxl-Job-Admin Login Exception : ", e);
            throw e;
        }
    }


    public void addJobGroup(String appName, String appDesc) {
        Map<String, Object> jobGroupAddParam = new HashMap<>();
        jobGroupAddParam.put("appname", appName);
        jobGroupAddParam.put("title", appDesc);
        jobGroupAddParam.put("addressType", 0);
        try {
            HttpResponse jobGroupAddResponse = HttpRequest.post(this.xxlJobServerUrl + "/jobgroup/save")
                    .header("Cookie", this.cookie)
                    .header("Content-Type", ContentType.FORM_URLENCODED.getValue())
                    .charset("UTF-8")
                    .form(jobGroupAddParam)
                    .execute();
            if (jobGroupAddResponse.isOk() && HttpStatus.HTTP_OK == jobGroupAddResponse.getStatus()) {
                LOGGER.info("Xxl-Job-Admin Add Job Group {} Success!", appName);
                return;
            }
        } catch (Exception e) {
            LOGGER.error("Xxl-Job-Admin Add Job Group {} Exception : ", appName, e);
            throw e;
        }
    }

    /**
     * 获取执行器
     *
     * @param appName 执行器名称
     */
    public JobGroup findJobGroup(String appName) {
        Map<String, Object> jobGroupFindParam = new HashMap<>();
        jobGroupFindParam.put("appname", appName);
        try {
            HttpResponse jobGroupFindResponse = HttpRequest.get(this.xxlJobServerUrl + "/jobgroup/pageList")
                    .header("Cookie", this.cookie)
                    .header("Content-Type", ContentType.FORM_URLENCODED.getValue())
                    .charset("UTF-8")
                    .form(jobGroupFindParam)
                    .execute();
            if (jobGroupFindResponse.isOk() && HttpStatus.HTTP_OK == jobGroupFindResponse.getStatus()) {
                JSONObject jsonObject = JSONUtil.parseObj(jobGroupFindResponse.body());
                if (jsonObject.getJSONArray("data") != null) {
                    List<JobGroup> jobGroups = jsonObject.getJSONArray("data").toList(JobGroup.class);
                    return CollectionUtils.isEmpty(jobGroups) ? null : jobGroups.get(0);
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Xxl-Job-Admin Find Job Group {} Exception :", appName, e);
            throw e;
        }
    }


    /**
     * 获取执行器下面的任务
     *
     * @param groupId 执行id
     */
    public List<Job> findJobByGroupId(long groupId) {
        Map<String, Object> jobFindParam = new HashMap<>();
        jobFindParam.put("jobGroup", String.valueOf(groupId));
        jobFindParam.put("triggerStatus", 0);
        try {
            HttpResponse jobFindResponse = HttpRequest.get(this.xxlJobServerUrl + "/jobinfo/pageList")
                    .header("Cookie", this.cookie)
                    .header("Content-Type", ContentType.FORM_URLENCODED.getValue())
                    .charset("UTF-8")
                    .form(jobFindParam)
                    .execute();
            if (jobFindResponse.isOk() && HttpStatus.HTTP_OK == jobFindResponse.getStatus()) {
                JSONObject jsonObject = JSONUtil.parseObj(jobFindResponse.body());
                if (jsonObject.getJSONArray("data") != null) {
                    return jsonObject.getJSONArray("data").toList(Job.class);
                }
            }
            throw new IllegalStateException("Xxl-Job-Admin Find Job Failed !!!");
        } catch (HttpException e) {
            LOGGER.error("Xxl-Job-Admin Find Job Exception : ", e);
            throw e;
        }
    }

    /**
     * 添加任务信息
     *
     * @param groupId       执行器id
     * @param desc          任务描述
     * @param corn          任务corn表达式
     * @param beanClassName 任务的Bean名称
     * @param blockStrategy 阻塞策略
     * @param timeout       超时时间
     * @param maxRetryCount 重试次数
     */
    public long addJob(long groupId, String desc, String corn, String beanClassName, String blockStrategy, long timeout, int maxRetryCount) {
        Map<String, Object> addJobParam = new HashMap<>();
        addJobParam.put("jobGroup", String.valueOf(groupId));
        desc = StrUtil.isEmpty(desc) ? beanClassName : desc;
        addJobParam.put("jobDesc", desc);
        addJobParam.put("author", "system");
        addJobParam.put("cornGen_display", corn);
        addJobParam.put("jobCron", corn);
        addJobParam.put("glueType", "BEAN");
        addJobParam.put("executorHandler", beanClassName);
        addJobParam.put("executorParam", "");
        addJobParam.put("executorRouteStrategy", "ROUND");
        addJobParam.put("executorBlockStrategy", blockStrategy);
        addJobParam.put("executorTimeout", String.valueOf(timeout));
        addJobParam.put("executorFailRetryCount", String.valueOf(maxRetryCount));
        addJobParam.put("glueRemark", "GLUE代码初始化");
        try {
            HttpResponse addJobResponse = HttpRequest.post(this.xxlJobServerUrl + "/jobinfo/add")
                    .header("Cookie", this.cookie)
                    .header("Content-Type", ContentType.FORM_URLENCODED.getValue())
                    .charset("UTF-8")
                    .form(addJobParam)
                    .execute();
            if (addJobResponse.isOk() && HttpStatus.HTTP_OK == addJobResponse.getStatus()) {
                JSONObject jsonObject = JSONUtil.parseObj(addJobResponse.body());
                if (jsonObject.getInt("code") == 200) {
                    return jsonObject.getLong("content");
                }
            }
            throw new IllegalStateException(String.format("Xxl-Job-Admin Add Job %s Failed ! ", beanClassName));
        } catch (Exception e) {
            LOGGER.error("Xxl-Job-Admin Add Job Info Exception : ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动任务
     *
     * @param id job id
     */
    public void startJob(long id) {
        if (id != -1) {
            Map<String, Object> jobStartParam = new HashMap<>();
            jobStartParam.put("id", id);
            try {
                HttpResponse jobStartResponse = HttpRequest.post(this.xxlJobServerUrl + "/jobinfo/start")
                        .header("Cookie", this.cookie)
                        .header("Content-Type", ContentType.FORM_URLENCODED.getValue())
                        .charset("UTF-8")
                        .form(jobStartParam)
                        .execute();
                if (jobStartResponse.isOk() && HttpStatus.HTTP_OK == jobStartResponse.getStatus()) {
                    JSONObject jsonObject = JSONUtil.parseObj(jobStartResponse.body());
                    if (jsonObject.getInt("code") != 200) {
                        throw new IllegalStateException("Xxl Job" + id + "Start Failed , Please Start By Manual!");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Xxl-Job-Admin Start Job Exception : ", e);
                throw new RuntimeException(e);
            }
        }
    }
}
