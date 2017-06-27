/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
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

package org.objectweb.asm.commons;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 * ModuleTarget attribute.
 * This attribute is specific to the OpenJDK and may change in the future.
 * 
 * Unlike a classical ASM attribute, this attribute can interact
 * with {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, Attribute[], int)}
 * in two different ways.
 * The usual way, by creating an empty attribute using {@link #ModuleTargetAttribute()}
 * that will be sent as argument of the method accept of the ClassReader and
 * during the parsing the method {@link org.objectweb.asm.ClassVisitor#visitAttribute(Attribute)}
 * will be called.
 * The visitor way, by creating an empty attribute using
 * {@link #ModuleTargetAttribute(int, ModuleAttributeVisitor)} that will called
 * the methods of the {@link ModuleAttributeVisitor} when the attribute is found.
 * In that case the method {@link org.objectweb.asm.ClassVisitor#visitAttribute(Attribute)}
 * will not be called.
 * 
 * Moreover, like the Tree API, this attribute is itself a visitor and has a method
 * {@link #accept(ModuleAttributeVisitor)} that allow to extract the values of this
 * attribute using a visitor.
 * 
 * @author Remi Forax
 */
public final class ModuleTargetAttribute extends ModuleAttributeVisitor {
    public String platform;
    
    /**
     * Creates an attribute with a platform name.
     * @param platform the platform name on which the module can run.
     */
    public ModuleTargetAttribute(final String platform) {
        super(Opcodes.ASM6, "ModuleTarget");
        this.platform = platform;
    }
    
    /**
     * Creates an empty attribute that can be used as prototype
     * to be passed as argument of the method
     * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, Attribute[], int)}.
     */
    public ModuleTargetAttribute() {
        this(null);
    }
    
    /**
     * Create an empty attribute that when used with
     * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, Attribute[], int)}
     * will called the visitor taken as parameter if an attribute of the same kind
     * is found
     * 
     * @param api the ASM api to use, only {@link Opcodes#ASM6} is valid.
     * @param mv a module attribute visitor
     */
    public ModuleTargetAttribute(final int api, final ModuleAttributeVisitor mv) {
        super(api, "ModuleTarget");
        this.mv = mv;
    }
    
    /**
     * Makes the given visitor visit this attribute.
     * 
     * @param mv a module attribute visitor.
     */
    public void accept(final ModuleAttributeVisitor mv) {
        String platform = this.platform;
        if (platform != null) {
            mv.visitPlatform(platform);
        }
    }
    
    @Override
    public void visitPlatform(String platform) {
       this.platform = platform;
    }
    
    @Override
    protected Attribute read(ClassReader cr, int off, int len, char[] buf,
            int codeOff, Label[] labels) {
        String platform = cr.readUTF8(off, buf); 
        if (mv != null) {
            mv.visitPlatform(platform);
            return null;
        }
        return new ModuleTargetAttribute(platform);
    }
    
    @Override
    protected ByteVector write(ClassWriter cw, byte[] code, int len,
            int maxStack, int maxLocals) {
        ByteVector v = new ByteVector();
        int index = (platform == null)? 0: cw.newUTF8(platform);
        v.putShort(index);
        return v;
    }
}
