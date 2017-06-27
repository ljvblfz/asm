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

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 * ModuleHashes attribute.
 * This attribute is specific to the OpenJDK and may change in the future.
 * 
 * Unlike a classical ASM attribute, this attribute can interact
 * with {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, Attribute[], int)}
 * in two different ways.
 * The usual way, by creating an empty attribute using {@link #ModuleHashesAttribute()}
 * that will be sent as argument of the method accept of the ClassReader and
 * during the parsing the method {@link org.objectweb.asm.ClassVisitor#visitAttribute(Attribute)}
 * will be called.
 * The visitor way, by creating an empty attribute using
 * {@link #ModuleHashesAttribute(int, ModuleAttributeVisitor)} that will called
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
public final class ModuleHashesAttribute extends ModuleAttributeVisitor {
    public String algorithm;
    public List<String> modules;
    public List<byte[]> hashes;
    
    /**
     * Creates an attribute with a hashing algorithm, a list of module names,
     * and a list of the same length of hashes.
     * @param algorithm the hashing algorithm name.
     * @param modules a list of module name
     * @param hashes a list of hash, one for each module name.
     */
    public ModuleHashesAttribute(final String algorithm,
            final List<String> modules, final List<byte[]> hashes) {
        super(Opcodes.ASM6, "ModuleHashes");
        this.algorithm = algorithm;
        this.modules = modules;
        this.hashes = hashes;
    }
    
    /**
     * Creates an empty attribute that can be used as prototype
     * to be passed as argument of the method
     * {@link ClassReader#accept(org.objectweb.asm.ClassVisitor, Attribute[], int)}.
     */
    public ModuleHashesAttribute() {
        this(null, null, null);
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
    public ModuleHashesAttribute(final int api, final ModuleAttributeVisitor mv) {
        super(api, "ModuleHashes");
        this.mv = mv;
    }
    
    /**
     * Makes the given visitor visit this attribute.
     * 
     * @param mv a module attribute visitor.
     */
    public void accept(final ModuleAttributeVisitor mv) {
        mv.visitHashAlgorithm(algorithm);
        List<String> modules = this.modules;
        if (modules != null) {
            List<byte[]> hashes = this.hashes;
            int count = modules.size();
            for(int i = 0; i < count; i++) {
                mv.visitModuleHash(modules.get(i), hashes.get(i));
            }
        }
    }
    
    @Override
    public void visitHashAlgorithm(String hashAlgorithm) {
        this.algorithm = hashAlgorithm;
    }
    @Override
    public void visitModuleHash(String module, byte[] hash) {
        if (modules == null) {
            modules = new ArrayList<String>();
            hashes = new ArrayList<byte[]>();
        }
        modules.add(module);
        hashes.add(hash);
    }
    
    @Override
    protected Attribute read(ClassReader cr, int off, int len, char[] buf,
            int codeOff, Label[] labels) {
        ModuleHashesAttribute attr = null;
        ModuleAttributeVisitor mv = (this.mv == null)? attr = new ModuleHashesAttribute(): this.mv;
        
        String hashAlgorithm = cr.readUTF8(off, buf); 
        mv.visitHashAlgorithm(hashAlgorithm);
        
        int count = cr.readUnsignedShort(off + 2);
        off += 4;

        for (int i = 0; i< count; i++) {
            String module = cr.readModule(off, buf);
            int hashLength = cr.readUnsignedShort(off + 2);
            off += 4;

            byte[] hash = new byte[hashLength];
            for (int j = 0; j < hashLength; j++) {
                hash[j] = (byte) (cr.readByte(off + j) & 0xff);
            }
            off += hashLength;

            mv.visitModuleHash(module, hash);
        }
        return attr;
    }
    
    @Override
    protected ByteVector write(ClassWriter cw, byte[] code, int len,
            int maxStack, int maxLocals) {
        ByteVector v = new ByteVector();
        int index = cw.newUTF8(algorithm);
        v.putShort(index);

        int count = (modules == null)? 0: modules.size();
        v.putShort(count);
        
        for(int i = 0; i < count; i++) {
            String module = modules.get(i);
            v.putShort(cw.newModule(module));
            
            byte[] hash = hashes.get(i);
            v.putShort(hash.length);
            for(byte b: hash) {
                v.putByte(b);
            }
        }
        return v;
    }
}
