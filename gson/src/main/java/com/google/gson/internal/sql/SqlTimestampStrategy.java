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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Strategy for converting between java.sql.Timestamp and JSON representations.
 * Delegates to a Date TypeAdapter for the actual conversion logic.
 */
final class SqlTimestampStrategy implements SqlValueStrategy<Timestamp> {
  private final TypeAdapter<Date> dateTypeAdapter;

  SqlTimestampStrategy(TypeAdapter<Date> dateTypeAdapter) {
    this.dateTypeAdapter = dateTypeAdapter;
  }

  @Override
  public Timestamp fromJsonString(String jsonValue) throws IOException {
    // For timestamp strategy, conversion is handled through streams
    throw new UnsupportedOperationException("Use readFromReader() instead");
  }

  @Override
  public String toJsonString(Timestamp sqlValue) {
    // For timestamp strategy, conversion is handled through streams
    throw new UnsupportedOperationException("Use writeToWriter() instead");
  }

  /**
   * Reads a Timestamp from the JSON reader using the date adapter.
   */
  Timestamp readFromReader(JsonReader in) throws IOException {
    Date date = dateTypeAdapter.read(in);
    return date != null ? new Timestamp(date.getTime()) : null;
  }

  /**
   * Writes a Timestamp to the JSON writer using the date adapter.
   */
  void writeToWriter(JsonWriter out, Timestamp value) throws IOException {
    dateTypeAdapter.write(out, value);
  }
    /**
     * Reads a Timestamp from the date adapter, which is used for non-streaming conversions.
     */
  Timestamp readFromAdapter(TypeAdapter<Date> adapter) throws IOException {
    Date date = adapter.read(null); // Note: this is a simplified version
    return date != null ? new Timestamp(date.getTime()) : null;
  }

  /**
   * Writes a Timestamp to the JSON writer using the date adapter.
   */
  void writeToAdapter(TypeAdapter<Date> adapter, Timestamp value) throws IOException {
    adapter.write(null, value); // Note: this is a simplified version
  }
}