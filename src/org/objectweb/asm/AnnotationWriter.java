/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000,2002,2003 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.objectweb.asm;

/**
 * AnnotationWriter
 *
 * @author Eugene Kuleshov
 */

final class AnnotationWriter implements AnnotationVisitor {
  
  private final ClassWriter cw;
  
  final ByteVector bv;
  
  private final ByteVector parent;
  
  private final int offset;
  
  private final boolean named;
  
  private int size;
  
  AnnotationWriter next;
  
  AnnotationWriter (
    final ClassWriter cw, 
    final ByteVector bv,
    final ByteVector parent,
    final int offset,
    final boolean named)
  {
    this.cw = cw;
    this.bv = bv;
    this.parent = parent;
    this.offset = offset;
    this.named = named;
  }
  
  public void visitValue (final String name, final Object value) {
    ++size;
    if (named) {
      bv.putShort(cw.newUTF8(name));
    }
    // TODO redundant with CW.newConst
    if (value instanceof Byte) {
      bv.putByte('B').putShort(cw.newConst(value));
    } else if (value instanceof Character) {
      bv.putByte('C').putShort(cw.newConst(value));
    } else if (value instanceof Boolean) {
      bv.putByte('Z').putShort(cw.newConst(value));
    } else if (value instanceof Short) {
      bv.putByte('S').putShort(cw.newConst(value));
    } else if (value instanceof Integer) {
      bv.putByte('I').putShort(cw.newConst(value));
    } else if (value instanceof Double) {
      bv.putByte('D').putShort(cw.newConst(value));
    } else if (value instanceof Float) {
      bv.putByte('F').putShort(cw.newConst(value));
    } else if (value instanceof Long) {
      bv.putByte('J').putShort(cw.newConst(value));
    } else if (value instanceof String) {
      bv.putByte('s').putShort(cw.newUTF8((String)value));
    } else if (value instanceof Type) {
      bv.putByte('c').putShort(cw.newUTF8(((Type)value).getDescriptor()));  
    }
  }

  public void visitEnumValue (
    final String name, 
    final String type, 
    final String value) 
  {
    ++size;
    if (named) {
      bv.putShort(cw.newUTF8(name));
    }
    bv.putByte('e').putShort(cw.newUTF8(type)).putShort(cw.newUTF8(value));
  }

  public AnnotationVisitor visitAnnotationValue (
    final String name, 
    final String type) 
  {
    ++size;
    if (named) {
      bv.putShort(cw.newUTF8(name));
    }
    // write tag and type, and reserve space for values count
    bv.putByte('@').putShort(cw.newUTF8(type)).putShort(0);
    return new AnnotationWriter(cw, bv, bv, bv.length - 2, true);
  }

  public AnnotationVisitor visitArrayValue (final String name) {
    ++size;
    if (named) {
      bv.putShort(cw.newUTF8(name));
    }
    // write tag, and reserve space for array size
    bv.putByte('[').putShort(0);
    return new AnnotationWriter(cw, bv, bv, bv.length - 2, false);
  }
  
  public void visitEnd () {
    byte[] data = parent.data;
    data[offset] = (byte)(size >>> 8);
    data[offset+1] = (byte)size;
  }
  
  int getSize () {
    int size = 0;
    AnnotationWriter aw = this;
    while (aw != null) {
      size += aw.bv.length;
      aw = aw.next;
    }
    return size;
  }
  
  void put (ByteVector out) {
    int n = 0;
    int size = 0;
    AnnotationWriter aw = this;
    while (aw != null) {
      n += 1;
      size += aw.bv.length;
      aw = aw.next;
    }
    out.putInt(8 + size); // TODO reserve space, complete after
    out.putShort(n); // TODO reserve space, complete after
    aw = this;
    while (aw != null) {
      out.putByteArray(aw.bv.data, 0, aw.bv.length);
      aw = aw.next;
    }
  }
  
  static void put (AnnotationWriter[] panns, ByteVector out) {
    out.putInt(7 + 2*panns.length + 0/*totalsize*/); //TODO reserve space, complete after
    out.putByte(panns.length);
    for (int i = 0; i < panns.length; ++i) {
      AnnotationWriter aw = panns[i];
      int n = 0;
      while (aw != null) {
        aw = aw.next;
        ++n;
      }
      out.putShort(n); // TODO reserve space, complete after
      aw = panns[i];
      while (aw != null) {
        out.putByteArray(aw.bv.data, 0, aw.bv.length);
        aw = aw.next;
      }
    }
  }
}
