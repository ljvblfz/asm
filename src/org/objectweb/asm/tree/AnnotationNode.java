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

package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;

/**
 * A node that represents an annotationn.
 * 
 * @author Eric Bruneton
 */

public class AnnotationNode implements AnnotationVisitor {

  /**
   * The type of this annotation.
   */
  
  public String type;
  
  /**
   * The name value pairs of this annotation. TODO more details
   */
  
  public List values;

  public AnnotationNode (final String type) {
    this.type = type;
    this.values = new ArrayList(); 
  }
  
  AnnotationNode (final List values) {
    this.values = values; 
  }
  
  public void visitValue (final String name, final Object value) {
    if (type != null) {
      values.add(name);
    }
    values.add(value);
  }

  public void visitEnumValue (
    final String name, 
    final String type, 
    final String value)
  {
    if (type != null) {
      values.add(name);
    }
    values.add(new String[] {type, value});
  }

  public AnnotationVisitor visitAnnotationValue (
    final String name, 
    final String type) 
  {
    if (type != null) {
      values.add(name);
    }
    AnnotationNode annotation = new AnnotationNode(type);
    values.add(annotation);
    return annotation;
  }

  public AnnotationVisitor visitArrayValue (final String name) {
    if (type != null) {
      values.add(name);
    }
    List array = new ArrayList();
    values.add(array);
    return new AnnotationNode(array);
  }
  
  public void visitEnd () {
  }
  
  public void accept (final AnnotationVisitor av) {
    for (int i = 0; i < values.size(); i += 2) {
      String name = (String)values.get(i);
      Object value = values.get(i + 1);
      accept(av, name, value);
    }
  }
  
  static void accept (
    final AnnotationVisitor av, 
    final String name, 
    final Object value) 
  {
    if (value instanceof String[]) {
      String[] typeconst = (String[])value;
      av.visitEnumValue(name, typeconst[0], typeconst[1]);
    } else if (value instanceof AnnotationNode) {
      AnnotationNode an = (AnnotationNode)value;
      an.accept(av.visitAnnotationValue(name, an.type));
    } else if (value instanceof List) {
      AnnotationVisitor v = av.visitArrayValue(name);
      List array = (List)value;
      for (int j = 0; j < array.size(); ++j) {
        accept(v, null, array.get(j)); 
      }
    } else {
      av.visitValue(name, value);
    }
  }
}
