/*
 * Copyright area
 */

package org.objectweb.asm.signature;

// (visitFormalTypeParameter visitClassBound? visitInterfaceBound*)* visitParameterType* visitReturnType visitExceptionType*

public interface MethodSignatureVisitor extends MemberSignatureVisitor {

  TypeSignatureVisitor visitParameterType ();

  TypeSignatureVisitor visitReturnType ();

  TypeSignatureVisitor visitExceptionType ();
}
