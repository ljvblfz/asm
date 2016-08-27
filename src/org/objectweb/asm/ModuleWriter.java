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

package org.objectweb.asm;

/**
 * @author Remi Forax
 */
final class ModuleWriter extends ModuleVisitor {
    /**
     * The class writer to which this Module attribute must be added.
     */
    private final ClassWriter cw;
    
    /**
     * size in byte of the Module attribute.
     */
    int size;
    
    /**
     * Number of attributes associated with the current module
     * (Version, ConcealPackages, etc) 
     */
    int attributeCount;
    
    /**
     * Size in bytes of the attributes associated with the current module
     */
    int attributesSize;
    
    /**
     * module version index in the constant pool or 0
     */
    private int version;
    
    /**
     * module main class index in the constant pool or 0
     */
    private int mainClass;
    
    /**
     * module platform target OS name index in the constant pool or 0
     */
    private int osName;
    
    /**
     * module platform target OS architecture index in the constant pool or 0
     */
    private int osArch;
    
    /**
     * module platform target OS version index in the constant pool or 0
     */
    private int osVersion;
    
    /**
     * number of concealed packages
     */
    private int concealedPackageCount;
    
    /**
     * The concealed packages in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in concealedPackageCount
     */
    private ByteVector concealedPackages;
    
    /**
     * number of requires items
     */
    private int requireCount;
    
    /**
     * The requires items in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in requireCount
     */
    private ByteVector requires;
    
    /**
     * number of exports items
     */
    private int exportCount;
    
    /**
     * The exports items in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in exportCount
     */
    private ByteVector exports;
    
    /**
     * number of uses items
     */
    private int useCount;
    
    /**
     * The uses items in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in useCount
     */
    private ByteVector uses;
    
    /**
     * number of provides items
     */
    private int provideCount;
    
    /**
     * The uses provides in bytecode form. This byte vector only contains
     * the items themselves, the number of items is store in provideCount
     */
    private ByteVector provides;
    
    ModuleWriter(final ClassWriter cw) {
        super(Opcodes.ASM6);
        this.cw = cw;
        this.size = 8;
    }
    
    @Override
    public void visitVersion(String version) {
        if (this.version == 0) {  // protect against several calls to visitVersion
            cw.newUTF8("Version");
            attributeCount++;
            attributesSize += 8;    
        }
        this.version = cw.newUTF8(version);
    }
    @Override
    public void visitMainClass(String mainClass) {
        if (this.mainClass == 0) { // protect against several calls to visitMainClass
            cw.newUTF8("MainClass");
            attributeCount++;
            attributesSize += 8;
        }
        this.mainClass = cw.newClass(mainClass);
    }
    @Override
    public void visitTargetPlatform(String osName, String osArch,
            String osVersion) {
        if (osName == null && osArch == null && osVersion == null) {
            return;
        }
        if (this.osName == 0 && this.osArch == 0 && this.osVersion == 0) {
            // protect against several calls to visitTargetPlatform
            cw.newUTF8("TargetPlatform");
            attributeCount++;
            attributesSize += 12;
        }
        if (osName != null) {
            this.osName = cw.newUTF8(osName);
        }
        if (osArch != null) {
            this.osArch = cw.newUTF8(osArch);
        }
        if (osVersion != null) {
            this.osVersion = cw.newUTF8(osVersion);
        }
    }
    @Override
    public void visitConcealedPackage(String packaze) {
        if (concealedPackages == null) {
            cw.newUTF8("ConcealedPackages");
            concealedPackages = new ByteVector();
            attributeCount++;
            attributesSize += 8;
        }
        concealedPackages.putShort(cw.newUTF8(packaze));
        concealedPackageCount++;
        attributesSize += 2;
    }
    
    @Override
    public void visitRequire(String module, int access) {
        if (requires == null) {
            requires = new ByteVector();
        }
        requires.putShort(cw.newUTF8(module)).putShort(access);
        requireCount++;
        size += 4;
    }
    
    @Override
    public void visitExport(String packaze, int access, String... modules) {
        if (exports == null) {
            exports = new ByteVector();
        }
        exports.putShort(cw.newUTF8(packaze)).putShort(access);
        if (modules == null) {
            exports.putShort(0);
            size += 6;
        } else {
            exports.putShort(modules.length);
            for(String to: modules) {
                exports.putShort(cw.newUTF8(to));
            }    
            size += 6 + 2 * modules.length; 
        }
        exportCount++;
    }
    
    @Override
    public void visitUse(String service) {
        if (uses == null) {
            uses = new ByteVector();
        }
        uses.putShort(cw.newClass(service));
        useCount++;
        size += 2;
    }
    
    @Override
    public void visitProvide(String service, String impl) {
        if (provides == null) {
            provides = new ByteVector();
        }
        provides.putShort(cw.newClass(service)).putShort(cw.newClass(impl));
        provideCount++;
        size += 4;
    }
    
    @Override
    public void visitEnd() {
        // empty
    }

    void putAttributes(ByteVector out) {
        if (version != 0) {
            out.putShort(cw.newUTF8("Version")).putInt(2).putShort(version);
        }
        if (mainClass != 0) {
            out.putShort(cw.newUTF8("MainClass")).putInt(2).putShort(mainClass);
        }
        if (osName != 0 || osArch != 0 || osVersion != 0) {
            out.putShort(cw.newUTF8("TargetPlatform"))
               .putInt(6)
               .putShort(osName)
               .putShort(osArch)
               .putShort(osVersion);
        }
        if (concealedPackages != null) {
            out.putShort(cw.newUTF8("ConcealedPackages"))
               .putInt(2 + 2 * concealedPackageCount)
               .putShort(concealedPackageCount)
               .putByteArray(concealedPackages.data, 0, concealedPackages.length);
        }
    }

    void put(ByteVector out) {
        out.putInt(size);
        out.putShort(requireCount);
        if (requires != null) {
            out.putByteArray(requires.data, 0, requires.length);
        }
        out.putShort(exportCount);
        if (exports != null) {
            out.putByteArray(exports.data, 0, exports.length);
        }
        out.putShort(useCount);
        if (uses != null) {
            out.putByteArray(uses.data, 0, uses.length);
        }
        out.putShort(provideCount);
        if (provides != null) {
            out.putByteArray(provides.data, 0, provides.length);
        }
    }    
}
