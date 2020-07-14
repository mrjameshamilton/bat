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
package com.github.netomi.bat.dexfile.value;

import com.github.netomi.bat.dexfile.io.DexDataInput;
import com.github.netomi.bat.dexfile.io.DexDataOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.netomi.bat.dexfile.DexConstants.NO_INDEX;

public class EncodedAnnotationValue
extends      EncodedValue
{
    public int typeIndex; // uleb128
    // public int size;      // uleb128

    public List<AnnotationElement> elements;

    public EncodedAnnotationValue() {
        typeIndex = NO_INDEX;
        elements  = Collections.emptyList();
    }

    @Override
    public int getValueType() {
        return VALUE_ANNOTATION;
    }

    @Override
    public void read(DexDataInput input, int valueArg) {
        typeIndex = input.readUleb128();
        int size  = input.readUleb128();

        elements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            AnnotationElement element = new AnnotationElement();
            element.read(input);
            elements.add(element);
        }
    }

    @Override
    public void write(DexDataOutput output) {
        writeType(output, 0);
        output.writeUleb128(typeIndex);
        output.writeUleb128(elements.size());
        for (AnnotationElement element : elements) {
            element.write(output);
        }
    }

    public String toString() {
        return String.format("EncodedAnnotationValue[typeIndex=%d,elements=%s]", typeIndex, elements);
    }
}