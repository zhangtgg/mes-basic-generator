package cn.tfinfo.microservice.mes.basic.generator.controller;

import cn.tfinfo.microservice.mes.basic.generator.common.page.PageResult;
import cn.tfinfo.microservice.mes.basic.generator.common.query.Query;
import cn.tfinfo.microservice.mes.basic.generator.common.utils.Result;
import cn.tfinfo.microservice.mes.basic.generator.entity.FieldTypeEntity;
import cn.tfinfo.microservice.mes.basic.generator.service.FieldTypeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;

/**
 * 字段类型管理
 */
@RestController
@RequestMapping("/gen/fieldtype")
@AllArgsConstructor
public class FieldTypeController {
    private final FieldTypeService fieldTypeService;

    @GetMapping("page")
    public Result<PageResult<FieldTypeEntity>> page(Query query) {
        PageResult<FieldTypeEntity> page = fieldTypeService.page(query);

        return Result.ok(page);
    }

    @GetMapping("{id}")
    public Result<FieldTypeEntity> get(@PathVariable("id") Long id) {
        FieldTypeEntity data = fieldTypeService.getById(id);

        return Result.ok(data);
    }

    @GetMapping("list")
    public Result<Set<String>> list() {
        Set<String> set = fieldTypeService.getList();

        return Result.ok(set);
    }

    @PostMapping
    public Result<String> save(@RequestBody FieldTypeEntity entity) {
        fieldTypeService.save(entity);

        return Result.ok();
    }

    @PutMapping
    public Result<String> update(@RequestBody FieldTypeEntity entity) {
        fieldTypeService.updateById(entity);

        return Result.ok();
    }

    @DeleteMapping
    public Result<String> delete(@RequestBody Long[] ids) {
        fieldTypeService.removeBatchByIds(Arrays.asList(ids));

        return Result.ok();
    }
}