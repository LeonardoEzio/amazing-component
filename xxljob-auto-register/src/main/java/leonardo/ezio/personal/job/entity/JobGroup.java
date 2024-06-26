package leonardo.ezio.personal.job.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 任务分组信息 （其实就是执行器信息） -- 对应 xxl-job-admin 中的 XxlJobGroup
 * @author LeonardoEzio
 * @version v1.0.0 create at 2024-04-01 23:01
 */
public class JobGroup implements Serializable {

    /**
     * 主键
     * */
    private int id;

    /**
     * 执行器名称
     * */
    private String appName;

    /**
     * 执行标题
     * */
    private String title;

    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     * */
    private int addressType;

    /**
     * 执行器地址列表，多地址逗号分隔(手动录入)
     * */
    private String addressList;

    /**
     * 执行器地址列表
     * */
    private List<String> registryList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public List<String> getRegistryList() {
        if (addressList!=null && !addressList.trim().isEmpty()) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }
}
