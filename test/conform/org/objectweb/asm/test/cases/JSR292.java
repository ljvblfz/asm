/***
 * ASM tests
 * Copyright (c) 2002-2005 France Telecom
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
package org.objectweb.asm.test.cases;

import java.io.IOException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * Generates a class that contains new JSR 292 constant pool constants 
 * 
 * @author Remi Forax
 */
public class JSR292 extends Generator {
    int field;
    int static_field;
    
    void foo(int x) { }
    static void static_foo(int x) { }
    
    public void generate(final String dir) throws IOException {
        generate(dir, "pkg/JSR292.class", dump());
    }

    public byte[] dump() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_7,
                ACC_PUBLIC + ACC_SUPER,
                "pkg/JSR292",
                null,
                null,
                null);

        // JSR 292 constants
        constInsns(cw);
        
        cw.visitEnd();

        return cw.toByteArray();
    }

    private void constInsns(final ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC,
                "constInsns",
                "()V",
                null,
                null);
        
        mv.visitCstClassInsn("java/lang/Object");
        
        mv.visitCstMTypeInsn("()V");
        mv.visitCstMTypeInsn("(IJ)[Ljava/lang/Object;");
        
        mv.visitCstMHandleInsn(REF_getField, "pkg/JSR292", "field", "I");
        mv.visitCstMHandleInsn(REF_putField, "pkg/JSR292", "field", "I");
        mv.visitCstMHandleInsn(REF_getStatic, "pkg/JSR292", "static_field", "I");
        mv.visitCstMHandleInsn(REF_putStatic, "pkg/JSR292", "static_field", "I");
        
        mv.visitCstMHandleInsn(REF_invokeVirtual, "pkg/JSR292", "foo", "(I)V");
        mv.visitCstMHandleInsn(REF_invokeStatic, "pkg/JSR292", "static_foo", "(I)V");
        
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
