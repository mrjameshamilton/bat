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
 * A constant representing a CONSTANT_MethodHandle_info structure in a class file.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html#jvms-4.4.7">CONSTANT_Utf8_info Structure</a>
 *
 * @author Thomas Neidhart
 */
data class MethodHandleConstant internal constructor(var referenceKind:  Int =  0,
                                                     var referenceIndex: Int = -1) : Constant() {

    override val type: Type
        get() = Type.METHOD_HANDLE

    @Throws(IOException::class)
    override fun readConstantInfo(input: DataInput) {
        referenceKind = input.readUnsignedByte()
        referenceIndex = input.readUnsignedShort()
    }

    @Throws(IOException::class)
    override fun writeConstantInfo(output: DataOutput) {
        output.writeByte(referenceKind)
        output.writeShort(referenceIndex)
    }

    override fun accept(classFile: ClassFile,
                        visitor:   ConstantVisitor) {
        visitor.visitMethodHandleConstant(classFile, this)
    }

    override fun accept(classFile:    ClassFile,
                        constantPool: ConstantPool,
                        index:        Int,
                        visitor:      ConstantPoolVisitor) {
        visitor.visitMethodHandleConstant(classFile, constantPool, index, this)
    }

    companion object {
        @JvmStatic
        fun create(): MethodHandleConstant {
            return MethodHandleConstant()
        }

        @JvmStatic
        fun create(referenceKind: Int, referenceIndex: Int): MethodHandleConstant {
            return MethodHandleConstant(referenceKind, referenceIndex)
        }
    }
}