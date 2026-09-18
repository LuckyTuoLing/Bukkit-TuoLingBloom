@echo off
chcp 65001 >nul 2>&1
title TuoLingBloom Editor Server
cd /d "%~dp0"
echo ========================================
echo   TuoLingBloom Editor Server (PowerShell)
echo ========================================
echo.
echo Starting server on port 17965...
powershell -ExecutionPolicy Bypass -NoProfile -Command "[Console]::OutputEncoding=[System.Text.Encoding]::UTF8; $OutputEncoding=[System.Text.Encoding]::UTF8; $bytes=[System.IO.File]::ReadAllBytes('%~dp0server.ps1'); $text=[System.Text.Encoding]::UTF8.GetString($bytes); Invoke-Expression $text"
echo.
echo Server stopped. Press any key to exit.
pause >nul
