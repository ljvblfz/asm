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

package org.objectweb.asm.util;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;

public class TraceAnnotationVisitor implements AnnotationVisitor {

  protected final AnnotationVisitor av;
  
  protected final List text;
  
  protected final StringBuffer buf;
  
  public TraceAnnotationVisitor (final AnnotationVisitor av) {
    this.av = av;
    this.text = new ArrayList();
    this.buf = new StringBuffer();
  }
  
  public void visitValue (final String name, final Object value) {
    buf.setLength(0);
    if (name != null) {
      buf.append(name).append("=");
    }
    buf.append(value);
    text.add(buf.toString());
    
    if (av != null) {
      av.visitValue(name, value); 
    }
  }

  public void visitEnumValue (
    final String name, 
    final String type, 
    final String value) 
  {
    buf.setLength(0);
    if (name != null) {
      buf.append(name).append("=");
    }
    buf.append(type).append('.').append(value);
    text.add(buf.toString());

    if (av != null) {
      av.visitEnumValue(name, type, value); 
    }
  }

  public AnnotationVisitor visitAnnotationValue (
    final String name, 
    final String type) 
  {
    buf.setLength(0);
    if (name != null) {
      buf.append(name).append('=');
    }
    buf.append('@').append(type).append('(');
    text.add(buf.toString());
    TraceAnnotationVisitor tav = new TraceAnnotationVisitor(
      av == null ? null : av.visitAnnotationValue(name, type));
    text.add(tav.getText());
    text.add(")");
    return tav;
  }

  public AnnotationVisitor visitArrayValue (final String name) {
    buf.setLength(0);
    if (name != null) {
      buf.append(name).append("=");
    }
    buf.append('{');
    text.add(buf.toString());
    TraceAnnotationVisitor tav = new TraceAnnotationVisitor(
      av == null ? null : av.visitArrayValue(name));
    text.add(tav.getText());
    text.add("}");
    return tav;
  }

  public void visitEnd () {
  }
  
  public List getText () {
    return text;
  }
}
