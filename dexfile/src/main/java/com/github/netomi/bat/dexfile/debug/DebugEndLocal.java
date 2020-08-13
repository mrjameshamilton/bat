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
package com.github.netomi.bat.dexfile.debug;

import com.github.netomi.bat.dexfile.DexFile;
import com.github.netomi.bat.dexfile.io.DexDataInput;
import com.github.netomi.bat.dexfile.io.DexDataOutput;
import com.github.netomi.bat.dexfile.visitor.DebugSequenceVisitor;

import java.util.Objects;

/**
 * Represents a debug instruction that ends a local variable at the current address.
 *
 * @author Thomas Neidhart
 */
public class DebugEndLocal
extends      DebugInstruction
{
    private int registerNum;

    public static DebugEndLocal of(int registerNum) {
        return new DebugEndLocal(registerNum);
    }

    DebugEndLocal() {
        this(0);
    }

    private DebugEndLocal(int registerNum) {
        super(DBG_END_LOCAL);
        this.registerNum = registerNum;
    }

    public int getRegisterNum() {
        return registerNum;
    }

    @Override
    protected void read(DexDataInput input) {
        registerNum = input.readUleb128();
    }

    @Override
    protected void write(DexDataOutput output) {
        output.writeByte(getOpcode());
        output.writeUleb128(registerNum);
    }

    @Override
    public void accept(DexFile dexFile, DebugInfo debugInfo, DebugSequenceVisitor visitor) {
        visitor.visitEndLocal(dexFile, debugInfo, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebugEndLocal other = (DebugEndLocal) o;
        return registerNum == other.registerNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registerNum);
    }

    @Override
    public String toString() {
        return String.format("DebugEndLocal[registerNum=%d]", registerNum);
    }
}
