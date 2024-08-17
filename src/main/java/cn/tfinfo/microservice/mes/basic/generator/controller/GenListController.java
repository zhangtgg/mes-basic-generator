package cn.tfinfo.microservice.mes.basic.generator.controller;

import cn.tfinfo.microservice.mes.basic.generator.common.page.PageResult;
import cn.tfinfo.microservice.mes.basic.generator.common.query.Query;
import cn.tfinfo.microservice.mes.basic.generator.common.utils.Result;
import cn.tfinfo.microservice.mes.basic.generator.entity.GenListEntity;
import cn.tfinfo.microservice.mes.basic.generator.service.GenListService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gen/list")
@AllArgsConstructor
public class GenListController {

    // 注入GenListService服务
    private final GenListService genListService;

    /**
     * 获取GenListEntity列表
     * @param query 查询参数
     * @return 包含分页结果的通用结果对象
     */
    @GetMapping("/findList")
    public Result<PageResult<GenListEntity>> findList(Query query) {
        PageResult<GenListEntity> page = genListService.page(query);
        return Result.ok(page);
    }

    /**
     * 保存GenListEntity
     * @param entity 请求体中的GenListEntity对象
     * @return 成功结果
     */
    @PostMapping("/save")
    public Result<String> save(@RequestBody GenListEntity entity) {
        genListService.save(entity);
        return Result.ok();
    }

    /**
     * 删除GenListEntity
     * @param id 要删除的实体ID
     * @return 成功结果
     */
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        genListService.deleteById(id);
        return Result.ok();
    }

    /**
     * 查询GenListEntity详情
     * @param id 要查询的实体ID
     * @return 包含实体详细信息的通用结果对象
     */
    @GetMapping("/queryDetail")
    public Result<GenListEntity> queryDetail(@RequestParam Long id) {
        GenListEntity genListEntity = genListService.getById(id);
        return Result.ok(genListEntity);
    }
}
