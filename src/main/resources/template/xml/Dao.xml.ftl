<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="cn.tfinfo.microservice.${package}.persistance.dao.${moduleName}.${ClassName}Dao">

    <!--字段列表-->
    <sql id="Base_Column">
        <#list fieldList as field>
        A.${field.fieldName},
        </#list>
        A.DEL_FLAG,
        A.CREATE_DATE,
        A.UPDATE_DATE,
        A.CREATE_BY,
        A.UPDATE_BY,
        A.REMARKS
    </sql>

    <!--新增-->
    <insert id="insert">
        INSERT INTO ${tableName}(
        <#list fieldList as field>
        ${field.fieldName},
        </#list>
        CREATE_DATE,
        UPDATE_DATE,
        CREATE_BY,
        UPDATE_BY,
        REMARKS,
        DEL_FLAG
        )
        values (
        <#list fieldList as field>
        ${r'#{'}${field.attrName}${r'}'},
        </#list>
        sysdate,
        sysdate,
        ${r'#{createBy}'},
        ${r'#{updateBy}'},
        ${r'#{remarks}'},
        '0'
        )
    </insert>

    <!--更新-->
    <update id="update">
        UPDATE
        ${tableName}
        SET
        <#list fieldList as field>
        ${field.fieldName} = ${r'#{'}${field.attrName}${r'}'},
        </#list>
        REMARKS = ${r'#{remarks}'},
        UPDATE_BY = ${r'#{updateBy}'},
        UPDATE_DATE = sysdate
        where
        ID = ${r'#{id}'}
    </update>

    <!--更新状态-->
    <update id="updateStatus">
        UPDATE
        ${tableName}
        SET
        STATUS = ${r'#{status}'},
        UPDATE_BY = ${r'#{updateBy}'},
        UPDATE_DATE = sysdate
        WHERE ID IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            ${r'#{id}'}
        </foreach>
    </update>

    <!--批量删除-->
    <update id="deleteByIds">
        UPDATE
        ${tableName}
        SET
        DEL_FLAG = '1',
        UPDATE_BY = ${r'#{userId}'},
        UPDATE_DATE = sysdate
        WHERE
        ID IN
        <foreach collection="ids" item="item" open="(" separator="," close=")">
            ${r'#{item}'}
        </foreach>
    </update>

    <update id="deleteByParentIds">
        UPDATE
        ${tableName}
        SET
        DEL_FLAG = '1',
        UPDATE_BY = ${r'#{userId}'},
        UPDATE_DATE = sysdate
        WHERE
        PARENT_ID IN
        <foreach collection="ids" item="item" open="(" separator="," close=")">
            ${r'#{item}'}
        </foreach>
    </update>

    <!--====================================================↓↓↓↓↓↓查询操作↓↓↓↓↓↓==========================================================-->

    <!--分页查询-->
    <select id="queryList" resultType="cn.tfinfo.microservice.${package}.common.entity.${moduleName}.${ClassName}Entity">
        SELECT
        <include refid="Base_Column"/>
        FROM  ${tableName} A
        WHERE 1 = 1
        <#list fieldList as field>
        <if test="${field.attrName} != null and ${field.attrName} != ''">
        and A.${field.fieldName} like CONCAT('%',CONCAT(${r'#{'}${field.attrName}${r'}'},'%'))
        </if>
        </#list>
        <if test="startDate != null and startDate != ''">
            and to_char(A.CREATE_DATE,'yyyy-MM-dd HH24:MI:SS') >= ${r'#{startDate}'}
        </if>
        <if test="endDate  != null and endDate != '' ">
            and to_char(A.CREATE_DATE,'yyyy-MM-dd HH24:MI:SS') <![CDATA[ <= ]]> ${r'#{endDate}'}
        </if>
        and A.DEL_FLAG = '0'
        ORDER BY A.UPDATE_DATE DESC
    </select>

    <!--根据id查询-->
    <select id="queryDetail" resultType="cn.tfinfo.microservice.${package}.common.entity.${moduleName}.${ClassName}Entity">
        SELECT
        <include refid="Base_Column"/>
        FROM  ${tableName} A
        where A.ID = ${r'#{id}'}
        AND A.DEL_FLAG = '0'
    </select>

    <!--根据parentId查询-->
    <select id="queryListByParentId"
            resultType="cn.tfinfo.microservice.${package}.common.entity.${moduleName}.${ClassName}Entity">
        select
        <include refid="Base_Column"/>
        FROM  ${tableName} A
        where A.PARENT_ID = ${r'#{id}'}
        and A.DEL_FLAG = '0'
    </select>


    <!--====================================================↓↓↓↓↓↓批量操作↓↓↓↓↓↓==========================================================-->

    <!--批量插入-->
    <insert id="saveBatch">
        INSERT ALL
        <foreach collection="list" item="item">
            INTO ${tableName} (
            <#list fieldList as field>
            ${field.fieldName},
            </#list>
            CREATE_DATE,
            UPDATE_DATE,
            CREATE_BY,
            UPDATE_BY,
            REMARKS,
            DEL_FLAG
            )
            VALUES (
            <#list fieldList as field>
            ${r'#{item.'}${field.attrName}${r'}'},
            </#list>
            sysdate,
            sysdate,
            ${r'#{item.createBy}'},
            ${r'#{item.updateBy}'},
            ${r'#{item.remarks}'},
            '0'
            )
        </foreach>
        SELECT 1 FROM DUAL
    </insert>

    <!--批量更新-->
    <update id="updateBatch">
        <foreach collection="list" index="index" item="item" open="begin" close=";end;" separator=";">
            UPDATE ${tableName}
            <set>
            <#list fieldList as field>
            <if test=" ${r'#{item.'}${field.attrName}${r'}'} != null">
                ${field.fieldName} = ${r'#{item.'}${field.attrName}${r'}'},
            </if>
            </#list>
            UPDATE_DATE = sysdate,
            UPDATE_BY = ${r'#{item.updateBy}'},
            REMARKS = ${r'#{item.remarks}'}
            </set>
            WHERE
            DEL_FLAG = '0' AND
            ID = ${r'#{item.id}'}
        </foreach>
    </update>


</mapper>