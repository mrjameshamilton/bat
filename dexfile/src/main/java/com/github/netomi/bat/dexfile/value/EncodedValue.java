/*
 *  Copyright (c) 2020 Thomas Neidhart.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.netomi.bat.dexfile.value;

import com.github.netomi.bat.dexfile.DexFile;
import com.github.netomi.bat.dexfile.io.DexDataInput;
import com.github.netomi.bat.dexfile.io.DexDataOutput;
import com.github.netomi.bat.dexfile.io.DexFormatException;
import com.github.netomi.bat.dexfile.visitor.EncodedValueVisitor;
import com.github.netomi.bat.util.Primitives;

public abstract class EncodedValue
{
    // Value types.

    public static final int VALUE_BYTE          = 0x00;
    public static final int VALUE_SHORT         = 0x02;
    public static final int VALUE_CHAR          = 0x03;
    public static final int VALUE_INT           = 0x04;
    public static final int VALUE_LONG          = 0x06;
    public static final int VALUE_FLOAT         = 0x10;
    public static final int VALUE_DOUBLE        = 0x11;
    public static final int VALUE_METHOD_TYPE   = 0x15;
    public static final int VALUE_METHOD_HANDLE = 0x16;
    public static final int VALUE_STRING        = 0x17;
    public static final int VALUE_TYPE          = 0x18;
    public static final int VALUE_FIELD         = 0x19;
    public static final int VALUE_METHOD        = 0x1a;
    public static final int VALUE_ENUM          = 0x1b;
    public static final int VALUE_ARRAY         = 0x1c;
    public static final int VALUE_ANNOTATION    = 0x1d;
    public static final int VALUE_NULL          = 0x1e;
    public static final int VALUE_BOOLEAN       = 0x1f;

    public abstract int getValueType();

    public abstract void read(DexDataInput input, int valueArg);

    protected final void writeType(DexDataOutput output, int valueArg) {
        int typeAndArg = (valueArg << 5) | getValueType();
        output.writeUnsignedByte((byte) typeAndArg);
    }

    public abstract void write(DexDataOutput output);

    public abstract void accept(DexFile dexFile, EncodedValueVisitor visitor);

    public static EncodedValue read(DexDataInput input) {
        int typeAndArg = input.readUnsignedByte();

        int valueArg = typeAndArg >>> 5;
        int valueType = typeAndArg & 0x1f;

        EncodedValue encodedValue = create(valueType);

        encodedValue.read(input, valueArg);
        return encodedValue;
    }

    private static EncodedValue create(int valueType) {
        switch (valueType) {
            case VALUE_BYTE:          return new EncodedByteValue();
            case VALUE_SHORT:         return new EncodedShortValue();
            case VALUE_CHAR:          return new EncodedCharValue();
            case VALUE_INT:           return new EncodedIntValue();
            case VALUE_LONG:          return new EncodedLongValue();
            case VALUE_FLOAT:         return new EncodedFloatValue();
            case VALUE_DOUBLE:        return new EncodedDoubleValue();
            case VALUE_METHOD_TYPE:   return new EncodedMethodTypeValue();
            case VALUE_METHOD_HANDLE: return new EncodedMethodHandleValue();
            case VALUE_STRING:        return new EncodedStringValue();
            case VALUE_TYPE:          return new EncodedTypeValue();
            case VALUE_FIELD:         return new EncodedFieldValue();
            case VALUE_METHOD:        return new EncodedMethodValue();
            case VALUE_ENUM:          return new EncodedEnumValue();
            case VALUE_ARRAY:         return new EncodedArrayValue();
            case VALUE_ANNOTATION:    return new EncodedAnnotationValue();
            case VALUE_NULL:          return EncodedNullValue.INSTANCE;
            case VALUE_BOOLEAN:       return new EncodedBooleanValue();
            default: throw new DexFormatException("Unexpected EncodedValue type: " + Primitives.toHexString((short) valueType));
        }
    }

    public static EncodedAnnotationValue readAnnotationValue(DexDataInput input) {
        EncodedAnnotationValue annotation = new EncodedAnnotationValue();
        annotation.read(input, 0);
        return annotation;
    }
}
