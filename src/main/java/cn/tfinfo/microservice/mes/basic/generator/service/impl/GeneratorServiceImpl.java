package cn.tfinfo.microservice.mes.basic.generator.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.tfinfo.microservice.mes.basic.generator.common.exception.ServerException;
import cn.tfinfo.microservice.mes.basic.generator.common.utils.DateUtils;
import cn.tfinfo.microservice.mes.basic.generator.config.Config;
import cn.tfinfo.microservice.mes.basic.generator.config.template.GeneratorConfig;
import cn.tfinfo.microservice.mes.basic.generator.config.template.GeneratorInfo;
import cn.tfinfo.microservice.mes.basic.generator.config.template.TemplateInfo;
import cn.tfinfo.microservice.mes.basic.generator.constant.TemplateConstant;
import cn.tfinfo.microservice.mes.basic.generator.entity.*;
import cn.tfinfo.microservice.mes.basic.generator.service.*;
import cn.tfinfo.microservice.mes.basic.generator.utils.TemplateUtils;
import cn.tfinfo.microservice.mes.basic.generator.vo.PreviewVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 代码生成
 */
@Service
@Slf4j
@AllArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {
    private final DataSourceService datasourceService;
    private final FieldTypeService fieldTypeService;
    private final BaseClassService baseClassService;
    private final GeneratorConfig generatorConfig;
    private final TableService tableService;
    private final TableFieldService tableFieldService;
    private final GenListDtlService genListDtlService;

    @Override
    public void downloadCode(Long tableId, ZipOutputStream zip) {
        // 数据模型
        Map<String, Object> dataModel = getDataModel(tableId);

        // 代码生成器信息
        GeneratorInfo generator = generatorConfig.getGeneratorConfig();

        // 渲染模板并输出
        for (TemplateInfo template : generator.getTemplates()) {
            dataModel.put("templateName", template.getTemplateName());
            String content = TemplateUtils.getContent(template.getTemplateContent(), dataModel);
            String path = TemplateUtils.getContent(template.getGeneratorPath(), dataModel);

            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(path));
                IoUtil.writeUtf8(zip, false, content);
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                throw new ServerException("模板写入失败：" + path, e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generatorCode(Long id) {
        List<PreviewVO> previews = previews(id);
        for (PreviewVO vo : previews) {
            String content = vo.getContent();
            String path = vo.getPath();
            FileUtil.writeUtf8String(content, path);
        }
    }

    /**
     * 获取渲染的数据模型
     *
     * @param tableId 表ID
     */
    private Map<String, Object> getDataModel(Long tableId) {
        // 主表ID
        Long MTableId = Config.getInstance().getMTableId();
        List<GenListDtlEntity> genListDtlEntityList = Config.getInstance().getGenListDtlEntityList();

        // 表信息
        TableEntity table = tableService.getById(tableId);
        List<TableFieldEntity> fieldList = tableFieldService.getByTableId(tableId);
        table.setFieldList(fieldList);

        // 数据模型
        Map<String, Object> dataModel = new HashMap<>();
        Optional<Long> tableId2 = genListDtlEntityList.stream()
                .map(GenListDtlEntity::getTableId)
                .filter(id -> !Objects.equals(id, MTableId))
                .findFirst();
        // 表信息
        if (tableId2.isPresent()) {
            TableEntity table2 = tableService.getById(tableId2.get());
            List<TableFieldEntity> fieldList2 = tableFieldService.getByTableId(tableId2.get());
            table2.setFieldList(fieldList2);
            dataModel.put("fieldList2", table2.getFieldList());
            dataModel.put("className2", StrUtil.lowerFirst(table2.getClassName()));
            dataModel.put("ClassName2", table2.getClassName());
        }


        // 获取数据库类型
        String dbType = datasourceService.getDatabaseProductName(table.getDatasourceId());
        dataModel.put("dbType", dbType);

        // 项目信息
        dataModel.put("package", table.getPackageName());
        dataModel.put("packageName", table.getPackageName());
        dataModel.put("packagePath", table.getPackageName().replace(".", "-"));
        dataModel.put("version", table.getVersion());
        dataModel.put("moduleName", table.getModuleName());
        dataModel.put("ModuleName", StrUtil.upperFirst(table.getModuleName()));
        dataModel.put("functionName", table.getFunctionName());
        dataModel.put("FunctionName", StrUtil.upperFirst(table.getFunctionName()));
        dataModel.put("formLayout", table.getFormLayout());


        String packageName = table.getPackageName();

        String[] parts = packageName.split("\\.", 2); // 只分隔成两个部分

        if (parts.length == 2) {
            String firstPart = parts[0];
            String secondPart = parts[1];

            dataModel.put("firstPart", firstPart);
            dataModel.put("secondPart", secondPart);
            dataModel.put("packageName2", firstPart + "\\\\" + secondPart);
        } else {
            dataModel.put("packageName2",packageName);
        }

        // 开发者信息
        dataModel.put("author", table.getAuthor());
        dataModel.put("email", table.getEmail());
        dataModel.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        dataModel.put("date", DateUtils.format(new Date(), DateUtils.DATE_PATTERN));


        // 设置字段分类
        setFieldTypeList(dataModel, table);

        // 设置基类信息
        setBaseClass(dataModel, table);

        // 导入的包列表
        Set<String> importList = fieldTypeService.getPackageByTableId(table.getId());
        dataModel.put("importList", importList);

        // 表信息
        dataModel.put("tableName", table.getTableName());
        dataModel.put("tableComment", table.getTableComment());
        dataModel.put("className", StrUtil.lowerFirst(table.getClassName()));
        dataModel.put("ClassName", table.getClassName());
        dataModel.put("fieldList", table.getFieldList());

        // 生成路径
        dataModel.put("backendPath", StrUtil.isEmpty(table.getBackendPath()) ? "backendPath" : table.getBackendPath());
        dataModel.put("frontendPath", StrUtil.isEmpty(table.getFrontendPath()) ? "frontendPath" : table.getFrontendPath());




        return dataModel;
    }

    /**
     * 设置基类信息
     *
     * @param dataModel 数据模型
     * @param table     表
     */
    private void setBaseClass(Map<String, Object> dataModel, TableEntity table) {
        if (table.getBaseclassId() == null) {
            return;
        }

        // 基类
        BaseClassEntity baseClass = baseClassService.getById(table.getBaseclassId());
        baseClass.setPackageName(baseClass.getPackageName());
        dataModel.put("baseClass", baseClass);

        // 基类字段
        String[] fields = baseClass.getFields().split(",");

        // 标注为基类字段
        for (TableFieldEntity field : table.getFieldList()) {
            if (ArrayUtil.contains(fields, field.getFieldName())) {
                field.setBaseField(true);
            }
        }
    }

    /**
     * 设置字段分类信息
     *
     * @param dataModel 数据模型
     * @param table     表
     */
    private void setFieldTypeList(Map<String, Object> dataModel, TableEntity table) {
        // 主键列表 (支持多主键)
        List<TableFieldEntity> primaryList = new ArrayList<>();
        // 表单列表
        List<TableFieldEntity> formList = new ArrayList<>();
        // 网格列表
        List<TableFieldEntity> gridList = new ArrayList<>();
        // 查询列表
        List<TableFieldEntity> queryList = new ArrayList<>();

        for (TableFieldEntity field : table.getFieldList()) {
            if (field.isPrimaryPk()) {
                primaryList.add(field);
            }
            if (field.isFormItem()) {
                formList.add(field);
            }
            if (field.isGridItem()) {
                gridList.add(field);
            }
            if (field.isQueryItem()) {
                queryList.add(field);
            }
        }
        dataModel.put("primaryList", primaryList);
        dataModel.put("formList", formList);
        dataModel.put("gridList", gridList);
        dataModel.put("queryList", queryList);
    }

    /**
     * 代码预览
     *
     * @return 预览内容
     */
    @Override
    public List<PreviewVO> previews(Long id) {
        List<PreviewVO> previewList = new ArrayList<>();
        LambdaQueryWrapper<GenListDtlEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GenListDtlEntity::getParentId,id);
        List<GenListDtlEntity> genListDtlEntityList = genListDtlService.list(queryWrapper);
        if (CollectionUtils.isEmpty(genListDtlEntityList)) return previewList;
        List<Long> tableIdList = genListDtlEntityList.stream().map(GenListDtlEntity::getTableId).collect(Collectors.toList());
        Optional<GenListDtlEntity> firstEntity = genListDtlEntityList.stream()
                .filter(entity -> Objects.equals(entity.getMainTableFlag(), 0))
                .findFirst();


        Long MTableId = firstEntity.map(GenListDtlEntity::getTableId).orElse(null);
        if (Objects.isNull(MTableId)) return previewList;
        choseTemplate(tableIdList,MTableId,genListDtlEntityList);

        // 遍历表ID列表
        for (Long tableId : tableIdList) {
            Map<String, Object> dataModel = getDataModel(tableId);
            GeneratorInfo generator = generatorConfig.getGeneratorConfig();
            List<HandlerEntity> handlerList = createHandlerList(tableIdList);
            dataModel.put("handlerList", handlerList);
            // 遍历模板列表
            for (TemplateInfo t : generator.getTemplates()) {
                dataModel.put("templateName", t.getTemplateName());
                String fileName = t.getGeneratorPath().substring(t.getGeneratorPath().lastIndexOf("\\") + 1);
                // 过滤条件
                if (shouldSkipFile(tableId, MTableId, fileName)) {
                    continue;
                }
                String content = TemplateUtils.getContent(t.getTemplateContent(), dataModel);
                String path = TemplateUtils.getContent(t.getGeneratorPath(), dataModel);
                previewList.add(new PreviewVO(TemplateUtils.getContent(fileName, dataModel), content, path));
            }
        }
        return previewList;
    }

    // 创建handlerList
    private List<HandlerEntity> createHandlerList(List<Long> tableIdList) {
        List<TableEntity> tableList = tableService.listByIds(tableIdList);
        List<HandlerEntity> handlerList = new ArrayList<>();

        for (TableEntity table : tableList) {
            HandlerEntity handler = new HandlerEntity();
            handler.setAttrType(table.getClassName() + "Handler");
            handler.setAttrName(StrUtil.lowerFirst(table.getClassName()) + "Handler");
            handlerList.add(handler);
        }

        return handlerList;
    }

    // 判断是否跳过文件
    private boolean shouldSkipFile(Long tableId, Long MTableId, String fileName) {
        int tableNumber = Config.getInstance().getGenListDtlEntityList().size();
        // 仅主表生成
        if (!tableId.equals(MTableId) && TemplateConstant.fileNameList2.contains(fileName)) {
            return true;
        }

        if (tableNumber == 2 && !tableId.equals(MTableId) && TemplateConstant.fileNameList3.contains(fileName)) {
            return true;
        }

        // 仅主表不生成
        return tableId.equals(MTableId) && TemplateConstant.fileNameList10.contains(fileName);
    }

    public void choseTemplate(List<Long> tableIdList,Long MTableId,List<GenListDtlEntity> genListDtlEntityList) {
        if (tableIdList.size() == 2) {
            Config.getInstance().setTemplateFile("ms.json");
        } else {
            Config.getInstance().setTemplateFile("single.json");
        }
        Config.getInstance().setMTableId(MTableId);
        Config.getInstance().setGenListDtlEntityList(genListDtlEntityList);
    }

}