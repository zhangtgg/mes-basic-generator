package cn.tfinfo.microservice.mes.basic.generator.controller;

import cn.hutool.core.io.IoUtil;
import cn.tfinfo.microservice.mes.basic.generator.common.utils.Result;
import cn.tfinfo.microservice.mes.basic.generator.service.GeneratorService;
import cn.tfinfo.microservice.mes.basic.generator.vo.PreviewVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成
 */
@RestController
@RequestMapping("/gen/generator")
@AllArgsConstructor
public class GeneratorController {
    private final GeneratorService generatorService;

    /**
     * 生成代码（zip压缩包）
     */
    @GetMapping("download")
    public void download(String tableIds, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        // 生成代码
        for (String tableId : tableIds.split(",")) {
            generatorService.downloadCode(Long.valueOf(Long.parseLong(tableId)), zip);
        }

        IoUtil.close(zip);

        // zip压缩包数据
        byte[] data = outputStream.toByteArray();

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"maku.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), false, data);
    }

    /**
     * 生成代码（自定义目录）
     */
    @ResponseBody
    @PostMapping("code")
    public Result<String> code(@RequestBody Long[] ids) throws Exception {
        // 生成代码
        for (Long id : ids) {
            generatorService.generatorCode(id);
        }

        return Result.ok();
    }

    /**
     * 预览代码 - 多表
     */
    @GetMapping("/previews")
    public Result<List<PreviewVO>> previews(@RequestParam Long id) {
        List<PreviewVO> results = generatorService.previews(id);
        return Result.ok(results);
    }


}