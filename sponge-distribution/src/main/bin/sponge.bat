@echo off

if "%JAVA_HOME%"=="" (set JAVA_CMD=java) else (set JAVA_CMD=%JAVA_HOME%\bin\java)
if "%JAVA_OPTIONS%"=="" (set JAVA_OPTIONS=-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication)
if "%SPONGE_HOME%"=="" (set SPONGE_HOME=%~dp0\..)

%JAVA_CMD% %JAVA_OPTIONS% -Dfile.encoding=UTF8 -cp "%SPONGE_HOME%/lib/*;%SPONGE_HOME%/lib/ext/*" -Dsponge.home=%SPONGE_HOME% -Dlogback.configurationFile=%SPONGE_HOME%\config\logback.xml ${mainClass} %*
