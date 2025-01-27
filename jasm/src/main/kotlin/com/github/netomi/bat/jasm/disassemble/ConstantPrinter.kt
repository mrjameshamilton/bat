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

package com.github.netomi.bat.jasm.disassemble

import com.github.netomi.bat.classfile.ClassFile
import com.github.netomi.bat.classfile.constant.Constant
import com.github.netomi.bat.classfile.constant.IntegerConstant
import com.github.netomi.bat.classfile.constant.Utf8Constant
import com.github.netomi.bat.classfile.constant.visitor.ConstantVisitor
import com.github.netomi.bat.io.IndentingPrinter

internal class ConstantPrinter constructor(private val printer: IndentingPrinter): ConstantVisitor {

    override fun visitAnyConstant(classFile: ClassFile, index: Int, constant: Constant) {
        TODO("Not yet implemented")
    }

    override fun visitIntegerConstant(classFile: ClassFile, index: Int, constant: IntegerConstant) {
        val v = constant.value
        if (v < 0) {
            printer.print("-0x%x".format(-v))
        } else {
            printer.print("0x%x".format(v))
        }
    }

    override fun visitUtf8Constant(classFile: ClassFile, index: Int, constant: Utf8Constant) {
        printer.print("\"${constant.value}\"")
    }
}