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
import org.objectweb.asm.Attribute;
import org.objectweb.asm.AttributeVisitor;

/**
 * 
 * @author Eric Bruneton
 */

public abstract class AttributeNode implements AttributeVisitor {

  /**
   * TODO.
   */
  
  public List visibleAnnotations;
  
  /**
   * TODO.
   */
  
  public List invisibleAnnotations;
  
  /**
   * The non standard attributes of TODO.  This list is a list of 
   * {@link Attribute Attribute} objects.
   */

  public List attrs;

  public AttributeNode () {
    this.visibleAnnotations = new ArrayList();
    this.invisibleAnnotations = new ArrayList();
    this.attrs = new ArrayList();
  }
  
  public AnnotationVisitor visitAnnotation (
    final String type, 
    final boolean visible) 
  {
    AnnotationNode an = new AnnotationNode(type);
    if (visible) {
      visibleAnnotations.add(an);
    } else {
      invisibleAnnotations.add(an);
    }
    return an;
  }
  
  public void visitAttribute (final Attribute attr) {
    attrs.add(attr);
  }

  /**
   * Makes the given attribute visitor visit this attribute node.
   *
   * @param av an attribute visitor.
   */

  public void accept (final AttributeVisitor av) {
    int i;
    for (i = 0; i < visibleAnnotations.size(); ++i) {
      AnnotationNode an = (AnnotationNode)visibleAnnotations.get(i); 
      an.accept(av.visitAnnotation(an.type, true));
    }
    for (i = 0; i < invisibleAnnotations.size(); ++i) {
      AnnotationNode an = (AnnotationNode)invisibleAnnotations.get(i); 
      an.accept(av.visitAnnotation(an.type, false));
    }
    for (i = 0; i < attrs.size(); ++i) {
      av.visitAttribute((Attribute)attrs.get(i));
    }
  }
}
