## 简单前端配置介绍

### 增加页面介绍

``` json
{
  "layout": "TitleContent",
  "title": "添加项目",
  "items": []
}
```

* layout: 页面布局格式
* title: 标题
* items: 具体内容

  

items里面的代码:

``` json
     { 
       "layout": "Empty",
       "component": "Form",
       "config": {}
     }
````

items里面有三个属性

*  layout: 布局样式
*  component: 表格类型
*  config: 具体字段

config 里面的代码:

``` 
  {
       "layout": "Grid",
       "layoutConfig": {
          "layoutType": "inline",
          "value": [12, 12] 
       }
       "fields":[],
       "API":{"createAPI": API.createAPI }
}
````

*  layout：表格布局样式
*  layoutConfig: 对应布局下的具体配置
*  fields: 表格字段
*  Api: 调用的api

layout内可选的属性：

* Empty 不使用布局
* Grid 网格布局

> layout 是可选配置, 默认值为 `Empty` 

layoutConfig

layoutType:

*   inline 水平行内布局布局, layout Grid 的默认布局方式
*   horizontal 水平布局
*   vertical 垂直布局

value:

> 在网格布局中一行设为24列
> [12, 12]这个值设置了一行中 第一个字段占12列 第二个字段占12列
> 如果要改为一行有四个字段则改为：[6, 6, 6, 6]

##### fields详解

一个fields中有多个用户可以填的字段  不同的字段间用逗号隔开

``` 
 "fields": [
    { "label": "字段1" ……},
    { "label": "字段2" ……},
    { "label": "字段3" ……},
    { "label": "字段4" ……}
]
```

``` 
  {
    "label": "合同金额",
    "field": "contractAmount",
    "type": "input",
    "props": {
      "placeholder": "请输入合同金额"
    },
    "rules": [
      "required"
    ]
  ,
````

下面对fields里面的字段进行解释

* label: 标签 位于组件左侧 用于显示给用户看的值
* field: 字段 对应api字段
* type: 类型  当前组件的类型
* props 属性 目前只用到了 placeholder 用于在文本框为空的时候进行提示
* rules 规则 "required" 非空, "phone" 校验手机格式, "email" 校验邮箱格式

###### type类型详解

> input 基本输入框
> palin 文本框
> select 列表单选框
>> select 类型需要进行额外的配置 配置options
>> "type": "select", 
> >

``` 
      options: [
       { label: '社保', value: '社保' },
       { label: '租金', value: '租金' }
       ],
```

> * date  日期选择框
> * radio 按钮单选框
>> radio 类型需要进行额外的配置 配置options
>> 配置方式与select相同

> * text-area 文本域 可换行的输入框
> * modal-checkbox 模态框-多选列表

> * modal-radio    模态框-单选列表
>> modal-radio 和 modal-checkbox  类型需要进行额外的配置
> >

 `

``` 
 "options": {
    "title": "选择项目",
    "label": "name",
    "editLabel": "name",
    "value": "id",
    pagination: true,
    "API": "/api/crud/project/projects",
    "fields": [
      {
        "label": "名称",
        "field": "name",
        valueType: 'ellipsis'
      },
      {
        "options": {
          "map": {
            "NOT_START": "未出工",
            "WORK_IN_PROCESS": "检测中",
            "CHECK_COMPLETED": "检测完成",
            "REPORT_ALREADY": "已出报告",
            "NORMAL_ENDED": "正常结束",
            "FORCED_ENDED": "强制结束"
          },
          "color": {
            "NOT_START": "#888",
            "WORK_IN_PROCESS": "#777",
            "CHECK_COMPLETED": "#1892ff",
            "REPORT_ALREADY": "#1893ff",
            "NORMAL_ENDED": "#1894ff",
            "FORCED_ENDED": "#1895ff"
          }
        },
        "className": "",
        "valueType": "tag",
        "field": "status",
        "label": "状态"
      },
    ]
}
 ````

 >> * pagination: true 启用分页
 >> * label 显示的值
 >> * editLabel  使用表中的值修改显示的值
 >> * value 传入后端的字段
 >> * API 调用的api
 >> * fields 显示的字段 fields使用的格式按照查询页面fields字段的格式填写

> number     数字输入框

简单页面例子

``` js
module.exports = {
  "layout": "TitleContent",
  "title": "收款管理",
  "items": [
    {
      "layout": "Empty",
      "component": "Form",
      "config": {
        "layout": "Grid",
        "layoutConfig": {
          "layoutType": "horizontal", // 默认值 inline
        },
        "fields": [
          {
            "label": "项目",
            "field": "projectId",
            "type": "modal-radio",
            "props": {},
            "rules": [
              "required"
            ],
            "options": {
              "title": "选择数据",
              "label": "name",
              "editLabel": "name",
              "value": "id",
              pagination: true,
              "API": "/api/crud/project/projects",
              "fields": [
                {
                  "label": "名称",
                  "field": "name"
                }
              ]
            }
          },
          {
            "label": "付款金额",
            span: 24,
            "field": "paymentAmount",
            "type": "input",
            "props": {
              "placeholder": "请输入……"
            },
            "rules": [
              "required"
            ]
          },
          {
            "label": "备注",
            span: 24,
            "field": "note",
            "type": "text-area",
            "props": {
              "placeholder": "请输入……"
            },
            "rules": []
          }
        ],
        "API": {
          "createAPI": API.createAPI
        }
      }
    }
  ],
}

```

前端请求信息：

``` JSON
{"projectId":237,"paymentAmount":"1000","note":"5555"}
```

API返回信息：

``` JSON
{"code":200,"message":"Success","data":1}
```
