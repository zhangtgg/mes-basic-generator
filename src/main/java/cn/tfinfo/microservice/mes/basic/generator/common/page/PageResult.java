package cn.tfinfo.microservice.mes.basic.generator.common.page;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类
 */
@Data
public class PageResult<T> implements Serializable {
    // 总记录数
    private int total;

    // 列表数据
    private List<T> list;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public PageResult(List<T> list, long total) {
        this.list = list;
        this.total = (int) total;
    }
}