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
package com.github.netomi.bat.dexfile.instruction;

import com.github.netomi.bat.dexfile.DexConstants;
import com.github.netomi.bat.dexfile.DexFile;
import com.github.netomi.bat.dexfile.FieldID;
import com.github.netomi.bat.dexfile.util.Primitives;

public class FieldInstruction
extends      DexInstruction
{
    private int fieldIndex;

    static FieldInstruction create(DexOpCode opCode, byte ident) {
        return new FieldInstruction(opCode);
    }

    FieldInstruction(DexOpCode opcode) {
        super(opcode);
        fieldIndex = DexConstants.NO_INDEX;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }

    public FieldID getField(DexFile dexFile) {
        return dexFile.getFieldID(fieldIndex);
    }

    @Override
    public void read(short[] instructions, int offset) {
        super.read(instructions, offset);

        switch (opcode.getFormat()) {
            case FORMAT_21c:
            case FORMAT_22c:
                fieldIndex = instructions[offset + 1] & 0xffff;
                break;

            default:
                throw new IllegalStateException("unexpected format for opcode " + opcode.getMnemonic());
        }
    }

    @Override
    public String toString(DexFile dexFile) {
        StringBuilder sb = new StringBuilder(super.toString(dexFile));

        sb.append(", ");

        FieldID fieldID = dexFile.getFieldID(fieldIndex);

        sb.append(fieldID.getClassName(dexFile));
        sb.append('.');
        sb.append(fieldID.getName(dexFile));
        sb.append(':');
        sb.append(fieldID.getType(dexFile));

        sb.append(" // field@");
        sb.append(Primitives.asHexValue(fieldIndex, 4));

        return sb.toString();
    }
}
