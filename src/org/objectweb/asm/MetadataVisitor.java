
package org.objectweb.asm;


/**
 * MetadataVisitor
 *
 * @author Eugene Kuleshov
 */
public interface MetadataVisitor {

  AnnotationVisitor visitAnnotation (String type, boolean visible);
  
  void visitEnd ();

}
