package cn.tfinfo.microservice.mes.basic.generator.config.query;


import cn.tfinfo.microservice.mes.basic.generator.config.DbType;

/**
 * Query
 */
public interface AbstractQuery {

    /**
     * 数据库类型
     */
    DbType dbType();

    /**
     * 表信息查询 SQL
     */
    String tableSql(String tableName);

    /**
     * 表名称
     */
    String tableName();

    /**
     * 表注释
     */
    String tableComment();

    /**
     * 表字段信息查询 SQL
     */
    String tableFieldsSql();

    /**
     * 字段名称
     */
    String fieldName();

    /**
     * 字段类型
     */
    String fieldType();

    /**
     * 字段注释
     */
    String fieldComment();

    /**
     * 主键字段
     */
    String fieldKey();

    default String selectCount(String tableName) {
        return "select count(1) from information_schema.tables " +
                "where table_schema = (select database()) " +
                "and table_name like '%" + tableName + "%'";
    }
}
