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
import org.objectweb.asm.Opcodes;

/**
 * A visitor for the OpenJDK specific attributes {@link ModuleTargetAttribute},
 * {@link ModuleHashesAttribute} and {@link ModuleResolutionAttribute}.
 * 
 * This visitor inherits from {@link Attribute} but should not be used as
 * an Attribute, it allows subclasses to be Attributes and to inherits from
 * this visitor.
 * 
 * @author Remi Forax
 */
public class ModuleAttributeVisitor extends Attribute {
    /**
     * Resolution state of a module meaning the module is marked as deprecated.
     */
    public static final int RESOLUTION_WARN_DEPRECATED = 2;
    
    /**
     * Resolution state of a module meaning the module is marked as deprecated
     * and will be removed in a future release.
     */
    public static final int RESOLUTION_WARN_DEPRECATED_FOR_REMOVAL = 4;
    
    /**
     * Resolution state of a module meaning the module is not yet standardized,
     * so in incubating mode.
     */
    public static final int RESOLUTION_WARN_INCUBATING = 8;
    
    /**
     * The ASM API version implemented by this visitor. The value of this field
     * must be {@link Opcodes#ASM6}.
     */
    protected final int api;
    
    /**
     * The module visitor to which this visitor must delegate method calls. May
     * be null.
     */
    protected ModuleAttributeVisitor mv;
    
    ModuleAttributeVisitor(final int api, final String type) {
        super(type);
        if (api != Opcodes.ASM6) {
            throw new IllegalArgumentException();
        }
        this.api = api;
    }

    /**
     * Constructs a new {@link ModuleAttributeVisitor}.
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be {@link Opcodes#ASM6}.
     */
    public ModuleAttributeVisitor(final int api) {
        this(api, (String)null);
    }

    /**
     * Constructs a new {@link ModuleAttributeVisitor}.
     * 
     * @param api
     *            the ASM API version implemented by this visitor. Must be {@link Opcodes#ASM6}.
     * @param mv
     *            the attribute visitor to which this visitor must delegate method
     *            calls. May be null.
     */
    public ModuleAttributeVisitor(final int api, final ModuleAttributeVisitor mv) {
       this(api, (String)null);
       this.mv = mv;
    }
    
    /**
     * Visit the platform name.
     * @param platform the platform name.
     */
    public void visitPlatform(final String platform) {
        if (mv != null) {
            mv.visitPlatform(platform);
        }
    }
    
    /**
     * Visit the name of the algorithm used for hashing.
     * @param algorithm the name of the algorithm used for hashing.
     */
    public void visitHashAlgorithm(final String algorithm) {
        if (mv != null) {
            mv.visitHashAlgorithm(algorithm);
        }
    }
    
    /**
     * Visit the hash of a module.
     * @param module the module name.
     * @param hash the hash.
     * 
     * @see #visitHashAlgorithm(String)
     */
    public void visitModuleHash(final String module, final byte[] hash) {
        if (mv != null) {
            mv.visitModuleHash(module, hash);
        }
    }
    
    /**
     * The resolution state of a module.
     * @param resolution the resolution state of the module among
     *   {@linkplain #RESOLUTION_WARN_DEPRECATED},
     *   {@linkplain #RESOLUTION_WARN_DEPRECATED_FOR_REMOVAL} 
     *   and {@linkplain #RESOLUTION_WARN_INCUBATING}.
     */
    public void visitResolution(final int resolution) {
        if (mv != null) {
            mv.visitResolution(resolution);
        }
    }
}
