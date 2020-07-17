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

import com.github.netomi.bat.dexfile.DexFile;
import com.github.netomi.bat.dexfile.util.Primitives;

/**
 * @author Thomas Neidhart
 */
public class DexInstruction
{
    private static final int[] EMPTY_REGISTERS = new int[0];

    protected final DexOpCode opcode;
    public          int[]     registers;

    public static DexInstruction create(short[] instructions, int offset) {
        byte opcode = (byte) (instructions[offset]         & 0xff);
        byte ident  = (byte) ((instructions[offset] >>> 8) & 0xff);

        DexOpCode opCode = DexOpCode.get(opcode);

        if (opCode != null) {
            DexInstruction instruction = opCode.createInstruction(ident);
            instruction.read(instructions, offset);
            return instruction;
        } else {
            throw new IllegalArgumentException("unknown opcode " + Primitives.toHexString(opcode));
        }
    }

    static DexInstruction createGeneric(DexOpCode opCode, byte ident) {
        return new DexInstruction(opCode);
    }

    public DexInstruction(DexOpCode opcode) {
        this.opcode    = opcode;
        this.registers = EMPTY_REGISTERS;
    }

    public int getLength() {
        return opcode.getLength();
    }

    public String getMnemonic() {
        return opcode.getMnemonic();
    }

    public void read(short[] instructions, int offset) {
        switch (opcode.getFormat()) {
            case FORMAT_00x:
            case FORMAT_10x:
                registers = EMPTY_REGISTERS;
                break;

            case FORMAT_12x:
            case FORMAT_22c:
                registers = new int[] {
                    instructions[offset] >>> 8  & 0xf,
                    instructions[offset] >>> 12 & 0xf,
                };
                break;

            case FORMAT_21c:
                registers = new int[] {
                    instructions[offset] >>> 8  & 0xff,
                };
                break;

            case FORMAT_23x:
                registers = new int[] {
                    instructions[offset] >>> 8      & 0xff,
                    instructions[offset + 1]        & 0xff,
                    instructions[offset + 1] >>> 8  & 0xff
                };
                break;
        }
    }

    public String toString(DexFile dexFile) {
        StringBuilder sb = new StringBuilder();

        sb.append(getMnemonic());

        if (registers.length > 0) {
            sb.append(' ');
            for (int idx = 0; idx < registers.length; idx++) {
                if (idx > 0) {
                    sb.append(", ");
                }
                sb.append('v');
                sb.append(registers[idx]);
            }
        }

        return sb.toString();
    }

    public String toString() {
        return opcode.getMnemonic();
    }
}
