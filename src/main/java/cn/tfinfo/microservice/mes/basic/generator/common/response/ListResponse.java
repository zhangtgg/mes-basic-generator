package cn.tfinfo.microservice.mes.basic.generator.common.response;

import lombok.Data;

import java.util.List;

@Data
public class ListResponse<T> {
    private Integer pageCount;
    private Integer count;
    private Integer pageNo;
    private Integer pageSize;

    private List<T> rows;

    public ListResponse() {
        this.count = 0;
        this.pageCount = 0;
        this.pageNo = 1;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
