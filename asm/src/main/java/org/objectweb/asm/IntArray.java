// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package org.objectweb.asm;

/**
 * An immutable array of integers.
 *
 * @author Eric Bruneton
 */
public final class IntArray {

  public static final IntArray EMPTY = new IntArray(new int[0]);

  final int[] elements;

  IntArray(final int[] elements) { // NOPMD(ArrayIsStoredDirectly) private constructor
    this.elements = elements;
  }

  public static IntArray of(final int element1) {
    return new IntArray(new int[] {element1});
  }

  public static IntArray of(final int element1, final int element2) {
    return new IntArray(new int[] {element1, element2});
  }

  public static IntArray of(final int element1, final int element2, final int element3) {
    return new IntArray(new int[] {element1, element2, element3});
  }

  public static IntArray of(final int... elements) {
    return new IntArray(elements.clone());
  }

  public static IntArray.Builder newBuilder(final int length) {
    return new IntArray.Builder(new int[length]);
  }

  public int size() {
    return elements.length;
  }

  public int get(final int index) {
    return elements[index];
  }

  public int[] toArray() {
    return elements.length == 0 ? elements : elements.clone();
  }

  public Builder toBuilder() {
    return new Builder(toArray());
  }

  /**
   * An immutable array builder.
   *
   * @author Eric Bruneton
   */
  public static final class Builder {

    private int[] elements;

    Builder(final int[] elements) { // NOPMD(ArrayIsStoredDirectly) private constructor
      this.elements = elements;
    }

    public Builder set(final int index, final int element) {
      elements[index] = element;
      return this;
    }

    /**
     * Returns an {@link IntArray} with the content of this builder. The builder must then no longer
     * be used (or NullPointerException will occur).
     *
     * @return an {@link IntArray} with the content of this builder
     */
    public IntArray build() {
      IntArray array = new IntArray(elements);
      elements = null;
      return array;
    }
  }
}
