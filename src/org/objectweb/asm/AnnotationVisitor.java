
package org.objectweb.asm;

/**
 * AnnotationVisitor
 *
 * @author Eugene Kuleshov
 */
public interface AnnotationVisitor {

  void visitValue (String name, Object value);
  
  void visitEnumValue (String name, String type, String value);
  
  AnnotationVisitor visitAnnotationValue (String name, String type);
  
  AnnotationVisitor visitArrayValue (String name);
  
  // void visitEnd ();

}
