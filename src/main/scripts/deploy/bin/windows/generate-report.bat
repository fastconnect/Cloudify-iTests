@echo on

@echo generating report

set BUILD_NUMBER=%1
set SUITE_NAME=%2
set MAJOR_VERSION=%3
set MINOR_VERSION=%4
set BUILD_LOG_URL=%5
set SUITE_TYPE=%6
set MAVEN_PROJECTS_VERSION_XAP=%7
set MAVEN_PROJECTS_VERSION_CLOUDIFY=%8

pushd %SGTEST_HOME%

set CLOUDIFY_HOME=%BUILD_LOCATION%

call mvn exec:java -Dexec.mainClass="iTests.framework.testng.report.TestsReportMerger" -Dexec.args="%SUITE_NAME% %SGTEST_HOME%\..\%SUITE_NAME% %SGTEST_HOME%\..\%SUITE_NAME%" -Dcloudify.home=%CLOUDIFY_HOME% -DgsVersion=%MAVEN_PROJECTS_VERSION_XAP% -DcloudifyVersion=%MAVEN_PROJECTS_VERSION_CLOUDIFY%

call mvn exec:java -Dexec.mainClass="iTests.framework.testng.report.wiki.WikiReporter" -Dexec.args="%SGTEST_HOME%\..\%SUITE_NAME% %SUITE_TYPE% %BUILD_NUMBER% %MAJOR_VERSION% %MINOR_VERSION% %BUILD_LOG_URL%" -Dcloudify.home=%CLOUDIFY_HOME% -DgsVersion=%MAVEN_PROJECTS_VERSION_XAP% -DcloudifyVersion=%MAVEN_PROJECTS_VERSION_CLOUDIFY%

popd
