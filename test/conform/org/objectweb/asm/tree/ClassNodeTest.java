/***
 * ASM tests
 * Copyright (c) 2002,2003 France Telecom
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

import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ClassWriter tests.
 * 
 * @author Eric Bruneton, Eugene Kuleshov
 */

public class ClassNodeTest extends TestCase {
  
  private String className;
  
  public ClassNodeTest (String className) {
    super("testClassNode");
    this.className = className;
  }
  
  public static TestSuite suite () throws Exception {
    TestSuite suite = new TestSuite(ClassNodeTest.class.getName());
    Class c = ClassNodeTest.class;
    String u = c.getResource("/java/lang/String.class").toString();
    int n = u.indexOf('!');
    ZipInputStream zis = 
      new ZipInputStream(new URL(u.substring(4, n)).openStream());
    ZipEntry ze = null;
    while ((ze = zis.getNextEntry()) != null) {
      if (ze.getName().endsWith(".class")) {
        suite.addTest(
            new ClassNodeTest(u.substring(0, n + 2).concat(ze.getName())));
      }
    }
    return suite;
  }
  
  public void testClassNode () throws Exception {
    ClassReader cr = new ClassReader(new URL(className).openStream());
    ClassNode cn = new ClassNode();
    cr.accept(cn, false);
    ClassWriter cw = new ClassWriter(false, true);
    cn.accept(cw);
    cw.toByteArray();
  }

  // workaround for Ant's JUnit test runner
  public String getName() {
    return super.getName()+" : "+className;
  }
}
