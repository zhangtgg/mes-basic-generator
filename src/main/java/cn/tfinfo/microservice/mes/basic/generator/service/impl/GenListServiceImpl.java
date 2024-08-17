package cn.tfinfo.microservice.mes.basic.generator.service.impl;

import cn.tfinfo.microservice.mes.basic.generator.common.page.PageResult;
import cn.tfinfo.microservice.mes.basic.generator.common.query.Query;
import cn.tfinfo.microservice.mes.basic.generator.common.service.impl.BaseServiceImpl;
import cn.tfinfo.microservice.mes.basic.generator.constant.DeleteFlag;
import cn.tfinfo.microservice.mes.basic.generator.dao.GenListDao;
import cn.tfinfo.microservice.mes.basic.generator.entity.GenListDtlEntity;
import cn.tfinfo.microservice.mes.basic.generator.entity.GenListEntity;
import cn.tfinfo.microservice.mes.basic.generator.entity.TableEntity;
import cn.tfinfo.microservice.mes.basic.generator.service.GenListDtlService;
import cn.tfinfo.microservice.mes.basic.generator.service.GenListService;
import cn.tfinfo.microservice.mes.basic.generator.service.TableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GenListServiceImpl extends BaseServiceImpl<GenListDao, GenListEntity> implements GenListService {
    private final GenListDtlService genListDtlService;
    private final TableService tableService;

    @Override
    public PageResult<GenListEntity> page(Query query) {
        IPage<GenListEntity> page = baseMapper.selectPage(
                getPage(query),
                getGenListWrapper(query)
        );
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public GenListEntity getById(Long id) {
        GenListEntity genListEntity = baseMapper.selectById(id);
        genListEntity.setGenListDtlEntityList(getDetailsByParentId(id));
        return genListEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(GenListEntity genListEntity) {
        List<GenListDtlEntity> list = genListEntity.getGenListDtlEntityList();
        if (CollectionUtils.isNotEmpty(list)) {
            updateTableEntities(genListEntity, list);
        }

        if (Objects.isNull(genListEntity.getId())) {
            return createGenListEntity(genListEntity);
        } else {
            return updateGenListEntity(genListEntity);
        }
    }

    @Override
    public void deleteById(Long id) {
        List<GenListDtlEntity> genListDtlEntityList = getDetailsByParentId(id);
        if (CollectionUtils.isNotEmpty(genListDtlEntityList)) {
            genListDtlService.removeBatchByIds(genListDtlEntityList);
        }
        baseMapper.deleteById(id);
    }

    private List<GenListDtlEntity> getDetailsByParentId(Long parentId) {
        LambdaQueryWrapper<GenListDtlEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(GenListDtlEntity::getParentId, parentId);
        return genListDtlService.list(queryWrapper);
    }

    private void updateTableEntities(GenListEntity genListEntity, List<GenListDtlEntity> list) {
        List<TableEntity> tableEntities = new ArrayList<>();
        for (GenListDtlEntity genListDtlEntity : list) {
            Long tableId = genListDtlEntity.getTableId();
            TableEntity table = tableService.getById(tableId);
            table.setModuleName(genListEntity.getModuleName());
            table.setPackageName(genListEntity.getProjectName());
            tableEntities.add(table);
        }
        tableService.updateBatchById(tableEntities);
    }

    private boolean createGenListEntity(GenListEntity genListEntity) {
        genListEntity.setCreatedAt(new Date());
        super.save(genListEntity);
        if (CollectionUtils.isNotEmpty(genListEntity.getGenListDtlEntityList())) {
            for (GenListDtlEntity dtl : genListEntity.getGenListDtlEntityList()) {
                dtl.setParentId(genListEntity.getId());
            }
        }
        return genListDtlService.saveBatch(genListEntity.getGenListDtlEntityList());
    }

    private boolean updateGenListEntity(GenListEntity genListEntity) {
        List<GenListDtlEntity> genListDtlEntityList = genListEntity.getGenListDtlEntityList();
        List<GenListDtlEntity> addList = new ArrayList<>();
        List<GenListDtlEntity> updateList = new ArrayList<>();
        List<GenListDtlEntity> deleteList = new ArrayList<>();
        classifyDetails(genListDtlEntityList, addList, updateList, deleteList, genListEntity);

        genListDtlService.saveBatch(addList);
        genListDtlService.updateBatchById(updateList);
        genListDtlService.removeBatchByIds(deleteList);

        genListEntity.setUpdatedAt(new Date());
        return super.updateById(genListEntity);
    }

    private void classifyDetails(List<GenListDtlEntity> details, List<GenListDtlEntity> addList, List<GenListDtlEntity> updateList, List<GenListDtlEntity> deleteList, GenListEntity genListEntity) {
        if (CollectionUtils.isEmpty(details)) return;

        for (GenListDtlEntity detail : details) {
            if (DeleteFlag.Y.equals(detail.getDeleteFlag())) {
                deleteList.add(detail);
            } else if (Objects.isNull(detail.getId())) {
                detail.setParentId(genListEntity.getId());
                addList.add(detail);
            } else {
                updateList.add(detail);
            }
        }
    }

}
