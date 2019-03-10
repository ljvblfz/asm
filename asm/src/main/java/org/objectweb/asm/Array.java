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
 * An immutable array.
 *
 * @param <E> the type of the array elements.
 * @author Eric Bruneton
 */
public final class Array<E> {

  private static final Array<Label> EMPTY_LABEL_ARRAY = new Array<>(new Label[0], false);
  private static final Array<Object> EMPTY_OBJECT_ARRAY = new Array<>(new Object[0], false);
  private static final Array<String> EMPTY_STRING_ARRAY = new Array<>(new String[0], false);

  static final Array<Label> PUBLIC_EMPTY_LABEL_ARRAY = new Array<>(new Label[0], true);
  static final Array<Object> PUBLIC_EMPTY_OBJECT_ARRAY = new Array<>(new Object[0], true);
  static final Array<String> PUBLIC_EMPTY_STRING_ARRAY = new Array<>(new String[0], true);

  final E[] elements;

  private final boolean isPublic;

  public Array(final E[] elements) {
    this(elements.clone(), true);
  }

  Array(final E[] elements, final boolean isPublic) {
    this.elements = elements;
    this.isPublic = isPublic;
  }

  static Array<Label> of(final Label[] elements, final boolean isPublic) {
    if (elements == null) {
      return isPublic ? PUBLIC_EMPTY_LABEL_ARRAY : EMPTY_LABEL_ARRAY;
    }
    return new Array<>(elements, isPublic);
  }

  static Array<Object> of(final Object[] elements, final boolean isPublic) {
    if (elements == null) {
      return isPublic ? PUBLIC_EMPTY_OBJECT_ARRAY : EMPTY_OBJECT_ARRAY;
    }
    return new Array<>(elements, isPublic);
  }

  static Array<String> of(final String[] elements, final boolean isPublic) {
    if (elements == null) {
      return isPublic ? PUBLIC_EMPTY_STRING_ARRAY : EMPTY_STRING_ARRAY;
    }
    return new Array<>(elements, isPublic);
  }

  public static Array<Label> of(final Label element1) {
    return new Array<>(new Label[] {element1}, true);
  }

  public static Array<Label> of(final Label element1, final Label element2) {
    return new Array<>(new Label[] {element1, element2}, true);
  }

  public static Array<Label> of(final Label element1, final Label element2, final Label element3) {
    return new Array<>(new Label[] {element1, element2, element3}, true);
  }

  public static Array<Label> of(final Label... elements) {
    return new Array<>(elements.clone(), true);
  }

  public static Array<Object> of(final Object element1) {
    return new Array<>(new Object[] {element1}, true);
  }

  public static Array<Object> of(final Object element1, final Object element2) {
    return new Array<>(new Object[] {element1, element2}, true);
  }

  public static Array<Object> of(
      final Object element1, final Object element2, final Object element3) {
    return new Array<>(new Object[] {element1, element2, element3}, true);
  }

  public static Array<Object> of(final Object... elements) {
    return new Array<>(elements.clone(), true);
  }

  public static Array<String> of(final String element1) {
    return new Array<>(new String[] {element1}, true);
  }

  public static Array<String> of(final String element1, final String element2) {
    return new Array<>(new String[] {element1, element2}, true);
  }

  public static Array<String> of(
      final String element1, final String element2, final String element3) {
    return new Array<>(new String[] {element1, element2, element3}, true);
  }

  public static Array<String> of(final String... elements) {
    return new Array<>(elements.clone(), true);
  }

  public static Array.Builder<Label> newLabels(final int length) {
    return new Array.Builder<>(new Label[length], true);
  }

  public static Array.Builder<Object> newObjects(final int length) {
    return new Array.Builder<>(new Object[length], true);
  }

  public static Array.Builder<String> newStrings(final int length) {
    return new Array.Builder<>(new String[length], true);
  }

  public boolean isPublic() {
    return isPublic;
  }

  Array<E> toPublic() {
    return isPublic ? this : new Array<>(elements, true);
  }

  public int size() {
    return elements.length;
  }

  public E get(final int index) {
    return elements[index];
  }

  public E[] toArray() {
    return elements.length == 0 ? elements : elements.clone();
  }

  public Builder<E> toBuilder() {
    return new Builder<>(toArray(), isPublic);
  }

  /**
   * An immutable array builder.
   *
   * @param <E> the type of the array elements.
   * @author Eric Bruneton
   */
  public static final class Builder<E> {

    private E[] elements;

    private final boolean isPublic;

    Builder(final E[] elements, final boolean isPublic) {
      this.elements = elements;
      this.isPublic = isPublic;
    }

    public Builder<E> set(final int index, final E element) {
      elements[index] = element;
      return this;
    }

    /**
     * Returns an {@link Array} with the content of this builder. The builder must then no longer be
     * used (or NullPointerException will occur).
     *
     * @return an {@link Array} with the content of this builder
     */
    public Array<E> build() {
      Array<E> array = new Array<>(elements, isPublic);
      elements = null;
      return array;
    }
  }
}
