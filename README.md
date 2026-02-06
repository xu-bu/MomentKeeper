MomentKeeper/
├── app/
│   ├── build.gradle.kts                      # App 模块配置
│   └── src/main/
│       ├── java/com/example/momentkeeper/
│       │   ├── MainActivity.kt               # 主 Activity (Compose)
│       │   │
│       │   ├── model/                        # 数据模型
│       │   │   ├── Sticker.kt               # 贴纸数据类
│       │   │   └── JournalData.kt           # 手帐数据（标题、时间、事件等）
│       │   │
│       │   ├── ui/
│       │   │   ├── theme/                    # Compose 主题
│       │   │   │   ├── Color.kt             # 颜色定义
│       │   │   │   ├── Theme.kt             # MaterialTheme 配置
│       │   │   │   └── Type.kt              # 字体样式
│       │   │   │
│       │   │   ├── canvas/                   # 画布相关 Composables
│       │   │   │   ├── CanvasScreen.kt      # 画布主屏幕
│       │   │   │   └── StickerItem.kt       # 单个贴纸组件（可拖动）
│       │   │   │
│       │   │   ├── sticker/                  # 贴纸容器相关
│       │   │   │   ├── StickerContainer.kt  # 贴纸容器 Composable
│       │   │   │   └── StickerGrid.kt       # 贴纸网格列表
│       │   │   │
│       │   │   └── sidebar/                  # 侧边栏相关
│       │   │       └── InfoSidebar.kt       # 左侧信息栏 Composable
│       │   │
│       │   ├── viewmodel/                    # ViewModel (可选)
│       │   │   └── JournalViewModel.kt      # 状态管理
│       │   │
│       │   └── data/                         # 数据层
│       │       └── StickerRepository.kt     # 预设贴纸数据
│       │
│       ├── res/
│       │   ├── drawable/                     # 贴纸图片资源
│       │   │   ├── sticker_heart.xml
│       │   │   ├── sticker_star.xml
│       │   │   └── ...
│       │   │
│       │   └── values/
│       │       └── strings.xml              # 字符串资源 (可选保留)
│       │
│       └── AndroidManifest.xml              # 应用配置
│
└── build.gradle.kts                          # 项目级配置