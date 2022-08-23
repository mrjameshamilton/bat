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
package com.github.netomi.bat.classfile.constant

import com.github.netomi.bat.classfile.ClassFile
import com.github.netomi.bat.classfile.constant.visitor.ConstantPoolVisitor
import com.github.netomi.bat.classfile.constant.visitor.ConstantVisitor
import com.github.netomi.bat.util.mutableListOfCapacity
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

internal class ConstantPool private constructor(private var constants: MutableList<Constant?> = mutableListOfCapacity(1)) {

    init {
        if (constants.isEmpty()) {
            constants.add(null)
        }
    }

    operator fun get(index: Int): Constant {
        require(constants[index] != null) { "trying to retrieve a null constant at index $index" }
        return constants[index]!!
    }

    internal fun addConstant(constant: Constant): Int {
        constants.add(constant)
        return constants.lastIndex
    }

    @Throws(IOException::class)
    fun read(input: DataInput) {
        val entries = input.readUnsignedShort()
        constants = mutableListOfCapacity(entries)
        constants.add(null)
        var i = 1
        while (i < entries) {
            val constant = Constant.read(input)
            constants.add(constant)
            if (constant.constantPoolSize > 1) {
                constants.add(null)
                i += 2
            } else {
                i++
            }
        }
    }

    @Throws(IOException::class)
    fun write(output: DataOutput) {
        val entries = constants.size
        output.writeShort(entries)
        val it: ListIterator<Constant?> = constants.listIterator(1)
        while (it.hasNext()) {
            val constant = it.next()
            constant?.let {
                output.writeByte(it.type.tag)
                it.writeConstantInfo(output)
            }
        }
    }

    fun accept(classFile: ClassFile, visitor: ConstantPoolVisitor) {
        visitor.visitConstantPoolStart(classFile)
        constants.forEachIndexed { index, constant -> constant?.accept(classFile, index, visitor) }
        visitor.visitConstantPoolEnd(classFile)
    }

    fun constantAccept(classFile: ClassFile, index: Int, visitor: ConstantVisitor) {
        require(constants[index] != null) { "trying to accept a null constant at index $index" }
        constants[index]?.accept(classFile, visitor)
    }

    companion object {
        fun empty(): ConstantPool {
            return ConstantPool()
        }
    }
}