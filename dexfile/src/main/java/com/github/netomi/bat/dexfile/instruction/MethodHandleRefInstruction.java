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

import com.github.netomi.bat.dexfile.*;
import com.github.netomi.bat.dexfile.visitor.InstructionVisitor;

public class MethodHandleRefInstruction
extends      DexInstruction
{
    private int methodHandleIndex;

    static MethodHandleRefInstruction create(DexOpCode opCode, byte ident) {
        return new MethodHandleRefInstruction(opCode);
    }

    MethodHandleRefInstruction(DexOpCode opcode) {
        super(opcode);
        methodHandleIndex = DexConstants.NO_INDEX;
    }

    public int getMethodHandleIndex() {
        return methodHandleIndex;
    }

    public MethodHandle getMethodHandle(DexFile dexFile) {
        return dexFile.getMethodHandle(methodHandleIndex);
    }

    @Override
    public void read(short[] instructions, int offset) {
        super.read(instructions, offset);

        switch (opcode.getFormat()) {
            case FORMAT_21c:
                methodHandleIndex = instructions[offset + 1] & 0xffff;
                break;

            default:
                throw new IllegalStateException("unexpected format for opcode " + opcode.getMnemonic());
        }
    }

    @Override
    public void accept(DexFile dexFile, ClassDef classDef, EncodedMethod method, Code code, int offset, InstructionVisitor visitor) {
        visitor.visitMethodHandleRefInstruction(dexFile, classDef, method, code, offset, this);
    }
}
