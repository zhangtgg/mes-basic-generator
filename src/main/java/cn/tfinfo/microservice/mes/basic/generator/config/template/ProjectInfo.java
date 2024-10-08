package cn.tfinfo.microservice.mes.basic.generator.config.template;

import lombok.Data;

/**
 * 项目信息
 */
@Data
public class ProjectInfo {
    /**
     * 项目包名
     */
    private String packageName;
    /**
     * 项目版本号
     */
    private String version;
    /**
     * 后端生成路径
     */
    private String backendPath;
    /**
     * 前端生成路径
     */
    private String frontendPath;
}