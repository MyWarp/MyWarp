/*
 * Copyright (C) 2011 - 2018, MyWarp team and contributors
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
import io.github.mywarp.mywarp.command.util.printer.InfoPrinter;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.i18n.LocaleManager;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Creates and handles conversations with players who are asked to accept a Warp.
 */
public class AcceptancePromptFactory {

  private static final DynamicMessages msg = new DynamicMessages(MyWarpPlugin.CONVERSATION_RESOURCE_BUNDLE_NAME);

  private final AuthorizationResolver authorizationResolver;
  private final ConversationFactory factory;
  private final Game game;
  private final PlayerNameResolver resolver;
  private final MyWarpPlugin plugin;

  /**
   * Creates an instance.
   *
   * @param factory               the ConversationFactory to build conversations with
   * @param authorizationResolver the AuthorizationResolver to resolve warp authorizations
   * @param game                  the running game
   * @param resolver              the resolver to be used when resolving player names
   * @param plugin                the plugin instance
   */
  public AcceptancePromptFactory(ConversationFactory factory, AuthorizationResolver authorizationResolver, Game game,
                                 PlayerNameResolver resolver, MyWarpPlugin plugin) {
    this.authorizationResolver = authorizationResolver;
    this.game = game;
    this.resolver = resolver;
    this.plugin = plugin;
    this.factory = factory.withFirstPrompt(new QuestionPrompt());
  }

  /**
   * Creates a acceptance-prompt for the given player to accept the given Warp.
   *
   * @param forWhom   the player for whom the conversation should be created
   * @param warp      the Warp to accept
   * @param initiator the Actor who initiated the action
   */
  public void create(BukkitPlayer forWhom, Warp warp, Actor initiator) {
    Map<Object, Object> initialSessionData = new HashMap<Object, Object>();
    initialSessionData.put(Locale.class, forWhom.getLocale());
    initialSessionData.put(Warp.class, warp);
    initialSessionData.put(String.class, initiator.getName());

    factory.withInitialSessionData(initialSessionData).buildConversation(forWhom.getWrapped()).begin();
  }

  /**
   * Asks for acceptance of a warp ownership and handles answers.
   */
  private class QuestionPrompt extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
      LocaleManager.setLocale((Locale) context.getSessionData(Locale.class));
      if (input.equalsIgnoreCase(infoSequence())) {
        return new InfoPrompt();
      }
      if (input.equalsIgnoreCase(acceptanceSequence())) {
        return new AcceptPrompt();
      }
      if (input.equalsIgnoreCase(declineSequence())) {
        return new DeclinePrompt();
      }

      return new QuestionPrompt();
    }

    @Override
    public String getPromptText(ConversationContext context) {
      Warp warp = (Warp) context.getSessionData(Warp.class);
      String initiatorName = (String) context.getSessionData(String.class);

      LocaleManager.setLocale((Locale) context.getSessionData(Locale.class));

      Message
          message =
          Message
              .of(msg.getString("warp-acceptance.want-to-accept", initiatorName, warp.getName(), acceptanceSequence(),
                                declineSequence(), infoSequence(), MyWarpPlugin.CONVERSATION_TIMEOUT));
      return BukkitMessageInterpreter.interpret(message);
    }

    private String acceptanceSequence() {
      return msg.getString("warp-acceptance.acceptance-sequence");
    }

    private String declineSequence() {
      return msg.getString("warp-acceptance.decline-sequence");
    }

    private String infoSequence() {
      return msg.getString("warp-acceptance.info-sequence");
    }
  }

  /**
   * Displays information about the warp and calls {@link QuestionPrompt} afterwards.
   */
  private class InfoPrompt extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
      return new QuestionPrompt();
    }

    @Override
    public String getPromptText(ConversationContext context) {
      Warp warp = (Warp) context.getSessionData(Warp.class);

      LocaleManager.setLocale((Locale) context.getSessionData(Locale.class));
      return BukkitMessageInterpreter.interpret(new InfoPrinter(warp, authorizationResolver, game, resolver)
                                                    .getText(plugin.wrap((Player) context.getForWhom())));
    }
  }

  /**
   * Handles the acceptance of the ownership change and ends the conversation.
   */
  private class AcceptPrompt extends MessagePrompt {

    @Override
    public String getPromptText(ConversationContext context) {
      Warp warp = (Warp) context.getSessionData(Warp.class);
      warp.setCreator(((Player) context.getForWhom()).getUniqueId());

      LocaleManager.setLocale((Locale) context.getSessionData(Locale.class));
      Message message = Message.of(msg.getString("warp-acceptance.accepted-successful", warp.getName()));
      return BukkitMessageInterpreter.interpret(message);
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
      return Prompt.END_OF_CONVERSATION;
    }

  }

  /**
   * Handles the decline of the ownership transfer and ends the conversation.
   */
  private class DeclinePrompt extends MessagePrompt {

    @Override
    public String getPromptText(ConversationContext context) {
      LocaleManager.setLocale((Locale) context.getSessionData(Locale.class));
      Message message = Message.of(msg.getString("warp-acceptance.declined-successful"));
      return BukkitMessageInterpreter.interpret(message);
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext context) {
      return Prompt.END_OF_CONVERSATION;
    }

  }
}
