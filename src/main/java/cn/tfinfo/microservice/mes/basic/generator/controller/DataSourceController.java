package cn.tfinfo.microservice.mes.basic.generator.controller;

import cn.tfinfo.microservice.mes.basic.generator.common.page.PageResult;
import cn.tfinfo.microservice.mes.basic.generator.common.query.Query;
import cn.tfinfo.microservice.mes.basic.generator.common.response.ListResponse;
import cn.tfinfo.microservice.mes.basic.generator.common.utils.Result;
import cn.tfinfo.microservice.mes.basic.generator.config.GenDataSource;
import cn.tfinfo.microservice.mes.basic.generator.entity.DataSourceEntity;
import cn.tfinfo.microservice.mes.basic.generator.entity.TableEntity;
import cn.tfinfo.microservice.mes.basic.generator.service.DataSourceService;
import cn.tfinfo.microservice.mes.basic.generator.utils.DbUtils;
import cn.tfinfo.microservice.mes.basic.generator.utils.GenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 数据源管理
 */
@Slf4j
@RestController
@RequestMapping("/gen/datasource")
@AllArgsConstructor
public class DataSourceController {
    private final DataSourceService datasourceService;

    @GetMapping("page")
    public Result<PageResult<DataSourceEntity>> page(Query query) {
        PageResult<DataSourceEntity> page = datasourceService.page(query);

        return Result.ok(page);
    }

    @GetMapping("list")
    public Result<List<DataSourceEntity>> list() {
        List<DataSourceEntity> list = datasourceService.getList();

        return Result.ok(list);
    }

    @GetMapping("{id}")
    public Result<DataSourceEntity> get(@PathVariable("id") Long id) {
        DataSourceEntity data = datasourceService.getById(id);

        return Result.ok(data);
    }

    @GetMapping("test/{id}")
    public Result<String> test(@PathVariable("id") Long id) {
        try {
            DataSourceEntity entity = datasourceService.getById(id);

            DbUtils.getConnection(new GenDataSource(entity));
            return Result.ok("连接成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("连接失败，请检查配置信息");
        }
    }

    @PostMapping
    public Result<String> save(@RequestBody DataSourceEntity entity) {
        datasourceService.save(entity);

        return Result.ok();
    }

    @PutMapping
    public Result<String> update(@RequestBody DataSourceEntity entity) {
        datasourceService.updateById(entity);

        return Result.ok();
    }

//    @DeleteMapping
    public Result<String> delete(@RequestBody Long[] ids) {
        datasourceService.removeBatchByIds(Arrays.asList(ids));

        return Result.ok();
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam Long id) {
        datasourceService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据数据源ID，获取全部数据表
     *
     */
    @GetMapping("table/list")
    public Result<ListResponse<TableEntity>> tableList(Query query) {
        ListResponse<TableEntity> response = new ListResponse<>();
        try {
            // 获取数据源
            GenDataSource datasource = datasourceService.get(query.getId());
            // 根据数据源，获取全部数据表
            int count = GenUtils.getTableCount(datasource,query.getTableName());

            List<TableEntity> tableList = GenUtils.getTableList(datasource, query.getTableName(), query.getLimit(),query.getPage());
            response.setRows(tableList);
            response.setCount(count);
            return Result.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("数据源配置错误，请检查数据源配置！");
        }
    }
}