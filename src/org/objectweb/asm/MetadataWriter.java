
package org.objectweb.asm;


/**
 * MetadataWriter
 *
 * @author Eugene Kuleshov
 */
public class MetadataWriter implements MetadataVisitor {
  private static final String[] NAMES = {
      "RuntimeVisibleAnnotations",
      "RuntimeInvisibleAnnotations",
      "RuntimeVisibleParameterAnnotations",
      "RuntimeInvisibleParameterAnnotations"
    };
  
  private final ClassWriter cw;
  private final ByteVector bv;
  private final boolean isParameter;

  private int[] counts = new int[ 2];
  private ByteVector[] anns = new ByteVector[ 2];
  
  
  public MetadataWriter( ClassWriter cw, ByteVector bv, boolean isParameter) {
    this.cw = cw;
    this.bv = bv;
    this.isParameter = isParameter;
  }

  public AnnotationVisitor visitAnnotation( String type, boolean visible) {
    int n = visible ? 0 : 1;
    ++counts[ n];
    if( anns[ n]==null) {
      anns[ n] = new ByteVector();
    }
    anns[ n].putShort( cw.newUTF8( type));
    return new AnnotationWriter( cw, anns[ n]);
  }
  
  public void visitEnd() {
    int n = isParameter ? 2 : 0;
    putAnnotations( NAMES[ n], counts[ 0], anns[ 0]);
    putAnnotations( NAMES[ n+1], counts[ 1], anns[ 1]);
  }

  private void putAnnotations( String name, int annCount, ByteVector ann) {
    if( annCount>0) {
      bv.putShort( cw.newUTF8( name));
      bv.putShort( 2 + ann.length);  // TODO verify size
      bv.putShort( annCount);
      bv.putByteArray( ann.data, 0, ann.length);
    }
  }

}

