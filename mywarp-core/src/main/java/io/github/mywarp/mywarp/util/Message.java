/*
 * Copyright (C) 2011 - 2017, MyWarp team and contributors
 *
 * This file is part of MyWarp.
 *
 * MyWarp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyWarp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyWarp. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.mywarp.mywarp.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;

import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.warp.Warp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * An immutable chat message.
 *
 * <p>A message consists of a list of non-null objects. To form a human readable message, these must be interpreted by
 * the client. While there is no guarantee about the type of objects inside the message, the following ones should be
 * expected: <ul> <li>{@link Warp}</li> <li>{@link LocalPlayer}</li> <li>{@link
 * LocalWorld}</li> <li>{@link Style}</li> <li>{@link CharSequence}</li> <li>{@link Number}</li> </ul> </p>
 *
 * <p>Use a {@link Builder} to create instances.</p>
 */
public class Message {

  /**
   * Gets a Builder to build a Message instance.
   *
   * @return a builder
   */
  public static Builder builder() {
    return new Builder();
  }

  private final ImmutableList<Object> elements;

  private Message(List<Object> elements) {
    this.elements = ImmutableList.copyOf(elements);
  }

  /**
   * Gets all elements of this message.
   *
   * @return an immutable list of all elements
   */
  public ImmutableList<Object> getElements() {
    return elements;
  }

  /**
   * Builds {@link Message} instances.
   */
  public static class Builder implements Appendable {

    private final List<Object> elements = new ArrayList<Object>();

    private Builder() {
      append(Style.DEFAULT);
    }

    @Override
    public Builder append(char c) {
      return append(Character.valueOf(c));
    }

    @Override
    public Builder append(CharSequence charSequence) {
      return append((Object) charSequence);
    }

    @Override
    public Builder append(CharSequence charSequence, int start, int end) {
      checkNotNull(charSequence);

      return append((Object) charSequence.subSequence(start, end));
    }

    /**
     * Appends the given {@code object}.
     *
     * <p>{@code null} is accepted and will append a String equal to "{@code null}".</p>
     *
     * @param object the object
     * @return this Builder
     */
    public Builder append(@Nullable Object object) {
      if (object == null) {
        object = "null";
      }
      elements.add(object);
      return this;
    }

    /**
     * Appends the elements of the given Message and keeps its styling.
     *
     * <p>The appended message may ignore the currently set styling of this Builder and the style of elements appended
     * afterwards may be entirely different.</p>
     *
     * @param message the message
     * @return this Builder
     * @see #appendAndAdjustStyle(Message)
     */
    public Builder appendAndKeepStyle(Message message) {
      checkNotNull(message);
      elements.addAll(message.elements);
      return this;
    }

    /**
     * Appends the elements of the given Message and adjusts its styling to match the active one on this Builder.
     *
     * <p>All styles configured for the given message will be entirely ignored.</p>
     *
     * @param message the message
     * @return this Builder
     * @see #appendAndKeepStyle(Message)
     */
    public Builder appendAndAdjustStyle(Message message) {
      checkNotNull(message);

      for (Object element : message.elements) {
        if (element instanceof Style) {
          continue;
        }
        elements.add(element);
      }
      return this;
    }

    /**
     * Appends the elements of the given Iterable and seperates each with "{@code , }".
     *
     * @param elements the elements to add
     * @return this Builder
     */
    public Builder appendWithSeparators(Iterable<?> elements) {
      return appendWithSeparators(elements, ", ");
    }

    /**
     * Appends the elements of the given Iterable and seperates each with the given {@code separator}.
     *
     * @param elements  the elements to add
     * @param separator the separator
     * @return this Builder
     */
    public Builder appendWithSeparators(Iterable<?> elements, String separator) {
      checkNotNull(elements);
      checkNotNull(separator);

      for (Iterator<?> iterator = elements.iterator(); iterator.hasNext(); ) {
        append(iterator.next());

        if (iterator.hasNext()) {
          append(separator);
        }

      }
      return this;
    }

    /**
     * Appends a new line.
     *
     * @return this Builder
     */
    public Builder appendNewLine() {
      // Apparently MC requires '\n' only, regardless of the OS it is running on,
      // see issues #157 and #61.
      return append("\n");
    }

    /**
     * Builds a Message form the contents of this Builder.
     *
     * @return a build message
     */
    public Message build() {
      return new Message(elements);
    }

  }

  /**
   * A combination of color and emphasis to be used in a certain context.
   */
  public enum Style {
    /**
     * The default style: non emphasised, aquamarine text.
     */
    DEFAULT, /**
     * Indicates an error: non emphasised, light red text.
     */
    ERROR, /**
     * Indicates an additional information: non emphasised, grey text.
     *
     * <p>An additional information is a message that contains information the user might not have asked for, but that
     * are still important for him.</p>
     */
    INFO, /**
     * Indicates a headline on the first level: bold, gold text.
     *
     * <p>Similar to {@code <h1>} in HTML.</p>
     */
    HEADLINE_1, /**
     * Indicates a headline on the second level: non emphasised, white text.
     *
     * <p>Similar to {@code <h2>} in HTML, should only be used if {@link #HEADLINE_1} has already been applied.</p>
     */
    HEADLINE_2, /**
     * Indicates a key of a key - value listing: non emphasised, grey text.
     */
    KEY, /**
     * Indicates the value of a key - value listing: non emphasised, white text.
     */
    VALUE
  }
}
