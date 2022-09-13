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
import com.github.netomi.bat.classfile.attribute.*
import com.github.netomi.bat.classfile.attribute.annotation.RuntimeInvisibleAnnotationsAttribute
import com.github.netomi.bat.classfile.attribute.annotation.RuntimeVisibleAnnotationsAttribute
import com.github.netomi.bat.classfile.attribute.visitor.AttributeVisitor
import com.github.netomi.bat.io.IndentingPrinter

internal class AttributePrinter constructor(private val printer:         IndentingPrinter,
                                            private val constantPrinter: ConstantPrinter): AttributeVisitor {

    private val annotationPrinter = AnnotationPrinter(printer, constantPrinter)

    var printedAttributes: Boolean = false
        private set

    fun reset() {
        printedAttributes = false
    }

    // Common Attributes.

    override fun visitAnyAttribute(classFile: ClassFile, attribute: Attribute) {
        //TODO("implement")
    }

    override fun visitAnyDeprecatedAttribute(classFile: ClassFile, attribute: DeprecatedAttribute) {
        printer.println(".deprecated")
        printedAttributes = true
    }

    override fun visitAnySyntheticAttribute(classFile: ClassFile, attribute: SyntheticAttribute) {
        printer.println(".synthetic")
        printedAttributes = true
    }

    override fun visitAnySignatureAttribute(classFile: ClassFile, attribute: SignatureAttribute) {
        printer.println(".signature \"${attribute.getSignature(classFile)}\"")
        printedAttributes = true
    }

    override fun visitAnyRuntimeVisibleAnnotationsAttribute(classFile: ClassFile, attribute: RuntimeVisibleAnnotationsAttribute) {
        if (attribute.size > 0) {
            printer.println(".annotations visible")
            printer.levelUp()
            attribute.annotationsAccept(classFile, annotationPrinter)
            printer.levelDown()
            printer.println(".end annotations")
            printedAttributes = true
        }
    }

    override fun visitAnyRuntimeInvisibleAnnotationsAttribute(classFile: ClassFile, attribute: RuntimeInvisibleAnnotationsAttribute) {
        if (attribute.size > 0) {
            printer.println(".annotations invisible")
            printer.levelUp()
            attribute.annotationsAccept(classFile, annotationPrinter)
            printer.levelDown()
            printer.println(".end annotations")
            printedAttributes = true
        }
    }

    // ClassAttributeVisitor.

    override fun visitSourceFileAttribute(classFile: ClassFile, attribute: SourceFileAttribute) {
        val sourceFile = attribute.getSourceFile(classFile)
        printer.println(".source \"$sourceFile\"")
        printedAttributes = true
    }

    // FieldAttributeVisitor.
}