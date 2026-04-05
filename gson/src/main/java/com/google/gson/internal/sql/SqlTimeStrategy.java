/*
 * Copyright (C) 2024 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.internal.sql;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Strategy for converting between java.sql.Time and JSON string representations.
 * Uses the format "hh:mm:ss a" for time conversion.
 */
final class SqlTimeStrategy implements SqlValueStrategy<Time> {
  private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");

  @Override
  public Time fromJsonString(String jsonValue) throws IOException {
    synchronized (this) {
      TimeZone originalTimeZone = format.getTimeZone();
      try {
        Date date = format.parse(jsonValue);
        return new Time(date.getTime());
      } catch (ParseException e) {
        throw new JsonSyntaxException("Failed parsing '" + jsonValue + "' as SQL Time", e);
      } finally {
        format.setTimeZone(originalTimeZone);
      }
    }
  }

  @Override
  public String toJsonString(Time sqlValue) {
    synchronized (this) {
      return format.format(sqlValue);
    }
  }
}