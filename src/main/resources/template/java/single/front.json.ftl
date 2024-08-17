{
  "module": "",
  "menu": "",
  "api": {
    "list": "/api/${r"${System.SYSTEM_KONKA_API}"}/${className}/findList",
    "add": "/api/${r"${System.SYSTEM_KONKA_API}"}/${className}/save",
    "update": "/api/${r"${System.SYSTEM_KONKA_API}"}/${className}/save",
    "remove": "/api/${r"${System.SYSTEM_KONKA_API}"}/${className}/deleteByIds",
    "detail": "/api/${r"${System.SYSTEM_KONKA_API}"}/${className}/queryDetail"
  },
  "menuMap": {
    "id": "",
    "name": "",
    "label": ""
  },
  "view": {
    "onlyQuery": false,
    "index": {
      "queryPannel": [
      <#list fieldList as field>
          <#if !field.baseField && field.fieldName != "ID">
            {"label": "${field.fieldComment}","type": "input","model": "${field.attrName}","value": ""}<#if field_has_next>,</#if>
          </#if>
      </#list>
      ],
      "table": {
        "tableHeaders": [
            <#list fieldList as field>
                <#if !field.baseField && field.fieldName != "ID">
                    {"name": "${field.fieldComment}","code": "${field.attrName}","width": "100"}<#if field_has_next>,</#if>
                </#if>
            </#list>
        ]
      }
    },
    "info": {
      "baseInfo": [
        <#list fieldList as field>
            <#if !field.baseField && field.fieldName != "ID">
            {"label": "${field.fieldComment}","type": "input","model": "${field.attrName}"}<#if field_has_next>,</#if>
            </#if>
        </#list>

      ],
      "pannelList": [
        {
          "title": "",
          "model": "details",
          "type": "commonTable",
          "dialogList": [
            {
              "type": "add"
            }
          ],
          "tableHeaders": [
            <#list fieldList as field>
                <#if !field.baseField && field.fieldName != "ID">
                {"name": "${field.fieldComment}","code": "${field.attrName}","type": "input","width": 100}<#if field_has_next>,</#if>
                </#if>
            </#list>
          ]
        }
      ]
    }
  }
}
