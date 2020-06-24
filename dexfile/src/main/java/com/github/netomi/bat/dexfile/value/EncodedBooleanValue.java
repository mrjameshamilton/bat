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

import com.github.netomi.bat.dexfile.io.DexDataInput;
import com.github.netomi.bat.dexfile.io.DexDataOutput;

public class EncodedBooleanValue extends EncodedValue
{
    public boolean value;

    @Override
    public int getValueType() {
        return VALUE_BOOLEAN;
    }

    @Override
    public void read(DexDataInput input, int valueArg) {
        value = (valueArg & 0x1) == 1;
    }

    @Override
    public void write(DexDataOutput output) {
        writeType(output, value ? 1 : 0);
    }

    public String toString() {
        return String.format("EncodedBooleanValue[value=%s]", Boolean.toString(value));
    }
}
