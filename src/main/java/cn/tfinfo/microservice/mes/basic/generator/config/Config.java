package cn.tfinfo.microservice.mes.basic.generator.config;

import cn.tfinfo.microservice.mes.basic.generator.entity.GenListDtlEntity;
import lombok.Data;

import java.util.List;

@Data
public class Config {
    public static final ThreadLocal<Config> threadLocalInstance = ThreadLocal.withInitial(Config::new);

    private String templateFile = "single.json";

    private Long MTableId = -1L;

    private List<GenListDtlEntity> genListDtlEntityList;

    public static Config getInstance() {
        return threadLocalInstance.get();
    }

}
