# TuoLingBloom Editor Backend Server (PowerShell)
# 基于 System.Net.HttpListener，零依赖，Windows 自带即可运行
# 端口: 17965

$ErrorActionPreference = 'Stop'

# ========== 常量与目录设置 ==========
$PORT = 17965
$ROOT_DIR = $PSScriptRoot
if (-not $ROOT_DIR) { $ROOT_DIR = (Get-Location).Path }
$TASKS_DIR = Join-Path $ROOT_DIR 'tasks'
$HTML_FILE = Join-Path $ROOT_DIR 'index.html'
$DC_DIR_FILE = Join-Path $ROOT_DIR 'dc_dir.txt'
$PREVIEW_TEX_FILE = Join-Path $ROOT_DIR 'preview_tex.json'

$VALID_EXT = @('.yml', '.yaml')
$SAFE_NAME_PATTERN = '^[\w\u4e00-\u9fff\u3400-\u4dbf\-\s./]+$'
$IMG_EXT_MAP = @{
    '.png'  = 'image/png'
    '.jpg'  = 'image/jpeg'
    '.jpeg' = 'image/jpeg'
    '.gif'  = 'image/gif'
    '.webp' = 'image/webp'
    '.bmp'  = 'image/bmp'
}
$IMG_EXTS = @('.png', '.jpg', '.jpeg', '.gif', '.webp', '.bmp')
$SEP = [System.IO.Path]::DirectorySeparatorChar

# 创建 tasks 目录
if (-not (Test-Path $TASKS_DIR)) {
    New-Item -ItemType Directory -Path $TASKS_DIR -Force | Out-Null
}

# ========== 辅助函数 ==========

function Get-RelDir {
    param([string]$relPath)
    $idx = $relPath.LastIndexOf('/')
    if ($idx -ge 0) { return $relPath.Substring(0, $idx) }
    return ''
}

function Get-RelName {
    param([string]$relPath)
    $idx = $relPath.LastIndexOf('/')
    if ($idx -ge 0) { return $relPath.Substring($idx + 1) }
    return $relPath
}

function Resolve-TaskPath {
    param([string]$relPath)
    if (-not $relPath) { return @{ path = $null; error = '路径不能为空' } }
    $relPath = $relPath.Replace('\', '/').Trim('/')
    $absPath = [System.IO.Path]::GetFullPath([System.IO.Path]::Combine($TASKS_DIR, $relPath))
    $normTasks = [System.IO.Path]::GetFullPath($TASKS_DIR)
    if ($absPath -ne $normTasks -and -not $absPath.StartsWith($normTasks + $SEP)) {
        return @{ path = $null; error = '非法路径' }
    }
    return @{ path = $absPath; error = $null }
}

function Test-ValidFilePath {
    param([string]$relPath)
    if (-not $relPath) { return $false }
    if ($relPath -notmatch $SAFE_NAME_PATTERN) { return $false }
    $lower = $relPath.ToLower()
    foreach ($ext in $VALID_EXT) {
        if ($lower.EndsWith($ext)) { return $true }
    }
    return $false
}

function Get-NormalizedBase {
    param([string]$dir)
    $norm = [System.IO.Path]::GetFullPath($dir)
    if (-not $norm.EndsWith($SEP)) { $norm = $norm + $SEP }
    return $norm
}

function Build-FileTree {
    $files = @()
    $items = Get-ChildItem -Path $TASKS_DIR -Recurse -File -ErrorAction SilentlyContinue
    $normTasks = Get-NormalizedBase $TASKS_DIR
    foreach ($item in $items) {
        $ext = $item.Extension.ToLower()
        if ($VALID_EXT -notcontains $ext) { continue }
        $fullNorm = [System.IO.Path]::GetFullPath($item.FullName)
        $rel = $fullNorm.Substring($normTasks.Length).Replace('\', '/')
        $files += @{ path = $rel; name = $item.Name; dir = (Get-RelDir $rel) }
    }
    $files = @($files | Sort-Object { $_['path'] })

    $tree = @{}
    foreach ($f in $files) {
        $parts = $f['path'].Split('/')
        $node = $tree
        for ($i = 0; $i -lt $parts.Length; $i++) {
            if ($i -eq $parts.Length - 1) {
                $node[$parts[$i]] = 'file'
            } else {
                if (-not $node.ContainsKey($parts[$i])) {
                    $node[$parts[$i]] = @{}
                }
                $node = $node[$parts[$i]]
            }
        }
    }

    return @{ files = $files; tree = $tree }
}

function Send-Response {
    param(
        $context,
        [byte[]]$bytes,
        [string]$contentType = 'application/json; charset=utf-8',
        [int]$statusCode = 200
    )
    $response = $context.Response
    $response.StatusCode = $statusCode
    $response.ContentType = $contentType
    $response.ContentLength64 = if ($bytes) { $bytes.Length } else { 0 }
    $response.Headers.Add('Access-Control-Allow-Origin', '*')
    $response.Headers.Add('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
    $response.Headers.Add('Access-Control-Allow-Headers', 'Content-Type')
    if ($bytes -and $bytes.Length -gt 0) {
        $response.OutputStream.Write($bytes, 0, $bytes.Length)
    }
    $response.OutputStream.Close()
}

function Send-Json {
    param($context, $data, [int]$statusCode = 200)
    $json = ConvertTo-Json -InputObject $data -Depth 100 -Compress
    if (-not $json) { $json = 'null' }
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($json)
    Send-Response $context $bytes 'application/json; charset=utf-8' $statusCode
}

function Send-Text {
    param($context, [string]$text, [string]$contentType = 'text/plain; charset=utf-8', [int]$statusCode = 200)
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($text)
    Send-Response $context $bytes $contentType $statusCode
}

function Send-FileBytes {
    param($context, [string]$filePath, [string]$contentType, [int]$statusCode = 200)
    $bytes = [System.IO.File]::ReadAllBytes($filePath)
    Send-Response $context $bytes $contentType $statusCode
}

function Send-Options {
    param($context)
    $response = $context.Response
    $response.StatusCode = 204
    $response.ContentLength64 = 0
    $response.Headers.Add('Access-Control-Allow-Origin', '*')
    $response.Headers.Add('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
    $response.Headers.Add('Access-Control-Allow-Headers', 'Content-Type')
    $response.OutputStream.Close()
}

function Parse-QueryString {
    param([string]$query)
    $result = @{}
    if ($query.StartsWith('?')) { $query = $query.Substring(1) }
    if (-not $query) { return $result }
    foreach ($pair in $query.Split('&')) {
        if (-not $pair) { continue }
        $kv = $pair.Split('=', 2)
        $key = [Uri]::UnescapeDataString($kv[0])
        $val = if ($kv.Length -gt 1) { [Uri]::UnescapeDataString($kv[1]) } else { '' }
        $result[$key] = $val
    }
    return $result
}

function Read-RequestBody {
    param($request)
    try {
        $reader = New-Object System.IO.StreamReader($request.InputStream, [System.Text.Encoding]::UTF8)
        $body = $reader.ReadToEnd()
        $reader.Close()
        return $body
    } catch {
        return ''
    }
}

function Show-FolderPicker {
    param([string]$Description = '选择目录')
    $resultFile = Join-Path $env:TEMP "bloom_pick_$([guid]::NewGuid().ToString()).txt"
    $psScript = @"
Add-Type -AssemblyName System.Windows.Forms
`$f = New-Object System.Windows.Forms.FolderBrowserDialog
`$f.Description = '$Description'
`$f.ShowNewFolderButton = `$false
if (`$f.ShowDialog() -eq 'OK') {
    [System.IO.File]::WriteAllText('$resultFile', `$f.SelectedPath, [System.Text.Encoding]::UTF8)
}
"@
    $encoded = [Convert]::ToBase64String([Text.Encoding]::Unicode.GetBytes($psScript))
    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = 'powershell.exe'
    $psi.Arguments = "-STA -NoProfile -WindowStyle Hidden -EncodedCommand $encoded"
    $psi.UseShellExecute = $true
    try {
        $p = [System.Diagnostics.Process]::Start($psi)
        if (-not $p.WaitForExit(120000)) {
            try { $p.Kill() } catch {}
            return $null
        }
        if (Test-Path $resultFile) {
            $result = [System.IO.File]::ReadAllText($resultFile, [System.Text.Encoding]::UTF8).Trim()
            Remove-Item $resultFile -Force -ErrorAction SilentlyContinue
            if ($result) { return $result }
        }
    } catch {
        Remove-Item $resultFile -Force -ErrorAction SilentlyContinue
    }
    return $null
}

# ========== 请求处理 ==========

function Handle-Request {
    param($context)

    $request = $context.Request
    $method = $request.HttpMethod
    $path = [Uri]::UnescapeDataString($request.Url.AbsolutePath)
    $query = Parse-QueryString $request.Url.Query

    if ($method -eq 'OPTIONS') {
        Send-Options $context
        return
    }

    if ($method -eq 'GET') {
        Handle-Get $context $path $query
        return
    }

    if ($method -eq 'POST') {
        $body = Read-RequestBody $request
        Handle-Post $context $path $body
        return
    }

    if ($method -eq 'PUT') {
        $body = Read-RequestBody $request
        Handle-Put $context $path $body
        return
    }

    if ($method -eq 'DELETE') {
        Handle-Delete $context $path
        return
    }

    Send-Text $context 'Method not allowed' 'text/plain; charset=utf-8' 405
}

function Handle-Get {
    param($context, $path, $query)

    # GET / - 首页
    if ($path -eq '/' -or $path -eq '/index.html') {
        if (Test-Path $HTML_FILE) {
            Send-FileBytes $context $HTML_FILE 'text/html; charset=utf-8' 200
        } else {
            Send-Text $context '<h1>HTML file not found</h1>' 'text/html; charset=utf-8' 404
        }
        return
    }

    # GET /api/files - 列出所有 yaml 文件（树形结构）
    if ($path -eq '/api/files') {
        $result = Build-FileTree
        Send-Json $context $result 200
        return
    }

    # GET /api/files/{path} - 读取文件内容
    if ($path.StartsWith('/api/files/')) {
        $relPath = $path.Substring('/api/files/'.Length)
        if (-not (Test-ValidFilePath $relPath)) {
            Send-Json $context @{ error = '非法文件路径' } 400
            return
        }
        $resolved = Resolve-TaskPath $relPath
        if ($resolved['error']) {
            Send-Json $context @{ error = $resolved['error'] } 400
            return
        }
        $absPath = $resolved['path']
        if (-not (Test-Path $absPath -PathType Leaf)) {
            Send-Json $context @{ error = '文件不存在' } 404
            return
        }
        try {
            $content = [System.IO.File]::ReadAllText($absPath, [System.Text.Encoding]::UTF8)
            Send-Json $context @{ path = $relPath; name = (Get-RelName $relPath); content = $content } 200
        } catch {
            Send-Json $context @{ error = $_.Exception.Message } 500
        }
        return
    }

    # GET /api/dc-dir - 获取持久化的 DragonCore 目录
    if ($path -eq '/api/dc-dir') {
        $dcDir = ''
        if (Test-Path $DC_DIR_FILE) {
            $dcDir = ([System.IO.File]::ReadAllText($DC_DIR_FILE, [System.Text.Encoding]::UTF8)).Trim()
        }
        Send-Json $context @{ dir = $dcDir } 200
        return
    }

    # GET /api/preview-tex - 获取预览贴图映射
    if ($path -eq '/api/preview-tex') {
        $data = @{}
        if (Test-Path $PREVIEW_TEX_FILE) {
            try {
                $text = [System.IO.File]::ReadAllText($PREVIEW_TEX_FILE, [System.Text.Encoding]::UTF8)
                $data = $text | ConvertFrom-Json
                # ConvertFrom-Json 返回 PSCustomObject，转为 hashtable 以便 Send-Json 正确序列化
                if ($data -isnot [Hashtable]) {
                    $ht = @{}
                    if ($data) {
                        foreach ($prop in $data.PSObject.Properties) {
                            $subHt = @{}
                            if ($prop.Value) {
                                foreach ($subProp in $prop.Value.PSObject.Properties) {
                                    $subHt[$subProp.Name] = $subProp.Value
                                }
                            }
                            $ht[$prop.Name] = $subHt
                        }
                    }
                    $data = $ht
                }
            } catch {
                $data = @{}
            }
        }
        Send-Json $context $data 200
        return
    }

    # GET /api/pick-folder - Windows 原生文件夹选择对话框
    if ($path -eq '/api/pick-folder') {
        $folder = Show-FolderPicker
        if ($folder -and (Test-Path $folder -PathType Container)) {
            Send-Json $context @{ dir = $folder.Replace('\', '/') } 200
        } else {
            Send-Json $context @{ dir = ''; cancelled = $true } 200
        }
        return
    }

    # GET /api/browse-dirs - 浏览目录（列出子目录）
    if ($path -eq '/api/browse-dirs') {
        $browsePath = $query['path']
        if (-not $browsePath) {
            $drives = @()
            foreach ($code in 65..90) {
                $letter = [char]$code
                $drive = "${letter}:\"
                if (Test-Path $drive) {
                    $drives += "${letter}:/"
                }
            }
            Send-Json $context @{ current = ''; dirs = $drives } 200
            return
        }
        $browsePath = [System.IO.Path]::GetFullPath($browsePath)
        if (-not (Test-Path $browsePath -PathType Container)) {
            Send-Json $context @{ error = "目录不存在: $browsePath" } 404
            return
        }
        $dirs = @()
        try {
            $entries = Get-ChildItem -Path $browsePath -Directory -ErrorAction Stop | Sort-Object Name
            foreach ($e in $entries) {
                $dirs += $e.Name
            }
        } catch {
            Send-Json $context @{ error = '无权限访问该目录' } 403
            return
        }
        Send-Json $context @{ current = $browsePath.Replace('\', '/'); dirs = $dirs } 200
        return
    }

    # GET /api/textures - 列出贴图目录下所有图片文件
    if ($path -eq '/api/textures') {
        $texDir = $query['dir']
        if (-not $texDir) {
            Send-Json $context @{ error = '缺少 dir 参数' } 400
            return
        }
        $texDir = [System.IO.Path]::GetFullPath($texDir)
        if (-not (Test-Path $texDir -PathType Container)) {
            Send-Json $context @{ error = "目录不存在: $texDir" } 404
            return
        }
        $textures = @()
        $items = Get-ChildItem -Path $texDir -Recurse -File -ErrorAction SilentlyContinue
        $baseNorm = Get-NormalizedBase $texDir
        foreach ($item in $items) {
            $ext = $item.Extension.ToLower()
            if ($IMG_EXTS -notcontains $ext) { continue }
            $fullNorm = [System.IO.Path]::GetFullPath($item.FullName)
            $rel = $fullNorm.Substring($baseNorm.Length).Replace('\', '/')
            $textures += $rel
        }
        $textures = @($textures | Sort-Object)
        Send-Json $context @{ dir = $texDir; textures = $textures } 200
        return
    }

    # GET /api/texture-file/{path}?dir=xxx - 读取贴图文件（代理图片）
    if ($path.StartsWith('/api/texture-file/')) {
        $relPath = $path.Substring('/api/texture-file/'.Length)
        $baseDir = $query['dir']
        if (-not $baseDir) {
            Send-Json $context @{ error = '缺少 dir 参数' } 400
            return
        }
        $baseDir = [System.IO.Path]::GetFullPath($baseDir)
        $fullPath = [System.IO.Path]::GetFullPath([System.IO.Path]::Combine($baseDir, $relPath))
        $baseNorm = Get-NormalizedBase $baseDir
        if ($fullPath -ne $baseDir -and -not $fullPath.StartsWith($baseNorm)) {
            Send-Json $context @{ error = '非法路径' } 403
            return
        }
        if (-not (Test-Path $fullPath -PathType Leaf)) {
            Send-Text $context 'Not found' 'text/plain; charset=utf-8' 404
            return
        }
        $ext = [System.IO.Path]::GetExtension($fullPath).ToLower()
        $ct = if ($IMG_EXT_MAP.ContainsKey($ext)) { $IMG_EXT_MAP[$ext] } else { 'application/octet-stream' }
        Send-FileBytes $context $fullPath $ct 200
        return
    }

    # GET /tasks/{path} - 静态文件（tasks 目录内的 yml 文件）
    if ($path.StartsWith('/tasks/')) {
        $relPath = $path.Substring('/tasks/'.Length)
        $resolved = Resolve-TaskPath $relPath
        if ($resolved['error']) {
            Send-Text $context 'Not found' 'text/plain; charset=utf-8' 404
            return
        }
        $absPath = $resolved['path']
        if ($absPath -and (Test-Path $absPath -PathType Leaf)) {
            Send-FileBytes $context $absPath 'text/yaml; charset=utf-8' 200
            return
        }
        Send-Text $context 'Not found' 'text/plain; charset=utf-8' 404
        return
    }

    Send-Text $context 'Not found' 'text/plain; charset=utf-8' 404
}

function Handle-Post {
    param($context, $path, $body)

    # POST /api/dc-dir - 保存 DragonCore 目录路径
    if ($path -eq '/api/dc-dir') {
        try {
            $data = if ($body) { $body | ConvertFrom-Json } else { [PSCustomObject]@{} }
            $dcDir = ([string]$data.dir).Trim()
            $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
            [System.IO.File]::WriteAllText($DC_DIR_FILE, $dcDir, $utf8NoBom)
            Send-Json $context @{ dir = $dcDir; saved = $true } 200
        } catch {
            Send-Json $context @{ error = $_.Exception.Message } 500
        }
        return
    }

    # POST /api/files - 创建新文件
    if ($path -eq '/api/files') {
        try {
            $data = if ($body) { $body | ConvertFrom-Json } else { [PSCustomObject]@{} }
            $relPath = ([string]$data.path).Trim().Replace('\', '/')
            if (-not (Test-ValidFilePath $relPath)) {
                Send-Json $context @{ error = '非法文件路径，只允许中文、字母、数字、横线、下划线、点、斜杠，且必须以 .yml 或 .yaml 结尾' } 400
                return
            }
            $resolved = Resolve-TaskPath $relPath
            if ($resolved['error']) {
                Send-Json $context @{ error = $resolved['error'] } 400
                return
            }
            $absPath = $resolved['path']
            if (Test-Path $absPath) {
                Send-Json $context @{ error = '文件已存在' } 409
                return
            }
            $parent = [System.IO.Path]::GetDirectoryName($absPath)
            if (-not (Test-Path $parent)) {
                New-Item -ItemType Directory -Path $parent -Force | Out-Null
            }
            $content = [string]$data.content
            $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
            [System.IO.File]::WriteAllText($absPath, $content, $utf8NoBom)
            Send-Json $context @{ path = $relPath; name = (Get-RelName $relPath); created = $true } 200
        } catch {
            Send-Json $context @{ error = $_.Exception.Message } 500
        }
        return
    }

    Send-Text $context 'Not found' 'text/plain; charset=utf-8' 404
}

function Handle-Put {
    param($context, $path, $body)

    # PUT /api/dc-dir - 同 POST
    if ($path -eq '/api/dc-dir') {
        try {
            $data = if ($body) { $body | ConvertFrom-Json } else { [PSCustomObject]@{} }
            $dcDir = ([string]$data.dir).Trim()
            $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
            [System.IO.File]::WriteAllText($DC_DIR_FILE, $dcDir, $utf8NoBom)
            Send-Json $context @{ dir = $dcDir; saved = $true } 200
        } catch {
            Send-Json $context @{ error = $_.Exception.Message } 500
        }
        return
    }

    # PUT /api/preview-tex - 保存预览贴图映射
    if ($path -eq '/api/preview-tex') {
        try {
            # body 就是整个 JSON 对象，直接格式化写入文件
            if ($body) {
                $data = $body | ConvertFrom-Json
                $json = ConvertTo-Json -InputObject $data -Depth 100
                $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
                [System.IO.File]::WriteAllText($PREVIEW_TEX_FILE, $json, $utf8NoBom)
            }
            Send-Json $context @{ saved = $true } 200
        } catch {
            Send-Json $context @{ error = $_.Exception.Message } 500
        }
        return
    }

    # PUT /api/files/{path}/rename - 重命名/移动文件
    if ($path.StartsWith('/api/files/') -and $path.EndsWith('/rename')) {
        $prefixLen = '/api/files/'.Length
        $suffixLen = '/rename'.Length
        $oldRel = $path.Substring($prefixLen, $path.Length - $prefixLen - $suffixLen)
        if (-not (Test-ValidFilePath $oldRel)) {
            Send-Json $context @{ error = '非法文件路径' } 400
            return
        }
        $resolved = Resolve-TaskPath $oldRel
        if ($resolved['error']) {
            Send-Json $context @{ error = $resolved['error'] } 400
            return
        }
        $oldAbs = $resolved['path']
        try {
            $data = if ($body) { $body | ConvertFrom-Json } else { [PSCustomObject]@{} }
            $newRel = ([string]$data.newPath).Trim().Replace('\', '/')
            if (-not (Test-ValidFilePath $newRel)) {
                Send-Json $context @{ error = '非法新文件路径' } 400
                return
            }
            $newResolved = Resolve-TaskPath $newRel
            if ($newResolved['error']) {
                Send-Json $context @{ error = $newResolved['error'] } 400
                return
            }
            $newAbs = $newResolved['path']
            if (-not (Test-Path $oldAbs -PathType Leaf)) {
                Send-Json $context @{ error = '源文件不存在' } 404
                return
            }
            if (Test-Path $newAbs) {
                Send-Json $context @{ error = '目标文件已存在' } 409
                return
            }
            $parent = [System.IO.Path]::GetDirectoryName($newAbs)
            if (-not (Test-Path $parent)) {
                New-Item -ItemType Directory -Path $parent -Force | Out-Null
            }
            Move-Item -Path $oldAbs -Destination $newAbs -Force
            Send-Json $context @{ oldPath = $oldRel; newPath = $newRel; renamed = $true } 200
        } catch {
            Send-Json $context @{ error = $_.Exception.Message } 500
        }
        return
    }

    # PUT /api/files/{path} - 保存文件内容
    if ($path.StartsWith('/api/files/')) {
        $relPath = $path.Substring('/api/files/'.Length)
        if (-not (Test-ValidFilePath $relPath)) {
            Send-Json $context @{ error = '非法文件路径' } 400
            return
        }
        $resolved = Resolve-TaskPath $relPath
        if ($resolved['error']) {
            Send-Json $context @{ error = $resolved['error'] } 400
            return
        }
        $absPath = $resolved['path']
        try {
            $data = if ($body) { $body | ConvertFrom-Json } else { [PSCustomObject]@{} }
            $content = [string]$data.content
            $parent = [System.IO.Path]::GetDirectoryName($absPath)
            if (-not (Test-Path $parent)) {
                New-Item -ItemType Directory -Path $parent -Force | Out-Null
            }
            $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
            [System.IO.File]::WriteAllText($absPath, $content, $utf8NoBom)
            Send-Json $context @{ path = $relPath; name = (Get-RelName $relPath); saved = $true } 200
        } catch {
            Send-Json $context @{ error = $_.Exception.Message } 500
        }
        return
    }

    Send-Text $context 'Not found' 'text/plain; charset=utf-8' 404
}

function Handle-Delete {
    param($context, $path)

    # DELETE /api/files/{path} - 删除文件
    if ($path.StartsWith('/api/files/')) {
        $relPath = $path.Substring('/api/files/'.Length)
        if (-not (Test-ValidFilePath $relPath)) {
            Send-Json $context @{ error = '非法文件路径' } 400
            return
        }
        $resolved = Resolve-TaskPath $relPath
        if ($resolved['error']) {
            Send-Json $context @{ error = $resolved['error'] } 400
            return
        }
        $absPath = $resolved['path']
        if (-not (Test-Path $absPath -PathType Leaf)) {
            Send-Json $context @{ error = '文件不存在' } 404
            return
        }
        try {
            Remove-Item -Path $absPath -Force
            Send-Json $context @{ path = $relPath; deleted = $true } 200
        } catch {
            Send-Json $context @{ error = $_.Exception.Message } 500
        }
        return
    }

    Send-Text $context 'Not found' 'text/plain; charset=utf-8' 404
}

# ========== 主循环 ==========

$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add("http://127.0.0.1:${PORT}/")

try {
    $listener.Start()
} catch {
    Write-Host "无法启动服务器: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "可能端口 $PORT 已被占用，请检查后重试。" -ForegroundColor Yellow
    exit 1
}

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  TuoLingBloom 编辑器后端服务已启动" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  地址: http://127.0.0.1:${PORT}" -ForegroundColor White
Write-Host "  工作目录: $TASKS_DIR" -ForegroundColor White
Write-Host "  按 Ctrl+C 停止" -ForegroundColor Yellow
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

try {
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        try {
            Handle-Request $context
        } catch {
            try {
                Send-Json $context @{ error = $_.Exception.Message } 500
            } catch {
                try { $context.Response.OutputStream.Close() } catch {}
            }
        }
    }
} catch {
    # Ctrl+C 或其他中断
} finally {
    if ($listener.IsListening) {
        $listener.Stop()
    }
    $listener.Close()
    Write-Host ""
    Write-Host "服务器已停止" -ForegroundColor Yellow
}
