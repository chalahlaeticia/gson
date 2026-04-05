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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Strategy for converting between java.sql.Date and JSON string representations.
 * Uses the format "MMM d, yyyy" for date conversion.
 */
final class SqlDateStrategy implements SqlValueStrategy<java.sql.Date> {
  private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

  @Override
  public java.sql.Date fromJsonString(String jsonValue) throws IOException {
    synchronized (this) {
      TimeZone originalTimeZone = format.getTimeZone();
      try {
        Date utilDate = format.parse(jsonValue);
        return new java.sql.Date(utilDate.getTime());
      } catch (ParseException e) {
        throw new JsonSyntaxException("Failed parsing '" + jsonValue + "' as SQL Date", e);
      } finally {
        format.setTimeZone(originalTimeZone);
      }
    }
  }

  @Override
  public String toJsonString(java.sql.Date sqlValue) {
    synchronized (this) {
      return format.format(sqlValue);
    }
  }
}