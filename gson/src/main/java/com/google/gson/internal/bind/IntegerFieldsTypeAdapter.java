package com.google.gson.internal.bind;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract {@link TypeAdapter} for classes whose JSON serialization consists of a fixed set of
 * integer fields. That is the case for {@link Calendar} and the legacy serialization of various
 * {@code java.time} types.
 */
abstract class IntegerFieldsTypeAdapter<T> extends TypeAdapter<T> {
  private final List<String> fields;

  IntegerFieldsTypeAdapter(String... fields) {
    this.fields = Arrays.asList(fields);
  }

  abstract T create(long[] values);

  abstract long[] integerValues(T t);

  @Override
  public T read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    in.beginObject();
    long[] values = new long[fields.size()];
    while (in.peek() != JsonToken.END_OBJECT) {
      String name = in.nextName();
      int index = fields.indexOf(name);
      if (index >= 0) {
        values[index] = in.nextLong();
      } else {
        in.skipValue();
      }
    }
    in.endObject();
    return create(values);
  }

  @Override
  public void write(JsonWriter out, T value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }
    out.beginObject();
    long[] values = integerValues(value);
    for (int i = 0; i < fields.size(); i++) {
      out.name(fields.get(i));
      out.value(values[i]);
    }
    out.endObject();
  }
}