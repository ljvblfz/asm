/*
 * Copyright area
 */

package org.objectweb.asm;

final class FieldWriter implements MemberVisitor {

  ClassWriter cw;
  
  FieldWriter next;
  
  private int access;
  
  private String name;
  
  private String desc;
  
  private String signature;
  
  private Object value;
  
  private AnnotationWriter anns;
  
  private AnnotationWriter ianns;
  
  private Attribute attrs;

  public FieldWriter (
    final ClassWriter cw, 
    final int access,
    final String name,
    final String desc,
    final String signature,
    final Object value)
  {
    if (cw.firstField == null) {
      cw.firstField = this;
    } else {
      cw.lastField.next = this;
    }
    cw.lastField = this;
    this.cw = cw;
    this.access = access;
    this.name = name;
    this.desc = desc;
    this.signature = signature;
    this.value = value;
  }

  public AnnotationVisitor visitAnnotation (String type, boolean visible) {
    ByteVector bv = new ByteVector();
    // write type, and reserve space for values count
    bv.putShort(cw.newUTF8(type)).putShort(0);
    AnnotationWriter aw = new AnnotationWriter(cw, bv, bv, bv.length - 2, true);
    if (visible) {
      aw.next = anns;
      anns = aw;
    } else {
      aw.next = ianns;
      ianns = aw;
    }
    return aw;
  }

  public void visitAttribute (Attribute attr) {
    attr.next = attrs;
    attrs = attr;
  }
  
  int getSize () {
    int size = 8;
    if (value != null) {
      cw.newUTF8("ConstantValue");
      size += 8;
    }
    if ((access & Constants.ACC_SYNTHETIC) != 0) {
      cw.newUTF8("Synthetic");
      size += 6;
    }
    if ((access & Constants.ACC_DEPRECATED) != 0) {
      cw.newUTF8("Deprecated");
      size += 6;
    }
    if (signature != null) {
      cw.newUTF8("Signature");
      size += 8;
    }
    if (anns != null) {
      cw.newUTF8("RuntimeVisibleAnnotations");
      size += 8 + anns.getSize();
    }
    if (ianns != null) {
      cw.newUTF8("RuntimeInvisibleAnnotations");
      size += 8 + ianns.getSize();
    }
    if (attrs != null) {
      size += attrs.getSize(cw, null, 0, -1, -1);      
    }
    return size;
  }
  
  void put (ByteVector out) {
    out.putShort(access).putShort(cw.newUTF8(name)).putShort(cw.newUTF8(desc));
    int attributeCount = 0;
    if (value != null) {
      ++attributeCount;
    }
    if ((access & Constants.ACC_SYNTHETIC) != 0) {
      ++attributeCount;
    }
    if ((access & Constants.ACC_DEPRECATED) != 0) {
      ++attributeCount;
    }
    if (signature != null) {
      ++attributeCount;
    }
    if (anns != null) {
      ++attributeCount;
    }
    if (ianns != null) {
      ++attributeCount;
    }
    if (attrs != null) {
      attributeCount += attrs.getCount();
    }
    out.putShort(attributeCount);
    if (value != null) {
      out.putShort(cw.newUTF8("ConstantValue"));
      out.putInt(2).putShort(cw.newConstItem(value).index);
    }
    if ((access & Constants.ACC_SYNTHETIC) != 0) {
      out.putShort(cw.newUTF8("Synthetic")).putInt(0);
    }
    if ((access & Constants.ACC_DEPRECATED) != 0) {
      out.putShort(cw.newUTF8("Deprecated")).putInt(0);
    }
    if (signature != null) {
      out.putShort(cw.newUTF8("Signature"));
      out.putInt(2).putShort(cw.newUTF8(signature));
    }
    if (anns != null) {
      out.putShort(cw.newUTF8("RuntimeVisibleAnnotations"));
      anns.put(out);
    }
    if (ianns != null) {
      out.putShort(cw.newUTF8("RuntimeInvisibleAnnotations"));
      ianns.put(out);
    }
    if (attrs != null) {
      attrs.put(cw, null, 0, -1, -1, out);
    }
  }
}
