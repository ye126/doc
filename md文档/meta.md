### 当前项目的测试文档位于：git@github.com:zelejs/saas-test-cases.git 项目的meta文件夹中
<br>
## 状态模块说明
### 一.使用方式

#### 1. 项目依赖

```
    <groupId>com.jfeat</groupId>
    <artifactId>mbcs</artifactId>
    <version>1.1.0</version>
```

#### 2. 初始化数据库

```SQL

DROP TABLE IF EXISTS `meta_status_machine`;

CREATE TABLE `meta_status_machine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entity` varchar(64) NOT NULL COMMENT '实体',
  `entity_table_name` varchar(64) NOT NULL COMMENT '实体对应的表名',
  `from_status` varchar(64) NOT NULL COMMENT '原状态',
  `to_status` varchar(64) NOT NULL COMMENT '下一个状态',
  `permission` varchar(64) DEFAULT NULL COMMENT '操作权限控制',
  PRIMARY KEY (`id`),
  UNIQUE KEY `entity` (`entity`,`from_status`,`to_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `meta_workflow_lite_activity`;
CREATE TABLE `meta_workflow_lite_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entity` varchar(64) NOT NULL COMMENT '实体',
  `entity_id` bigint(20) NOT NULL COMMENT '实体id',
  `note` varchar(256) DEFAULT NULL COMMENT '意见',
  `from_status` varchar(64) NOT NULL COMMENT '原状态',
  `to_status` varchar(64) NOT NULL COMMENT '下一个状态',
  `created_by_id` bigint(20) NOT NULL COMMENT '创建者id',
  `created_by` varchar(64) NOT NULL COMMENT '创建者名称',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `field1` varchar(128) DEFAULT NULL COMMENT '保留字段1',
  `field2` varchar(128) DEFAULT NULL COMMENT '保留字段2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工作流记录表';

```
<br>
数据库说明

|表名|说明|
|:--:|:--:|
|meta_status_machine|表用于存储配置信息|
|meta_workflow_lite_activity|表用于记录用户的操作|

<br>


#### 3. 对单个实体进行配置

api: POST /api/meta/entity/{entity}/status
参数说明：

|参数|参数位置|说明|
|:-:|:--:|:--:|
|entity|URL|配置标识|
|entityTableName|请求体|表名|
|fromStatus|请求体|当前状态|
|toStatus|请求体|下一个状态|

参数示例：
```

localhost:8088/api/meta/entity/test/status

{
  "entityTableName": "meta_status_test_table",
  "fromStatus": "MIDDLE",
  "toStatus": "SUPPER"
}
```

**内部已定义的3个类型**

|类型|说明|
|:-:|:--:|
|START|开始标识，定义第一个状态的时候需要定义 from_status=START to_status={实体第一个状态}|
|CANCEL|关闭标识 当配置中to_status为CANCEL 则表示当前状态可以关闭|
|END|结束标识 to_status为END 用于表示流程的结束|



#### 4.开始使用

##### 1.基本流程

```flow
st=>start: 开始
op3=>operation: 配置流程 至少配置 2个配置
其中一个from_status为START
另外一个to_status为END
op=>operation: 操作 ：通过/关闭/回退
cond=>condition: 实体的当前状态
是否有对应配置
sub1=>subroutine: 抛出异常
cond2=>condition: 是否存在多个
（to_status为
CANCEL时不算多个）
io=>inputoutput: 输入输出框
op2=>operation: 按照配置进行改变
将目标表中的status 从from_status改为to_status


st->op3->op->cond
cond(yes)->cond2
cond(no)->sub1
cond2(yes)->op2(right)
cond2(no)->sub1
```
<br><br><br><br>

##### 2.对于关闭和回退操作的额外说明

**关闭操作：**

` POST /api/meta/entity/{entity}/entities/{id}/action/cancel `

|参数|参数位置|说明|
|:-:|:--:|:--:|
|entity|URL|配置的entity 用于搜索配置|
|id|URL|实体的id|
|note|请求体|备注|

参数示例：
```
http://localhost:8088/api/meta/entity/test/entities/1/action/cancel
body:
{
  "note": "备注"
}
```


根据entity实体当前的状态 找到当前状态配置汇总to_status为CANCEL的配置
未找到则会报错
有配置则会将对应实体的状态改为CANCEL


**回退操作:**

` POST /api/meta/entity/{entity}/entities/{id}/action/back `

|参数|参数位置|说明|
|:-:|:--:|:--:|
|entity|URL|配置的entity 用于搜索配置|
|id|URL|实体的id|
|note|请求体|备注|



参数示例：
```
http://localhost:8088/api/meta/entity/test/entities/1/action/cancel
body:
{
  "note": "备注"
}
```


根据entity找到from_status为END 或 CANCEL的配置 然后将目标实体的状态改为对应的to_status的配置
目前只支持对于END和CANCEL状态下的回退






















