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
import com.github.netomi.bat.classfile.instruction.visitor.InstructionVisitor
import com.github.netomi.bat.util.mutableListOfCapacity

open class LookupSwitchInstruction
    private constructor(            opCode:            JvmOpCode,
                        private var _defaultOffset:    Int                          = 0,
                        private var _matchOffsetPairs: MutableList<MatchOffsetPair> = mutableListOfCapacity(0)): SwitchInstruction(opCode) {

    override fun getLength(offset: Int): Int {
        val padding = getPadding(offset + 1)
        return 1 + padding + 8 + matchOffsetPairs.size * 8
    }

    val defaultOffset: Int
        get() = _defaultOffset

    val numberOfPairs: Int
        get() = matchOffsetPairs.size

    val matchOffsetPairs: List<MatchOffsetPair>
        get() = _matchOffsetPairs

    private fun getPadding(offset: Int): Int {
        return (4 - (offset % 4)) % 4
    }

    override fun read(instructions: ByteArray, offset: Int) {
        super.read(instructions, offset)

        var currOffset = offset + 1
        currOffset    += getPadding(currOffset)

        _defaultOffset =
            getOffset(instructions[currOffset++],
                      instructions[currOffset++],
                      instructions[currOffset++],
                      instructions[currOffset++])

        val numberOfPairs =
            getLiteral(instructions[currOffset++],
                       instructions[currOffset++],
                       instructions[currOffset++],
                       instructions[currOffset++])

        _matchOffsetPairs = mutableListOfCapacity(numberOfPairs)

        for (i in 0 until numberOfPairs) {
            val match =
                getLiteral(instructions[currOffset++],
                           instructions[currOffset++],
                           instructions[currOffset++],
                           instructions[currOffset++])

            val offsetOfMatch =
                getOffset(instructions[currOffset++],
                          instructions[currOffset++],
                          instructions[currOffset++],
                          instructions[currOffset++])

            _matchOffsetPairs.add(MatchOffsetPair(match, offsetOfMatch))
        }
    }

    override fun accept(classFile: ClassFile, method: Method, code: CodeAttribute, offset: Int, visitor: InstructionVisitor) {
        visitor.visitLookupSwitchInstruction(classFile, method, code, offset, this)
    }

    companion object {
        internal fun create(opCode: JvmOpCode): JvmInstruction {
            return LookupSwitchInstruction(opCode)
        }
    }
}