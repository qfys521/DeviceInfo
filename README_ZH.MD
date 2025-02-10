# 设备信息应用

该项目是一个Android应用程序，用于显示各种设备信息，如设备名称、型号、操作系统版本、内存、存储等。

## 功能

- 显示设备信息（名称、型号、操作系统版本、品牌、制造商、SDK版本、系统语言、支持的ABI）
- 显示内存信息
- 显示存储信息
- 显示当前目录中的文件信息
- 显示CPU信息
- 显示本地和电话国家代码
- 复制信息到剪贴板
- 展开和折叠附加信息

## 使用技术

- Kotlin
- Java
- Gradle

## 设置

### 先决条件

- Android Studio或IntelliJ IDEA
- Android SDK

### 安装

1. 克隆仓库：
    ```sh
    git clone https://github.com/qfys521/deviceinfos.git
    ```
2. 在Android Studio或IntelliJ IDEA中打开项目。
3. 同步项目的Gradle文件。
4. 在Android设备或模拟器上构建并运行应用程序。

## 使用

1. 在您的Android设备上启动应用程序。
2. 主屏幕将显示各种设备信息。
3. 长按显示的信息以将其复制到剪贴板或展开/折叠附加信息。
4. 点击“操作”按钮以显示带有复制或展开/折叠信息选项的对话框。

## 代码结构

- `Main.kt`：设置UI并显示设备信息的主活动。
- `local.properties`：包含Android SDK路径的本地配置。

## 贡献

1. Fork此仓库。
2. 创建一个新分支（`git checkout -b feature-branch`）。
3. 提交您的更改（`git commit -am 'Add new feature'`）。
4. 推送到分支（`git push origin feature-branch`）。
5. 创建一个新的Pull Request。

## 许可证

该项目根据MIT许可证授权。有关详细信息，请参阅`LICENSE`文件。

## 致谢

- 感谢Android和Kotlin社区的支持和资源。