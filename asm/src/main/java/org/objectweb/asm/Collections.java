package org.objectweb.asm;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods to convert between {@link Array} and {@link ArrayList}.
 *
 * @author Eric Bruneton
 */
public final class Collections {

  private Collections() {}

  public static Array<Label> toLabelArray(final List<Label> labels) {
    return Array.of(labels == null ? null : labels.toArray(new Label[0]), true);
  }

  public static Array<Object> toObjectArray(final List<Object> objects) {
    return Array.of(objects == null ? null : objects.toArray(new Object[0]), true);
  }

  public static Array<String> toStringArray(final List<String> strings) {
    return Array.of(strings == null ? null : strings.toArray(new String[0]), true);
  }

  /**
   * Returns a mutable {@link ArrayList} with the content of the given array.
   *
   * @param array an array
   * @return a mutable {@link ArrayList} with the content of the given array.
   */
  public static <E> List<E> toArrayList(final Array<E> array) {
    E[] elements = array.elements;
    ArrayList<E> list = new ArrayList<>(elements.length);
    for (E value : elements) {
      list.add(value);
    }
    return list;
  }
}
