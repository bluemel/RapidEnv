@echo off
:##############################################################################################
:# RapidEnv: renv.cmd
:#
:# Copyright (C) 2011 Martin Bluemel
:#
:# Creation Date: 06/03/2010
:#
:# This program is free software; you can redistribute it and/or modify it under the terms of the
:# GNU Lesser General Public License as published by the Free Software Foundation;
:# either version 3 of the License, or (at your option) any later version.
:# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
:# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
:# See the GNU Lesser General Public License for more details.
:# You should have received a copies of the GNU Lesser General Public License and the
:# GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
:##############################################################################################

:###############################################
:# The RapidEnv command line wrapper
:# Windows script.
:###############################################

:# initialize RapidEnv environment variables

:# RAPID_ENV_HOME is the parent folder of where this script is located
if defined RAPID_ENV_HOME goto RAPID_ENV_HOME_OK
set RAPID_ENV_HOME=%~dp0
set RAPID_ENV_HOME=%RAPID_ENV_HOME:~0,-5%
:RAPID_ENV_HOME_OK

:# RAPID_ENV_PROFILE is the common file name part for both profiles including absolute path:
:# the properties and the environment variable set up shell script
set RAPID_ENV_PROFILES_HOME=%RAPID_ENV_HOME%\profile
if not exist "%RAPID_ENV_PROFILES_HOME%" mkdir "%RAPID_ENV_PROFILES_HOME%"
set RAPID_ENV_PROFILE=%RAPID_ENV_PROFILES_HOME%\renv_%USERNAME%_%COMPUTERNAME%

if not defined RAPID_ENV_LIBDIR set RAPID_ENV_LIBDIR=%RAPID_ENV_HOME%\lib

pushd %RAPID_ENV_HOME%

:# find Java to execute renv
set JAVA=java
if exist "%ProgramFiles%\Java\jre5\bin\java.exe" set JAVA=%ProgramFiles%\Java\jre5\bin\java.exe
if exist "%ProgramFiles%\Java\jre6\bin\java.exe" set JAVA=%ProgramFiles%\Java\jre6\bin\java.exe
if exist "%ProgramFiles%\Java\jre7\bin\java.exe" set JAVA=%ProgramFiles%\Java\jre7\bin\java.exe
if "%1" == "-v" if not "%JAVA%" == "java" echo using JRE "%JAVA%"
if "%1" == "-verbose" if not "%JAVA%" == "java" echo using JRE "%JAVA%"

:# RAPID_ENV_COMMAND is the JVM call to execute the "renv" command
set RAPID_ENV_COMMAND="%JAVA%"
if defined HTTP_PROXY_HOST set RAPID_ENV_COMMAND=%RAPID_ENV_COMMAND% -Dhttp.proxyHost=%HTTP_PROXY_HOST%
if defined HTTP_PROXY_PORT set RAPID_ENV_COMMAND=%RAPID_ENV_COMMAND% -Dhttp.proxyPort=%HTTP_PROXY_PORT%
set RAPID_ENV_COMMAND=%RAPID_ENV_COMMAND% -classpath "%RAPID_ENV_LIBDIR%\rapidenv-@version@.jar
set RAPID_ENV_COMMAND=%RAPID_ENV_COMMAND%;%RAPID_ENV_LIBDIR%\rapidbeans-framework-@version.rapidbeans-framework@.jar
set RAPID_ENV_COMMAND=%RAPID_ENV_COMMAND%;%RAPID_ENV_LIBDIR%\commons-compress-@version.commons-compress@.jar
set RAPID_ENV_COMMAND=%RAPID_ENV_COMMAND%;%RAPID_ENV_LIBDIR%\ant-@version.ant@.jar
set RAPID_ENV_COMMAND=%RAPID_ENV_COMMAND%" org.rapidbeans.rapidenv.cmd.CmdRenv

:# if there is not already an RapidEnv profile for this user and this host do the set up
if not exist "%RAPID_ENV_PROFILE%.cmd" call :SETUPENV %*
if not exist "%RAPID_ENV_PROFILE%.cmd" echo RapidEnv ERROR: boot error, %RAPID_ENV_PROFILE%.cmd not created.& goto END

:# source the RapidEnv profile
call "%RAPID_ENV_PROFILE%.cmd"

:# start the RapidEnv interpreter with the given options and command
call %RAPID_ENV_COMMAND% %*
if %ERRORLEVEL% == 9009 echo ERROR %ERRORLEVEL% during execution of renv.cmd: probably java is not the command path& goto END
if not %ERRORLEVEL% == 0 echo ERROR %ERRORLEVEL% during execution of renv.cmd& goto END

:# source the RapidEnv profile again in case it has changed
if exist "%RAPID_ENV_PROFILE%.cmd" call "%RAPID_ENV_PROFILE%.cmd"

goto END


:SETUPENV

:SETUPENV_LOOP
if "%1" == "" goto SETUPENV_CONT1
if "%1" == "-yes" set RAPID_ENV_OPTIONS=%RAPID_ENV_OPTIONS% -yes
if "%1" == "-y" set RAPID_ENV_OPTIONS=%RAPID_ENV_OPTIONS% -yes
if "%1" == "-verbose" set RAPID_ENV_OPTIONS=%RAPID_ENV_OPTIONS% -verbose
if "%1" == "-v" set RAPID_ENV_OPTIONS=%RAPID_ENV_OPTIONS% -verbose
if "%1" == "-debug" set RAPID_ENV_OPTIONS=%RAPID_ENV_OPTIONS% -debug
if "%1" == "-d" set RAPID_ENV_OPTIONS=%RAPID_ENV_OPTIONS% -debug
shift
goto SETUPENV_LOOP

:SETUPENV_CONT1
call %RAPID_ENV_COMMAND% %RAPID_ENV_OPTIONS% boot
if not %ERRORLEVEL% == 0 echo ERROR %ERRORLEVEL% during execution of renv.cmd& goto END
if not exist "%RAPID_ENV_PROFILE%.cmd" goto :EOF

:# source the RapidEnv profile the first time
call "%RAPID_ENV_PROFILE%.cmd"
title %RAPID_ENV_PROJECT% %RAPID_ENV_BRANCH% Command Prompt

:# initially install the environment
call %RAPID_ENV_COMMAND% install
if not %ERRORLEVEL% == 0 echo ERROR %ERRORLEVEL% during execution of renv.cmd& goto END

:# source the RapidEnv profile again in case it has changed
call "%RAPID_ENV_PROFILE%.cmd"

goto :EOF

:TESTEXPR
set retTestExpr=false
for /F %%i in ('echo %argTestString% ^| findstr /R "%argTestExpr%"') do if not "%%i" == "" set retTestExpr=true
goto :EOF

:END

popd

:# check if WD contains RAPID_ENV_PROJECT_HOME
if not defined RAPID_ENV_PROJECT_HOME goto END1

for /F %%i in ('cd') do set WD=%%i
set argTestString=%WD%
set argTestExpr=%RAPID_ENV_PROJECT_HOME:\=\\%
call :TESTEXPR
if "%retTestExpr%" == "false" echo WARNING: current working directory "%WD%"& echo.  is not under project root "%RAPID_ENV_PROJECT_HOME%"

:END1
