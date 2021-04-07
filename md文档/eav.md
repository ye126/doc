## EAV(Entity-Attribute-Value)自定义表单介绍 
###  一.如何使用EAV
#### 1.重写entityName()
** 服务类（com.jfeat.*.services.domain.service.impl.XXXImpl）重写entityName()方法，建议为实体名称 **
在继承了CRUDServiceOnlyImpl<>类的子类处添加
```
    @Override
    protected String entityName() {
        return "XXX";
    }
```

#### 2.启用META.EAV
**启用配置，在启动类（XXXApplication.java）的main方法添加如下代码**
`META.enabledEav(true);`

#### 3.使用注解启用列表的EAV功能
**在对应查询列表方法处打上注解**
`@EavAnnotation("testSaasEntity")`

#### 4.配置表配置
**在t_eav_entity加上一条对应的配置**

|t_eav_entity表中的字段|描述|
|:--:|:--:|
|entity_name|对应步骤1中重写的的entityName()|
|table_name|对应实体的表名|

#### 5.增删查改过程中使用自定义字段

**除了查询列表以外，其他的增删查改方法必须使用对应的方法进行，不然无法使用自定义字段**

| 类型  | 操作  |
| :------------: | :------------: |
| 增  | 调用createMaster()方法新增实体  |
| 删  |  deleteMaster（）方法 |
| 改  |  updateMaster（）方法 |
| 查详情  | retrieveMaster（）方法 |
| 查列表方法  |  在列表上面打上注解 `@EavAnnotation("entityName")`  |


<br>
### 二.EAV自定义表单介绍
#### 1.EAV介绍
作用：用户可以通过EAV直接增加某个实体的字段，并可以对这些字段进行基本的CRUD。


#### 2.数据库介绍
|表名|描述|
|:--:|:--:|
|t_eav_entity|实体表，用于确定要对哪个实体进行拓展，在对应实体出配置entityName后需要在本表中配置entity_name和table_name|
|t_eav_attribute|字段表，用于存储实体拓展的自定义字段|
|t_eav_value|值表，用于存储字段表中不同字段对应的值 根据row_id确定对应原表的id|
|t_eav_config|配置表，待补充，测试EAV过程中没用到|


#### 3.EAV的实现
##### 3.1 EAV组成

|功能点|位置|描述|
|:--:|:--:|:--:|
|自定义表单结构|uaas模块中的eav|EAV 4张基础表的CRUD|
|实体的CRUD增强|crud-plus|实体表进行增删查改的时候同时对自定义表单的值进行更改|
##### 3.2 自定义表单结构-功能点介绍
基本和其他模块的增删查改相同，同时数据进行了租户间的隔离
##### 3.3 实体的CRUD增强-功能点介绍

EAV 几个主要的类介绍

1.EavProxy为EAV功能的入口
2.EavSqlUtil定义了查询和删除的SQL
3.AttributeRecord 字段的包装类
4.EavDatabaseUtil 工具类 封装了EAV相关的数据库操作

5.EavAnnotation 注解，用于标注EavAop要拦截的类
6.EavAop 实现查询列表功能



<br>
##### 3.4 传递/返回数据
** 查询详情 **
```
//返回数据
{
  "code": 200,
  "data": {
    "createdTime": "2020-08-07 16:40:28",
    "extra": {
      "items": [
        {
          "attributeId": 1,
          "fieldName": "时间",
          "attr": "time",
          "fieldType": "T",
          "value": ""
        },
        {
          "attributeId": 2,
          "fieldName": "花费",
          "attr": "cost",
          "fieldType": "M",
          "value": ""
        },
        {
          "attributeId": 3,
          "fieldName": "数量",
          "attr": "number",
          "fieldType": "N",
          "value": ""
        },
        {
          "attributeId": 4,
          "fieldName": "昵称",
          "attr": "nick",
          "fieldType": "S",
          "value": ""
        }
      ]
    },
    "id": "2",
    "images": [],
    "name": "string",
    "orgId": "100000000000000010",
    "status": "string",
    "tags": "[]"
  },
  "message": "Success"
}
```

** 修改 **
```
//请求参数
{
    "createdTime": "2020-08-07 16:40:28",
    "extra": {
      "items": [
        {
          "attributeId": 1,
          "attr": "time",
          "value": "223"
        },
        {
          "attributeId": 2,
          "attr": "cost",
          "value": "444"
        },
        {
          "attributeId": 3,
          "attr": "number",
          "value": "222"
        },
        {
          "attributeId": 4,
          "attr": "nick",
          "value": "222"
        }
      ]
    },
    "id": "2",
    "name": "string",
    "status": "string"
  }
```

##### 2.4.注解介绍
```
@EavAnnotation
//暂时只能用于列表的EAV查询
//之后考虑通过增加属性type=?来进行拓展
```
加了这个注解后，对应方法会被crud-plus模块中EavAop增强
对应列表的返回信息中会增加和EAV相关的 "extra": {}字段

|属性|描述|
|:--:|:--:|
|value|用于确定entityName|

** 加注解后列表返回信息示例 **
```
{
  "code": 200,
  "data": {
    "orderByField": null,
    "openSort": true,
    "offset": 0,
    "optimizeCountSql": true,
    "records": [
      {
        "orgUser": false,
        "isOrgUser": false,
        "name": "string",
        "platformUser": false,
        "createdTime": "2020-08-07 16:40:02",
        "normalUser": false,
        "id": 1,
        "isPlatformUser": false,
        "orgId": "100000000000000010",
        "isNormalUser": false,
        "status": "string"
      },
      {
        "orgUser": false,
        "isOrgUser": false,
        "extra": {
          "nick": "222",
          "number": "222",
          "cost": "444",
          "time": "223"
        },
        "name": "string",
        "platformUser": false,
        "createdTime": "2020-08-07 16:40:28",
        "normalUser": false,
        "id": 2,
        "isPlatformUser": false,
        "orgId": "100000000000000010",
        "isNormalUser": false,
        "status": "string"
      }
    ],
    "searchCount": true,
    "ascs": null,
    "asc": true,
    "condition": null,
    "current": 1,
    "descs": null,
    "total": 14,
    "pages": 2,
    "size": 10,
    "limit": 2147483647
  },
  "message": "Success"
}
```

