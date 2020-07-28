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
package com.github.netomi.bat.smali;

import com.github.netomi.bat.dexfile.DexFile;
import com.github.netomi.bat.dexfile.FieldID;
import com.github.netomi.bat.dexfile.MethodID;
import com.github.netomi.bat.dexfile.value.*;
import com.github.netomi.bat.dexfile.visitor.AnnotationElementVisitor;
import com.github.netomi.bat.dexfile.visitor.EncodedValueVisitor;
import com.github.netomi.bat.io.IndentingPrinter;
import com.github.netomi.bat.util.Primitives;

class      EncodedValuePrinter
implements EncodedValueVisitor
{
    private final IndentingPrinter         printer;
    private final AnnotationElementVisitor annotationElementVisitor;
    private final String                   prefix;

    public EncodedValuePrinter(IndentingPrinter printer) {
        this(printer, null, null);
    }

    public EncodedValuePrinter(IndentingPrinter printer, AnnotationElementVisitor annotationElementVisitor) {
        this(printer, annotationElementVisitor, null);
    }

    public EncodedValuePrinter(IndentingPrinter printer, AnnotationElementVisitor annotationElementVisitor, String prefix) {
        this.printer                  = printer;
        this.annotationElementVisitor = annotationElementVisitor;
        this.prefix                   = prefix;
    }

    @Override
    public void visitAnyValue(DexFile dexFile, EncodedValue value) {
        throw new RuntimeException("unexpected value " + value);
    }

    @Override
    public void visitAnnotationValue(DexFile dexFile, EncodedAnnotationValue value) {
        append(".subannotation " + value.getType(dexFile));
        printer.println();
        printer.levelUp();

        if (annotationElementVisitor != null) {
            value.annotationElementsAccept(dexFile, annotationElementVisitor);
        } else {
            throw new RuntimeException("no AnnotationElementVisitor specified while visiting value " + value);
        }

        printer.levelDown();
        printer.print(".end subannotation");
    }

    @Override
    public void visitArrayValue(DexFile dexFile, EncodedArrayValue value) {
        append("{");
        printer.println();
        printer.levelUp();
        value.valuesAccept(dexFile, this, (df, v) -> printer.println());
        printer.levelDown();
        printer.print("}");
    }

    @Override
    public void visitTypeValue(DexFile dexFile, EncodedTypeValue value) {
        append(value.getType(dexFile));
    }

    @Override
    public void visitFieldValue(DexFile dexFile, EncodedFieldValue value) {
        FieldID fieldID = value.getField(dexFile);
        append(fieldID.getClassType(dexFile).getType(dexFile) + "->" +
               fieldID.getName(dexFile) + ":" +
               fieldID.getType(dexFile));
    }

    @Override
    public void visitMethodValue(DexFile dexFile, EncodedMethodValue value) {
        MethodID methodID = value.getMethod(dexFile);
        append(methodID.getClassType(dexFile).getType(dexFile) + "->" +
               methodID.getName(dexFile) +
               methodID.getProtoID(dexFile).getDescriptor(dexFile));
    }

    @Override
    public void visitBooleanValue(DexFile dexFile, EncodedBooleanValue value) {
        append(Boolean.toString(value.getValue()));
    }

    @Override
    public void visitByteValue(DexFile dexFile, EncodedByteValue value) {
        append(Primitives.toHexString(value.getValue()));
    }

    @Override
    public void visitCharValue(DexFile dexFile, EncodedCharValue value) {
        append(Character.toString(value.getValue()));
    }

    @Override
    public void visitDoubleValue(DexFile dexFile, EncodedDoubleValue value) {
        append(Double.toString(value.getValue()));
    }

    @Override
    public void visitEnumValue(DexFile dexFile, EncodedEnumValue value) {
        FieldID fieldID = value.getEnumField(dexFile);
        append(".enum " +
               fieldID.getClassType(dexFile).getType(dexFile) + "->" +
               fieldID.getName(dexFile) + ":" +
               fieldID.getType(dexFile));
    }

    @Override
    public void visitFloatValue(DexFile dexFile, EncodedFloatValue value) {
        append(Float.toString(value.getValue()));
    }

    @Override
    public void visitIntValue(DexFile dexFile, EncodedIntValue value) {
        append(Primitives.toHexString(value.getValue()));
    }

    @Override
    public void visitLongValue(DexFile dexFile, EncodedLongValue value) {
        append(Primitives.toHexString(value.getValue()));
    }

    @Override
    public void visitNullValue(DexFile dexFile, EncodedNullValue value) {
        append("null");
    }

    @Override
    public void visitShortValue(DexFile dexFile, EncodedShortValue value) {
        append(Primitives.toHexString(value.getValue()));
    }

    @Override
    public void visitStringValue(DexFile dexFile, EncodedStringValue value) {
        append("\"" + value.getString(dexFile) + "\"");
    }

    // private utility methods.

    private void append(String text) {
        if (prefix != null) {
            printer.print(prefix + text);
        } else {
            printer.print(text);
        }
    }
}
