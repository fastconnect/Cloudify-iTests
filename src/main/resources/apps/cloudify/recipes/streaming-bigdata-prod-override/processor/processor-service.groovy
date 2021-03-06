/*******************************************************************************
* Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
import java.util.concurrent.TimeUnit

service {
    name "processor"
    numInstances 1
    maxAllowedInstances 1
    statefulProcessingUnit {
        binaries "rt-analytics-processor.jar"
        //possible values "cassandra-archiver,cassandra-discovery" or "file-archiver"
        springProfilesActive "cassandra-archiver,cassandra-discovery"
        sla {
            memoryCapacity 128
            maxMemoryCapacity 128
            highlyAvailable false
            memoryCapacityPerContainer 128
        }

    }
}
