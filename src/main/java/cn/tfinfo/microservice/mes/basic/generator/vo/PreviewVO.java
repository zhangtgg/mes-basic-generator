package cn.tfinfo.microservice.mes.basic.generator.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 预览视图对象
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreviewVO {
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件内容
     */
    private String content;


}