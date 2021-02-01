@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  composer-language-server-expression-websocket startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and COMPOSER_LANGUAGE_SERVER_EXPRESSION_WEBSOCKET_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\org.galimatias.hello.websockets-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\org.galimatias.hello.ide-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\org.galimatias.hello-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\org.eclipse.xtext.xbase.ide-2.18.0.jar;%APP_HOME%\lib\org.eclipse.xtext.ide-2.18.0.jar;%APP_HOME%\lib\slf4j-simple-1.6.1.jar;%APP_HOME%\lib\slf4j-log4j12-1.7.24.jar;%APP_HOME%\lib\slf4j-api-1.7.24.jar;%APP_HOME%\lib\Java-WebSocket-1.3.8.jar;%APP_HOME%\lib\org.eclipse.xtext.xbase-2.18.0.jar;%APP_HOME%\lib\org.eclipse.xtext.common.types-2.18.0.jar;%APP_HOME%\lib\org.eclipse.xtext-2.18.0.jar;%APP_HOME%\lib\org.eclipse.xtext.util-2.18.0.jar;%APP_HOME%\lib\org.eclipse.lsp4j-0.8.0.jar;%APP_HOME%\lib\org.eclipse.lsp4j.generator-0.8.0.jar;%APP_HOME%\lib\org.eclipse.xtend.lib-2.18.0.jar;%APP_HOME%\lib\org.eclipse.xtend.lib.macro-2.18.0.jar;%APP_HOME%\lib\org.eclipse.xtext.xbase.lib-2.18.0.jar;%APP_HOME%\lib\guava-21.0.jar;%APP_HOME%\lib\guice-3.0.jar;%APP_HOME%\lib\classgraph-4.8.35.jar;%APP_HOME%\lib\javax-websocket-server-impl-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-annotations-9.4.16.v20190411.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\antlr-runtime-3.2.jar;%APP_HOME%\lib\asm-commons-7.1.jar;%APP_HOME%\lib\asm-analysis-7.1.jar;%APP_HOME%\lib\asm-tree-7.1.jar;%APP_HOME%\lib\asm-7.1.jar;%APP_HOME%\lib\org.eclipse.emf.common-2.12.0.jar;%APP_HOME%\lib\org.eclipse.emf.ecore-2.12.0.jar;%APP_HOME%\lib\org.eclipse.emf.ecore.change-2.11.0.jar;%APP_HOME%\lib\org.eclipse.emf.ecore.xmi-2.12.0.jar;%APP_HOME%\lib\org.eclipse.lsp4j.websocket-0.8.0.jar;%APP_HOME%\lib\org.eclipse.lsp4j.jsonrpc-0.8.0.jar;%APP_HOME%\lib\org.eclipse.equinox.common-3.10.200.jar;%APP_HOME%\lib\org.eclipse.osgi-3.13.200.jar;%APP_HOME%\lib\websocket-server-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-plus-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-webapp-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-servlet-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-security-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-server-9.4.16.v20190411.jar;%APP_HOME%\lib\websocket-servlet-9.4.16.v20190411.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\javax-websocket-client-impl-9.4.16.v20190411.jar;%APP_HOME%\lib\websocket-client-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-client-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-http-9.4.16.v20190411.jar;%APP_HOME%\lib\websocket-common-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-io-9.4.16.v20190411.jar;%APP_HOME%\lib\javax.websocket-api-1.0.jar;%APP_HOME%\lib\jetty-jndi-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-xml-9.4.16.v20190411.jar;%APP_HOME%\lib\jetty-util-9.4.16.v20190411.jar;%APP_HOME%\lib\javax.websocket-client-api-1.0.jar;%APP_HOME%\lib\gson-2.8.2.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\websocket-api-9.4.16.v20190411.jar

@rem Execute composer-language-server-expression-websocket
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %COMPOSER_LANGUAGE_SERVER_EXPRESSION_WEBSOCKET_OPTS%  -classpath "%CLASSPATH%" org.galimatias.hello.websockets.RunWebSocketServer3 %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable COMPOSER_LANGUAGE_SERVER_EXPRESSION_WEBSOCKET_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%COMPOSER_LANGUAGE_SERVER_EXPRESSION_WEBSOCKET_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
