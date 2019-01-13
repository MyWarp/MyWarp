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

package io.github.mywarp.mywarp.bukkit.util.conversation;

import io.github.mywarp.mywarp.bukkit.BukkitPlayer;
import io.github.mywarp.mywarp.bukkit.MyWarpPlugin;
import io.github.mywarp.mywarp.bukkit.util.BukkitMessageInterpreter;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Creates and handles conversations with players who want to change a Warp's welcome-message.
 */
public class WelcomeEditorFactory {

  private static final String ESCAPE_SEQUENCE = "#cancel";
  private static final String REMOVE_SEQUENCE = "#none";

  private static final DynamicMessages msg = new DynamicMessages(MyWarpPlugin.CONVERSATION_RESOURCE_BUNDLE_NAME);

  private final ConversationFactory factory;

  /**
   * Creates an instance.
   *
   * @param factory the ConversationFactory to build conversations with
   */
  public WelcomeEditorFactory(ConversationFactory factory) {
    this.factory = factory.withEscapeSequence(ESCAPE_SEQUENCE).withFirstPrompt(new MessageInputPrompt());
  }

  /**
   * Creates an welcome-editor for the given player to change the welcome-message of the given Warp.
   *
   * @param forWhom the LocalPlayer
   * @param warp    the Warp
   */
  public void create(BukkitPlayer forWhom, Warp warp) {
    Map<Object, Object> initialSessionData = new HashMap<Object, Object>();
    initialSessionData.put(Locale.class, forWhom.getLocale());
    initialSessionData.put(Warp.class, warp);

    factory.withInitialSessionData(initialSessionData).buildConversation(forWhom.getWrapped()).begin();
  }

  /**
   * Asks for a new welcome message and validates the input accordingly.
   */
  private class MessageInputPrompt extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
      input = input.trim();
      if (input.equalsIgnoreCase(REMOVE_SEQUENCE)) {
        input = "";
      }
      context.setSessionData(String.class, input);
      return new ChangePrompt();
    }

    @Override
    public String getPromptText(ConversationContext context) {
      Warp warp = (Warp) context.getSessionData(Warp.class);
      Locale locale = (Locale) context.getSessionData(Locale.class);

      Message
          message =
          Message.builder().append(
              msg.getString("welcome-message.enter-message", locale, warp.getName(), REMOVE_SEQUENCE, ESCAPE_SEQUENCE,
                            MyWarpPlugin.CONVERSATION_TIMEOUT)).build();

      return BukkitMessageInterpreter.interpret(message);
    }
  }

  /**
   * Changes the welcome message and ends the conversation afterwards.
   */
  private class ChangePrompt extends MessagePrompt {

    @Override
    public String getPromptText(ConversationContext context) {
      Warp warp = (Warp) context.getSessionData(Warp.class);
      String welcomeMessage = (String) context.getSessionData(String.class);
      warp.setWelcomeMessage(welcomeMessage);

      Locale locale = (Locale) context.getSessionData(Locale.class);

      Message message;

      if (welcomeMessage.isEmpty()) {
        message =
            Message.builder().append(msg.getString("welcome-message.removed-successful", locale, warp.getName()))
                .build();
      } else {
        message =
            Message.builder().append(msg.getString("welcome-message.changed-successful", locale, warp.getName()))
                .appendNewLine().append(Message.Style.INFO).append(welcomeMessage).build();
      }

      return BukkitMessageInterpreter.interpret(message);
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
      return Prompt.END_OF_CONVERSATION;
    }

  }
}
