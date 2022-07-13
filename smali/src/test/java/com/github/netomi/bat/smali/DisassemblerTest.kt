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
package com.github.netomi.bat.smali

import com.github.netomi.bat.dexfile.DexFile
import com.github.netomi.bat.dexfile.io.DexFileReader
import com.github.netomi.bat.io.OutputStreamFactory
import com.github.netomi.bat.util.Arrays
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DisassemblerTest {
    @ParameterizedTest
    @MethodSource("dexFiles")
    fun batchDisassemble(testFile: String) {
        javaClass.getResourceAsStream("/dex/$testFile").use { `is` ->
            println("checking $testFile...")

            val reader = DexFileReader(`is`, false)
            val dexFile = DexFile()
            reader.visitDexFile(dexFile)

            val outputStreamFactory = TestOutputStreamFactory()
            val disassembler = Disassembler(outputStreamFactory)
            dexFile.classDefsAccept(disassembler)

            val expectedArchive = testFile.replace(".dex".toRegex(), ".zip")

            ZipInputStream(javaClass.getResourceAsStream("/dex/$expectedArchive")!!).use { zipInputStream ->
                var zipEntry: ZipEntry? = zipInputStream.nextEntry
                while (zipEntry != null) {
                    if (zipEntry.name.endsWith(".smali")) {
                        val className = zipEntry.name.replace(".smali".toRegex(), "")

                        val expectedBytes = zipInputStream.readAllBytes()
                        val actualBytes   = outputStreamFactory.getOutputStream(className)!!.toByteArray()

                        // testing purposes only.
                        if (!Arrays.equals(expectedBytes, actualBytes, expectedBytes.size)) {
                            Files.write(Paths.get("${className}_expected.smali"), expectedBytes)
                            Files.write(Paths.get("${className}_actual.smali"), actualBytes)
                        }

                        assertArrayEquals(expectedBytes, actualBytes)
                    }
                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                }
            }
        }
    }

    private class TestOutputStreamFactory : OutputStreamFactory {
        private val outputStreamMap: MutableMap<String, ByteArrayOutputStream>

        init {
            outputStreamMap = TreeMap()
        }

        val classNames: Iterable<String>
            get() = outputStreamMap.keys

        fun getOutputStream(className: String): ByteArrayOutputStream? {
            return outputStreamMap[className]
        }

        @Throws(IOException::class)
        override fun createOutputStream(className: String): OutputStream {
            val baos = ByteArrayOutputStream()
            outputStreamMap[className] = baos
            return baos
        }
    }

    companion object {
        @JvmStatic
        fun dexFiles() = listOf(
            Arguments.of("all.dex"),
            Arguments.of("bytecodes.dex"),
            Arguments.of("checkers.dex"),
            Arguments.of("const-method-handle.dex"),
            Arguments.of("invoke-custom.dex"),
            Arguments.of("invoke-polymorphic.dex"),
            Arguments.of("staticfields.dex"),
            Arguments.of("values.dex")
        )
    }
}