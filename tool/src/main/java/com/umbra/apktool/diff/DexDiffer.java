package com.umbra.apktool.diff;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedField;
import org.jf.dexlib2.dexbacked.DexBackedMethod;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class DexDiffer {
    public DiffInfo diff(File newFile, File oldFile)
            throws IOException {
        DexBackedDexFile newDexFile = DexFileFactory.loadDexFile(newFile, 19,
                true);
        DexBackedDexFile oldDexFile = DexFileFactory.loadDexFile(oldFile, 19,
                true);

        DiffInfo info = DiffInfo.getInstance();

        boolean contains = false;
        for (DexBackedClassDef newClazz : newDexFile.getClasses()) {
            Set<? extends DexBackedClassDef> oldclasses = oldDexFile.getClasses();
            for (DexBackedClassDef oldClazz : oldclasses) {
                if (newClazz.equals(oldClazz)) {
                    compareField(newClazz, oldClazz, info);
                    compareMethod(newClazz, oldClazz, info);
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                info.addAddedClasses(newClazz);
            }
        }
        return info;
    }

    public void compareMethod(DexBackedClassDef newClazz, DexBackedClassDef oldClazz, DiffInfo info) {
        compareMethod(newClazz.getMethods(), oldClazz.getMethods(), info);
    }

    public void compareMethod(Iterable<? extends DexBackedMethod> news, Iterable<? extends DexBackedMethod> olds, DiffInfo info) {
        for (DexBackedMethod reference : news)
            if (!reference.getName().equals("<clinit>")) {
                compareMethod(reference, olds, info);
            }
    }

    public void compareMethod(DexBackedMethod object, Iterable<? extends DexBackedMethod> olds, DiffInfo info) {
        for (DexBackedMethod reference : olds) {
            if (reference.equals(object)) {
                if ((reference.getImplementation() == null) &&
                        (object.getImplementation() != null)) {
                    info.addModifiedMethods(object);
                    return;
                }
                if ((reference.getImplementation() != null) &&
                        (object.getImplementation() == null)) {
                    info.addModifiedMethods(object);
                    return;
                }
                if ((reference.getImplementation() == null) &&
                        (object.getImplementation() == null)) {
                    return;
                }

                if (!reference.getImplementation().equals(
                        object.getImplementation())) {
                    info.addModifiedMethods(object);
                    return;
                }
                return;
            }
        }

        info.addAddedMethods(object);
    }

    public void compareField(DexBackedClassDef newClazz, DexBackedClassDef oldClazz, DiffInfo info) {
        compareField(newClazz.getInstanceFields(), oldClazz.getInstanceFields(), info);
    }

    public void compareField(Iterable<? extends DexBackedField> news, Iterable<? extends DexBackedField> olds, DiffInfo info) {
        for (DexBackedField reference : news)
            compareField(reference, olds, info);
    }

    public void compareField(DexBackedField object, Iterable<? extends DexBackedField> olds, DiffInfo info) {
        for (DexBackedField reference : olds) {
            if (reference.equals(object)) {
                if ((reference.getInitialValue() == null) &&
                        (object.getInitialValue() != null)) {
                    info.addModifiedFields(object);
                    return;
                }
                if ((reference.getInitialValue() != null) &&
                        (object.getInitialValue() == null)) {
                    info.addModifiedFields(object);
                    return;
                }
                if ((reference.getInitialValue() == null) &&
                        (object.getInitialValue() == null)) {
                    return;
                }
                if (reference.getInitialValue().compareTo(
                        object.getInitialValue()) != 0) {
                    info.addModifiedFields(object);
                    return;
                }
                return;
            }
        }

        info.addAddedFields(object);
    }
}