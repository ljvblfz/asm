/*
 * Copyright area
 */

package org.objectweb.asm.signature;

public interface MemberSignatureVisitor {

  void visitFormalTypeParameter (String name);

  TypeSignatureVisitor visitClassBound ();

  TypeSignatureVisitor visitInterfaceBound ();

}
