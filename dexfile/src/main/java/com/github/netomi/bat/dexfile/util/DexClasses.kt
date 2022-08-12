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
package com.github.netomi.bat.dexfile.util

import com.github.netomi.bat.dexfile.*
import com.github.netomi.bat.dexfile.value.*
import com.github.netomi.bat.util.*

object DexClasses {
    fun isClassType(type: String): Boolean {
        return type.startsWith("L") && type.endsWith(";")
    }

    fun isArrayType(type: String): Boolean {
        return type.startsWith("[")
    }

    fun isReferenceType(type: String): Boolean {
        return isClassType(type) || isArrayType(type)
    }
    
    fun internalClassNameFromInternalType(type: String): String {
        return if (isClassType(type)) {
            type.substring(1, type.length - 1)
        } else type
    }

    fun internalTypeFromExternalClassName(externalClassName: String): String {
        return internalTypeFromInternalClassName(internalClassNameFromExternalClassName(externalClassName))
    }

    fun internalTypeFromInternalClassName(internalClassName: String): String {
        return "L$internalClassName;"
    }

    fun internalClassNameFromExternalClassName(externalClassName: String): String {
        return externalClassName.replace('.', '/')
    }

    fun externalClassNameFromInternalClassName(internalClassName: String): String {
        return internalClassName.replace('/', '.')
    }

    fun externalClassNameFromInternalType(internalType: String): String {
        val className = internalClassNameFromInternalType(internalType)
        return className.replace('/', '.')
    }

    fun fullExternalMethodSignature(dexFile: DexFile, classDef: ClassDef, method: EncodedMethod): String {
        return "%s.%s:%s".format(externalClassNameFromInternalClassName(classDef.getClassName(dexFile)),
                                                                        method.getName(dexFile),
                                                                        method.getDescriptor(dexFile)
        )
    }

    fun fullExternalMethodDescriptor(dexFile: DexFile, method: EncodedMethod): String {
        return fullExternalMethodDescriptor(dexFile, method.getMethodID(dexFile))
    }

    fun fullExternalMethodDescriptor(dexFile: DexFile, methodID: MethodID): String {
        return "%s.%s%s".format(externalClassNameFromInternalType(methodID.getClassType(dexFile)),
                                                                  methodID.getName(dexFile),
                                                                  methodID.getProtoID(dexFile).getDescriptor(dexFile))
    }

    fun fullExternalFieldDescriptor(dexFile: DexFile, field: EncodedField): String {
        return fullExternalFieldDescriptor(dexFile, field.getFieldID(dexFile))
    }

    fun fullExternalFieldDescriptor(dexFile: DexFile, fieldID: FieldID): String {
        return "%s.%s:%s".format(externalClassNameFromInternalType(fieldID.getClassType(dexFile)),
                                                                   fieldID.getName(dexFile),
                                                                   fieldID.getType(dexFile))
    }

    fun parseParameters(parameters: String): List<String> {
        val result = mutableListOf<String>()

        var index = 0
        while (index < parameters.length) {
            val char = parameters[index]

            when (char) {
                'L' -> {
                    val colon = parameters.indexOf(';', index)
                    result.add(parameters.substring(index, colon + 1))
                    index = colon + 1
                }

                '[' -> {
                    var j = index + 1
                    while (parameters[j] == '[') j++
                    index = when (parameters[j]) {
                        'L' -> {
                            val colon = parameters.indexOf(';', j)
                            result.add(parameters.substring(index, colon + 1))
                            colon + 1
                        }

                        else -> {
                            result.add(parameters.substring(index, j + 1))
                            j + 1
                        }
                    }
                }

                else -> {
                    result.add(char.toString())
                    index++
                }
            }
        }

        return result
    }

    fun toShortyFormat(parameterTypes: List<String>, returnType: String): String {
        var result = toShortyFormat(returnType)

        for (parameter in parameterTypes) {
            result += toShortyFormat(parameter)
        }

        return result
    }

    fun toShortyFormat(type: String): String {
        return if (type.startsWith("L") || type.startsWith("[")) {
            "L"
        } else {
            type
        }
    }

    @JvmStatic
    fun getDefaultEncodedValueForType(type: String): EncodedValue {
        when (type) {
            BYTE_TYPE    -> return EncodedByteValue.of(0x00.toByte())
            SHORT_TYPE   -> return EncodedShortValue.of(0x00.toShort())
            CHAR_TYPE    -> return EncodedCharValue.of(0x00.toChar())
            INT_TYPE     -> return EncodedIntValue.of(0)
            LONG_TYPE    -> return EncodedLongValue.of(0L)
            FLOAT_TYPE   -> return EncodedFloatValue.of(0.0f)
            DOUBLE_TYPE  -> return EncodedDoubleValue.of(0.0)
            BOOLEAN_TYPE -> return EncodedBooleanValue.of(false)
        }
        return if (type.startsWith("L") && type.endsWith(";")) {
            EncodedNullValue
        } else {
            EncodedNullValue
        }
    }

    fun getArgumentSize(parameterTypes: Iterable<String>): Int {
        return parameterTypes.fold(0) { size, type -> size + getArgumentSizeForType(type) }
    }

    fun getArgumentSizeForType(type: String): Int {
        return when (type) {
            "B",
            "S",
            "C",
            "I",
            "Z",
            "F"  -> 1

            "J",
            "D"  -> 2

            else -> 1
        }
    }
}