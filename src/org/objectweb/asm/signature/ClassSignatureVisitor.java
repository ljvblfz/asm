/*
 * Copyright area
 */

package org.objectweb.asm.signature;

// (visitFormalTypeParameter visitClassBound? visitInterfaceBound*)* visitSuperClass visitInterface*

public interface ClassSignatureVisitor extends MemberSignatureVisitor {

  TypeSignatureVisitor visitSuperclass ();

  TypeSignatureVisitor visitInterface ();
}
