
package org.objectweb.asm;


/**
 * AnnotationWriter
 *
 * @author Eugene Kuleshov
 */
public class AnnotationWriter implements AnnotationVisitor {
  private final ByteVector bv;
  private final ClassWriter cw;

  public AnnotationWriter( ClassWriter cw, ByteVector bv) {
    this.cw = cw;
    this.bv = bv;
  }

  /**
   * TODO move marker bytes into Item class
   */
  public void visitValue( String name, Object value) {
    bv.putShort( cw.newUTF8( name));
    if( value instanceof Byte) {
      bv.putByte( 'B').putShort( cw.newConst( value));
    
    } else if( value instanceof Character) {
      bv.putByte( 'C').putShort( cw.newConst( value));
    
    } else if( value instanceof Boolean) {
      bv.putByte( 'Z').putShort( cw.newConst( value));
    
    } else if( value instanceof Short) {
      bv.putByte( 'S').putShort( cw.newConst( value));
    
    } else if( value instanceof Integer) {
      bv.putByte( 'I').putShort( cw.newConst( value));
    
    } else if( value instanceof Double) {
      bv.putByte( 'D').putShort( cw.newConst( value));
    
    } else if( value instanceof Float) {
      bv.putByte( 'F').putShort( cw.newConst( value));
    
    } else if( value instanceof Long) {
      bv.putByte( 'J').putShort( cw.newConst( value));
    
    } else if( value instanceof String) {
      bv.putByte( 's').putShort( cw.newUTF8(( String) value));
    
    } else if( value instanceof Type) {
      // TODO verify this
      bv.putByte( 'c').putShort( cw.newUTF8((( Type) value).getDescriptor()));  
    
    }
  }

  public void visitEnumValue( String name, String type, String value) {
    bv.putShort( cw.newUTF8( name)).putByte( 'e');
    bv.putShort( cw.newUTF8( type));
    bv.putShort( cw.newUTF8( value));
  }

  public AnnotationVisitor visitAnnotationValue( String name, String type) {
    bv.putShort( cw.newUTF8( name)).putByte( '@');
    return new AnnotationWriter( cw, bv);
  }

  public AnnotationVisitor visitArrayValue( String name) {
    bv.putShort( cw.newUTF8( name)).putByte( '[');
    // TODO where to add array size?
    return new AnnotationWriter( cw, bv);
  }

}

