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
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

abstract class AbstractSqlTypeAdapter<T> extends TypeAdapter<T> {
  private final DateFormat format;

  AbstractSqlTypeAdapter(DateFormat format) {
    this.format = format;
  }

  AbstractSqlTypeAdapter() {
    this.format = null;
  }

  @Override
  public final T read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    return readNonNull(in);
  }

  @Override
  public final void write(JsonWriter out, T value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }
    writeNonNull(out, value);
  }

  protected abstract T readNonNull(JsonReader in) throws IOException;

  protected abstract void writeNonNull(JsonWriter out, T value) throws IOException;

  protected final Date parseString(String value) {
    if (format == null) {
      throw new IllegalStateException("DateFormat is not configured for this adapter");
    }
    try {
      TimeZone originalTimeZone = format.getTimeZone();
      synchronized (this) {
        try {
          return format.parse(value);
        } catch (ParseException e) {
          throw new JsonSyntaxException("Failed parsing '" + value + "' as SQL date/time value", e);
        } finally {
          format.setTimeZone(originalTimeZone);
        }
      }
    } catch (IllegalArgumentException e) {
      throw new JsonSyntaxException("Failed parsing '" + value + "' as SQL date/time value", e);
    }
  }

  protected final String formatDate(Date date) {
    if (format == null) {
      throw new IllegalStateException("DateFormat is not configured for this adapter");
    }
    synchronized (this) {
      return format.format(date);
    }
  }
}
