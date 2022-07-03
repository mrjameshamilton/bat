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
package com.github.netomi.bat.classfile.constant

import com.github.netomi.bat.classfile.ClassFile
import com.github.netomi.bat.classfile.ConstantPool
import com.github.netomi.bat.classfile.visitor.ConstantPoolVisitor
import com.github.netomi.bat.classfile.visitor.ConstantVisitor
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * A constant representing a CONSTANT_Double_info structure in a class file.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4.5">CONSTANT_Double_info Structure</a>
 *
 * @author Thomas Neidhart
 */
data class DoubleConstant internal constructor(override val owner: ConstantPool,
                                                        var value: Double = 0.0) : Constant() {

    override val type: Type
        get() = Type.DOUBLE

    @Throws(IOException::class)
    override fun readConstantInfo(input: DataInput) {
        val highBytes = input.readInt()
        val lowBytes  = input.readInt()
        val bits = (highBytes.toLong() shl 32) + lowBytes
        value = Double.fromBits(bits)
    }

    @Throws(IOException::class)
    override fun writeConstantInfo(output: DataOutput) {
        val bits = value.toBits()
        val highBytes = (bits shr 32).toInt()
        val lowBytes  = bits.toInt()
        output.writeInt(highBytes)
        output.writeInt(lowBytes)
    }

    override fun accept(classFile: ClassFile,
                        visitor:   ConstantVisitor) {
        visitor.visitDoubleConstant(classFile, this)
    }

    override fun accept(classFile: ClassFile,
                        index:     Int,
                        visitor:   ConstantPoolVisitor) {
        visitor.visitDoubleConstant(classFile, index, this)
    }

    companion object {
        @JvmStatic
        fun create(owner: ConstantPool): DoubleConstant {
            return DoubleConstant(owner)
        }

        @JvmStatic
        fun create(owner: ConstantPool, value: Double): DoubleConstant {
            return DoubleConstant(owner, value)
        }
    }
}