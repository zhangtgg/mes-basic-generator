package cn.tfinfo.microservice.mes.basic.generator.service;


import cn.tfinfo.microservice.mes.basic.generator.vo.PreviewVO;

import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成
 */
public interface GeneratorService {

    void downloadCode(Long tableId, ZipOutputStream zip);

    void generatorCode(Long tableId);

    List<PreviewVO> preview(Long tableId);

    List<PreviewVO> previews(Long id);
}
