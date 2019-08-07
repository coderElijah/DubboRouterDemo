package com.elijah.dubbograystarter.model;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterprovider.provider
 * Date: 2019-08-06 14:23
 */
public enum GrayRulesType {
    Acme(1, "Acme", "/CustomRouterRules/Acme/routerrules");

    GrayRulesType(Integer code, String ruleName, String rulesPath) {
        this.code = code;
        this.ruleName = ruleName;
        this.rulesPath = rulesPath;
    }

    private Integer code;
    private String ruleName;
    private String rulesPath;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRulesPath() {
        return rulesPath;
    }

    public void setRulesPath(String rulesPath) {
        this.rulesPath = rulesPath;
    }
}
