package cn.tfinfo.microservice.mes.basic.generator.controller;

import cn.tfinfo.microservice.mes.basic.generator.common.page.PageResult;
import cn.tfinfo.microservice.mes.basic.generator.common.query.Query;
import cn.tfinfo.microservice.mes.basic.generator.common.utils.Result;
import cn.tfinfo.microservice.mes.basic.generator.entity.GenListDtlEntity;
import cn.tfinfo.microservice.mes.basic.generator.entity.TableEntity;
import cn.tfinfo.microservice.mes.basic.generator.entity.TableFieldEntity;
import cn.tfinfo.microservice.mes.basic.generator.service.GenListDtlService;
import cn.tfinfo.microservice.mes.basic.generator.service.TableFieldService;
import cn.tfinfo.microservice.mes.basic.generator.service.TableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据表管理
 */
@RestController
@RequestMapping("/gen/table")
@AllArgsConstructor
public class TableController {
    private final TableService tableService;
    private final TableFieldService tableFieldService;
    private final GenListDtlService genListDtlService;

    /**
     * 分页
     *
     * @param query 查询参数
     */
    @GetMapping("page")
    public Result<PageResult<TableEntity>> page(Query query) {
        PageResult<TableEntity> page = tableService.page(query);

        return Result.ok(page);
    }

    /**
     * 获取表信息
     *
     * @param id 表ID
     */
    @GetMapping("{id}")
    public Result<TableEntity> get(@PathVariable("id") Long id) {
        LambdaQueryWrapper<GenListDtlEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GenListDtlEntity::getParentId, id);
        List<GenListDtlEntity> genListDtlEntityList = genListDtlService.list(queryWrapper);
        if (CollectionUtils.isEmpty(genListDtlEntityList)) {
            return Result.ok();
        }
        Long tableId = genListDtlEntityList.stream()
                .map(GenListDtlEntity::getTableId)
                .findFirst()
                .orElse(null);

        TableEntity table = tableService.getById(tableId);
        table.setGenListId(id);

        return Result.ok(table);
    }

    /**
     * 修改
     *
     * @param table 表信息
     */
    @PutMapping
    public Result<String> update(@RequestBody TableEntity table) {
        LambdaQueryWrapper<GenListDtlEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GenListDtlEntity::getParentId,table.getGenListId());
        List<GenListDtlEntity> genListDtlEntityList = genListDtlService.list(queryWrapper);
        if (CollectionUtils.isEmpty(genListDtlEntityList)) {
            return Result.ok();
        }
        List<Long> tableIdList = genListDtlEntityList.stream().map(GenListDtlEntity::getTableId).collect(Collectors.toList());

        tableService.updateGenPath(table,tableIdList);

        return Result.ok();
    }

    /**
     * 删除
     *
     * @param ids 表id数组
     */
    @DeleteMapping
    public Result<String> delete(@RequestBody Long[] ids) {
        tableService.deleteBatchIds(ids);

        return Result.ok();
    }

    /**
     * 同步表结构
     *
     * @param ids 表ID
     */
    @PostMapping("/sync")
    public Result<String> sync(@RequestBody List<Long> ids) {
        ExecutorService executorService = Executors.newFixedThreadPool(ids.size() / 5 + 1);

        for (Long id : ids) {
            executorService.submit(() -> tableService.sync(id));
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        return Result.ok();
    }

    /**
     * 导入数据源中的表
     *
     * @param datasourceId  数据源ID
     * @param tableNameList 表名列表
     */
    @PostMapping("import/{datasourceId}")
    public Result<List<TableEntity>> tableImport(@PathVariable("datasourceId") Long datasourceId, @RequestBody List<String> tableNameList) {
        List<TableEntity> list = new ArrayList<>();
        for (String tableName : tableNameList) {
            tableService.tableImport(datasourceId, tableName);
        }
        for (String tableName : tableNameList) {
            QueryWrapper<TableEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("table_name", tableName);
            queryWrapper.orderByDesc("create_time");
            TableEntity table = tableService.getOne(queryWrapper);
            list.add(table);
        }

        return Result.ok(list);

    }

    /**
     * 修改表字段数据
     *
     * @param tableId        表ID
     * @param tableFieldList 字段列表
     */
    @PutMapping("field/{tableId}")
    public Result<String> updateTableField(@PathVariable("tableId") Long tableId, @RequestBody List<TableFieldEntity> tableFieldList) {
        tableFieldService.updateTableField(tableId, tableFieldList);

        return Result.ok();
    }



}
