/*
 * Copyright area
 */

package org.objectweb.asm.signature;

public class SignatureWriter implements
  ClassSignatureVisitor,
  MethodSignatureVisitor,
  TypeSignatureVisitor
{

  private final StringBuffer buf = new StringBuffer();

  private boolean hasFormals;

  private boolean hasClassBound;

  private boolean hasParameters;

  private int argumentStack;

  public void visitFormalTypeParameter (String name) {
    if (!hasFormals) {
      hasFormals = true;
      buf.append('<');
    }
    buf.append(name);
    hasClassBound = false;
  }

  public TypeSignatureVisitor visitClassBound () {
    buf.append(':');
    hasClassBound = true;
    return this;
  }

  public TypeSignatureVisitor visitInterfaceBound () {
    if (!hasClassBound) {
      buf.append(':');
    }
    buf.append(':');
    return this;
  }

  public TypeSignatureVisitor visitSuperclass () {
    endFormals();
    return this;
  }

  public TypeSignatureVisitor visitInterface () {
    return this;
  }

  public TypeSignatureVisitor visitParameterType () {
    endFormals();
    if (!hasParameters) {
      hasParameters = true;
      buf.append('(');
    }
    return this;
  }

  public TypeSignatureVisitor visitReturnType () {
    endFormals();
    endParameters();
    return this;
  }

  public TypeSignatureVisitor visitExceptionType () {
    return this;
  }

  public void visitBaseType (char descriptor) {
    buf.append(descriptor);
  }

  public void visitTypeVariable (String name) {
    buf.append('T').append(name).append(';');
  }

  public TypeSignatureVisitor visitArrayType () {
    buf.append('[');
    return this;
  }

  public void visitClassType (String name) {
    buf.append('L').append(name);
    argumentStack *= 2;
  }

  public void visitInnerClassType (String name) {
    endArguments();
    buf.append('.').append(name);
    argumentStack *= 2;
  }

  public TypeSignatureVisitor visitTypeArgument (char tag) {
    if (argumentStack%2 == 0) {
      ++argumentStack;
      buf.append('<');
    }
    if (tag != ' ') {
      buf.append(tag);
    }
    return this;
  }

  public void visitEnd () {
    endArguments();
    buf.append(';');
  }

  public String toString () {
    return buf.toString();
  }

  // -----------------------------------------------------------------

  private void endFormals () {
    if (hasFormals) {
      hasFormals = false;
      buf.append('>');
    }
  }

  private void endParameters () { // can be inlined
    if (!hasParameters) {
      buf.append('(');
    }
    buf.append(')');
  }

  private void endArguments () {
    if (argumentStack%2 == 1) {
      buf.append('>');
    }
    argumentStack /= 2;
  }
}