/**
 * Copyright 2016 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.origin.jdbc.table;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ListBeanModel;
import com.streamsets.pipeline.api.PushSource;
import com.streamsets.pipeline.api.Source;
import com.streamsets.pipeline.api.Stage;
import com.streamsets.pipeline.api.ValueChooserModel;
import com.streamsets.pipeline.config.TimeZoneChooserValues;
import com.streamsets.pipeline.lib.jdbc.JdbcErrors;

import java.util.List;

public class TableJdbcConfigBean {
  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      label = "Table Configs",
      displayPosition  = 10,
      group = "TABLE"
  )
  @ListBeanModel
  public List<TableConfigBean> tableConfigs;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      defaultValue = "UTC",
      label = "Data Time Zone",
      description = "Time zone to use to resolve time based expressions",
      displayPosition = 70,
      group = "JDBC"
  )
  @ValueChooserModel(TimeZoneChooserValues.class)
  public String timeZoneID;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      defaultValue = "SWITCH_TABLES",
      label = "Per Batch Strategy",
      description = "Determines the strategy for each batch to generate records from.",
      displayPosition = 80,
      group = "JDBC"
  )
  @ValueChooserModel(BatchTableStrategyChooserValues.class)
  public BatchTableStrategy batchTableStrategy;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      defaultValue = "-1",
      label = "Result Set Cache Size",
      description = "Determines how many open statements/result sets can be cached." +
          " Leave -1 to Opt Out and have one statement open per table.",
      displayPosition = 180,
      group = "JDBC",
      dependsOn = "batchTableStrategy",
      //For Process all rows we will need a cache with size 1, user does not have to configure it.
      triggeredByValue = "SWITCH_TABLES"
  )
  public int resultCacheSize;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      defaultValue = "NONE",
      label = "Table Order Strategy",
      description = "Determines the strategy for table ordering",
      displayPosition = 190,
      group = "ADVANCED"
  )
  @ValueChooserModel(TableOrderStrategyChooserValues.class)
  public TableOrderStrategy tableOrderStrategy;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      defaultValue = "1000",
      label = "Fetch Size",
      description = "Fetch Size for the JDBC Statement. Should not be 0",
      displayPosition = 210,
      group = "JDBC"
  )
  public int fetchSize;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      defaultValue = "1",
      label = "Number of Threads",
      description = "Number of threads to parallely read data from",
      displayPosition = 220,
      group = "JDBC"
  )
  public int numberOfThreads;

  private static final String TABLE_JDBC_CONFIG_BEAN_PREFIX = "tableJdbcConfigBean.";
  public static final String TABLE_CONFIG = TABLE_JDBC_CONFIG_BEAN_PREFIX + "tableConfigs";

  public List<Stage.ConfigIssue> validateConfigs(PushSource.Context context, List<Stage.ConfigIssue> issues) {
    if (tableConfigs.isEmpty()) {
      issues.add(context.createConfigIssue(Groups.TABLE.name(), TABLE_CONFIG, JdbcErrors.JDBC_66));
    }
    return issues;
  }
}
