package cn.tfinfo.microservice.mes.basic.generator.service.impl;

import cn.hutool.core.text.NamingCase;
import cn.tfinfo.microservice.mes.basic.generator.common.exception.ServerException;
import cn.tfinfo.microservice.mes.basic.generator.common.page.PageResult;
import cn.tfinfo.microservice.mes.basic.generator.common.query.Query;
import cn.tfinfo.microservice.mes.basic.generator.common.service.impl.BaseServiceImpl;
import cn.tfinfo.microservice.mes.basic.generator.config.GenDataSource;
import cn.tfinfo.microservice.mes.basic.generator.config.template.GeneratorConfig;
import cn.tfinfo.microservice.mes.basic.generator.config.template.GeneratorInfo;
import cn.tfinfo.microservice.mes.basic.generator.dao.TableDao;
import cn.tfinfo.microservice.mes.basic.generator.entity.TableEntity;
import cn.tfinfo.microservice.mes.basic.generator.entity.TableFieldEntity;
import cn.tfinfo.microservice.mes.basic.generator.enums.FormLayoutEnum;
import cn.tfinfo.microservice.mes.basic.generator.enums.GeneratorTypeEnum;
import cn.tfinfo.microservice.mes.basic.generator.service.DataSourceService;
import cn.tfinfo.microservice.mes.basic.generator.service.TableFieldService;
import cn.tfinfo.microservice.mes.basic.generator.service.TableService;
import cn.tfinfo.microservice.mes.basic.generator.utils.GenUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 数据表

 */
@Service
@AllArgsConstructor
public class TableServiceImpl extends BaseServiceImpl<TableDao, TableEntity> implements TableService {
    private final TableFieldService tableFieldService;
    private final DataSourceService dataSourceService;
    private final GeneratorConfig generatorConfig;

    @Override
    public PageResult<TableEntity> page(Query query) {
        IPage<TableEntity> page = baseMapper.selectPage(
                getPage(query),
                getWrapper(query)
        );
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public TableEntity getByTableName(String tableName) {
        LambdaQueryWrapper<TableEntity> queryWrapper = Wrappers.lambdaQuery();
        return baseMapper.selectOne(queryWrapper.eq(TableEntity::getTableName, tableName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Long[] ids) {
        // 删除表
        baseMapper.deleteBatchIds(Arrays.asList(ids));

        // 删除列
        tableFieldService.deleteBatchTableIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void tableImport(Long datasourceId, String tableName) {
        // 初始化配置信息
        GenDataSource dataSource = dataSourceService.get(datasourceId);
        // 查询表是否存在
        TableEntity table = this.getByTableName(tableName);
        // 表存在
        if (table != null) return;

        // 从数据库获取表信息
        table = GenUtils.getTable(dataSource, tableName);

        // 代码生成器信息
        GeneratorInfo generator = generatorConfig.getGeneratorConfig();

        // 保存表信息
        table.setPackageName(generator.getProject().getPackageName());
        table.setVersion(generator.getProject().getVersion());
        table.setBackendPath(generator.getProject().getBackendPath());
        table.setFrontendPath(generator.getProject().getFrontendPath());
        table.setAuthor(generator.getDeveloper().getAuthor());
        table.setEmail(generator.getDeveloper().getEmail());
        table.setFormLayout(FormLayoutEnum.ONE.getValue());
        table.setGeneratorType(GeneratorTypeEnum.ZIP_DOWNLOAD.ordinal());
        table.setClassName(NamingCase.toPascalCase(tableName));
        table.setModuleName(GenUtils.getModuleName(table.getPackageName()));
        table.setFunctionName(GenUtils.getFunctionName(tableName));
        table.setCreateTime(new Date());
        this.save(table);

        // 获取原生字段数据
        List<TableFieldEntity> tableFieldList = GenUtils.getTableFieldList(dataSource, table.getId(), table.getTableName());
        // 初始化字段数据
        tableFieldService.initFieldList(tableFieldList);

        // 保存列数据
        tableFieldList.forEach(tableFieldService::save);

        try {
            //释放数据源
            dataSource.getConnection().close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(Long id) {
        TableEntity table = this.getById(id);

        // 初始化配置信息
        GenDataSource datasource = dataSourceService.get(table.getDatasourceId());

        // 从数据库获取表字段列表
        List<TableFieldEntity> dbTableFieldList = GenUtils.getTableFieldList(datasource, table.getId(), table.getTableName());
        if (dbTableFieldList.size() == 0) {
            throw new ServerException("同步失败，请检查数据库表：" + table.getTableName());
        }

        List<String> dbTableFieldNameList = dbTableFieldList.stream().map(TableFieldEntity::getFieldName).collect(Collectors.toList());

        // 表字段列表
        List<TableFieldEntity> tableFieldList = tableFieldService.getByTableId(id);

        Map<String, TableFieldEntity> tableFieldMap = tableFieldList.stream().collect(Collectors.toMap(TableFieldEntity::getFieldName, Function.identity()));

        // 初始化字段数据
        tableFieldService.initFieldList(dbTableFieldList);

        // 同步表结构字段
        dbTableFieldList.forEach(field -> {
            // 新增字段
            if (!tableFieldMap.containsKey(field.getFieldName())) {
                tableFieldService.save(field);
                return;
            }

            // 修改字段
            TableFieldEntity updateField = tableFieldMap.get(field.getFieldName());
            updateField.setPrimaryPk(field.isPrimaryPk());
            updateField.setFieldComment(field.getFieldComment());
            updateField.setFieldType(field.getFieldType());
            updateField.setAttrType(field.getAttrType());

            tableFieldService.updateById(updateField);
        });

        // 删除数据库表中没有的字段
        List<TableFieldEntity> delFieldList = tableFieldList.stream().filter(field -> !dbTableFieldNameList.contains(field.getFieldName())).collect(Collectors.toList());
        if (delFieldList.size() > 0) {
            List<Long> fieldIds = delFieldList.stream().map(TableFieldEntity::getId).collect(Collectors.toList());
            tableFieldService.removeBatchByIds(fieldIds);
        }
    }

    @Override
    public void updateGenPath(TableEntity table, List<Long> tableIdList) {
        String packageName = table.getPackageName();
        String backendPath = table.getBackendPath();

        UpdateWrapper<TableEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("package_name", packageName)
                .set("backend_path", backendPath)
                .in("id", tableIdList);

        baseMapper.update(null, updateWrapper);
    }
}