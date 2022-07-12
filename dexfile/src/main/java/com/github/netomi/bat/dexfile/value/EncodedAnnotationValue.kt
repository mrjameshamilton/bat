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
package com.github.netomi.bat.dexfile.value

import com.github.netomi.bat.dexfile.DexContent
import com.github.netomi.bat.dexfile.DexFile
import com.github.netomi.bat.dexfile.NO_INDEX
import com.github.netomi.bat.dexfile.io.DexDataInput
import com.github.netomi.bat.dexfile.io.DexDataOutput
import com.github.netomi.bat.dexfile.visitor.AnnotationElementVisitor
import com.github.netomi.bat.dexfile.visitor.EncodedValueVisitor
import com.github.netomi.bat.util.Preconditions
import java.util.*
import kotlin.collections.ArrayList

/**
 * A class representing an annotation value (TypeID + AnnotationElements) inside a dex file.
 */
data class EncodedAnnotationValue internal constructor(
    var typeIndex: Int                          = NO_INDEX,
    val elements:  ArrayList<AnnotationElement> = ArrayList(0)): EncodedValue() {

    override val valueType: Int
        get() = VALUE_ANNOTATION

    fun getType(dexFile: DexFile): String {
        return dexFile.getTypeID(typeIndex).getType(dexFile)
    }

    fun addAnnotationElement(element: AnnotationElement) {
        elements.add(element)
    }

    override fun readValue(input: DexDataInput, valueArg: Int) {
        typeIndex = input.readUleb128()
        val size = input.readUleb128()
        elements.clear()
        elements.ensureCapacity(size)
        for (i in 0 until size) {
            val element = AnnotationElement.readContent(input)
            elements.add(element)
        }
    }

    override fun writeType(output: DexDataOutput): Int {
        return writeType(output, 0)
    }

    override fun writeValue(output: DexDataOutput, valueArg: Int) {
        output.writeUleb128(typeIndex)
        output.writeUleb128(elements.size)
        for (element in elements) {
            element.write(output)
        }
    }

    override fun accept(dexFile: DexFile, visitor: EncodedValueVisitor) {
        visitor.visitAnnotationValue(dexFile, this)
    }

    fun annotationElementsAccept(dexFile: DexFile, visitor: AnnotationElementVisitor) {
        for (element in elements) {
            element.accept(dexFile, visitor)
        }
    }

    override fun toString(): String {
        return "EncodedAnnotationValue[typeIndex=${typeIndex},elements=${elements}]"
    }

    companion object {
        fun of(typeIndex: Int, vararg elements: AnnotationElement): EncodedAnnotationValue {
            return EncodedAnnotationValue(typeIndex, arrayListOf(*elements))
        }
    }
}

/**
 * A class representing an annotation element inside a dex file.
 */
data class AnnotationElement private constructor(
    var nameIndex: Int =          NO_INDEX,
    var value:     EncodedValue = EncodedNullValue) : DexContent() {

    fun getName(dexFile: DexFile): String {
        return dexFile.getStringID(nameIndex).stringValue
    }

    override fun read(input: DexDataInput) {
        nameIndex = input.readUleb128()
        value     = EncodedValue.read(input)
    }

    override fun write(output: DexDataOutput) {
        output.writeUleb128(nameIndex)
        value.write(output)
    }

    fun accept(dexFile: DexFile, visitor: AnnotationElementVisitor) {
        visitor.visitAnnotationElement(dexFile, this)
    }

    override fun toString(): String {
        return "AnnotationElement[nameIndex=${nameIndex},value=${value}]"
    }

    companion object {
        fun of(nameIndex: Int, value: EncodedValue): AnnotationElement {
            Preconditions.checkArgument(nameIndex >= 0, "nameIndex must not be negative")
            Objects.requireNonNull(value, "value must not be null")
            return AnnotationElement(nameIndex, value)
        }

        fun readContent(input: DexDataInput): AnnotationElement {
            val annotationElement = AnnotationElement()
            annotationElement.read(input)
            return annotationElement
        }
    }
}