/*
 * Copyright area
 */

package org.objectweb.asm.signature;

// visitBaseType | visitTypeVariable | visitArrayType | (visitClassType visitTypeArgument* (visitInnerClassType visitTypeArgument*)* visitEnd))

public interface TypeSignatureVisitor {

  void visitBaseType (char descriptor); // including V for void

  void visitTypeVariable (String name);

  TypeSignatureVisitor visitArrayType ();

  void visitClassType (String name);

  void visitInnerClassType (String name);

  TypeSignatureVisitor visitTypeArgument (char tag);

  void visitEnd ();
}
