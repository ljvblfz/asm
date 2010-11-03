/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2005 INRIA, France Telecom
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

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A node that represents an LDC instruction of a constant method handle.
 * 
 * @author Remi Forax
 */
public class CstMHandleInsnNode extends AbstractInsnNode {

    /**
     * Constant method handle tag, should be a value among
     * REF_getField, REF_getStatic, REF_putField, REF_putStatic,
     * REF_invokeVirtual, REF_invokeStatic, REF_invokeSpecial,
     * REF_newInvokeSpecial and REF_invokeInterface.
     */
    public int tag;
    
    /**
     * Constant method handle owner internal name.
     */
    public String owner;
    
    /**
     * Constant method handle name.
     * This name is a field name or a method name depending on
     * the tag value.
     */
    public String name;
    
    /**
     * Constant method handle descriptor.
     * This name is a field descriptor or a method descriptor depending on
     * the tag value. 
     */
    public String desc;

    
    /**
     * Constructs a new {@link CstMHandleInsnNode}.
     * 
     * @param tag 
     *        one among REF_getField, REF_getStatic, REF_putField, REF_putStatic,
     *        REF_invokeVirtual, REF_invokeStatic, REF_invokeSpecial,
     *        REF_newInvokeSpecial and REF_invokeInterface.
     * @param owner internal name of the field reference/method reference. 
     * @param name name of the field/method.
     * @param desc field/method descriptor.
     */
    public CstMHandleInsnNode(final int tag, final String owner, final String name, final String desc) {
        super(Opcodes.LDC);
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    public int getType() {
        return CST_MHANDLE_INSN;
    }

    public void accept(final MethodVisitor mv) {
        mv.visitCstMHandleInsn(tag, owner, name, desc);
    }

    public AbstractInsnNode clone(final Map labels) {
        return new CstMHandleInsnNode(tag, owner, name, desc);
    }
}
