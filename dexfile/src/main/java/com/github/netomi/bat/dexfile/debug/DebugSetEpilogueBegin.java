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

/**
 * Represents a debug instruction that sets the epilogue begin state machine register.
 *
 * @author Thomas Neidhart
 */
public class DebugSetEpilogueBegin
extends      DebugInstruction
{
    private static final DebugSetEpilogueBegin INSTANCE = new DebugSetEpilogueBegin();

    public static DebugSetEpilogueBegin instance() {
        return INSTANCE;
    }

    private DebugSetEpilogueBegin() {
        super(DBG_SET_EPILOGUE_BEGIN);
    }

    @Override
    protected void read(DexDataInput input) {}

    @Override
    protected void write(DexDataOutput output) {
        output.writeByte(getOpcode());
    }

    @Override
    public void accept(DexFile dexFile, DebugInfo debugInfo, DebugSequenceVisitor visitor) {
        visitor.visitSetEpilogueBegin(dexFile, debugInfo, this);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public String toString() {
        return String.format("DebugSetEpilogueBegin[]");
    }
}
