/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

import static java.util.Objects.requireNonNull;

import com.google.common.collect.Lists;

import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;

import java.util.Iterator;
import java.util.List;

/**
 * A simple PaginatedContent implementation.
 */
public class SimplePaginatedContent implements PaginatedContent {

  static final String RESOURCE_BUNDLE_NAME = "io.github.mywarp.mywarp.lang.Paginator";
  private static final DynamicMessages msg = new DynamicMessages(RESOURCE_BUNDLE_NAME);

  private final String heading;
  private final String note;
  private final List<List<Message>> pages;

  private SimplePaginatedContent(String heading, String note, int numPerPage, List<Message> toDisplay) {
    this.heading = heading;
    this.note = note;
    this.pages = Lists.partition(toDisplay, showPerPage(numPerPage));
  }

  /**
   * Creates a builder for SimplePaginatedContent instances.
   *
   * <p>This method should not be called directly; instead use {@link Platform#createPaginatedContentBuilder()} as
   * individual Platforms may implement their own custom pagination service.</p>
   *
   * @return a new builder
   */
  public static SimplePaginatedContent.Builder builder() {
    return new SimplePaginatedContent.Builder();
  }

  @Override
  public Message getPage(int page) throws NoResultsException, UnknownPageException {
    if (pages.isEmpty()) {
      throw new NoResultsException();
    }
    if (page < 1 || page > pages.size()) {
      throw new UnknownPageException(pages.size());
    }

    Message.Builder builder = Message.builder();

    builder.append(Message.Style.HEADLINE_1);
    builder.append(heading);
    builder.append(" - ");
    builder.append(msg.getString("page"));
    builder.append(" ");
    builder.append(page);
    builder.append("/");
    builder.append(pages.size()); //max page number
    builder.appendNewLine();

    if (!note.isEmpty()) {
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

  private int showPerPage(int numPerPage) {
    if (note.isEmpty()) {
      return numPerPage;
    }
    return numPerPage - 1;
  }

  static class Builder implements PaginatedContent.Builder {

    private String heading = "";
    private String note = "";
    private int numPerPage = 9;

    @Override
    public PaginatedContent.Builder withHeading(String heading) {
      this.heading = requireNonNull(heading);
      return this;
    }

    @Override
    public PaginatedContent.Builder withNote(String note) {
      this.note = requireNonNull(note);
      return this;
    }

    @Override
    public PaginatedContent.Builder withNoPerPage(int numPerPage) {
      this.numPerPage = numPerPage;
      return null;
    }

    @Override
    public PaginatedContent build(List<Message> toDisplay) {
      return new SimplePaginatedContent(heading, note, numPerPage, toDisplay);
    }
  }
}
