package cn.tfinfo.microservice.mes.basic.generator.utils;

import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.StrUtil;
import cn.tfinfo.microservice.mes.basic.generator.common.exception.ServerException;
import cn.tfinfo.microservice.mes.basic.generator.config.DbType;
import cn.tfinfo.microservice.mes.basic.generator.config.GenDataSource;
import cn.tfinfo.microservice.mes.basic.generator.config.query.AbstractQuery;
import cn.tfinfo.microservice.mes.basic.generator.entity.TableEntity;
import cn.tfinfo.microservice.mes.basic.generator.entity.TableFieldEntity;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器 工具类
 */
@Slf4j
public class GenUtils {

    /**
     * 根据数据源，获取全部数据表
     *
     * @param datasource 数据源
     */
    public static List<TableEntity> getTableList(GenDataSource datasource, String tableName, int pageSize, int page) {
        List<TableEntity> tableList = new ArrayList<>();
        try {
            AbstractQuery query = datasource.getDbQuery();

            String baseSql = query.tableSql(tableName); // 获取基础查询语句，不包含分页

            // 构建带分页的完整查询语句
            String sql = buildPagedQuery(baseSql, datasource.getDbType(), pageSize, page);

            // 查询数据
            try (PreparedStatement preparedStatement = datasource.getConnection().prepareStatement(sql)) {
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    TableEntity table = new TableEntity();
                    table.setTableName(rs.getString(query.tableName()));
                    table.setTableComment(rs.getString(query.tableComment()));
                    table.setDatasourceId(datasource.getId());
                    tableList.add(table);
                }
            }

            datasource.getConnection().close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return tableList;
    }

    // 根据数据库类型构建带分页的查询语句
    private static String buildPagedQuery(String baseSql, DbType dbType, int pageSize, int page) {
        StringBuilder builder = new StringBuilder(baseSql);

        if (DbType.MySQL.equals(dbType)) {
            builder.append(" LIMIT ").append(pageSize).append(" OFFSET ").append((page - 1) * pageSize);
        } else if (DbType.Oracle.equals(dbType)) {
            builder.insert(0, "SELECT * FROM (SELECT t.*, ROWNUM AS rn FROM (");
            builder.append(") t WHERE ROWNUM <= ").append(page * pageSize);
            builder.append(") WHERE rn > ").append((page - 1) * pageSize);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }

        return builder.toString();
    }


    /**
     * 根据数据源，获取指定数据表
     *
     * @param datasource 数据源
     * @param tableName  表名
     */
    public static TableEntity getTable(GenDataSource datasource, String tableName) {
        try {
            AbstractQuery query = datasource.getDbQuery();

            // 查询数据
            PreparedStatement preparedStatement = datasource.getConnection().prepareStatement(query.tableSql(tableName));
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                TableEntity table = new TableEntity();
                table.setTableName(rs.getString(query.tableName()));
                table.setTableComment(rs.getString(query.tableComment()));
                table.setDatasourceId(datasource.getId());
                return table;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        throw new ServerException("数据表不存在：" + tableName);
    }


    /**
     * 获取表字段列表
     *
     * @param datasource 数据源
     * @param tableId    表ID
     * @param tableName  表名
     */
    public static List<TableFieldEntity> getTableFieldList(GenDataSource datasource, Long tableId, String tableName) {
        List<TableFieldEntity> tableFieldList = new ArrayList<>();

        try {
            AbstractQuery query = datasource.getDbQuery();
            String tableFieldsSql = query.tableFieldsSql();
            if (datasource.getDbType() == DbType.Oracle) {
                DatabaseMetaData md = datasource.getConnection().getMetaData();
                tableFieldsSql = String.format(tableFieldsSql.replace("#schema", md.getUserName()), tableName);
            } else {
                tableFieldsSql = String.format(tableFieldsSql, tableName);
            }
            PreparedStatement preparedStatement = datasource.getConnection().prepareStatement(tableFieldsSql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                TableFieldEntity field = new TableFieldEntity();
                field.setTableId(tableId);
                field.setFieldName(rs.getString(query.fieldName()));
                String fieldType = rs.getString(query.fieldType());
                if (fieldType.contains(" ")) {
                    fieldType = fieldType.substring(0, fieldType.indexOf(" "));
                }
                field.setFieldType(fieldType);
                field.setFieldComment(rs.getString(query.fieldComment()));
                String key = rs.getString(query.fieldKey());
                field.setPrimaryPk(StringUtils.isNotBlank(key) && "PRI".equalsIgnoreCase(key));
                if (filter(field)) {
                    tableFieldList.add(field);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return tableFieldList;
    }

    private static boolean filter(TableFieldEntity field) {
        return switch (field.getFieldName()) {
            case "CREATE_BY", "UPDATE_BY", "CREATE_DATE", "UPDATE_DATE", "DEL_FLAG", "REMARKS" -> false;
            default -> true;
        };
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName) {
        return StrUtil.subAfter(packageName, ".", true);
    }

    /**
     * 获取功能名，默认使用表名作为功能名
     *
     * @param tableName 表名
     * @return 功能名
     */
    public static String getFunctionName(String tableName) {
        return tableName;
    }

    /**
     * 表名转驼峰并移除前后缀
     *
     * @param upperFirst   首字母大写
     * @param tableName    表名
     * @param removePrefix 删除前缀
     * @param removeSuffix 删除后缀
     * @return java.lang.String
     */
    public static String camelCase(boolean upperFirst, String tableName, String removePrefix, String removeSuffix) {
        String className = tableName;
        // 移除前缀
        if (StrUtil.isNotBlank(removePrefix)) {
            className = StrUtil.removePrefix(tableName, removePrefix);
        }
        // 移除后缀
        if (StrUtil.isNotBlank(removeSuffix)) {
            className = StrUtil.removeSuffix(className, removeSuffix);
        }
        // 是否首字母大写
        if (upperFirst) {
            return NamingCase.toPascalCase(className);
        } else {
            return NamingCase.toCamelCase(className);
        }
    }

    public static Integer getTableCount(GenDataSource datasource,String tableName) {
        int count = 0;
        try {
            AbstractQuery query = datasource.getDbQuery();
            // 查询数据
            try (PreparedStatement preparedStatement = datasource.getConnection().prepareStatement(query.selectCount(tableName))) {
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return count;
    }

    public String tableSql(String tableName, int page, int pageSize, DbType dbType) {
        StringBuilder sql = new StringBuilder();
        sql.append("select table_name, table_comment from information_schema.tables ");
        sql.append("where table_schema = (select database()) ");
        // 表名查询
        if (StrUtil.isNotBlank(tableName)) {
            sql.append("and table_name = '").append(tableName).append("' ");
        }
        sql.append("order by table_name asc ");

        if (DbType.MySQL.equals(dbType)) {
            sql.append("LIMIT ").append(pageSize).append(" ");
            sql.append("OFFSET ").append((page - 1) * pageSize);
        } else if (DbType.Oracle.equals(dbType)) {
            // Oracle 分页查询
            sql.insert(0, "SELECT * FROM (SELECT t.*, ROWNUM AS rn FROM (");
            sql.append(") t WHERE ROWNUM <= ").append(page * pageSize);
            sql.append(") WHERE rn > ").append((page - 1) * pageSize);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }

        return sql.toString();
    }
}
