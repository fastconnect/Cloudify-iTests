#!/bin/bash

BUILD_NUMBER=$1 
 INCLUDE=$2
 EXCLUDE=$3
 SUITE_NAME=$4
/export/utils/ant/apache-ant-1.8.1/bin/ant -d -DBUILD_DIR=${BUILD_DIR} -DBUILD_NUMBER=${BUILD_NUMBER} -DINCLUDE=${INCLUDE} -DEXCLUDE=${EXCLUDE} -DSUITE_NAME=${SUITE_NAME} -f run.xml testsummary

#return java exit code. 
exit $?