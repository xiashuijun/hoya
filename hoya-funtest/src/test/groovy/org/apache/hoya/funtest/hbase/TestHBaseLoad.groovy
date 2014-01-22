/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hoya.funtest.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.util.test.LoadTestDataGenerator
import org.apache.hadoop.hbase.util.LoadTestTool

class TestHBaseLoad extends TestFunctionalHBaseCluster {

  @Override
  String getClusterName() {
    return "test_hbase_load"
  }

  @Override
  void clusterLoadOperations(Configuration clientConf, int numWorkers) {
    int numKeys = 4000 * numWorkers
    String[] args = ["-tn", "test", "-write", "4:100", "-num_keys", numKeys, "-zk",
         clientConf.get("hbase.zookeeper.quorum"), "-zk_root", "/yarnapps_hoya_yarn_"+getClusterName()]
    LoadTestTool loadTool = new LoadTestTool();
    loadTool.setConf(clientConf)
    int ret = loadTool.run(args);
    assert ret == 0;
  }
}
