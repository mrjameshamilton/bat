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

package com.github.netomi.bat.classfile.printer

import com.github.netomi.bat.classfile.ClassFile
import com.github.netomi.bat.classfile.constant.*
import com.github.netomi.bat.classfile.constant.visitor.ConstantVisitor
import com.github.netomi.bat.io.IndentingPrinter
import com.github.netomi.bat.util.escapeAsJavaString
import com.github.netomi.bat.util.isAsciiPrintable

internal class ConstantPrinter constructor(private val printer: IndentingPrinter): ConstantVisitor {

    override fun visitAnyConstant(classFile: ClassFile, constant: Constant) {
        printer.print(constant)
    }

    override fun visitIntegerConstant(classFile: ClassFile, constant: IntegerConstant) {
        printer.print(constant.value)
    }

    override fun visitLongConstant(classFile: ClassFile, constant: LongConstant) {
        printer.print(constant.value)
    }

    override fun visitFloatConstant(classFile: ClassFile, constant: FloatConstant) {
        printer.print("%f".format(constant.value))
    }

    override fun visitDoubleConstant(classFile: ClassFile, constant: DoubleConstant) {
        printer.print("%f".format(constant.value))
    }

    override fun visitUtf8Constant(classFile: ClassFile, constant: Utf8Constant) {
        val output = if (!constant.value.isAsciiPrintable()) {
            constant.value.escapeAsJavaString()
        } else {
            constant.value
        }

        printer.print(output)
    }

    override fun visitStringConstant(classFile: ClassFile, constant: StringConstant) {
        visitUtf8Constant(classFile, classFile.getConstant(constant.stringIndex) as Utf8Constant)
    }

    override fun visitAnyRefConstant(classFile: ClassFile, refConstant: RefConstant) {
        val className  = refConstant.getClassName(classFile)
        val memberName = refConstant.getMemberName(classFile)
        val descriptor = refConstant.getDescriptor(classFile)
        printer.print("$className.$memberName:$descriptor")
    }

    override fun visitClassConstant(classFile: ClassFile, constant: ClassConstant) {
        printer.print(constant.getClassName(classFile))
    }

    override fun visitNameAndTypeConstant(classFile: ClassFile, constant: NameAndTypeConstant) {
        val memberName = classFile.getString(constant.nameIndex)
        val descriptor = classFile.getString(constant.descriptorIndex)
        printer.print("$memberName:$descriptor")
    }

    override fun visitMethodTypeConstant(classFile: ClassFile, constant: MethodTypeConstant) {
        visitUtf8Constant(classFile, classFile.getConstant(constant.descriptorIndex) as Utf8Constant)
    }

    override fun visitMethodHandleConstant(classFile: ClassFile, constant: MethodHandleConstant) {
        printer.print("${constant.referenceKind.simpleName} ")
        constant.referenceAccept(classFile, this)
    }

    override fun visitModuleConstant(classFile: ClassFile, constant: ModuleConstant) {
        printer.print(constant.getModuleName(classFile))
    }

    override fun visitPackageConstant(classFile: ClassFile, constant: PackageConstant) {
        printer.print(constant.getPackageName(classFile))
    }
}