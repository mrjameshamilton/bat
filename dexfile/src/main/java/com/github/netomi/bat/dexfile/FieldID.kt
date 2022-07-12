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
package com.github.netomi.bat.dexfile

import com.github.netomi.bat.dexfile.io.DexDataInput
import com.github.netomi.bat.dexfile.io.DexDataOutput
import com.github.netomi.bat.dexfile.visitor.PropertyAccessor
import com.github.netomi.bat.dexfile.visitor.ReferencedIDVisitor
import com.google.common.base.Preconditions
import java.util.*

/**
 * A class representing a field id item inside a dex file.
 *
 * @see [field id item @ dex format](https://source.android.com/devices/tech/dalvik/dex-format.field-id-item)
 */
@DataItemAnn(
    type          = TYPE_FIELD_ID_ITEM,
    dataAlignment = 4,
    dataSection   = false)
class FieldID private constructor(_classIndex: Int = NO_INDEX, _nameIndex:  Int = NO_INDEX, _typeIndex:  Int = NO_INDEX) : DataItem() {

    var classIndex: Int = _classIndex
        private set

    var nameIndex: Int = _nameIndex
        private set

    var typeIndex: Int = _typeIndex
        private set

    fun getClassTypeID(dexFile: DexFile): TypeID {
        return dexFile.getTypeID(classIndex)
    }

    fun getClassType(dexFile: DexFile): String {
        return getClassTypeID(dexFile).getType(dexFile)
    }

    fun getType(dexFile: DexFile): String {
        return dexFile.getTypeID(typeIndex).getType(dexFile)
    }

    fun getName(dexFile: DexFile): String {
        return dexFile.getStringID(nameIndex).stringValue
    }

    /**
     * This item is considered to be empty only if its an uninitialized instance.
     */
    override val isEmpty: Boolean
        get() = classIndex == NO_INDEX

    override fun read(input: DexDataInput) {
        input.skipAlignmentPadding(dataAlignment)
        classIndex = input.readUnsignedShort()
        typeIndex  = input.readUnsignedShort()
        nameIndex  = input.readInt()
    }

    override fun write(output: DexDataOutput) {
        output.writeAlignmentPadding(dataAlignment)
        output.writeUnsignedShort(classIndex)
        output.writeUnsignedShort(typeIndex)
        output.writeInt(nameIndex)
    }

    internal fun referencedIDsAccept(dexFile: DexFile, visitor: ReferencedIDVisitor) {
        visitor.visitTypeID(dexFile, PropertyAccessor(this::classIndex))
        visitor.visitStringID(dexFile, PropertyAccessor(this::nameIndex))
        visitor.visitTypeID(dexFile, PropertyAccessor(this::typeIndex))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FieldID

        return classIndex == other.classIndex &&
               nameIndex  == other.nameIndex &&
               typeIndex  == other.typeIndex
    }

    override fun hashCode(): Int {
        return Objects.hash(classIndex, nameIndex, typeIndex)
    }

    override fun toString(): String {
        return "FieldID[classIdx=${classIndex},nameIdx=${nameIndex},typeIdx=${typeIndex}]"
    }

    companion object {
        fun of(classIndex: Int, nameIndex: Int, typeIndex: Int): FieldID {
            Preconditions.checkArgument(classIndex >= 0, "class index must not be negative")
            Preconditions.checkArgument(nameIndex >= 0, "name index must not be negative")
            Preconditions.checkArgument(typeIndex >= 0, "type index must not be negative")
            return FieldID(classIndex, nameIndex, typeIndex)
        }

        fun readContent(input: DexDataInput): FieldID {
            val fieldID = FieldID()
            fieldID.read(input)
            return fieldID
        }
    }
}