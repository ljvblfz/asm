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
package org.objectweb.asm.commons;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A {@link MethodAdapter} that can be used to approximate method size.
 * 
 * @author Eugene Kuleshov
 */
public class CodeSizeEvaluator extends MethodAdapter implements Opcodes {

    private int minSize;

    private int maxSize;

    public CodeSizeEvaluator(final MethodVisitor mv) {
        super(mv);
    }

    public int getMinSize() {
        return this.minSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    /**
     * u1 lrem opcode = 0x71 (113)
     */
    public void visitInsn(final int opcode) {
        minSize += 1;
        maxSize += 1;
        if (mv != null) {
            mv.visitInsn(opcode);
        }
    }

    /**
     * u1 sipush opcode = 0x11 (17) s2 <n>
     * 
     * u1 bipush opcode = 0x10 (16) s1 <n>
     * 
     * u1 newarray opcode = 0xBC (188) u1 array-type (see below)
     */
    public void visitIntInsn(final int opcode, final int operand) {
        if (opcode == SIPUSH) {
            minSize += 3;
            maxSize += 3;
        } else { // BIPUSH and NEWARRAY:
            minSize += 2;
            maxSize += 2;
        }
        if (mv != null) {
            mv.visitIntInsn(opcode, operand);
        }
    }

    /**
     * u1 iload_0 opcode = 0x1A (26) u1 iload_1 opcode = 0x1B (27) u1 iload_2
     * opcode = 0x1C (28) u1 iload_3 opcode = 0x1D (29)
     * 
     * u1 iload opcode = 0x15 (21) u1 <varnum>
     * 
     * Wide format for this instruction, supports access to all local variables
     * from 0 to 65535: u1 wide opcode = 0xC4 (196) u1 iload opcode = 0x15 (21)
     * u2 <varnum>
     */
    public void visitVarInsn(final int opcode, final int var) {
        // TODO verify logic in MethodWriter
        if (var >= 0 && var <= 3) {
            minSize += 1;
            maxSize += 1;
        } else if (var <= 255) {
            minSize += 2;
            maxSize += 2;
        } else {
            minSize += 4;
            maxSize += 4;
        }
        if (mv != null) {
            mv.visitVarInsn(opcode, var);
        }
    }

    /**
     * u1 new opcode = 0xBB (187) u2 index
     * 
     * ...
     */
    public void visitTypeInsn(final int opcode, final String desc) {
        minSize += 1 + 2;
        maxSize += 1 + 2;
        if (mv != null) {
            mv.visitTypeInsn(opcode, desc);
        }
    }

    /**
     * u1 getstatic opcode = 0xB2 (178) u2 index
     */
    public void visitFieldInsn(
        final int opcode,
        final String owner,
        final String name,
        final String desc)
    {
        minSize += 3;
        maxSize += 3;
        if (mv != null) {
            mv.visitFieldInsn(opcode, owner, name, desc);
        }
    }

    /**
     * u1 invokeinterface opcode = 0xB9 (185) u2 index u1 <n> u1 0
     * 
     * u1 invokespecial opcode = 0xB7 (183) u2 index
     * 
     * u1 invokestatic opcode = 0xB8 (184) u2 index
     * 
     * u1 invokevirtual opcode = 0xB6 (182) u2 index
     * 
     */
    public void visitMethodInsn(
        final int opcode,
        final String owner,
        final String name,
        final String desc)
    {
        if (opcode == INVOKEINTERFACE) {
            minSize += 1 + 2 + 1 + 1;
            maxSize += 1 + 2 + 1 + 1;
        } else {
            minSize += 1 + 2;
            maxSize += 1 + 2;
        }
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    /**
     * u1 ifge opcode = 0x9C (156) s2 branchoffset
     * 
     * u1 goto opcode = 0xA7 (167) s2 branchoffset
     * 
     * u1 goto_w opcode = 0xC8 (200) s4 branchoffset
     */
    public void visitJumpInsn(final int opcode, final Label label) {
        // TODO size for regular IF* instructtion may not be accurate
        minSize += 3;
        if (opcode == GOTO || opcode == JSR) {
            maxSize += 5;
        } else {
            maxSize += 3;
        }
        if (mv != null) {
            mv.visitJumpInsn(opcode, label);
        }
    }

    /**
     * u1 ldc opcode = 0x12 (18) u1 index
     * 
     * u1 ldc2_w opcode = 0x14 (20) u2 index
     * 
     * u1 ldc_w opcode = 0x13 (19) u2 index
     */
    public void visitLdcInsn(final Object cst) {
        if (cst instanceof Long || cst instanceof Double) {
            // LDC2_W
            minSize += 3;
            maxSize += 3;

        } else {
            minSize += 2;
            maxSize += 3; // LDC_W depends on constant pool sizes
        }
        if (mv != null) {
            mv.visitLdcInsn(cst);
        }
    }

    /**
     * u1 iinc opcode = 0x84 (132) u1 <varnum> s1 <n>
     * 
     * Wide format for this instruction, which supports access to all local
     * variables from 0 to 65535, and values of <n> between -32768 and 32767: u1
     * wide opcode = 0xC4 (196) u1 iinc opcode = 0x84 (132) u2 <varnum> s2 <n>
     */
    public void visitIincInsn(final int var, final int increment) {
        if (var <= 255 && increment >= 0 && increment <= 255) {
            minSize += 3;
            maxSize += 3;
        } else {
            minSize += 5;
            maxSize += 5;
        }
        if (mv != null) {
            mv.visitIntInsn(var, increment);
        }
    }

    /**
     * u1 tableswitch opcode = 0xAA (170) - ...0-3 bytes of padding ... s4
     * default_offset s4 <low> s4 <low> + N - 1 s4 offset_1 s4 offset_2 ... ...
     * s4 offset_N
     */
    public void visitTableSwitchInsn(
        final int min,
        final int max,
        final Label dflt,
        final Label[] labels)
    {
        minSize += 1 + 4 + 4 + 4 + (labels.length * 4);
        maxSize += 1 + 4 + 4 + 4 + (labels.length * 4) + 3;
        if (mv != null) {
            mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    /**
     * u1 lookupswitch opcode = 0xAB (171) - ...0-3 bytes of padding ... s4
     * default_offset s4 n s4 key_1 s4 offset_1 s4 key_2 s4 offset_2 ... ... s4
     * key_n s4 offset_n
     */
    public void visitLookupSwitchInsn(
        final Label dflt,
        final int[] keys,
        final Label[] labels)
    {
        minSize += 1 + 4 + 4 + (keys.length * 8);
        maxSize += 1 + 4 + 4 + (keys.length * 8) + 3;
        if (mv != null) {
            mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    /**
     * u1 multianewarray opcode = 0xC5 (197) u2 index u1 <n>
     */
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        minSize += 1 + 2 + 1;
        maxSize += 1 + 2 + 1;
        if (mv != null) {
            mv.visitMultiANewArrayInsn(desc, dims);
        }
    }
}
