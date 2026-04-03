package com.google.gson.internal.bind;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import com.google.gson.GsonBuilder;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.reflect.TypeToken;

final class NumberTypeAdapters {
    private NumberTypeAdapters() {
        throw new UnsupportedOperationException();
    }

    public static final TypeAdapter<Number> BYTE =
            new TypeAdapter<Number>() {
                @Override
                public Number read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }

                    int intValue;
                    try {
                        intValue = in.nextInt();
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException(e);
                    }

                    if (intValue > 255 || intValue < Byte.MIN_VALUE) {
                        throw new JsonSyntaxException(
                                "Lossy conversion from " + intValue + " to byte; at path " + in.getPreviousPath());
                    }
                    return (byte) intValue;
                }

                @Override
                public void write(JsonWriter out, Number value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.byteValue());
                    }
                }
            };

    public static final TypeAdapterFactory BYTE_FACTORY =
            TypeAdapters.newFactory(byte.class, Byte.class, BYTE);

    public static final TypeAdapter<Number> SHORT =
            new TypeAdapter<Number>() {
                @Override
                public Number read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }

                    int intValue;
                    try {
                        intValue = in.nextInt();
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException(e);
                    }

                    if (intValue > 65535 || intValue < Short.MIN_VALUE) {
                        throw new JsonSyntaxException(
                                "Lossy conversion from " + intValue + " to short; at path " + in.getPreviousPath());
                    }
                    return (short) intValue;
                }

                @Override
                public void write(JsonWriter out, Number value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.shortValue());
                    }
                }
            };

    public static final TypeAdapterFactory SHORT_FACTORY =
            TypeAdapters.newFactory(short.class, Short.class, SHORT);

    public static final TypeAdapter<Number> INTEGER =
            new TypeAdapter<Number>() {
                @Override
                public Number read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    try {
                        return in.nextInt();
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException(e);
                    }
                }

                @Override
                public void write(JsonWriter out, Number value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.intValue());
                    }
                }
            };

    public static final TypeAdapterFactory INTEGER_FACTORY =
            TypeAdapters.newFactory(int.class, Integer.class, INTEGER);

    public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER =
            new TypeAdapter<AtomicInteger>() {
                @Override
                public AtomicInteger read(JsonReader in) throws IOException {
                    try {
                        return new AtomicInteger(in.nextInt());
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException(e);
                    }
                }

                @Override
                public void write(JsonWriter out, AtomicInteger value) throws IOException {
                    out.value(value.get());
                }
            }.nullSafe();

    public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY =
            TypeAdapters.newFactory(AtomicInteger.class, ATOMIC_INTEGER);

    public static TypeAdapter<AtomicLong> atomicLongAdapter(TypeAdapter<Number> longAdapter) {
        Objects.requireNonNull(longAdapter);
        return new TypeAdapter<AtomicLong>() {
            @Override
            public AtomicLong read(JsonReader in) throws IOException {
                Number value = longAdapter.read(in);
                return new AtomicLong(value.longValue());
            }

            @Override
            public void write(JsonWriter out, AtomicLong value) throws IOException {
                longAdapter.write(out, value.get());
            }
        }.nullSafe();
    }

    public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN =
            new TypeAdapter<AtomicBoolean>() {
                @Override
                public AtomicBoolean read(JsonReader in) throws IOException {
                    return new AtomicBoolean(in.nextBoolean());
                }

                @Override
                public void write(JsonWriter out, AtomicBoolean value) throws IOException {
                    out.value(value.get());
                }
            }.nullSafe();

    public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY =
            TypeAdapters.newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);

    public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY =
            new TypeAdapter<AtomicIntegerArray>() {
                @Override
                public AtomicIntegerArray read(JsonReader in) throws IOException {
                    List<Integer> list = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        try {
                            int integer = in.nextInt();
                            list.add(integer);
                        } catch (NumberFormatException e) {
                            throw new JsonSyntaxException(e);
                        }
                    }
                    in.endArray();
                    int length = list.size();
                    AtomicIntegerArray array = new AtomicIntegerArray(length);
                    for (int i = 0; i < length; ++i) {
                        array.set(i, list.get(i));
                    }
                    return array;
                }

                @Override
                public void write(JsonWriter out, AtomicIntegerArray value) throws IOException {
                    out.beginArray();
                    for (int i = 0, length = value.length(); i < length; i++) {
                        out.value(value.get(i));
                    }
                    out.endArray();
                }
            }.nullSafe();

    public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY =
            TypeAdapters.newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);

    public static TypeAdapter<AtomicLongArray> atomicLongArrayAdapter(TypeAdapter<Number> longAdapter) {
        Objects.requireNonNull(longAdapter);
        return new TypeAdapter<AtomicLongArray>() {
            @Override
            public AtomicLongArray read(JsonReader in) throws IOException {
                List<Long> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    long value = longAdapter.read(in).longValue();
                    list.add(value);
                }
                in.endArray();
                int length = list.size();
                AtomicLongArray array = new AtomicLongArray(length);
                for (int i = 0; i < length; ++i) {
                    array.set(i, list.get(i));
                }
                return array;
            }

            @Override
            public void write(JsonWriter out, AtomicLongArray value) throws IOException {
                out.beginArray();
                for (int i = 0, length = value.length(); i < length; i++) {
                    longAdapter.write(out, value.get(i));
                }
                out.endArray();
            }
        }.nullSafe();
    }

    public static final TypeAdapter<Number> LONG =
            new TypeAdapter<Number>() {
                @Override
                public Number read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    try {
                        return in.nextLong();
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException(e);
                    }
                }

                @Override
                public void write(JsonWriter out, Number value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.longValue());
                    }
                }
            };

    public static final TypeAdapter<Number> LONG_AS_STRING =
            new TypeAdapter<Number>() {
                @Override
                public Number read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return in.nextLong();
                }

                @Override
                public void write(JsonWriter out, Number value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.value(value.toString());
                }
            };

    private static class FloatAdapter extends TypeAdapter<Number> {
        private final boolean strict;

        FloatAdapter(boolean strict) {
            this.strict = strict;
        }

        @Override
        public Float read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return (float) in.nextDouble();
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            float floatValue = value.floatValue();
            if (strict) {
                checkValidFloatingPoint(floatValue);
            }
            Number floatNumber = value instanceof Float ? value : floatValue;
            out.value(floatNumber);
        }
    }

    private static class DoubleAdapter extends TypeAdapter<Number> {
        private final boolean strict;

        DoubleAdapter(boolean strict) {
            this.strict = strict;
        }

        @Override
        public Double read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return in.nextDouble();
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            double doubleValue = value.doubleValue();
            if (strict) {
                checkValidFloatingPoint(doubleValue);
            }
            out.value(doubleValue);
        }
    }

    private static void checkValidFloatingPoint(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(
                    value
                            + " is not a valid double value as per JSON specification. To override this"
                            + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
        }
    }

    public static final TypeAdapter<Number> FLOAT = new FloatAdapter(false);
    public static final TypeAdapter<Number> FLOAT_STRICT = new FloatAdapter(true);

    public static final TypeAdapter<Number> DOUBLE = new DoubleAdapter(false);
    public static final TypeAdapter<Number> DOUBLE_STRICT = new DoubleAdapter(true);

    public static final TypeAdapter<BigDecimal> BIG_DECIMAL =
            new TypeAdapter<BigDecimal>() {
                @Override
                public BigDecimal read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    String s = in.nextString();
                    try {
                        return NumberLimits.parseBigDecimal(s);
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException(
                                "Failed parsing '" + s + "' as BigDecimal; at path " + in.getPreviousPath(), e);
                    }
                }

                @Override
                public void write(JsonWriter out, BigDecimal value) throws IOException {
                    out.value(value);
                }
            };

    public static final TypeAdapterFactory BIG_DECIMAL_FACTORY =
            TypeAdapters.newFactory(BigDecimal.class, BIG_DECIMAL);

    public static final TypeAdapter<BigInteger> BIG_INTEGER =
            new TypeAdapter<BigInteger>() {
                @Override
                public BigInteger read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    String s = in.nextString();
                    try {
                        return NumberLimits.parseBigInteger(s);
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException(
                                "Failed parsing '" + s + "' as BigInteger; at path " + in.getPreviousPath(), e);
                    }
                }

                @Override
                public void write(JsonWriter out, BigInteger value) throws IOException {
                    out.value(value);
                }
            };

    public static final TypeAdapterFactory BIG_INTEGER_FACTORY =
            TypeAdapters.newFactory(BigInteger.class, BIG_INTEGER);

    public static final TypeAdapter<LazilyParsedNumber> LAZILY_PARSED_NUMBER =
            new TypeAdapter<LazilyParsedNumber>() {
                @Override
                public LazilyParsedNumber read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return new LazilyParsedNumber(in.nextString());
                }

                @Override
                public void write(JsonWriter out, LazilyParsedNumber value) throws IOException {
                    out.value(value);
                }
            };

    public static final TypeAdapterFactory LAZILY_PARSED_NUMBER_FACTORY =
            TypeAdapters.newFactory(LazilyParsedNumber.class, LAZILY_PARSED_NUMBER);

    /** Type adapter for {@link Number}. */
    private static class NumberAdapter extends TypeAdapter<Number> {
        private final ToNumberStrategy toNumberStrategy;

        private NumberAdapter(ToNumberStrategy toNumberStrategy) {
            this.toNumberStrategy = toNumberStrategy;
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                case STRING:
                    return toNumberStrategy.readNumber(in);
                default:
                    throw new JsonSyntaxException(
                        "Expecting number, got: " + jsonToken + "; at path " + in.getPath());
            }
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    }

    /** Gson default factory using {@link ToNumberPolicy#LAZILY_PARSED_NUMBER}. */
    private static final TypeAdapterFactory LAZILY_PARSED_NUMBER_FACTORY_FOR_NUMBER =
            newFactory(ToNumberPolicy.LAZILY_PARSED_NUMBER);

    private static TypeAdapterFactory newFactory(ToNumberStrategy toNumberStrategy) {
        NumberAdapter adapter = new NumberAdapter(toNumberStrategy);
        return new TypeAdapterFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                return type.getRawType() == Number.class ? (TypeAdapter<T>) adapter : null;
            }
        };
    }

    public static TypeAdapterFactory getFactory(ToNumberStrategy toNumberStrategy) {
        if (toNumberStrategy == ToNumberPolicy.LAZILY_PARSED_NUMBER) {
            return LAZILY_PARSED_NUMBER_FACTORY_FOR_NUMBER;
        } else {
            return newFactory(toNumberStrategy);
        }
    }
}