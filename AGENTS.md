# AGENTS.md

## 项目概述

**项目名称**: MomentKeeper (手帐应用)  
**平台**: Android  
**语言**: Kotlin  
**主要功能**: 用户可以在空白画布上添加、拖动和放置贴纸，记录生活中的美好瞬间

---

## 项目架构

### 核心组件

#### 1. **MainActivity.kt** - 主活动控制器
- **职责**: 应用的入口点，协调各个UI组件的交互
- **关键功能**:
    - 管理 DrawerLayout（侧边栏抽屉）
    - 控制贴纸容器的显示/隐藏
    - 处理顶部和底部按钮的切换逻辑
    - 响应画布点击事件打开侧边栏
- **当前实现状态**: ✅ 已实现基础框架
- **待开发**: 与其他组件的集成

#### 2. **数据模型层** (`model/`)
- **Sticker.kt**: 贴纸数据类
    - 应包含: id, 图片资源, 位置(x, y), 缩放比例, 旋转角度等
    - **状态**: 📝 待实现

- **JournalData.kt**: 手帐数据类
    - 应包含: 标题, 日期/时间, 事件描述, 关联的贴纸列表
    - **状态**: 📝 待实现

#### 3. **UI组件层** (`ui/`)

##### 画布相关 (`ui/canvas/`)
- **CanvasView.kt**: 自定义画布视图
    - 功能: 提供空白画布，接收贴纸放置
    - 交互: 支持手势操作（点击、拖动）
    - **状态**: 📝 待实现

- **StickerView.kt**: 单个贴纸视图
    - 功能: 可拖动、缩放、旋转的贴纸
    - 实现: 需要自定义 View 或使用触摸监听器
    - **状态**: 📝 待实现

##### 贴纸容器相关 (`ui/sticker/`)
- **StickerContainer.kt**: 贴纸选择容器
    - 功能: 显示可用贴纸列表
    - 布局: 可能使用 RecyclerView 或 GridLayout
    - **状态**: 📝 待实现

- **StickerAdapter.kt**: 贴纸列表适配器
    - 功能: 为 RecyclerView 提供贴纸数据
    - **状态**: 📝 待实现

##### 侧边栏相关 (`ui/sidebar/`)
- **InfoSidebar.kt**: 信息侧边栏
    - 功能: 显示和编辑标题、时间、事件等元数据
    - 位置: DrawerLayout 的左侧抽屉
    - **状态**: 📝 待实现

#### 4. **工具类** (`util/`)
- **StickerData.kt**: 预设贴纸数据
    - 功能: 提供内置贴纸资源列表
    - **状态**: 📝 待实现
---

## 开发指南

### 为 AI 助手的说明

当协助开发此项目时，请注意：

#### ✅ 已完成的部分
1. MainActivity 基础框架
2. 基本的视图绑定和监听器设置
3. 贴纸容器显示/隐藏逻辑
4. 侧边栏打开逻辑
5. 返回键处理（关闭抽屉）

#### 🚧 需要实现的核心功能

**优先级 P0（核心功能）**:
1. **CanvasView** - 可交互的画布
    - 支持添加贴纸视图
    - 处理触摸事件（区分点击和拖动）
    - 管理贴纸的层级关系

2. **StickerView** - 可操作的贴纸
    - 实现拖动功能（触摸移动）
    - 可选：缩放和旋转（双指手势）
    - 视觉反馈（选中状态）

3. **贴纸数据和资源**
    - 定义 Sticker 数据类
    - 准备贴纸图片资源（drawable）
    - 创建 StickerData 工具类

**优先级 P1（重要功能）**:
4. **StickerContainer & Adapter**
    - 使用 RecyclerView 展示贴纸
    - 点击贴纸添加到画布
    - 网格布局展示

5. **InfoSidebar**
    - 标题输入框
    - 日期/时间选择器
    - 事件描述文本框

**优先级 P2（增强功能）**:
6. **数据持久化**
    - 保存手帐到本地（SharedPreferences 或 Room）
    - 加载已保存的手帐

7. **高级交互**
    - 贴纸删除功能
    - 撤销/重做
    - 导出为图片

#### 🎨 UI/UX 建议
- **主题色**: 温暖、柔和的色调（适合手帐风格）
- **贴纸样式**: 可爱、卡通风格或简约线条图标
- **动画**: 平滑的过渡动画（贴纸容器展开/收起）
- **触觉反馈**: 适当的震动反馈

#### 📦 依赖建议
```gradle
// 如需添加的依赖
dependencies {
    // 已有的 Material Design
    implementation 'com.google.android.material:material:1.x.x'
    implementation 'androidx.cardview:cardview:1.x.x'
    
    // 可能需要的额外依赖
    // implementation 'androidx.recyclerview:recyclerview:1.x.x'
    // implementation 'com.github.chrisbanes:PhotoView:2.x.x' // 如需高级手势
    // implementation 'androidx.room:room-runtime:2.x.x' // 如需本地数据库
}
```

---

## 代码约定

### Kotlin 代码风格
- 使用驼峰命名法
- 视图变量使用前缀（如 `btn`, `tv`, `iv` 等）
- 优先使用 `lateinit` 而非可空类型（视图绑定）
- 为复杂逻辑添加中文注释

### 架构模式
- 当前使用: 简单的 MVC 模式
- 可考虑升级: MVVM（如项目复杂度增加）

### 资源命名
- ID: 使用完整描述性名称（如 `btnToggleStickerTop`）

---

## 常见开发任务

### 添加新贴纸
1. 在 `res/drawable/` 添加 SVG/XML 或 PNG 图片
2. 更新 `StickerData.kt` 中的贴纸列表
3. 如需分类，在数据模型中添加 `category` 字段

### 实现新的画布功能
1. 修改 `CanvasView.kt`
2. 更新触摸事件处理逻辑
3. 测试与 MainActivity 的交互

### 修改侧边栏内容
1. 编辑 `view_info_sidebar.xml` 布局
2. 在 `InfoSidebar.kt` 中绑定视图和处理逻辑
3. 通过接口或回调与 MainActivity 通信

---

## 测试建议

### 手动测试清单
- [x] 顶部/底部按钮能正确切换贴纸容器
- [x] 点击画布能打开左侧抽屉
- [ ] 返回键先关闭抽屉，再退出应用
- [ ] 贴纸能从容器拖动到画布
- [ ] 贴纸在画布上可以移动
- [ ] 侧边栏信息能正确保存

### 单元测试（未来）
- Sticker 数据类的序列化/反序列化
- 坐标转换逻辑
- 手帐数据的保存/加载

---

## 已知问题 & TODO

### 当前问题
- [ ] 缺少贴纸资源文件
- [ ] 未实现实际的贴纸拖放逻辑

### 计划功能
- [ ] 多页手帐支持（ViewPager）
- [ ] 云同步功能
- [ ] 手写/涂鸦功能
- [ ] 模板系统

---

## 项目状态

**当前阶段**: 🚧 初期开发（框架搭建阶段）  
**完成度**: ~15%  
**下一步**: 实现 CanvasView 和 StickerView 核心功能

---

*本文档遵循 AGENTS.md 标准，旨在帮助 AI 助手更好地理解和协助项目开发。*