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

package org.apache.hoya.funtest.framework

import groovy.transform.CompileStatic
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem as HadoopFS
import org.apache.hadoop.util.ExitUtil
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.service.launcher.ServiceLauncher
import org.apache.hoya.testtools.HoyaTestUtils
import org.apache.hoya.tools.HoyaUtils
import org.apache.hoya.yarn.Arguments
import org.apache.hoya.yarn.client.HoyaClient
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.rules.Timeout
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static org.apache.hoya.HoyaExitCodes.*
import static HoyaFuntestProperties.*
import static org.apache.hoya.yarn.Arguments.*
import static org.apache.hoya.yarn.HoyaActions.*;
import static org.apache.hoya.testtools.HoyaTestUtils.*
import static org.apache.hoya.HoyaXMLConfKeysForTesting.*

@CompileStatic
abstract class HoyaCommandTestBase extends HoyaTestUtils {
  private static final Logger log =
      LoggerFactory.getLogger(HoyaCommandTestBase.class);
  
  public static final String BASH = '/bin/bash -s'
  public static final String HOYA_CONF_DIR = System.getProperty(
      HOYA_CONF_DIR_PROP)
  public static final String HOYA_BIN_DIR = System.getProperty(
      HOYA_BIN_DIR_PROP)
  public static final File HOYA_BIN_DIRECTORY = new File(
      HOYA_BIN_DIR).canonicalFile
  public static final File HOYA_SCRIPT = new File(
      HOYA_BIN_DIRECTORY,
      "bin/hoya").canonicalFile
  public static final File HOYA_CONF_DIRECTORY = new File(
      HOYA_CONF_DIR).canonicalFile
  public static final File HOYA_CONF_XML = new File(HOYA_CONF_DIRECTORY,
                                                    "hoya-client.xml").canonicalFile

  public static final YarnConfiguration HOYA_CONFIG
  public static final int THAW_WAIT_TIME
  public static final int FREEZE_WAIT_TIME
  public static final int HBASE_LAUNCH_WAIT_TIME
  public static final int ACCUMULO_LAUNCH_WAIT_TIME
  public static final int HOYA_TEST_TIMEOUT
  public static final boolean ACCUMULO_TESTS_ENABLED
  public static final boolean HBASE_TESTS_ENABLED

  static {
    HOYA_CONFIG = new YarnConfiguration()
    HOYA_CONFIG.addResource(HOYA_CONF_XML.toURI().toURL())
    THAW_WAIT_TIME = HOYA_CONFIG.getInt(
        KEY_HOYA_THAW_WAIT_TIME,
        DEFAULT_HOYA_THAW_WAIT_TIME)
    FREEZE_WAIT_TIME = HOYA_CONFIG.getInt(
        KEY_HOYA_FREEZE_WAIT_TIME,
        DEFAULT_HOYA_FREEZE_WAIT_TIME)
    HBASE_LAUNCH_WAIT_TIME = HOYA_CONFIG.getInt(
        KEY_HOYA_HBASE_LAUNCH_TIME,
        DEFAULT_HOYA_HBASE_LAUNCH_TIME)
    HOYA_TEST_TIMEOUT = HOYA_CONFIG.getInt(
        KEY_HOYA_TEST_TIMEOUT,
        DEFAULT_HOYA_TEST_TIMEOUT)
    ACCUMULO_LAUNCH_WAIT_TIME = HOYA_CONFIG.getInt(
        KEY_HOYA_ACCUMULO_LAUNCH_TIME,
        DEFAULT_HOYA_ACCUMULO_LAUNCH_TIME)
    ACCUMULO_TESTS_ENABLED =
        HOYA_CONFIG.getBoolean(KEY_HOYA_TEST_ACCUMULO_ENABLED, true)
    HBASE_TESTS_ENABLED =
        HOYA_CONFIG.getBoolean(KEY_HOYA_TEST_HBASE_ENABLED, true)
 }

  @Rule
  public final Timeout testTimeout = new Timeout(HOYA_TEST_TIMEOUT);


  @BeforeClass
  public static void setupClass() {
    Configuration conf = loadHoyaConf();
    if (HoyaUtils.maybeInitSecurity(conf)) {
      log.debug("Security enabled")
      HoyaUtils.forceLogin()
    }
    HoyaShell.hoyaConfDir = HOYA_CONF_DIRECTORY
    HoyaShell.hoyaScript = HOYA_SCRIPT
    log.info("Test using ${HadoopFS.getDefaultUri(HOYA_CONFIG)} " +
             "and YARN RM @ ${HOYA_CONFIG.get(YarnConfiguration.RM_ADDRESS)}")
  }

  /**
   * Exec any hoya command 
   * @param conf
   * @param commands
   * @return the shell
   */
  public static HoyaShell hoya(List<String> commands) {
    HoyaShell shell = new HoyaShell(commands)
    shell.execute()
    return shell
  }

  /**
   * Execute an operation, state the expected error code
   * @param exitCode exit code
   * @param commands commands
   * @return
   */
  public static HoyaShell hoya(int exitCode, List<String> commands) {
    return HoyaShell.run(commands, exitCode)
  }

  /**
   * Load the client XML file
   * @return
   */
  public static Configuration loadHoyaConf() {
    Configuration conf = new Configuration(true)
    conf.addResource(HOYA_CONF_XML.toURI().toURL())
    return conf
  }

  public static HadoopFS getClusterFS() {
    return HadoopFS.get(HOYA_CONFIG)
  }


  static HoyaShell destroy(String name) {
    hoya([
        ACTION_DESTROY, name
    ])
  }

  static HoyaShell destroy(int result, String name) {
    hoya(result, [
        ACTION_DESTROY, name
    ])
  }

  static HoyaShell exists(String name, boolean live = true) {

    List<String> args = [
        ACTION_EXISTS, name
    ]
    if (live) {
      args << Arguments.ARG_LIVE
    }
    hoya(args)
  }

  static HoyaShell exists(int result, String name, boolean live = true) {
    List<String> args = [
        ACTION_EXISTS, name
    ]
    if (live) {
      args << ARG_LIVE
    }
    hoya(result, args)
  }

  static HoyaShell freeze(String name) {
    hoya([
        ACTION_FREEZE, name
    ])
  }

  static HoyaShell getConf(String name) {
    hoya([
        ACTION_GETCONF, name
    ])
  }

  static HoyaShell getConf(int result, String name) {
    hoya(result,
         [
             ACTION_GETCONF, name
         ])
  }

  static HoyaShell freezeForce(String name) {
    hoya([
        ACTION_FREEZE, ARG_FORCE, name
    ])
  }

  static HoyaShell list(String name) {
    List<String> cmd = [
        ACTION_LIST
    ]
    if (name != null) {
      cmd << name
    }
    hoya(cmd)
  }

  static HoyaShell list(int result, String name) {
    List<String> cmd = [
        ACTION_LIST
    ]
    if (name != null) {
      cmd << name
    }
    hoya(result, cmd)
  }

  static HoyaShell status(String name) {
    hoya([
        ACTION_STATUS, name
    ])
  }

  static HoyaShell status(int result, String name) {
    hoya(result,
         [
             ACTION_STATUS, name
         ])
  }

  static HoyaShell thaw(String name) {
    hoya([
        ACTION_THAW, name
    ])
  }

  static HoyaShell thaw(int result, String name) {
    hoya(result,
         [
             ACTION_THAW, name
         ])
  }

  /**
   * Ensure that a cluster has been destroyed
   * @param name
   */
  static void ensureClusterDestroyed(String name) {
    if (freezeForce(name).ret != EXIT_UNKNOWN_HOYA_CLUSTER) {
      //cluster exists
      destroy(name)
    }
  }

  /**
   * Assert the exit code is that the cluster is unknown
   * @param shell shell
   */
  public static void assertSuccess(HoyaShell shell) {
    assertExitCode(shell, 0)
  }
  /**
   * Assert the exit code is that the cluster is unknown
   * @param shell shell
   */
  public static void assertUnknownCluster(HoyaShell shell) {
    assertExitCode(shell, EXIT_UNKNOWN_HOYA_CLUSTER)
  }

  /**
   * Assert a shell exited with a given error code
   * if not the output is printed and an assertion is raised
   * @param shell shell
   * @param errorCode expected error code
   */
  public static void assertExitCode(HoyaShell shell, int errorCode) {
    shell.assertExitCode(errorCode)
  }

  /**
   * Create a connection to the cluster by execing the status command
   *
   * @param clustername
   * @return
   */
  HoyaClient bondToCluster(Configuration conf, String clustername) {

    String address = getRequiredConfOption(conf, YarnConfiguration.RM_ADDRESS)

    ServiceLauncher<HoyaClient> launcher = launchHoyaClientAgainstRM(
        address,
        ["exists", clustername],
        conf)

    int exitCode = launcher.serviceExitCode
    if (exitCode) {
      throw new ExitUtil.ExitException(exitCode, "exit code = $exitCode")
    }
    HoyaClient hoyaClient = launcher.service
    hoyaClient.deployedClusterName = clustername
    return hoyaClient;
  }

  /**
   * Create or build a hoya cluster (the action is set by the first verb)
   * @param action operation to invoke: ACTION_CREATE or ACTION_BUILD
   * @param clustername cluster name
   * @param roles map of rolename to count
   * @param extraArgs list of extra args to add to the creation command
   * @param deleteExistingData should the data of any existing cluster
   * of this name be deleted
   * @param blockUntilRunning block until the AM is running
   * @param clusterOps map of key=value cluster options to set with the --option arg
   * @return shell which will have executed the command.
   */
  public HoyaShell createOrBuildHoyaCluster(
      String action,
      String clustername,
      Map<String, Integer> roles,
      List<String> extraArgs,
      boolean blockUntilRunning,
      Map<String, String> clusterOps) {
    assert action != null
    assert clustername != null



    List<String> roleList = [];
    roles.each { String role, Integer val ->
      log.info("Role $role := $val")
      roleList << ARG_ROLE << role << Integer.toString(val)
    }

    List<String> argsList = [action, clustername]

    argsList << ARG_ZKHOSTS <<
    HOYA_CONFIG.getTrimmed(KEY_HOYA_TEST_ZK_HOSTS, DEFAULT_HOYA_ZK_HOSTS)

    argsList << ARG_IMAGE <<
    HOYA_CONFIG.getTrimmed(KEY_HOYA_TEST_HBASE_TAR)

    argsList << ARG_CONFDIR <<
    HOYA_CONFIG.getTrimmed(KEY_HOYA_TEST_HBASE_APPCONF)

    if (blockUntilRunning) {
      argsList << ARG_WAIT << Integer.toString(THAW_WAIT_TIME)
    }

    argsList += roleList;

    //now inject any cluster options
    clusterOps.each { String opt, String val ->
      argsList << ARG_OPTION << opt << val;
    }

    if (extraArgs != null) {
      argsList += extraArgs;
    }
    hoya(0, argsList)
  }

  /**
   * Create a hoya cluster
   * @param clustername cluster name
   * @param roles map of rolename to count
   * @param extraArgs list of extra args to add to the creation command
   * @param blockUntilRunning block until the AM is running
   * @param clusterOps map of key=value cluster options to set with the --option arg
   * @return launcher which will have executed the command.
   */
  public HoyaShell createHoyaCluster(
      String clustername,
      Map<String, Integer> roles,
      List<String> extraArgs,
      boolean blockUntilRunning,
      Map<String, String> clusterOps) {
    return createOrBuildHoyaCluster(
        ACTION_CREATE,
        clustername,
        roles,
        extraArgs,
        blockUntilRunning,
        clusterOps)
  }


}
