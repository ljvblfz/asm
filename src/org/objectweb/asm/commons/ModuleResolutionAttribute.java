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
 * ModuleResolution_attribute.
 * This attribute is specific to the OpenJDK and may change in the future.
 * 
 * Unlike a classical ASM attribute, this attribute can interact
 * with {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, Attribute[], int)}
 * in two different ways.
 * The usual way, by creating an empty attribute using {@link #ModuleResolutionAttribute()}
 * that will be sent as argument of the method accept of the ClassReader and
 * during the parsing the method {@link org.objectweb.asm.ClassVisitor#visitAttribute(Attribute)}
 * will be called.
 * The visitor way, by creating an empty attribute using
 * {@link #ModuleResolutionAttribute(int, ModuleAttributeVisitor)} that will called
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
public final class ModuleResolutionAttribute extends ModuleAttributeVisitor {
    public int resolution;
    
    /**
     * Creates an attribute with a resolution state value.
     * @param resolution the resolution state among
     *        {@link #RESOLUTION_WARN_DEPRECATED},
     *        {@link #RESOLUTION_WARN_DEPRECATED_FOR_REMOVAL}, and
     *        {@link #RESOLUTION_WARN_INCUBATING}.
     */
    public ModuleResolutionAttribute(final int resolution) {
        super(Opcodes.ASM6, "ModuleResolution");
        this.resolution = resolution;
    }
    
    /**
     * Creates an empty attribute that can be used as prototype
     * to be passed as argument of the method
     * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, Attribute[], int)}.
     */
    public ModuleResolutionAttribute() {
        this(0);
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
    public ModuleResolutionAttribute(final int api, final ModuleAttributeVisitor mv) {
        super(api, "ModuleResolution");
        this.mv = mv;
    }
    
    /**
     * Makes the given visitor visit this attribute.
     * 
     * @param mv a module attribute visitor.
     */
    public void accept(final ModuleAttributeVisitor mv) {
        int resolution = this.resolution;
        if (resolution != 0) {
            mv.visitResolution(resolution);
        }
    }
    
    @Override
    public void visitResolution(int resolution) {
        this.resolution = resolution;
    }
    
    @Override
    protected Attribute read(ClassReader cr, int off, int len, char[] buf,
            int codeOff, Label[] labels) {
        int resolution = cr.readUnsignedShort(off); 
        if (mv != null) {
            mv.visitResolution(resolution);
            return null;
        }
        return new ModuleResolutionAttribute(resolution);
    }
    
    @Override
    protected ByteVector write(ClassWriter cw, byte[] code, int len,
            int maxStack, int maxLocals) {
        ByteVector v = new ByteVector();
        v.putShort(resolution);
        return v;
    }
}
