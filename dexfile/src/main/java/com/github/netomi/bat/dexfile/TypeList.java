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
package com.github.netomi.bat.dexfile;

import com.github.netomi.bat.dexfile.io.DexDataInput;
import com.github.netomi.bat.dexfile.io.DexDataOutput;
import com.github.netomi.bat.dexfile.util.PrimitiveIterable;
import com.github.netomi.bat.dexfile.visitor.TypeVisitor;
import com.github.netomi.bat.util.IntArray;

@DataItemAnn(
    type          = DexConstants.TYPE_TYPE_LIST,
    dataAlignment = 4,
    dataSection   = false
)
public class TypeList
implements   DataItem
{
    //private int   size; // uint
    private IntArray typeList;

    public static TypeList empty() {
        return new TypeList();
    }

    private TypeList() {
        this.typeList = new IntArray(0);
    }

    public int getTypeCount() {
        return typeList.size();
    }

    public String getType(DexFile dexFile, int typeIndex) {
        return dexFile.getTypeID(typeList.get(typeIndex)).getType(dexFile);
    }

    public Iterable<String> getTypes(DexFile dexFile) {
        return PrimitiveIterable.of(dexFile,
                                    (df, idx) -> df.getTypeID(idx).getType(df),
                                    typeList);
    }

    @Override
    public void read(DexDataInput input) {
        input.skipAlignmentPadding(getDataAlignment());

        int size = (int) input.readUnsignedInt();
        typeList.resize(size);
        for (int i = 0; i < size; i++) {
            int typeIndex = input.readUnsignedShort();
            typeList.set(i, typeIndex);
        }
    }

    @Override
    public void write(DexDataOutput output) {
        output.writeAlignmentPadding(getDataAlignment());

        int size = typeList.size();
        output.writeInt(size);
        for (int i = 0; i < size; i++) {
            output.writeUnsignedShort(typeList.get(i));
        }
    }

    public void typesAccept(DexFile dexFile, TypeVisitor visitor) {
        int size = typeList.size();
        for (int i = 0; i < size; i++) {
            visitor.visitType(dexFile, this, i, dexFile.getTypeID(typeList.get(i)).getType(dexFile));
        }
    }

    @Override
    public String toString() {
        return String.format("TypeList[size=%d,types=%s]", typeList.size(), typeList);
    }
}
