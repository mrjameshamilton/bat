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

package com.github.netomi.bat.classfile.instruction

import com.github.netomi.bat.classfile.ClassFile
import com.github.netomi.bat.classfile.Method
import com.github.netomi.bat.classfile.attribute.CodeAttribute
import com.github.netomi.bat.classfile.instruction.editor.InstructionWriter
import com.github.netomi.bat.classfile.instruction.visitor.InstructionVisitor

open class VariableInstruction protected constructor(opCode: JvmOpCode, wide: Boolean): JvmInstruction(opCode) {

    var variable: Int = 0
        private set

    val variableIsImplicit: Boolean
        get() = opCode.length == 1

    var wide: Boolean = wide
        private set

    override fun getLength(offset: Int): Int {
        return if (wide) {
            super.getLength(offset) + 2
        } else {
            super.getLength(offset)
        }
    }

    override fun read(instructions: ByteArray, offset: Int) {
        variable = if (!variableIsImplicit) {
            if (wide) {
                getIndex(instructions[offset + 2], instructions[offset + 3])
            } else {
                instructions[offset + 1].toInt() and 0xff
            }
        } else {
            val (variableString) = VARIABLE_REGEX.find(mnemonic)!!.destructured
            variableString.toInt()
        }
    }

    override fun write(writer: InstructionWriter, offset: Int) {
        if (!variableIsImplicit) {
            var currOffset = offset
            if (wide) {
                writer.write(currOffset++, JvmOpCode.WIDE.value.toByte())
            }
            writer.write(currOffset++, opCode.value.toByte())
            if (wide) {
                writeIndex(writer, currOffset, variable)
            } else {
                writer.write(currOffset, variable.toByte())
            }
        } else {
            writer.write(offset, opCode.value.toByte())
        }
    }

    override fun accept(classFile: ClassFile, method: Method, code: CodeAttribute, offset: Int, visitor: InstructionVisitor) {
        visitor.visitVariableInstruction(classFile, method, code, offset, this)
    }

    companion object {
        private val VARIABLE_REGEX = "\\w+_(\\d)".toRegex()

        internal fun create(opCode: JvmOpCode, wide: Boolean = false): JvmInstruction {
            return VariableInstruction(opCode, wide)
        }
    }
}