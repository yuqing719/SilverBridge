# 🌉 银龄桥 (SilverBridge)
> 社区智慧助老服务平台 | Community Elderly Care Platform

---

## 📖 项目简介 (Introduction)

**银龄桥 (SilverBridge)** 是一款**单系统架构**的社区助老服务平台。我们采用**响应式 Web 设计**，一套代码同时适配电脑端（管理/志愿者）与手机端（老人/子女），降低开发与维护成本，聚焦业务逻辑与社会价值。

本项目为 **软件工程课程** 小组作业。

---

## 🎯 核心目标用户 (Target Users)

| 用户角色 | 登录后的界面差异 | 核心需求 |
| :--- | :--- | :--- |
| 👴 **老人/子女** | 移动端适配布局 (大字体/卡片式) | 平安打卡、健康记录、活动报名 |
| 🎓 **志愿者** | 混合布局 | 接单服务、查看积分、活动签到 |
| 🛡️ **管理员** | 桌面端管理后台 (数据看板) | 用户审核、活动发布、数据统计 |

---

## 🚀 核心功能 (Core Features)

### 1. 🛡️ 每日平安打卡 (Daily Check-in)
- **极简操作**：老人每日点击一次“我平安”按钮。
- **异常预警**：若当日 20:00 前未打卡，子女端显示提醒状态。
- **紧急联系**：内置静态紧急联系人列表，一键调用手机拨号。
- *技术实现：数据库状态更新 + 定时任务检查（或前端逻辑判断）*

### 2. 🤝 志愿服务“时间银行”

- 志愿者发布服务，老人/子女下单。
- 服务完成后积分入账，形成互助闭环。
- *技术实现：订单状态机 + 积分事务处理*

### 3. 🏥 健康档案与用药记录

- 手动录入血压/血糖数值，支持 ECharts 折线图展示趋势。
- 记录用药计划，支持打卡标记。
- *技术实现：CRUD + 前端图表库*

### 4. 🗓️ 社区活动日历
- 大字版日历展示社区活动，支持一键报名。
- 活动现场出示个人二维码，志愿者扫码核销。
- *技术实现：关联表查询 + 二维码生成库*

### 5. 📚 数字课堂与资讯
- 发布大字版文章与视频教程链接。
- 支持后台富文本发布，前端自适应展示。
- *技术实现：内容管理模块 (CMS)*

## 🛠️ 技术栈 (Tech Stack)

### 前端 
- **核心框架:** Vue 3 + Vite
- **UI 组件库:** Element Plus
- **HTTP 客户端:** Axios
- **图表库:** ECharts

### 后端
- **核心框架:** Java Spring Boot 3
- **安全认证:** Spring Security + JWT
- **API 文档:** Swagger / Knif4j

### 数据库
- **关系型数据库:** MySQL 
- **设计范式:** 第三范式，包含用户表、角色表、活动表、订单表等

## 📂 项目结构 (Project Structure)

```text
SilverBridge/
├── silver-bridge-web/       # 前端：统一 Vue3 项目 (含所有角色页面)
│   ├── src/views/elder/     # 老人/子女端页面 
│   ├── src/views/admin/     # 管理员端页面 
│   └── src/components/      # 公共组件 (大字按钮/导航栏)
├── silver-bridge-server/    # 后端：Spring Boot 核心代码
├── docs/                    # 文档：需求、设计、测试报告
├── sql/                     # 数据库：初始化脚本
└── README.md

---

## ✅ 本仓库已实现：用户安全与每日平安监护（可运行 Demo）

> 说明：原 README 描述的是目标“前后端分仓/多模块”结构；但当前工作区只有一个 Maven 工程。
> 我已将其升级为可直接运行的 Spring Boot 单体 Demo（内置 H2 数据库 + 静态页面），覆盖：
> - 老人每日平安打卡（每天一次、防重复、展示打卡时间）
> - 20:00 自动检测未打卡并生成预警（子女端可见）
> - 紧急联系人列表 + `tel:` 一键拨号（移动端可用）
> - 子女端查看老人今日状态与预警

### 运行

在项目根目录执行：

- 启动：`mvn spring-boot:run`
- 测试：`mvn test`

启动后访问：

- 首页：`http://localhost:8080/`
- 老人端：`http://localhost:8080/elder.html?elderId=1`
- 子女端：`http://localhost:8080/child.html?childId=2`

默认演示数据：

- 老人：ID=1（王阿姨）
- 子女：ID=2（小王）

### API（用于验收/联调）

- `POST /api/elders/{elderId}/checkins/today` 今日打卡（重复会返回 409）
- `GET /api/elders/{elderId}/status/today` 老人今日状态
- `GET /api/elders/{elderId}/contacts` 紧急联系人列表
- `GET /api/children/{childId}/elders/status/today` 子女查看绑定老人状态
- `GET /api/children/{childId}/alerts/today` 子女查看今日未打卡预警

### 定时预警

- 服务端定时任务：每天 20:00（Asia/Shanghai 时区）生成未打卡预警
- 你也可以通过调用服务层 `generateMissingCheckinAlerts(date)`（已在测试中覆盖）来验证逻辑