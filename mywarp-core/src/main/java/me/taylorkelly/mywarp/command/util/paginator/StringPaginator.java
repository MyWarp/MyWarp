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

package me.taylorkelly.mywarp.command.util.paginator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import me.taylorkelly.mywarp.platform.Actor;
import me.taylorkelly.mywarp.util.Message;
import me.taylorkelly.mywarp.util.i18n.DynamicMessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Paginates results.
 *
 * @param <E> the type of elements that should be paginated
 */
public class StringPaginator<E> {

  private static final DynamicMessages msg = new DynamicMessages("me.taylorkelly.mywarp.lang.Paginator");


  private final String header;
  private final List<? extends E> elements;

  @SuppressWarnings("unchecked")
  private Function<E, Message> mapping = new Function<E, Message>() {
    @Override
    public Message apply(@Nullable E input) {
      return Message.builder().append(String.valueOf(input)).build();
    }
  };
  private List<String> notes = new ArrayList<String>();
  private int entriesPerPage = 9;

  private StringPaginator(String header, List<? extends E> elements) {
    this.header = header;
    this.elements = elements;
  }

  /**
   * Creates a Paginator of the given elements while using the given header.
   *
   * @param <E>      the type of elements that should be paginated
   * @param header   the header to display on top
   * @param elements the elements to display
   * @return a Paginator
   */
  public static <E> StringPaginator<E> of(String header, E... elements) {
    return new StringPaginator<E>(header, Lists.newArrayList(elements));
  }

  /**
   * Creates a Paginator of the given elements while using the given header.
   *
   * @param <E>      the type of elements that should be paginated
   * @param header   the header to display on top
   * @param elements the elements to display
   * @return a Paginator
   */
  public static <E> StringPaginator<E> of(String header, Iterable<? extends E> elements) {
    return new StringPaginator<E>(header, Lists.newArrayList(elements));
  }

  /**
   * Creates a Paginator of the given elements while using the given header.
   *
   * @param <E>      the type of elements that should be paginated
   * @param header   the header to display on top
   * @param elements the elements to display
   * @return a Paginator
   */
  public static <E> StringPaginator<E> of(String header, List<? extends E> elements) {
    return new StringPaginator<E>(header, elements);
  }

  /**
   * Adds a note-line. Notes will be displayed on each page, directly under the header but before the paginated content.
   * Do not add to many notes or the content itself becomes unreadable!
   *
   * @param note the note
   * @return this Paginator for chaining
   */
  public StringPaginator<E> withNote(String note) {
    notes.add(note);
    return this;
  }

  /**
   * Sets how many entries should be displayed on each page. The number must be higher than the amount of note lines.
   *
   * @param entriesPerPage the number of entries to display on each page
   * @return this Paginator for chaining
   */
  public StringPaginator<E> withEntriesPerPage(int entriesPerPage) {
    this.entriesPerPage = entriesPerPage;
    return this;
  }

  /**
   * Sets the mapping function that outputs the string that is shown in the final listing.
   *
   * @param mapping the mapping function
   * @return this Paginator for chaining
   */
  public StringPaginator<E> withMapping(Function<E, Message> mapping) {
    this.mapping = mapping;
    return this;
  }

  /**
   * Paginates the elements.
   *
   * @return the paginated elements
   */
  public PaginatedResults paginate() {
    return new PaginatedResults(this);
  }

  /**
   * Results that are paginated.
   */
  public class PaginatedResults {

    private final String header;
    private final List<String> notes;
    private final List<List<Message>> pages;

    private PaginatedResults(StringPaginator<E> paginator) {
      this.header = paginator.header;
      this.notes = paginator.notes;
      this.pages =
              Lists.partition(Lists.transform(paginator.elements, paginator.mapping),
                      paginator.entriesPerPage - notes.size());
    }

    /**
     * Gets the given page as List. Each entry is one single line.
     *
     * @param page the page number
     * @return the given page as List
     * @throws NoResultsException   if there are no results that could be paginated
     * @throws UnknownPageException if there are results that could be paginated, but no page with the given number
     *                              exists
     */
    public Message getPage(int page) throws NoResultsException, UnknownPageException {
      if (pages.isEmpty()) {
        throw new NoResultsException();
      }
      if (page < 1 || page > pages.size()) {
        throw new UnknownPageException(pages.size());
      }

      Message.Builder builder = Message.builder();

      builder.append(Message.Style.HEADLINE_1);
      builder.append(header);
      builder.append(" - ");
      builder.append(msg.getString("page"));
      builder.append(" ");
      builder.append(page);
      builder.append("/");
      builder.append(pages.size()); //max page number
      builder.appendNewLine();

      for (String note : notes) {
        builder.append(Message.Style.INFO);
        builder.append(note);
        builder.appendNewLine();
      }

      for (Iterator<Message> iterator = pages.get(page - 1).iterator(); iterator.hasNext(); ) {
        Message entry = iterator.next();
        builder.append(Message.Style.VALUE);
        builder.appendAndAdjustStyle(entry);

        if (iterator.hasNext()) {
          builder.appendNewLine();
        }
      }

      return builder.build();
    }

    /**
     * Displays the given page to the given Actor. This method will catch checked exceptions and send the appropriate
     * error message instead.
     *
     * @param actor the Actor
     * @param page  the page number
     */
    public void display(Actor actor, int page) {
      try {
        actor.sendMessage(getPage(page));
      } catch (NoResultsException e) {
        actor.sendError(msg.getString("no-results"));
      } catch (UnknownPageException e) {
        actor.sendError(msg.getString("unknown-page", e.getHighestPage()));
      }
    }
  }

}
