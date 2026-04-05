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

/**
 * Strategy interface for converting between SQL types and JSON string representations.
 * This interface defines the contract for different SQL type conversion strategies,
 * allowing for better separation of concerns and easier testing.
 *
 * @param <T> the SQL type this strategy handles
 */
interface SqlValueStrategy<T> {
  /**
   * Converts a JSON string value to the corresponding SQL type.
   *
   * @param jsonValue the JSON string value to convert
   * @return the converted SQL type instance
   * @throws java.io.IOException if the conversion fails
   */
  T fromJsonString(String jsonValue) throws java.io.IOException;

  /**
   * Converts an SQL type instance to its JSON string representation.
   *
   * @param sqlValue the SQL type instance to convert
   * @return the JSON string representation
   */
  String toJsonString(T sqlValue);
}