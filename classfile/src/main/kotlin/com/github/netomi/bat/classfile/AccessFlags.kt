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
package com.github.netomi.bat.classfile

import com.github.netomi.bat.classfile.AccessFlagTarget.*

class AccessFlags(val rawFlags: Int, private val target: AccessFlagTarget) {
    val modifiers: Collection<AccessFlag> by lazy {
        val result = mutableListOf<AccessFlag>()
        val flagsOfTarget = AccessFlag.flagsByTarget(target)
        result.addAll(flagsOfTarget.filter { rawFlags and it.value != 0 })
        result
    }
}

enum class AccessFlagTarget(val value: Int) {
    CLASS(0x01),
    FIELD(0x02),
    METHOD(0x04)
}

enum class AccessFlag(val value: Int, val target: Int) {

    PUBLIC(ACC_PUBLIC, arrayOf(CLASS, FIELD, METHOD)),
    PRIVATE(ACC_PRIVATE, arrayOf(FIELD, METHOD)),
    PROTECTED(ACC_PROTECTED, arrayOf(FIELD, METHOD)),
    STATIC(ACC_STATIC, arrayOf(FIELD, METHOD)),
    FINAL(ACC_FINAL, arrayOf(CLASS, FIELD, METHOD)),
    SUPER(ACC_SUPER, arrayOf(CLASS)),
    SYNCHRONIZED(ACC_SYNCHRONIZED, arrayOf(METHOD)),
    VOLATILE(ACC_VOLATILE, arrayOf(FIELD)),
    BRIDGE(ACC_BRIDGE, arrayOf(METHOD)),
    VARARGS(ACC_VARARGS, arrayOf(METHOD)),
    NATIVE(ACC_NATIVE, arrayOf(METHOD)),
    TRANSIENT(ACC_TRANSIENT, arrayOf(FIELD)),
    INTERFACE(ACC_INTERFACE, arrayOf(CLASS)),
    ABSTRACT(ACC_ABSTRACT, arrayOf(CLASS, METHOD)),
    STRICT(ACC_STRICT, arrayOf(METHOD)),
    SYNTHETIC(ACC_SYNTHETIC, arrayOf(CLASS, FIELD, METHOD)),
    ANNOTATION(ACC_ANNOTATION, arrayOf(CLASS)),
    ENUM(ACC_ENUM, arrayOf(CLASS, FIELD)),
    MODULE(ACC_MODULE, arrayOf(CLASS));

    constructor(value: Int, targets: Array<AccessFlagTarget>): this(value, targets.fold(0) { acc, t -> acc or t.value })

    companion object {
        fun flagsByTarget(target: AccessFlagTarget): Collection<AccessFlag> {
            return values().filter { it.target and target.value != 0 }
        }
    }
}
