# GoPuzzle - 围棋死活训练 APP

一款安卓单机版围棋做题应用，支持死活题、手筋题、官子题练习。

## 功能特性

- 🏆 **多种题型**: 死活题、手筋题、官子题
- 📐 **多棋盘尺寸**: 9路、11路、13路、19路
- ⭐ **难度分级**: 入门、简单、中级、困难、高级
- 🎯 **智能提示**: 提示、答案、悔棋、重置
- 📊 **进度追踪**: 正确率统计、错题本、收藏夹
- 🎨 **精美界面**: 木纹棋盘、现代Material Design 3

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM + Clean Architecture
- **本地存储**: Room + DataStore
- **最低版本**: Android 7.0 (API 24)

## 项目结构

```
app/src/main/java/com/gopuzzle/app/
├── data/           # 数据层
│   ├── repository/  # 仓库实现
│   └── sgf/        # SGF解析器
├── domain/         # 领域层
│   ├── model/      # 领域模型
│   └── usecase/    # 用例
└── ui/             # UI层
    ├── home/       # 首页
    ├── puzzle/     # 做题页
    ├── select/     # 题目选择
    └── components/ # 通用组件
```

## 构建

```bash
# 克隆项目
git clone https://github.com/YOUR_USERNAME/GoPuzzle.git
cd GoPuzzle

# 构建Debug APK
./gradlew assembleDebug

# 构建Release APK
./gradlew assembleRelease
```

## 预览

<p align="center">
  <img src="screenshots/home.png" width="300" alt="首页">
  <img src="screenshots/puzzle.png" width="300" alt="做题">
</p>

## License

MIT License
