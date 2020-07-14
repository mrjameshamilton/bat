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
import com.github.netomi.bat.dexfile.FieldID;
import com.github.netomi.bat.dexfile.io.DexDataInput;
import com.github.netomi.bat.dexfile.io.DexDataOutput;

public class EncodedEnumValue
extends      EncodedValue
{
    public int fieldIndex;

    public FieldID getEnumField(DexFile dexFile) {
        return dexFile.fieldIDs[fieldIndex];
    }

    @Override
    public int getValueType() {
        return VALUE_ENUM;
    }

    @Override
    public void read(DexDataInput input, int valueArg) {
        fieldIndex = input.readUnsignedInt(valueArg + 1);
    }

    @Override
    public void write(DexDataOutput output) {
        writeType(output, 3);
        output.writeInt(fieldIndex, 4);
    }

    public String toString() {
        return String.format("EncodedEnumValue[fieldIdx=%d]", fieldIndex);
    }
}