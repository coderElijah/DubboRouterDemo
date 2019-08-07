package com.elijah.dubbograystarter.model;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterprovider
 * Date: 2019-08-05 16:36
 */
public class GrayRule {
    public GrayRule() {
    }

    public GrayRule(String bizzKey, String applicationId) {
        super();
        this.bizzKey = bizzKey;
        this.applicationId = applicationId;
    }

    /**
     * 业务id
     */
    private String bizzKey;
    /**
     * 应用id
     */
    private String applicationId;
    /**
     * 规则开启 0-关闭 1-开启
     */
    private Integer isEnable;
    /**
     * 创建时间
     */
    private Long createTime;


    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getBizzKey() {
        return bizzKey;
    }

    public void setBizzKey(String bizzKey) {
        this.bizzKey = bizzKey;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }
}
