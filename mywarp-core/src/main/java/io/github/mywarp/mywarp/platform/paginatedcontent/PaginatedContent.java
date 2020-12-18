/*
 * Copyright (C) 2011 - 2020, MyWarp team and contributors
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

package io.github.mywarp.mywarp.platform.paginatedcontent;

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.util.Message;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Paginated content that can be displayed page per page.
 *
 * <p>Instances can be created using a {@link PaginatedContent.Builder}.</p>
 */
public interface PaginatedContent {

  /**
   * Gets the contents of given page. The returned Message includes the header (if set), the note (if set) and the
   * appropriate number of entries. If there are no entries or the given page does not exist, an exception is thrown.
   *
   * @param page the number of the page to display
   * @return the contents of the given page
   * @throws NoResultsException   if there are no entries to display
   * @throws UnknownPageException if the given page does not exist
   */
  Message getPage(int page) throws NoResultsException, UnknownPageException;

  /**
   * Displays the contents of the given page to the given Actor. If the given page does not exist, or there are no
   * results to display, an appropriate error messages will be displayed instead.
   *
   * @param actor the Actor
   * @param page  the number of the page to display
   */
  default void display(Actor actor, int page) {
    try {
      actor.sendMessage(getPage(page));
    } catch (NoResultsException | UnknownPageException e) {
      actor.sendError(e);
    }
  }

  /**
   * A builder for {@link PaginatedContent}.
   */
  interface Builder {

    /**
     * Sets the heading. The heading will be displayed on the top of each page, followed by the number of the current
     * page.
     *
     * <p>The heading should be as short as possible so that its contents can be displayed within a single line.</p>
     *
     * @param heading the note
     * @return this Builder for chaining
     */
    Builder withHeading(String heading);

    /**
     * Sets the note. The note will be displayed on each page, directly under the heading but before the paginated
     * content.
     *
     * <p>The note should be as short as possible so that its contents can be displayed within a single line.</p>
     *
     * @param note the note
     * @return this Builder for chaining
     */
    Builder withNote(String note);

    /**
     * Sets how many entries should be displayed on each page.
     *
     * @param number the number of entries to display on each page
     * @return this Builder for chaining
     */
    Builder withNoPerPage(int number);

    /**
     * Creates a PaginatedContent instance from the given List of Messages. Each Message will be treated as a single
     * entry, displayed on an individual line.
     *
     * <p>To paginate a collection of certain objects, they should be sorted using {@link Stream#sorted()} and
     * converted to {@link Message} using using {@link Stream#map(Function)} before beeing handed over to this
     * method.</p>
     *
     * @param toDisplay a list of messages to display
     * @return a PaginatedContent instance
     */
    PaginatedContent build(List<Message> toDisplay);

  }
}
