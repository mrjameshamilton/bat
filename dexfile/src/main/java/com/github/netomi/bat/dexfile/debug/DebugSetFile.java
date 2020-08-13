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
import com.github.netomi.bat.dexfile.io.DexDataOutput;
import com.github.netomi.bat.dexfile.io.DexDataInput;
import com.github.netomi.bat.dexfile.visitor.DebugSequenceVisitor;

import java.util.Objects;

/**
 * Represents a debug instruction that sets associated source file for subsequent line number entries.
 *
 * @author Thomas Neidhart
 */
public class DebugSetFile
extends      DebugInstruction
{
    private int nameIndex;

    public static DebugSetFile of(int nameIndex) {
        return new DebugSetFile(nameIndex);
    }

    DebugSetFile() {
        this(0);
    }

    private DebugSetFile(int nameIndex) {
        super(DBG_SET_FILE);
        this.nameIndex = nameIndex;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public String getName(DexFile dexFile) {
        return dexFile.getStringID(nameIndex).getStringValue();
    }

    @Override
    protected void read(DexDataInput input) {
        nameIndex = input.readUleb128p1();
    }

    @Override
    protected void write(DexDataOutput output) {
        output.writeByte(getOpcode());
        output.writeUleb128p1(nameIndex);
    }

    @Override
    public void accept(DexFile dexFile, DebugInfo debugInfo, DebugSequenceVisitor visitor) {
        visitor.visitSetFile(dexFile, debugInfo, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebugSetFile other = (DebugSetFile) o;
        return nameIndex == other.nameIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameIndex);
    }

    @Override
    public String toString() {
        return String.format("DebugSetFile[nameIndex=%d]", nameIndex);
    }
}
