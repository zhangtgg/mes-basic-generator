package cn.tfinfo.microservice.mes.basic.generator.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误编码
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "服务器异常，请稍后再试");

    private final int code;
    private final String msg;
}
