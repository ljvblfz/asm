package org.objectweb.asm;

import junit.framework.TestCase;

public class ValueTypeUnitTest extends TestCase {
    public void testGetTypeString() {
        Type type = Type.getType("Qmvt/Test;");
        assertEquals("Qmvt/Test;", type.getDescriptor());
    }

    public void testGetObjectType() {
        Type type = Type.getObjectType(";Qmvt/Test;");
        assertEquals("Qmvt/Test;", type.getDescriptor());
    }

    public void testGetMethodTypeString() {
        Type methodType = Type.getMethodType("(Qmvt/Test;Ljava/lang/Object;Qmvt/Test;)Qmvt/Test;");
        Type[] parameterTypes = methodType.getArgumentTypes();
        assertEquals(3, parameterTypes.length);
        assertEquals("Qmvt/Test;", parameterTypes[0].getDescriptor());
        assertEquals("Ljava/lang/Object;", parameterTypes[1].getDescriptor());
        assertEquals("Qmvt/Test;", parameterTypes[2].getDescriptor());
        Type returnType = methodType.getReturnType();
        assertEquals("Qmvt/Test;", returnType.getDescriptor());
    }

    public void testGetMethodTypeTypeTypeArray() {
        Type returnType = Type.getType("Qmvt/Test;");
        Type type1 = Type.getType("Lmvt/Test;");
        Type type2 = Type.getType("Qmvt/Test;");

        Type methodType = Type.getMethodType(returnType, type1, type2);
        assertEquals("(Lmvt/Test;Qmvt/Test;)Qmvt/Test;", methodType.getDescriptor());
    }

    public void testGetArgumentTypesString() {
        Type[] argumentTypes = Type.getArgumentTypes("(IQmvt/Test;J)Z");
        assertEquals(3, argumentTypes.length);
        assertEquals("I", argumentTypes[0].getDescriptor());
        assertEquals("Qmvt/Test;", argumentTypes[1].getDescriptor());
        assertEquals("J", argumentTypes[2].getDescriptor());
    }

    public void testGetReturnTypeString() {
        Type returnType = Type.getReturnType("(IQmvt/Test;J)Qmvt/Test2;");
        assertEquals("Qmvt/Test2;", returnType.getDescriptor());
    }

    public void testGetArgumentsAndReturnSizesString() {
        int argumentsAndReturnSizes = Type.getArgumentsAndReturnSizes("(Qmvt/Test2;Ljava/lang/Object;Qmvt/Test;F)Qmvt/Test3;");
        int argSize = (argumentsAndReturnSizes >> 2) - 1;
        int retSize = argumentsAndReturnSizes & 0x03;
        assertEquals(4, argSize);
        assertEquals(1, retSize);
    }

    public void testGetSort() {
        Type type = Type.getObjectType(";Qmvt/Test;");
        assertEquals(Type.OBJECT, type.getSort());
        Type array = Type.getObjectType("[;Qmvt/Test;");
        assertEquals(Type.ARRAY, array.getSort());
    }

    public void testGetDimensions() {
        Type array = Type.getObjectType("[;Qmvt/Test;");
        assertEquals(1, array.getDimensions());
    }

    public void testGetElementType() {
        Type array = Type.getObjectType("[;Qmvt/Test;");
        assertEquals("Qmvt/Test;", array.getElementType().getDescriptor());
        assertEquals(";Qmvt/Test;", array.getElementType().getInternalName());
    }

    public void testGetClassName() {
        Type type = Type.getObjectType(";Qmvt/Test;");
        assertEquals("mvt.Test", type.getClassName());
        Type type2 = Type.getType("Qmvt/Test;");
        assertEquals("mvt.Test", type2.getClassName());
    }

    public void testGetInternalName() {
        Type type = Type.getObjectType(";Qmvt/Test;");
        assertEquals(";Qmvt/Test;", type.getInternalName());
        Type type2 = Type.getType("Qmvt/Test;");
        assertEquals(";Qmvt/Test;", type2.getInternalName());
    }

    public void testGetArgumentsAndReturnSizes() {
        Type methodType = Type.getMethodType("(Ljava/lang/Object;Qmvt/Test2;JQmvt/Test;)V");
        int argumentsAndReturnSizes = methodType.getArgumentsAndReturnSizes();
        int argSize = (argumentsAndReturnSizes >> 2) - 1;
        int retSize = argumentsAndReturnSizes & 0x03;
        assertEquals(5, argSize);
        assertEquals(0, retSize);
    }

    public void testGetDescriptor() {
        Type returnType = Type.getType("Qmvt/Test;");
        assertEquals("Qmvt/Test;", returnType.getDescriptor());
    }

    public void testGetMethodDescriptorTypeTypeArray() {
        Type returnType = Type.getObjectType(";Qmvt/RTest;");
        Type type1 = Type.getType("Qmvt/Test;");
        String desc = Type.getMethodDescriptor(returnType, type1);
        assertEquals(desc, "(Qmvt/Test;)Qmvt/RTest;");
    }

    public void testGetSize() {
        Type type = Type.getType("Qmvt/Test;");
        assertEquals(1, type.getSize());
    }

    public void testArrayBug() {
        Type type = Type.getType("[Ljava/util/List;");
        assertEquals("[Ljava/util/List;", type.getInternalName());
    }

    public void testArrayBug2() {
        Type type = Type.getObjectType("[Ljava/util/List;");
        assertEquals("[Ljava/util/List;", type.getInternalName());
    }
}