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

import com.github.netomi.bat.dexfile.*;
import com.github.netomi.bat.dexfile.annotation.*;
import com.github.netomi.bat.dexfile.io.DexFileReader;
import com.github.netomi.bat.dexfile.value.AnnotationElement;
import com.github.netomi.bat.dexfile.value.EncodedAnnotationValue;
import com.github.netomi.bat.dexfile.visitor.*;
import com.github.netomi.bat.smali.io.FileOutputStreamFactory;
import com.github.netomi.bat.smali.io.IndentingPrinter;
import com.github.netomi.bat.smali.io.OutputStreamFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Disassembler
implements   ClassDefVisitor
{
    private final OutputStreamFactory outputStreamFactory;

    public Disassembler(OutputStreamFactory outputStreamFactory) {
        this.outputStreamFactory = outputStreamFactory;
    }

    @Override
    public void visitClassDef(DexFile dexFile, int index, ClassDef classDef) {
        try (OutputStream   os  = outputStreamFactory.createOutputStream(classDef.getClassName(dexFile));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), 8192))
        {
            new SmaliPrinter(out).visitClassDef(dexFile, index, classDef);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Inner helper classes.

    private class SmaliPrinter
    implements    ClassDefVisitor,
                  ClassDataVisitor,
                  AnnotationSetVisitor,
                  AnnotationVisitor,
                  AnnotationElementVisitor,
                  EncodedFieldVisitor,
                  TypeListVisitor
    {
        private final IndentingPrinter printer;

        public SmaliPrinter(Writer writer) {
            this.printer = new IndentingPrinter(writer);
        }

        @Override
        public void visitClassDef(DexFile dexFile, int index, ClassDef classDef) {
            StringBuilder sb = new StringBuilder();

            printer.print(".class");

            String accessFlags =
                    DexAccessFlags.formatAsHumanReadable(classDef.accessFlags, DexAccessFlags.Target.CLASS)
                                  .toLowerCase();

            if (!accessFlags.isEmpty()) {
                printer.print(" " + accessFlags);
            }

            printer.print(" " + DexUtil.internalTypeFromClassName(classDef.getClassName(dexFile)));
            printer.println();

            printer.println(".super " + classDef.getSuperClassType(dexFile));

            String sourceFile = classDef.getSourceFile(dexFile);
            if (sourceFile != null) {
                printer.println(".source \"" + sourceFile + "\"");
            }

            printer.println();

            classDef.interfacesAccept(dexFile, this);

            if (classDef.annotationsDirectory != null) {
                classDef.annotationsDirectory.classAnnotationSetAccept(dexFile, classDef, this);
            }

            classDef.classDataAccept(dexFile, this);
        }

        @Override
        public void visitClassData(DexFile dexFile, ClassDef classDef, ClassData classData) {
            if (!classData.staticFields.isEmpty()) {
                printer.println("# static fields");
                classData.staticFieldsAccept(dexFile, classDef, this);
            }

            if (!classData.instanceFields.isEmpty()) {
                printer.println("# instance fields");
                classData.instanceFieldsAccept(dexFile, classDef, this);
            }
            printer.println();
        }

        @Override
        public void visitInterfaces(DexFile dexFile, ClassDef classDefItem, TypeList typeList) {
            printer.println("# interfaces");
            typeList.typesAccept(dexFile, (dexFile1, typeList1, index, type) -> printer.println(".implements " + type));
            printer.println();
        }

        @Override
        public void visitInstanceField(DexFile dexFile, ClassDef classDef, int index, EncodedField field) {
            printer.print(".field");

            String accessFlags =
                    DexAccessFlags.formatAsHumanReadable(field.accessFlags, DexAccessFlags.Target.FIELD)
                                  .toLowerCase();

            if (!accessFlags.isEmpty()) {
                printer.print(" " + accessFlags);
            }

            printer.println(" " + field.getName(dexFile) + ":" + field.getType(dexFile));
            printer.println();
        }

        @Override
        public void visitStaticField(DexFile dexFile, ClassDef classDef, int index, EncodedField field) {
            printer.print(".field");

            String accessFlags =
                    DexAccessFlags.formatAsHumanReadable(field.accessFlags, DexAccessFlags.Target.FIELD)
                            .toLowerCase();

            if (!accessFlags.isEmpty()) {
                printer.print(" " + accessFlags);
            }

            printer.print(" " + field.getName(dexFile) + ":" + field.getType(dexFile));

            field.staticValueAccept(dexFile, classDef, index, new EncodedValuePrinter(printer, " = "));

            printer.println();
            printer.println();
        }

        @Override
        public void visitClassAnnotationSet(DexFile dexFile, ClassDef classDef, AnnotationSet annotationSet) {
            if (annotationSet.getAnnotationCount() > 0) {
                printer.println("# annotations");
                annotationSet.accept(dexFile, classDef, this);
            }
        }

        @Override
        public void visitFieldAnnotationSet(DexFile dexFile, ClassDef classDef, FieldAnnotation fieldAnnotation, AnnotationSet annotationSet) {

        }

        @Override
        public void visitMethodAnnotationSet(DexFile dexFile, ClassDef classDef, MethodAnnotation methodAnnotation, AnnotationSet annotationSet) {

        }

        @Override
        public void visitParameterAnnotationSet(DexFile dexFile, ClassDef classDef, ParameterAnnotation parameterAnnotation, AnnotationSetRefList annotationSetRefList) {

        }

        @Override
        public void visitAnnotation(DexFile dexFile, ClassDef classDef, AnnotationSet annotationSet, int index, Annotation annotation) {
            printer.print(".annotation ");

            switch (annotation.getVisibility()) {
                case VISIBILITY_BUILD:
                    printer.print("build");
                    break;

                case VISIBILITY_SYSTEM:
                    printer.print("system");
                    break;

                case VISIBILITY_RUNTIME:
                    printer.print("runtime");
                    break;
            }

            EncodedAnnotationValue annotationValue = annotation.getAnnotationValue();
            printer.println(annotationValue.getType(dexFile));
            printer.levelUp();
            annotationValue.annotationElementsAccept(dexFile, this);
            printer.levelDown();
            printer.println(".end annotation");
            printer.println();
        }

        @Override
        public void visitAnnotationElement(DexFile dexFile, AnnotationElement element) {
            printer.print(element.getName(dexFile));
            printer.print(" = ");
            element.value.accept(dexFile, new EncodedValuePrinter(printer));
            printer.println();
        }
    }

    public static void main(String[] args) {
        DexFile dexFile = new DexFile();

        try (InputStream is = new FileInputStream("classes-io.dex"))
        {
            DexFileReader reader = new DexFileReader(is);
            reader.visitDexFile(dexFile);

            dexFile.classDefsAccept(new Disassembler(new FileOutputStreamFactory(Paths.get("out2"))));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
