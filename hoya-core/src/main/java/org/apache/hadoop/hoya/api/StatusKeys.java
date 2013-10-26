/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *   
 *    http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License. See accompanying LICENSE file.
 */

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

package org.apache.hadoop.hoya.api;

public interface StatusKeys {
  String STAT_CONTAINERS_ACTIVE_REQUESTS = "containers.active.requests";
  String STAT_CONTAINERS_COMPLETED = "containers.completed";
  String STAT_CONTAINERS_DESIRED = "containers.desired";
  String STAT_CONTAINERS_FAILED = "containers.failed";
  String STAT_CONTAINERS_LIVE =
    "containers.live";
  String STAT_CONTAINERS_REQUESTED = "containers.requested";
  String STAT_CONTAINERS_STARTED =
    "containers.start.started";
  String STAT_CONTAINERS_START_FAILED =
      "containers.start.failed";
  String STAT_CONTAINERS_SURPLUS =
      "containers.surplus";
  String STAT_CONTAINERS_UNKNOWN_COMPLETED =
      "containers.unknown.completed";

  String STAT_CREATE_TIME = "create.time";
}
