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
 * AttributeWriter
 *
 * @author Eric Bruneton
 */

class AttributeWriter implements AttributeVisitor {
  
  private final ClassWriter cw;
  
  ByteVector bv;
  
  int access;
  
  String signature;
  
  Object value;
  
  private AnnotationWriter dann;
  
  private AnnotationWriter vanns;
  
  private AnnotationWriter ivanns;
  
  private int nvis, vsize;
  
  private int ninvis, ivsize;
  
  private AnnotationWriter[] vpanns;
  
  private AnnotationWriter[] ivpanns;
  
  private int npvis, vpsize;
  
  private int npinvis, ivpsize;
  
  Attribute attrs;
  
  AttributeWriter next;
  
  AttributeWriter (final ClassWriter cw, final int params) {
    this.cw = cw;
    if (params > 0) {
      this.vpanns = new AnnotationWriter[params];
      this.ivpanns = new AnnotationWriter[params];
    }
  }
  
  AnnotationVisitor visitAnnotationDefault () {
    dann = new AnnotationWriter(cw, new ByteVector(), null, 0, false);
    return dann;
  }
  
  public AnnotationVisitor visitAnnotation (
    final String type, 
    final boolean visible)
  {
    ByteVector bv = new ByteVector();
    // write type, and reserve space for values count
    bv.putShort(cw.newUTF8(type)).putShort(0);
    AnnotationWriter aw = new AnnotationWriter(cw, bv, bv, bv.length - 2, true);
    if (visible) {
      aw.next = vanns;
      vanns = aw;
      ++nvis;
    } else {
      aw.next = ivanns;
      ivanns = aw;
      ++ninvis;
    }
    return aw;
  }
    
  AnnotationVisitor visitParameterAnnotation (
    final int parameter, 
    final String type, 
    final boolean visible)
  {
    ByteVector bv = new ByteVector();
    // write type, and reserve space for values count
    bv.putShort(cw.newUTF8(type)).putShort(0);
    AnnotationWriter aw = new AnnotationWriter(cw, bv, bv, bv.length - 2, true);
    if (visible) {
      aw.next = vpanns[parameter];
      vpanns[parameter] = aw;
      ++nvis;
    } else {
      aw.next = ivpanns[parameter];
      ivpanns[parameter] = aw;
      ++ninvis;
    }
    return aw;
  }
  
  public void visitAttribute (Attribute attr) {
    if (attr.isUnknown()) {
      if (cw.checkAttributes) {
        throw new IllegalArgumentException("Unkown attribute type " + attr.type);
      } else {
        return;
      }
    }
    attr.next = attrs;
    attrs = attr;
  }

  int getSize (
    final byte[] code,
    final int len,
    final int maxStack,
    final int maxLocals) 
  {
    int size = 2;
    if (bv != null) {
      size += bv.length;
    }
    if ((access & Constants.ACC_DEPRECATED) != 0) {
      cw.newUTF8("Deprecated");
      size += 6;
    }
    if ((access & Constants.ACC_SYNTHETIC) != 0) {
      cw.newUTF8("Synthetic");
      size += 6;
    }
    if (signature != null) {
      cw.newUTF8("Signature");
      cw.newConstItem(signature);
      size += 8;
    }
    if (value != null) {
      cw.newUTF8("ConstantValue");
      cw.newConstItem(value);
      size += 8;
    }
    if (dann != null) {
      cw.newUTF8("AnnotationDefault");
      size += 6 + dann.bv.length;
    }
    if (nvis > 0) {
      cw.newUTF8("RuntimeVisibleAnnotations");
      size += 8;
    }
    if (ninvis > 0) {
      cw.newUTF8("RuntimeInvisibleAnnotations");
      size += 8;
    }
    AnnotationWriter aw = vanns;
    while (aw != null) {
      size += aw.bv.length;
      vsize += aw.bv.length;
      aw = aw.next;
    }
    aw = ivanns;
    while (aw != null) {
      size += aw.bv.length;
      ivsize += aw.bv.length;
      aw = aw.next;
    }
    if (npvis > 0) {
      cw.newUTF8("RuntimeVisibleParameterAnnotations");
      size += 7 + 2*vpanns.length;
    }
    if (npinvis > 0) {
      cw.newUTF8("RuntimeInvisibleParameterAnnotations");
      size += 7 + 2*ivpanns.length;
    }
    if (vpanns != null) {
      for (int i = vpanns.length - 1; i >= 0; --i) {
        aw = vpanns[i];
        while (aw != null) {
          size += aw.bv.length;
          vsize += aw.bv.length;
          aw = aw.next;
        }
        aw = ivpanns[i];
        while (aw != null) {
          size += aw.bv.length;
          ivsize += aw.bv.length;
          aw = aw.next;
        }
      }
    }
    if (attrs != null) {
      size += attrs.getSize(cw, code, len, maxStack, maxLocals);
    }
    return size;
  }
  
  void put (
    final int attributeCount,
    final byte[] code,
    final int len,
    final int maxStack,
    final int maxLocals,
    final ByteVector out) 
  {
    if (bv != null) {
      out.putByteArray(bv.data, 0, bv.length);
    }
    out.putShort(
      attributeCount + 
      ((access & Constants.ACC_DEPRECATED) != 0 ? 1 : 0) +
      ((access & Constants.ACC_SYNTHETIC) != 0 ? 1 : 0) +
      (signature != null ? 1 : 0) +
      (value != null ? 1 : 0) +
      (dann != null ? 1 : 0) +
      (nvis > 0 ? 1 : 0) + 
      (ninvis > 0 ? 1 : 0) + 
      (npvis > 0 ? 1 : 0) + 
      (npinvis > 0 ? 1 : 0) +
      (attrs != null ? attrs.getCount() : 0));
    if ((access & Constants.ACC_DEPRECATED) != 0) {
      out.putShort(cw.newUTF8("Deprecated")).putInt(0);
    }
    if ((access & Constants.ACC_SYNTHETIC) != 0) {
      out.putShort(cw.newUTF8("Synthetic")).putInt(0);
    }
    if (signature != null) {
      out.putShort(cw.newUTF8("Signature"));
      out.putInt(2).putShort(cw.newUTF8(signature));
    }
    if (value != null) {
      out.putShort(cw.newUTF8("ConstantValue"));
      out.putInt(2).putShort(cw.newConstItem(value).index);
    }
    if (dann != null) {
      out.putShort(cw.newUTF8("AnnotationDefault"));
      out.putInt(6 + dann.bv.length);
      out.putByteArray(dann.bv.data, 0, dann.bv.length);
    }
    if (nvis > 0) {
      out.putShort(cw.newUTF8("RuntimeVisibleAnnotations"));
      put(nvis, vsize, vanns, out);
    }
    if (ninvis > 0) {
      out.putShort(cw.newUTF8("RuntimeInvisibleAnnotations"));
      put(ninvis, ivsize, ivanns, out);
    }
    if (npvis > 0) {
      out.putShort(cw.newUTF8("RuntimeVisibleParameterAnnotations"));
      put(npvis, vpsize, vpanns, out);
    }
    if (npinvis > 0) {
      out.putShort(cw.newUTF8("RuntimeInvisibleParameterAnnotations"));
      put(npinvis, ivpsize, ivpanns, out);
    }
    if (attrs != null) {
      attrs.put(cw, code, len, maxStack, maxLocals, out);
    }
  }
  
  private void put (
    final int total, 
    final int totalSize,
    final AnnotationWriter ann, 
    final ByteVector out)
  {
    out.putInt(8 + totalSize);
    out.putShort(total);
    AnnotationWriter aw = ann;
    while (aw != null) {
      out.putByteArray(aw.bv.data, 0, aw.bv.length);
      aw = aw.next;
    }
  }

  private void put (
    final int total, 
    final int totalSize,
    final AnnotationWriter[] anns, 
    final ByteVector out)
  {
    out.putInt(7 + 2*anns.length + totalSize);
    out.putByte(anns.length);
    for (int i = 0; i < anns.length; ++i) {
      AnnotationWriter aw = anns[i];
      int n = 0;
      while (aw != null) {
        aw = aw.next;
        ++n;
      }
      out.putShort(n); 
      aw = anns[i];
      while (aw != null) {
        out.putByteArray(aw.bv.data, 0, aw.bv.length);
        aw = aw.next;
      }
    }
  }
}
