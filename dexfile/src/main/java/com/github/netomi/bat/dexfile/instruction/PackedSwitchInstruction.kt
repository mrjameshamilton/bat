/*
 *  Copyright (c) 2020-2022 Thomas Neidhart.
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

package com.github.netomi.bat.dexfile.instruction

import com.github.netomi.bat.dexfile.ClassDef
import com.github.netomi.bat.dexfile.Code
import com.github.netomi.bat.dexfile.DexFile
import com.github.netomi.bat.dexfile.EncodedMethod
import com.github.netomi.bat.dexfile.instruction.visitor.InstructionVisitor

class PackedSwitchInstruction private constructor(payloadOffset: Int = 0, register: Int = 0): SwitchInstruction(DexOpCode.PACKED_SWITCH, payloadOffset, register) {

    override fun accept(dexFile: DexFile, classDef: ClassDef, method: EncodedMethod, code: Code, offset: Int, visitor: InstructionVisitor) {
        visitor.visitPackedSwitchInstruction(dexFile, classDef, method, code, offset, this)
    }

    companion object {
        fun of(payloadOffset: Int, register: Int): PackedSwitchInstruction {
            return PackedSwitchInstruction(payloadOffset, register)
        }

        @JvmStatic
        fun create(opCode: DexOpCode, ident: Int): PackedSwitchInstruction {
            return PackedSwitchInstruction()
        }
    }
}