# 文档合集 · TuoLingBloom

> TuoLingBloom（拓灵绽放）是基于 DragonCore 的多功能动画绽放插件，支持魂环、贴图、模型、表情包、称号等多种效果，还能给 MythicMobs 怪物自动套魂环。
> 本文档合并且重写自 `wiki.md`（用户文档）与 `dev.md`（开发者文档）。网页版见 `docs.html`。

|              |                              |
|--------------|------------------------------|
| 版本         | `1.0`                        |
| 服务端       | Spigot / Paper 及其分支      |
| 适用版本      | 1.12.2                     |
| Java         | Java 8 及以上                |
| 硬依赖       | `DragonCore`、`TuoLingCore`  |
| 软依赖       | `MythicMobs`、`PlaceholderAPI`（可选）|

---

# 一、用户文档

> 安装、配置、命令、玩法与编辑器说明。读完这一部分，你就能把 TuoLingBloom 调到想要的样子。

---

## 1. 安装与前置

### 环境要求

| 项目     | 要求                                                |
|----------|-----------------------------------------------------|
| 服务端   | Spigot / Paper 及其分支                            |
| Java     | Java 8 及以上（与服务端版本要求一致即可）           |
| 硬依赖   | `DragonCore ≥ 2.6.1`：提供世界贴图、客户端实体模型挂载等核心 API |
| 硬依赖   | `TuoLingCore ≥ 1.0`：拓灵核心框架，提供命令注册、通用管理器基座 |
| 软依赖   | `MythicMobs`：模型路线生成挂载实体（不装仅 model 路线不可用） |
| 软依赖   | `PlaceholderAPI`：冷却查询变量（不装仅变量失效）    |

### 安装步骤

1. 将 `TuoLingBloom-1.0.jar` 放入服务端 `plugins/` 目录
2. 确保 `DragonCore.jar` 和 `TuoLingCore.jar` 也在 `plugins/` 中
3. **完全重启服务器**，插件会自动生成默认配置文件
4. 编辑 `plugins/TuoLingBloom/config.yml` 配置你的预设
5. 编辑 `plugins/TuoLingBloom/texture/` 或 `model/` 下的动画配置
6. 游戏内执行 `/bloom reload`（或 `/tuolingbloom reload`）重载配置

> [!WARNING]
> 首次启动才会生成默认配置文件，请整服重启而不是热重载。替换 jar 后要重启而不是依赖 `/reload`，否则可能仍加载旧类；排查问题时先核对报错日志行号与当前源码是否一致。

### 控制台提示

插件启用时会在控制台打印横幅，便于确认版本和配置是否加载成功；关闭时会清空所有绽放状态并打印关闭横幅。

---

## 2. 配置文件总览

配置都在 `plugins/TuoLingBloom/` 下：

```tree
plugins/TuoLingBloom/
├── config.yml             # 预设 + 识别系统 + 冷却组 + 开关
├── message.yml            # 玩家 / 控制台 / 帮助消息
├── world-texture.yml      # 固定坐标的世界贴图
├── texture/               # 贴图动画配置（分类目录下 *.yml）
│   └── <分类>/<动画名>.yml
└── model/                 # 模型动画配置（*.yml）
    └── <模型名>.yml
```

改完配置文件执行 `/bloom reload`（需要 OP）或重启生效。

### config.yml — 预设配置

这是插件的核心入口，定义了所有可绽放的预设组。基本结构如下：

```yaml
preset:
  <分类>:                # 头部索引，必须设置
    <预设名>:              # 最里层即"预设名"，使用时 bloom run <预设名>
      mode: "<策略模式>"    # 必填，5 种策略之一
      view: "<动画名>"      # 必填，texture/ 或 model/ 下的文件名（不含后缀）
      value: "<参数>"       # 选填，依模式而定，多个用英文逗号分隔
      group: "<组名>"       # 选填，同组冲突 / 冷却共享
      duration: <秒数>     # 选填，-1 为永久
```

#### 5 种策略模式

| 模式 | 说明 | value 填写 |
|------|------|------------|
| `main-hand` | 检测玩家主手物品 | 无需填写 |
| `off-hand` | 检测玩家副手物品 | 无需填写 |
| `dragon-slot` | 检测龙核自定义槽位 | 槽位名，多个用英文逗号分隔 |
| `inventory` | 检测背包指定槽位 | 槽位索引号，多个用英文逗号分隔 |
| `direct-bloom` | 直接绽放，无需物品 | `identify-texture` / `identify-model` 表里的名字 |

前四种都是「通过指定物品 → 获取该播放哪个动画、用哪张贴图」；第五种不需要物品就能绽放，`value` 直接填 `identify` 表里的名字。

#### 参数说明

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `mode` | String | 必填 | 上面 5 种策略之一 |
| `view` | String | 必填 | `texture/` 或 `model/` 目录下的文件名（不含后缀） |
| `value` | String | 选填 | 依模式而定，多个用英文逗号分隔 |
| `group` | String | 选填 | 所属组名，同组冲突 / 冷却共享 |
| `duration` | Integer | 选填 | 持续时间（秒），`-1` 为永久 |

> [!NOTE]
> 预设支持无限嵌套，最终获取最里层的 preset。**子索引（预设名）全局不允许重复**，例如「武器绽放」里不允许出现和「表情包」里一样的索引名。也不能跳过头部索引直接写 `mode`。

示例——一个带分类、组、持续时间的预设：

```yaml
preset:
  武器绽放:
    主手:
      mode: "main-hand"
      view: "贴图-底部"
      group: "魂环组"
      duration: 60
```

使用时执行 `/bloom run 主手` 即可。

### identify — 识别系统

插件通过物品识别来确定绽放用的贴图路径或模型名。

#### identify-texture — 贴图识别

```yaml
identify-texture:
  十年魂环:
    check-lore: "十年魂环"     # 检测物品 lore 是否包含此行
    path: "bloom/魂环/h1.png"
  大笑:
    path: "bloom/表情包/大笑.png" # 无 check，直接使用
```

- `check-lore`：检测物品 lore 是否包含此行
- `check-name`：检测物品名字是否包含此行
- 两者都写，则都匹配才能识别成功
- 都不写，则不检测，直接使用该 `path`

#### identify-model — 模型识别

```yaml
identify-model:
  罗刹神:
    check-name: "神祁:罗刹神"
    mm-name: "罗刹神"          # MythicMobs 配置里的 mob 名
```

同样支持 `check-lore` 和 `check-name`。`mm-name` 填写 MythicMobs 配置中的 mob 名。

### bloom-group — 冷却组

定义预设组的冷却时间和冲突关系：

```yaml
bloom-group:
  魂环组:
    cooldown: 30
  真身组:
    cooldown: 30
  表情包组:
    cooldown: 15
```

同组预设共享冷却时间；同时绽放中时不能绽放同组其他预设。

### op-not-cooldown

```yaml
op-not-cooldown: true
```

设为 `true` 时，OP 玩家不受冷却限制。

---

## 3. 贴图配置（texture/*.yml）

贴图动画配置文件放在 `plugins/TuoLingBloom/texture/` 目录下，支持按分类建子文件夹。一个文件里可以有多个层级（`setting.1/2/3...`），渲染时按 `delay` 依次播放。

```yaml
setting:
  <层级编号>:
    delay: 0               # 延迟播放（秒）
    texture:
      translate-x: 0        # X 轴偏移
      translate-y: 2.35     # Y 轴偏移
      translate-z: 0.0      # Z 轴偏移
      rotate-x: 90          # X 轴旋转
      rotate-y: -60         # Y 轴旋转
      rotate-z: 0           # Z 轴旋转
      path: "hh/h1.png"     # 默认贴图（识别不到时使用；path 也可由运行时从 identify 表动态填入）
      follow-player-eyes: false  # 贴图是否始终面向观察者视角
    animations:
      translate-animation:  # 位移动画
        direction: 'z'
        delay: 0
        distance: 2.30
        duration: 3000
        cycle-count: 1
        fixed: true
      scale-animation:      # 缩放动画
        delay: 0
        from-scale: 10.3
        to-scale: 1.5
        duration: 3000
        cycle-count: 1
        fixed: true
        resetTime: 0
      rotate-animation:     # 旋转动画
        direction: 'z'
        delay: 0
        angle: 360.0
        duration: 6000
        cycle-count: -1      # -1 为无限循环
        fixed: false
```

### 参数说明

| 参数 | 类型 | 说明 |
|------|------|------|
| `setting.<编号>.delay` | Integer | 该层延迟播放的秒数 |
| `texture.translate-x/y/z` | Double | 贴图位移坐标 |
| `texture.rotate-x/y/z` | Double | 贴图旋转角度 |
| `texture.path` | String | 默认贴图路径（运行时可能被 identify 结果覆盖） |
| `texture.follow-player-eyes` | Boolean | 贴图是否始终面向观察者视角 |
| `animations.translate-animation` | Object | 位移动画配置 |
| `animations.scale-animation` | Object | 缩放动画配置 |
| `animations.rotate-animation` | Object | 旋转动画配置 |

### 动画参数

| 参数 | 类型 | 说明 |
|------|------|------|
| `direction` | String | 动画方向（x/y/z） |
| `delay` | Integer | 动画延迟（毫秒） |
| `duration` | Integer | 动画持续时间（毫秒） |
| `cycle-count` | Integer | 循环次数，`-1` 为无限循环 |
| `fixed` | Boolean | 是否固定（不受玩家转向影响） |
| `distance` | Double | 位移距离（translate-animation） |
| `from-scale` | Double | 起始缩放（scale-animation） |
| `to-scale` | Double | 结束缩放（scale-animation） |
| `resetTime` | Integer | 缩放复位时间（毫秒） |
| `angle` | Double | 旋转角度（rotate-animation） |

---

## 4. 模型配置（model/*.yml）

模型动画配置文件放在 `plugins/TuoLingBloom/model/` 目录下，用于挂载三维模型（如武魂真身）。

```yaml
setting:
  <层级编号>:
    delay: 0
    location:          # 模型相对于实体的位置偏移
      x: 0
      y: 0
      z: 3
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `setting.<编号>.delay` | Integer | 延迟生成（秒） |
| `location.x/y/z` | Double | 模型相对于实体的位置偏移 |

> [!WARNING]
> 模型路线依赖 MythicMobs，且 `view` 指向模型时 `value` **只能填写一个参数**（配置里多余的值会被忽略）。

---

## 5. 世界贴图（world-texture.yml）

配置固定坐标的世界贴图，贴图显示在世界指定位置，不绑定实体。

```yaml
世界贴图1:
  world: "world"
  location:
    x: 100
    y: 100
    z: 100
  preset: "冰雪女王"    # 需在 config.yml 中定义的预设名
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `world` | String | 贴图所在世界 |
| `location.x/y/z` | Double | 贴图坐标 |
| `preset` | String | 对应的预设名（需在 config.yml 中定义） |

> [!NOTE]
> 世界贴图的 preset 对应 `duration` 建议设为 `-1`（永久存在），这样玩家进出服时也会被自动补发渲染。世界贴图渲染由 `ReissueWorldTextureService` 管理。

---

## 6. 消息配置（message.yml）

所有玩家消息、控制台日志和帮助信息均可自定义。分为三组：`player`（玩家收到）、`console`（控制台输出）、`help`（帮助命令显示）。

### 颜色写法

文案里用 `§` 加颜色代码，例如 `§f` 白、`§e` 黄、`§c` 红、`§6` 金、`§a` 绿、`§b` 青、`§l` 加粗。插件会自动转换成游戏内颜色。

> [!NOTE]
> 插件文案自带前缀（如 `§f[§c绽放§f]`），消息发送不经 SendMessageUtil，直连 Bukkit 通道。游戏内看前缀即可区分玩家消息与控制台后台日志。

### 消息占位符

| 占位符 | 说明 |
|--------|------|
| `%preset%` | 预设名 |
| `%view%` | 动画名 |
| `%entry%` | 世界贴图名 |
| `%cooldown%` | 冷却剩余秒数 |
| `%require%` | 需要的数量 |
| `%actual%` | 实际的数量 |
| `%value%` | 配置的 value 值 |
| `%mode%` | 策略模式 |
| `%type%` | 类型（texture/model） |
| `%index%` | 层级索引 |
| `%name%` | 名称 |
| `%key%` | 配置键 |
| `%error%` | 错误信息 |
| `%version%` | 版本号 |

---

## 7. 命令与权限

| 命令 | 说明 | 权限 |
|------|------|------|
| `/bloom run <预设名>` | 绽放指定预设组 | 无需，玩家可用 |
| `/bloom remove <预设名>` | 取消指定预设绽放 | 无需，玩家可用 |
| `/bloom reload` | 重载全部配置 | OP |
| `/bloom` · `/bloom help` | 显示帮助 | 无需 |

- 主命令 `/bloom`，别名 `/tuolingbloom`，完全等价
- 无参数或参数不全时自动显示帮助
- `reload` 判定走 `sender.isOp()`；无权限时提示「你没有权限执行此操作」
- 控制台执行 `run` / `remove` 会被提示「该命令只能由玩家执行」

补充：

- **取消绽放**：绽放中的预设再次执行 `/bloom run <同预设>` 即自动取消（重新执行时会先检测状态并取消）
- **重载机制**：`reload` 先统一取消清理所有正在绽放的实体（玩家 + 怪物）与全部世界贴图，再统一用新配置重新绽放，因此两侧状态始终一致
- **Tab 补全**：输入 `/bloom run ` 后按 Tab 会补全当前配置里的预设名

---

## 8. 绽放玩法 / 模式说明

### 冲突与冷却规则

- **同组冲突**：同 `group` 的预设不能同时绽放。例如「魂环组」里已绽放「主手」，就不能再绽放「主手2」或同组其他预设（提示冲突）
- **同组冷却**：同 `group` 共享冷却时间，冷却中不能绽放（提示 `%cooldown%` 剩余秒数）
- **无组预设**：`group` 不给则该预设独立走完，彼此不冲突不共享冷却，适合魂兽/怪物（见下）
- **怪物不走冷却**：绽放入口只对 `Player` 做冷却判定，怪物（mob）一律放行
- **OP 无视冷却**：`op-not-cooldown: true` 时 OP 不受冷却限制

### 怪物绽放（魂兽 / 怪物套魂环、套模型）

插件自动给 MythicMobs 怪物套绽放效果：**生成时自动绽放，死亡时自动清理**，无需玩家操作。

配置分两步：

1. 在 `config.yml` 定义怪物用的预设（用 `direct-bloom` 模式、`value` 填 identify 名、`duration: -1`）
2. 在 MythicMobs 的 mob 配置中添加 `TuoLingBloom` 列表

```yaml
preset:
  魂兽绽放:
    十年魂兽:
      value: "十年魂环"
      mode: "direct-bloom"
      view: "贴图-底部"
      duration: -1    # 永久存在，等待怪物死亡自动清理
```

```yaml
# MythicMobs 配置
十年魂兽:
  Type: ZOMBIE
  Display: '&a十年魂兽'
  Options:
    # ... 其他选项
  TuoLingBloom:
    - "十年魂兽"     # 填 config.yml 最里层的预设名（扁平名，不能用点号分隔路径）
```

> [!WARNING]
> 怪物预设**不要设置 `group`**，否则同一只怪物上不同预设会冲突（同组互斥导致后面的无法绽放）。
>
> `TuoLingBloom` 列表里**只能写扁平预设名**，不要用 `魂兽绽放.十年魂兽` 这类点号嵌套路径，否则配置解析失败。

给怪物挂载模型（如武魂真身）方式相同，`view` 指向 `model/*.yml` 即可：

```yaml
preset:
  魂兽绽放:
    武魂魂兽:
      value: "罗刹神"
      mode: "direct-bloom"
      view: "模型-神祁-背部"
      duration: -1
```

工作机制：怪物生成时插件监听 `MythicMobSpawnEvent`，读取 `TuoLingBloom` 列表逐一向怪物绽放；绽放贴图时附近所有玩家都会收到该怪物的贴图渲染包；怪物死亡时监听 `MythicMobDeathEvent`，自动取消所有绽放并清理所有玩家的客户端贴图。模型路线通过 MythicMobs API 生成挂载实体，2 tick 延迟后挂载模型。

---

## 9. PlaceholderAPI

软依赖，未安装不影响使用。装了会自动注册本插件的占位符（identifier 为 `tuolingbloom`）。

| 变量 | 说明 | 示例 |
|------|------|------|
| `%tuolingbloom_cooldown_<预设名>%` | 返回玩家该预设的冷却剩余秒数 | `%tuolingbloom_cooldown_主手%` |

冷却为 `0` 表示无冷却或可绽放。控制台请求（无玩家）时返回 `null`，不会抛异常。

---

## 10. 跨版本说明

插件对贴图、模型、动画做了运行时适配，尽量按当前服务端能力选择实现。模型路线与怪物路线以软依赖方式隔离，未装 MythicMobs 时相关代码不会加载，不会因此报错。

> [!NOTE]
> 涉及 DragonCore 客户端实体加载的部分（如玩家进服补发）存在延迟，插件会自动延迟 40 tick（2 秒）补发，避免实体未加载完成导致贴图不显示。

---

## 11. 可视化编辑器

插件附赠一个 **3D 可视化配置编辑器**，无需手写 YAML，所见即所得。前端基于 Three.js，后端是零依赖的 PowerShell `HttpListener` 服务。

### 启动方式

1. 打开 `Bloom编辑器/` 目录
2. 双击 `start.bat` 启动后端服务（Windows 自带 PowerShell 即可）
3. 浏览器打开 `http://127.0.0.1:17965`（默认端口 17965）

> [!NOTE]
> 编辑器后端是 `server.ps1`，基于 Windows 自带 `System.Net.HttpListener`，无需安装任何额外依赖。Ctrl+C 可停止服务。

### 功能特性

- **3D 实时预览**：Three.js 渲染，直接预览魂环 / 贴图在玩家身上的最终效果；底部时间轴可播放 / 重置动画
- **可视化拖拽**：调整位置、旋转、缩放，参数实时同步到配置（左键拖=移动，Shift+左键拖=旋转 X/Y，Ctrl+左键拖=旋转 Z）
- **双编辑模式**：贴图 / 模型文件支持「可视化」与「编辑文件（原始 YAML）」一键切换；切换编辑模式时当前内容会在两种表示间互转
- **文件管理**：在左侧文件树中浏览、新建、重命名、复制、下载、删除所有配置文件；支持按分类建子文件夹
- **自动保存**：修改自动保存到本地 `tasks/` 目录，无需手动操作
- **导出**：`导出` 按钮可把当前文件导出为 YAML

### 新建文件

- 点「+ 新建」打开新建弹窗，类型只有两种：`贴图动画 (texture/*.yml)`、`模型配置 (model/*.yml)`
- 文件名只需填**纯文件名**，无需输入目录和后缀（会自动补 `.yml`，可自动清理误输入的 `texture/`、`model/` 前缀）
- 新建文件会自动生成对应类型的默认内容（贴图或模型模板）

### DragonCore 目录选择与预览贴图

- 首次使用需在 `⚙ 全局设置` 中**手动选择客户端 DragonCore 贴图目录**（含贴图 png 的目录），用于在编辑器中预览贴图。该路径会持久化到 `dc_dir.txt`，下次打开自动加载
- 「修改模式」下可从 DragonCore 目录浏览并**套用贴图**到对应魂环层。套用的贴图只用于 3D **预览**，不会写回 YAML 配置；刷新后仍能恢复显示
- 预览贴图映射持久化在 `preview_tex.json`（与配置分离），因此不会污染 `texture/*.yml` 的 `path` 字段

### 目录结构说明

```tree
Bloom编辑器/
├── index.html           # 前端页面（Three.js 3D 编辑器）
├── server.ps1           # 后端服务（PowerShell HttpListener）
├── start.bat            # 启动脚本
├── dc_dir.txt           # DragonCore 贴图目录（持久化）
├── preview_tex.json     # 预览贴图映射（不写入配置）
└── tasks/               # 配置文件目录（编辑器直接读写这里）
    ├── config.yml
    ├── message.yml
    ├── world-texture.yml
    ├── texture/
    │   └── *.yml
    └── model/
        └── *.yml
```

> [!TIP]
> 编辑器的配置直接存在自带的 `tasks/` 文件夹里，**无需从服务端导入**。编辑完成后把这些文件（`tasks/` 下的 config.yml、message.yml、world-texture.yml、texture/、model/）同步到服务端 `plugins/TuoLingBloom/` 即可生效。

---

## 12. 常见问题（FAQ）

### 绽放没反应，也没提示
- 确认 `view` 对应的文件在 `texture/` 或 `model/` 下真实存在，且书写的是文件名（不含后缀）
- 确认预设名（最里层索引）未被重复定义
- 确认触发「模式」依赖的条件满足：`main-hand`/`off-hand` 需要手持对应识别物品，`dragon-slot` 需要对应槽位有物品，`inventory` 需要指定槽位索引在物品栏范围内
- 如果同组冲突：先 `/bloom remove <当前预设>` 或换个不同 `group` 的预设

### 提示冲突 / 冷却
- **冲突**：同 `group` 的预设正在绽放中，无法再绽放同组其他预设。用 `/bloom remove` 取消当前预设，或给它配到不同组
- **冷却**：同组共享冷却，`%cooldown%` 显示剩余秒数。OP 想无视冷却可将 `op-not-cooldown` 改为 `true`

### 怪物没套上魂环
- 确认预设 mode 是 `direct-bloom`、`group` **不要设置**
- 确认 `TuoLingBloom` 列表里写的是**扁平预设名**（不用点号嵌套路径）
- 确认 `value` 在 `identify-texture` / `identify-model` 中能查到

### 模型路线不能用
- 没装 MythicMobs 时 model 路线不可用（软依赖，未装不会报错，只是该路线不工作）
- 填了模型 `view` 时 `value` 只能填**一个**参数

### OP 一直没冷却，想限制怎么办
- 把 `op-not-cooldown` 改为 `false`，OP 就和其他玩家一样受冷却限制

### /bloom reload 报「没有权限」
- reload 只对 OP 开放（走 `isOp()`）。不是 OP 则无法重载，请用控制台或给临时 OP

### 改了配置没变化
- 执行 `/bloom reload`（需 OP）。若替换了 jar，请整服重启，不要依赖热重载

### 为什么有 /bloom 和 /tuolingbloom 两个命令
- 两者是同一个功能的别名，方便与其它插件命令名错开，任选其一即可

### 预览编辑器刷新后贴图没了
- 预览贴图存在 `preview_tex.json`（与配置分离），正常情况刷新会恢复。若缺失，请先在 `⚙ 全局设置` 确认 DragonCore 贴图目录已正确选择

---
---

# 二、开发者文档

> 面向想二次开发、阅读源码或接手维护的开发者。说明插件及其依赖框架 TuoLingCore 的项目结构、内部设计、核心调用链、缓存模型与扩展方式。

|              |                              |
|--------------|------------------------------|
| 语言         | Java 8                       |
| 构建         | Maven + Shade + Lombok       |
| 服务端 API   | Spigot API 1.12.2            |
| 硬依赖       | DragonCore 2.6.1 · TuoLingCore 1.0-SNAPSHOT |
| 软依赖       | MythicMobs 4.11.0 · PlaceholderAPI 2.11.2 |

---

## 13. 项目结构

整体分为插件本体 `TuoLingBloom` 和依赖框架 `TuoLingCore` 两部分。核心代码在 `com.tuoling.tuolingbloom` 包下：

```tree
src/main/java/com/tuoling/tuolingbloom
├── TuoLingBloom.java             主类：装配各 Manager + 软依赖注册
├── actor/                        Actor 层（状态管理与冲突仲裁）
│   ├── bloomer/
│   │   ├── Bloomer.java          绽放者：冲突/冷却/取消清理
│   │   └── BloomerData.java      绽放数据（tasks/textureIds/paths/bloomEntities）
│   └── looker/
│       ├── Looker.java           观察者：贴图 ID 缓存/补发/移除
│       └── LookerData.java       观察数据
├── bloom/                        路由层（bloomType → 实现）
│   ├── Bloom.java                接口（getName + bloom）
│   ├── BloomManager.java         KeyValueManager 按 bloomType 分流
│   ├── BloomTexture.java         贴图入口
│   └── BloomModel.java           模型入口
├── cache/                        缓存层（Guava Table 多维索引）
│   ├── CacheManager.java         InstanceManager 聚合
│   ├── BloomerCache.java         Table<UUID, preset, BloomerData>
│   ├── LookerCache.java          Map<UUID, Table<UUID, preset, LookerData>>
│   ├── CooldownCache.java        Table<UUID, preset, Long>
│   └── WorldTextureCache.java    Map<UUID, List<String>>
├── command/                      命令层
│   ├── CommandTrigger.java       Executor + TabCompleter
│   ├── CommandManager.java       ValueManager 遍历派发
│   └── maincommand/
│       ├── BloomCommand.java     /bloom run
│       ├── RemoveCommand.java    /bloom remove
│       ├── ReloadCommand.java    /bloom reload
│       └── HelpCommand.java      /bloom /bloom help
├── compatible/                   兼容层（DragonCore 实体模型）
│   ├── DragonEntityCompatible.java
│   ├── DragonEntityListener.java
│   └── DragonEntityData.java
├── config/                       配置层（不可变快照）
│   ├── data/                     纯字段载体（BloomPresetEntry/LayerSetting/ModelData/动画配置等）
│   ├── holder/                   配置持有者（final 字段，如 DefaultConfig/TextureConfig/…）
│   ├── loader/                   加载器（兜底 + 容错）
│   └── ConfigHolderManager.java  AbstractKeyedManager 聚合
├── listener/                     监听层
│   ├── PlayerJoin.java           进服补发（40L 延迟）
│   ├── PlayerQuit.java           退服清理缓存
│   ├── MobSpawn.java             怪物生成自动绽放（MM 隔离）
│   └── MobDeath.java             怪物死亡自动清理
├── papi/
│   └── BloomPlaceholderExpansion.java   PlaceholderAPI 扩展
├── preset/                       策略层（5 种物品获取模式）
│   ├── BloomStrategy.java        接口
│   ├── BloomStrategyManager.java AbstractKeyedManager 统一注册
│   ├── MainHandStrategy.java / OffHandStrategy.java
│   ├── DragonSlotStrategy.java / InventoryStrategy.java
│   └── DirectBloomStrategy.java
├── router/
│   └── BloomRouter.java          view 文件名 → bloomType 路由
├── service/                      执行层（装配渲染）
│   ├── BloomTextureService.java
│   ├── BloomModelService.java
│   ├── ReissuePlayerTextureService.java
│   └── ReissueWorldTextureService.java
└── utils/
    ├── PresetUtils.java          破除「死亡链」的预设访问封装
    ├── GetTexturePaths.java / GetMMName.java   物品识别
    ├── Message.java              消息派发（player/console/bloomer）
    ├── BloomEntityUtil.java      MythicMobs API 收敛点
    ├── BloomLogger.java          后台日志
    └── EnableMessageUtil.java    横幅
```

依赖框架 `com.tuoling.tuolingcore` 提供通用基座：

```tree
com.tuoling.tuolingcore
├── collect/               容器基座（KeyedMap/ClassMap，基于 Guava Forwarding）
├── framework/
│   ├── command/CommandHandler.java
│   ├── config/ConfigHolder.java
│   └── manager/   AbstractKeyedManager/KeyValueManager/ValueManager/InstanceManager
├── utils/          CommandUtil/ConfigLoadUtils/ItemUtil/NormalUtil
└── TuoLingCore.java
```

## 14. 模块职责

| 包 | 职责 | 不该做的事 |
|----|------|-----------|
| `command` | 命令判定、参数校验、调用执行逻辑 | 不直接拼消息文本，统一走 `Message` |
| `utils/Message` | 从 `MessageConfig` 取文案、替换占位符、按 sender/bloomer 类型派发 | 不判断权限、不处理冷却 |
| `actor` | 状态管理、冲突/冷却仲裁、取消清理 | 不参与具体渲染，转交 `BloomManager` |
| `bloom` | 按 bloomType 分流，接 `Bloom` 接口 | 不关心物品识别细节，交策略 |
| `preset` | 5 种物品获取策略 | 版本敏感操作统一返回 `String` |
| `service` | 装配渲染、写缓存、补发 | 不判断冲突/冷却 |
| `cache` | 进程内状态（多维索引） | 不发送消息、不依赖插件实例 |
| `config` | 不可变快照的加载与持有 | 业务代码一律读快照，不改内部 |
| `papi` | 对外稳定的占位符契约 | 改动需考虑兼容性 |

## 15. 启动流程

主类只做装配，具体工作交给各 Manager。`TuoLingBloom.inst()` 取代 `getInstance()` 作为单例访问入口：

```java
@Override
public void onEnable() {
    inst = this;
    bloomRouter = new BloomRouter();
    registerManager();      // ConfigHolderManager / CommandManager / BloomStrategyManager / CacheManager / BloomManager
    registerListeners();    // PlayerJoin/PlayerQuit/DragonEntityListener 常驻 + MobSpawn/MobDeath 按 MM
    registerCommands();     // bloom、tuolingbloom 的 executor + tabCompleter
    registerPlaceholder();  // 装了 PAPI 才注册

    // MythicMobs 软依赖：未安装时不创建
    if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
        dragonEntityCompatible = new DragonEntityCompatible();
    }
    EnableMessageUtil.print(configHolderManager);
}
```

禁用时清理所有资源，防止泄漏：

```java
@Override
public void onDisable() {
    if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
        BloomEntityUtil.cancelAllMobBloom();   // 清理已绽放的 MM 实体
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
        // 清空 Bloomer / Looker / 世界贴图
    }
}
```

---

## 16. 核心设计：Actor + 路由策略 + Service

核心调用链是：**命令 → Actor（冲突/冷却仲裁）→ Router（按 bloomType 分流）→ Strategy（物品识别）→ Service（装配执行）**。每层职责单一、正交扩展。

### Actor 双角色

| 角色 | 文件 | 职责 | 持有数据 |
|------|------|------|----------|
| `Bloomer` | `actor/bloomer/Bloomer` | 绽放者：冲突检测、冷却判定、取消清理 | `BloomerData`（tasks / textureIds / paths / bloomEntities） |
| `Looker` | `actor/looker/Looker` | 观察者：贴图 ID 缓存、补发触发、清理移除 | `LookerData`（tasks / textureIds） |

关键职责：

- `Bloomer.bloom(preset)` 是玩家/mob 绽放的**唯一入口**，内部先做 `isBloomPreset → isConflict → isCooldown` 三重检查，再委托 `BloomManager.bloom()`
- `Bloomer.cancelPreset` 负责取消所有 BukkitTask、移除客户端贴图、清理生成实体、通知所有在线玩家对应的 `Looker` 清理
- 重新运行同预设会先 `cancelPreset`（取消旧绽放）再走正常流程
- 补发入口在 `Looker.reissueBloom`，模型路线直接跳过（模型不走补发）
- 冷却判定只对 `Player` 生效，mob 一律放行（`isCooldown` 里非 Player 直接返回 false）

### Bloom 路由 + 策略

绽放类型只有两种：`texture`（贴图）和 `model`（模型）。`view` 文件名 → 类型的映射由 `BloomRouter` 在配置加载时维护，`PresetUtils.getBloomType()` 经它查询：

```java
public interface Bloom {
    String getName();
    void bloom(LivingEntity entity, String preset);
}
// BloomManager（KeyValueManager）注册 "model" → BloomModel、"texture" → BloomTexture
public void bloom(LivingEntity entity, String preset) {
    PresetUtils pu = new PresetUtils(preset);
    String bloomType = pu.getBloomType();     // 经 BloomRouter 查询
    get(bloomType).bloom(entity, preset);
}
```

物品获取方式抽象为 `BloomStrategy`，5 种策略由 `BloomStrategyManager`（AbstractKeyedManager）统一注册：

| 策略 | `getModeName()` | 输入 |
|------|-----------------|------|
| `MainHandStrategy` | `main-hand` | 主手物品 |
| `OffHandStrategy` | `off-hand` | 副手物品 |
| `DragonSlotStrategy` | `dragon-slot` | 龙核自定义槽位 |
| `InventoryStrategy` | `inventory` | 背包指定槽位索引 |
| `DirectBloomStrategy` | `direct-bloom` | 直接按 value 查 identify 表 |

```java
public interface BloomStrategy {
    String getModeName();
    List<String> getTexturePaths(Player player, String preset);  // 返回贴图路径
    List<String> getMMNames(Player player, String preset);       // 返回模型名
}
```

> [!WARNING]
> 版本敏感操作（贴图路径、模型名）统一返回 `String`，交由策略封装——多版本适配时不改调用方，只加一个策略。这正是多版本兼容的关键设计。

### 三阶段业务管线

```text
验证   Bloomer.bloom → isBloomPreset ? isConflict ? isCooldown ?
   ↓
装配   BloomStrategyManager.getTexturePaths / getMMNames → 物品识别 → paths / mmNames
   ↓
执行   BloomTextureService.texPathToBloom / BloomModelService.mmNameToBloom → 渲染 + 写缓存 + 补发
```

---

## 17. 缓存设计

缓存是 Actor 数据的状态存储，全部基于 Guava Table 做多维索引。

| 缓存 | 结构 | 语义 |
|------|------|------|
| `BloomerCache` | `Table<UUID, String, BloomerData>` | 绽放者 UUID × preset → 绽放数据 |
| `LookerCache` | `Map<UUID, Table<UUID, String, LookerData>>` | 观察者 UUID →（绽放者 UUID × preset → 观察数据） |
| `CooldownCache` | `Table<UUID, String, Long>` | 玩家 UUID × preset → 冷却开始时间戳 |
| `WorldTextureCache` | `Map<UUID, List<String>>` | 玩家 UUID → 世界贴图 ID 列表 |

```java
// Bloomer 构造时直接拿到该实体在表里的「一行」Map；
// Guava row 视图的修改会直接作用到原表，无需显式回写
public Bloomer(LivingEntity bloomer) {
    this.textureData = TuoLingBloom.inst().getCacheManager().getBloomerCache().row(bloomer.getUniqueId());
}
```

> [!WARNING]
> 遍历 `cellSet / row` 时若要修改，必须先拷贝到 `new ArrayList<>(...)`，否则会抛 `ConcurrentModificationException`。参考 `Looker.removeAll()` 与 `ReloadCommand.reloadBloom()` 的实现。

生命周期清理：

- `PlayerQuit`：移除 BloomerCache / LookerCache / CooldownCache 中的 UUID 条目，防内存泄漏
- `MobDeath`：对死亡 mob 执行 `cancelAllPreset`
- `onDisable`：清理所有在线玩家的 Bloomer / Looker / 世界贴图 + MythicMobs 实体

---

## 18. 配置层（不可变快照）

配置层遵循「不可变快照 + 原子替换」，分四层：`data`（字段载体）→ `loader`（加载兜底）→ `holder`（持有）→ `ConfigHolderManager`（聚合）。

```java
// data 层：纯字段载体，字段 final，构造时一次性赋值；集合 getter 返回副本
@Getter
public class BloomPresetEntry {
    private final String presetName, mode, value, view, group;
    private final long duration;
    private final List<String> valueList;   // 存 unmodifiableList 副本
    public List<String> getValueList() { return new ArrayList<>(valueList); }
}
```

`ConfigHolderManager.reloadAll()`：

- 先 `clear()` 清空旧 Map 与 `BloomRouter` 路由
- 按顺序加载：`message.yml` → `model/*.yml`（登记路由 view→"model"）→ `texture/*.yml`（登记 view→"texture"）→ `world-texture.yml` → `config.yml`
- **先建好完整新 Map，再替换旧引用**（原子替换），避免 reload 期间出现 null 窗口

> [!TIP]
> 不可变的意义：下游永远拿到完整有效的不可变快照，业务代码无需判空。「所有对配置数据的修改」必须作用在 `getXxx()` 返回的副本上，禁止原地改内部字段。Loader 在加载边界统一兜底，保证下游永远有效数据（渐进式配置：缺 `mode` 兜底 `main-hand`）。

### PresetUtils：破除「死亡链」

所有对 `BloomPresetEntry` 字段的访问一律通过 `PresetUtils`，禁止直接 `entry.getXxx()` 链式调用：

```java
PresetUtils pu = new PresetUtils(preset);
pu.exists()            // 预设是否存在
pu.getView()           // 动画文件名
pu.getMode()           // 策略模式
pu.getValueList()      // value 逗号分割后的列表
pu.getGroup()          // 绽放组
pu.getDuration()       // 持续时间（-1 永久）
pu.getTextureLayers()  // 贴图层级 Map<Integer, LayerSetting>
pu.getModelConfig()    // 模型配置
pu.getBloomType()      // "texture" / "model"（经 BloomRouter）
```

---

## 19. 软依赖隔离

MythicMobs 与 PlaceholderAPI 是软依赖，相关代码必须**条件注册 + 隔离**，避免未安装时 `NoClassDefFoundError`：

```java
if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
    dragonEntityCompatible = new DragonEntityCompatible();
    registerEvents(new MobSpawn(), this);
    registerEvents(new MobDeath(), this);
}
if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
    new BloomPlaceholderExpansion(this).register();
}
```

> [!TIP]
> 隔离核心原则：所有直接引用 `io.lumine.xikage.mythicmobs.*` 的代码，必须收敛到 `BloomEntityUtil` 与 `DragonEntityCompatible` 两个类内。其他类只用纯 Bukkit 的 `LivingEntity`。这样软依赖类被 JVM 懒加载隔离，未安装时安全。

| 隔离点 | 文件 | 职责 |
|--------|------|------|
| MythicMobs API 收敛 | `utils/BloomEntityUtil` | spawn / reissueMobs / cancelAllMobBloom |
| DragonCore 实体模型 | `compatible/DragonEntityCompatible` | 读取 EntityModel 配置并挂载模型 |
| MythicMobs 监听 | `listener/MobSpawn` / `MobDeath` | 怪物生成/死亡自动绽放/清理 |

> [!WARNING]
> 模型挂载时序：MythicMobs 生成实体后，必须 **2 tick 延迟**再调 `ModelAPI.setEntityModel`，否则实体未完成注册会挂载失败。spawn 后立即转 `LivingEntity` 并做 `instanceof` 校验，spawn 本身要包 try-catch 处理 `InvalidMobTypeException`。
>
> 玩家进服补发也必须延迟 **40 tick（2 秒）**：DragonCore 客户端实体加载有延迟，玩家刚进服立即 `CoreAPI.setPlayerWorldTextureItem` 会因目标实体未加载而失败。延迟任务回调里要先检查玩家是否仍在线。

---

## 20. 绽放调用链

### 玩家 /bloom run 完整链路

```text
1. CommandTrigger.onCommand → CommandManager.runCommand
2. BloomCommand.isMatch("run") → execute → new Bloomer(player).bloom(preset)
3. Bloomer.bloom：isBloomPreset? isConflict? isCooldown? → BloomManager.bloom(player, preset)
4. BloomManager：PresetUtils.getBloomType() → get(bloomType) → BloomTexture / BloomModel
5. BloomTexture.bloom → BloomStrategyManager.getTexturePaths(player, preset) → List<path>
6. BloomTextureService.texPathToBloom(paths) → 逐层渲染 + 写缓存 + 补发
```

### 怪物自动绽放链路

```text
1. MythicMobSpawnEvent → MobSpawn.onMobSpawn
2. 读取 mob 配置的 TuoLingBloom 列表 → new Bloomer((LivingEntity) entity).bloom(preset)
3. 后续与玩家链路一致（mob 不进 isCooldown，因为非 Player）
```

### 补发（reissue）链路

```text
1. PlayerJoinEvent → 延迟 40L tick（等客户端实体加载完成）
2. reissueForPlayers：遍历在线 Bloomer，对每个 BloomerData 调 Looker.reissueBloom
3. reissueForMobs：MythicMobs 隔离，遍历 ActiveMob 补发
4. ReissueWorldTextureService.reissue：补发世界贴图
```

> [!NOTE]
> 补发按实体类型拆 `reissueForPlayers` / `reissueForMobs` 两个私有方法，提高可读性。模型路线不走补发。

---

## 21. 事件

插件**没有对外公开的监听事件**。外部插件如需感知绽放行为，建议通过命令/配置协作，或直接读取 `BloomManager` / `CacheManager` 提供的查询接口。当前对外契约只有 PlaceholderAPI 占位符。

## 22. 占位符扩展

插件通过 `BloomPlaceholderExpansion`（identifier 为 `tuolingbloom`）向 PlaceholderAPI 注册占位符，仅当检测到 PlaceholderAPI 存在时才注册：

| 占位符 | 返回 | 数据来源 |
|--------|------|----------|
| `%tuolingbloom_cooldown_<预设名>%` | 该玩家该预设剩余冷却秒数 | `Bloomer.getCooldown(preset)` → `CooldownCache` |

控制台请求（`player` 为 `null`）时返回 `null`，不会抛异常。

---

## 23. 扩展与构建

### 扩展指南

#### 1. 新增一种物品获取策略

1. 新建类实现 `BloomStrategy`：`getModeName()` + `getTexturePaths()` + `getMMNames()`
2. 在 `BloomStrategyManager` 构造器里 `add` 你的新策略
3. 在 config.yml 预设里写 `mode: "你的模式名"`

```java
public class MySlotStrategy implements BloomStrategy {
    public String getModeName() { return "my-slot"; }
    public List<String> getTexturePaths(Player p, String preset) { ... }
    public List<String> getMMNames(Player p, String preset) { ... }
}
```

#### 2. 新增一种绽放类型（除 texture/model 外）

1. 新建类实现 `Bloom`：`getName()` + `bloom()`
2. 在 `BloomManager` 构造器里 `register("newType", new BloomXxx())`
3. 新建对应 Service 完成装配执行
4. 在配置加载处为新类型目录登记路由：`bloomRouter.addRouter(文件名, "newType")`

#### 3. 新增一个命令

1. 新建类实现 `CommandHandler`：`execute()` + `isMatch()`
2. 在 `CommandManager` 构造器里 `register(...)`
3. 如需 Tab 补全，在 `CommandTrigger.onTabComplete` 里补充分支

#### 4. 新增一个缓存

1. 新建缓存类（继承 `ForwardingTable` / `ForwardingMap`）
2. 在 `CacheManager` 构造器里 `register(XxxCache.class, new XxxCache())`
3. 添加 `getXxxCache()` 方法（带 `containsKey` 防御 + 未注册提示）

### 构建与部署

#### 依赖

| 依赖 | 版本 | scope |
|------|------|-------|
| spigot-api | 1.12.2-R0.1-SNAPSHOT | `provided` |
| DragonCore | 2.6.1.5（本地 `libs/` jar） | `system` |
| TuoLingCore | 1.0-SNAPSHOT | `provided` |
| MythicMobs | 4.11.0 | `provided` |
| PlaceholderAPI | 2.11.2 | `provided` |
| lombok | 1.18.30 | `provided` |

除 DragonCore 外全部为 `provided`/`system`，不会打进产物，避免与其他插件冲突。

#### 打包

```shell
mvn clean package
```

构建后 `target/` 下会出现两个产物：

| 文件 | 说明 |
|------|------|
| `TuoLingBloom-1.0.jar` | **部署用**，shade 后的完整产物，放进 `plugins/` |
| `original-TuoLingBloom-1.0.jar` | shade 前的原始 jar，不要部署 |

> [!WARNING]
> 部署后务必整服重启。覆盖 jar 后用 `/reload` 或插件管理工具，常常仍会加载旧类，表现为「改了代码但行为没变」。排查时优先核对报错堆栈行号与当前源码是否一致。

### 编码约定

- **注释**：统一 `//` 行注释、简体中文；只在解释「为什么」或版本差异处写，不复述代码
- **单例访问**：统一 `TuoLingBloom.inst()`，不再用 `getInstance()`
- **Lombok**：数据类用 `@Getter` + `@AllArgsConstructor` 减少样板代码
- **集合 getter**：返回新集合实例（内含副本数据），禁止直接暴露内部引用
- **配置字段**：全部 `final`，构造时一次性赋值，禁止原地修改
- **消息派发**：玩家/控制台/绽放者三类消息统一走 `Message` 的 `sendPlayer` / `sendConsole` / `sendBloomer`，不直接 `sender.sendMessage`
- **后台日志**：后台日志用 `BloomLogger`，与玩家消息区分；消息文案**不走 SendMessageUtil**
- **域内放置**：领域管理器放对应领域包（如 `BloomManager` 在 `bloom/`），跨域基础设施放集中包（如 `CacheManager` 在 `cache/`）
- **Actor 放置**：Actor 类（`Bloomer`/`Looker`）放 `actor/` 包，与 model/service 数据类分开
- **纹理 ID 拼接**：组件间用下划线 `_` 分隔（`preset_index_uuid`），避免碰撞
- **补发拆分**：按实体类型拆 `reissueForPlayers` / `reissueForMobs` 两个私有方法
- **加载边界兜底**：容错集中在配置加载边界，下游组件接收永远有效数据，不做没必要的判空
- **模型配置结构**：`model/*.yml` 用 `Map<Integer, ModelData>`（`setting.1/2` 数字键），与 `texture/*.yml` 的 `LayerSetting` 对齐

#### 本地编译校验

没有 Maven 时，可用 javac 全量编译做语法与类型校验（classpath 指向本机 Maven 仓库与 `libs/` 中的依赖）：

```powershell
$repo = "$env:USERPROFILE\.m2\repository"
$cp = @(
  "$repo\org\spigotmc\spigot-api\1.12.2-R0.1-SNAPSHOT\spigot-api-1.12.2-R0.1-*.jar",
  "$repo\org\projectlombok\lombok\1.18.30\lombok-1.18.30.jar",
  "libs\DragonCore-2.6.1.5.jar",
  "$repo\io\lumine\xikage\mythicmobs\4.11.0\MythicMobs-4.11.0.jar",
  "$repo\me\clip\placeholderapi\2.11.2\placeholderapi-2.11.2.jar"
) -join ";"
$files = Get-ChildItem "src\main\java" -Recurse -Filter *.java | % { $_.FullName }
javac -encoding UTF-8 -cp $cp -d target\classes $files
```
