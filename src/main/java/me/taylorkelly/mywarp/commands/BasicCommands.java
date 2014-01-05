package me.taylorkelly.mywarp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.data.Warp;
import me.taylorkelly.mywarp.data.WarpLimit;
import me.taylorkelly.mywarp.data.WelcomeMessageHandler;
import me.taylorkelly.mywarp.economy.Fee;
import me.taylorkelly.mywarp.utils.CommandUtils;
import me.taylorkelly.mywarp.utils.MatchList;
import me.taylorkelly.mywarp.utils.MinecraftFontWidthCalculator;
import me.taylorkelly.mywarp.utils.PaginatedResult;
import me.taylorkelly.mywarp.utils.PopularityWarpComparator;
import me.taylorkelly.mywarp.utils.commands.Command;
import me.taylorkelly.mywarp.utils.commands.CommandContext;
import me.taylorkelly.mywarp.utils.commands.CommandException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class contains all commands that cover basic tasks. They should be
 * included in the <code>mywarp.warp.basic.*</code> permission container.
 * 
 */
public class BasicCommands {

    @Command(aliases = { "pcreate", "pset" }, usage = "<name>", desc = "commands.create-private.description", fee = Fee.CREATE_PRIVATE, min = 1, permissions = { "mywarp.warp.basic.createprivate" })
    public void createPrivateWarp(CommandContext args, Player sender) throws CommandException {
        String name = args.getJoinedStrings(0);

        CommandUtils.checkTotalLimit(sender);
        CommandUtils.checkPrivateLimit(sender);
        CommandUtils.checkWarpname(sender, name);

        MyWarp.inst().getWarpManager().addWarpPrivate(name, sender);
        sender.sendMessage(MyWarp.inst().getLocalizationManager()
                .getString("commands.create-private.created-successful", sender, name));
    }

    @Command(aliases = { "create", "set" }, usage = "<name>", desc = "commands.create.description", fee = Fee.CREATE, min = 1, permissions = { "mywarp.warp.basic.createpublic" })
    public void createPublicWarp(CommandContext args, Player sender) throws CommandException {
        String name = args.getJoinedStrings(0);

        CommandUtils.checkTotalLimit(sender);
        CommandUtils.checkPublicLimit(sender);
        CommandUtils.checkWarpname(sender, name);

        MyWarp.inst().getWarpManager().addWarpPublic(name, sender);
        sender.sendMessage(MyWarp.inst().getLocalizationManager()
                .getString("commands.create.created-successful", sender, name));
    }

    @Command(aliases = { "delete", "remove" }, usage = "<name>", desc = "commands.delete.description", fee = Fee.DELETE, min = 1, permissions = { "mywarp.warp.basic.delete" })
    public void deleteWarp(CommandContext args, CommandSender sender) throws CommandException {
        Warp warp = CommandUtils.getWarpForModification(sender, args.getJoinedStrings(0));

        MyWarp.inst().getWarpManager().deleteWarp(warp);
        sender.sendMessage(MyWarp.inst().getLocalizationManager()
                .getString("commands.delete.deleted-successful", sender, warp.getName()));
    }

    @Command(aliases = { "assets", "pstats", "pinfo", "limits" }, usage = "[player]", desc = "commands.assets.description", fee = Fee.ASSETS, max = 1, permissions = { "mywarp.warp.basic.assets" })
    public void listPlayerWarps(CommandContext args, CommandSender sender) throws CommandException {
        Player player = null;
        if (args.argsLength() == 0) {
            player = CommandUtils.checkPlayer(sender);
        } else {
            CommandUtils.checkPermissions(sender, "mywarp.warp.basic.assets.other");
            player = CommandUtils.matchPlayer(sender, args.getString(0));
        }

        sender.sendMessage(ChatColor.GOLD
                + MinecraftFontWidthCalculator.centralize(
                        " "
                                + MyWarp.inst()
                                        .getLocalizationManager()
                                        .getString("commands.assets.heading", sender,
                                                player.getName()) + " ", '-'));

        if (MyWarp.inst().getWarpSettings().limitsEnabled) {
            Map<WarpLimit, TreeSet<Warp>> userWarps = new HashMap<WarpLimit, TreeSet<Warp>>();
            List<String> worlds = new ArrayList<String>();
            for (WarpLimit limit : MyWarp.inst().getWarpSettings().limitsWarpLimits) {
                if (!MyWarp.inst().getPermissionsManager()
                        .hasPermission(sender, "mywarp.limit." + limit.getName())) {
                    continue;
                }
                userWarps.put(limit, null);
                worlds.addAll(limit.getAffectedWorlds());
            }
            // if there is a world that is not covered by all custom warp
            // limits, the default one is needed
            if (!worlds.containsAll(MyWarp.inst().getWarpSettings().limitsDefaultWarpLimit
                    .getAffectedWorlds())) {
                userWarps.put(MyWarp.inst().getWarpSettings().limitsDefaultWarpLimit, null);
            }

            for (Warp warp : MyWarp.inst().getWarpManager().getWarps(null, player.getName())) {
                for (WarpLimit limit : userWarps.keySet()) {
                    if (!limit.getAffectedWorlds().contains(warp.getWorld())) {
                        continue;
                    }
                    TreeSet<Warp> limitWarps = userWarps.get(limit);
                    if (limitWarps == null) {
                        limitWarps = new TreeSet<Warp>();
                        userWarps.put(limit, limitWarps);
                    }
                    limitWarps.add(warp);
                }
            }

            for (Entry<WarpLimit, TreeSet<Warp>> entry : userWarps.entrySet()) {
                Set<Warp> privateWarps = new TreeSet<Warp>();
                Set<Warp> publicWarps = new TreeSet<Warp>();
                for (Warp warp : entry.getValue()) {

                    if (warp.isPublicAll()) {
                        publicWarps.add(warp);
                    } else {
                        privateWarps.add(warp);
                    }
                }

                String publicEntry = MyWarp
                        .inst()
                        .getLocalizationManager()
                        .getString("commands.assets.public-warps", sender,
                                publicWarps.size() + "/" + entry.getKey().getPublicLimit(),
                                CommandUtils.joinWarps(publicWarps));
                String privateEntry = MyWarp
                        .inst()
                        .getLocalizationManager()
                        .getString("commands.assets.private-warps", sender,
                                privateWarps.size() + "/" + entry.getKey().getPrivateLimit(),
                                CommandUtils.joinWarps(privateWarps));

                sender.sendMessage(MyWarp
                        .inst()
                        .getLocalizationManager()
                        .getString("commands.assets.total-warps", sender,
                                StringUtils.join(entry.getKey().getAffectedWorlds(), ", "),
                                entry.getValue().size() + "/" + entry.getKey().getTotalLimit()));
                sender.sendMessage(MinecraftFontWidthCalculator.toList(publicEntry, privateEntry));
            }
        } else {
            Set<Warp> privateWarps = MyWarp.inst().getWarpManager().getWarps(false, player.getName());
            Set<Warp> publicWarps = MyWarp.inst().getWarpManager().getWarps(true, player.getName());

            String publicEntry = MyWarp
                    .inst()
                    .getLocalizationManager()
                    .getString("commands.assets.public-warps", sender, publicWarps.size(),
                            CommandUtils.joinWarps(publicWarps));
            String privateEntry = MyWarp
                    .inst()
                    .getLocalizationManager()
                    .getString("commands.assets.private-warps", sender, privateWarps.size(),
                            CommandUtils.joinWarps(privateWarps));

            StrBuilder worldNames = new StrBuilder();
            for (World world : MyWarp.server().getWorlds()) {
                worldNames.appendSeparator(", ");
                worldNames.append(world.getName());
            }

            sender.sendMessage(MyWarp
                    .inst()
                    .getLocalizationManager()
                    .getString("commands.assets.total-warps", sender, worldNames.toString(),
                            (privateWarps.size() + publicWarps.size())));
            sender.sendMessage(MinecraftFontWidthCalculator.toList(publicEntry, privateEntry));
        }
    }

    @Command(aliases = { "list", "alist" }, flags = "c:pw:", usage = "[-c creator] [-w world]", desc = "commands.list.description", fee = Fee.LIST, max = 1, permissions = { "mywarp.warp.basic.list" })
    public void listWarps(CommandContext args, CommandSender sender) throws CommandException {
        TreeSet<Warp> results = MyWarp
                .inst()
                .getWarpManager()
                .getUsableWarps(sender, args.getFlag('c'), args.getFlag('w'),
                        args.hasFlag('p') ? new PopularityWarpComparator() : null);

        PaginatedResult<Warp> cmdList = new PaginatedResult<Warp>(MyWarp.inst().getLocalizationManager()
                .getColorlessString("commands.list.heading", sender)
                + ", ") {

            @Override
            public String format(Warp warp, CommandSender sender) {
                // 'name'(+) by player
                StringBuilder first = new StringBuilder();

                if (warp.isCreator(sender)) {
                    if (warp.isPublicAll()) {
                        first.append(ChatColor.AQUA);
                    } else {
                        first.append(ChatColor.BLUE);
                    }
                } else if (warp.isPublicAll()) {
                    first.append(ChatColor.GREEN);
                } else {
                    first.append(ChatColor.RED);
                }
                first.append("'");
                first.append(warp.getName());
                first.append("'");
                first.append(ChatColor.WHITE);
                first.append(" (");
                first.append(warp.isPublicAll() ? "+" : "-");
                first.append(") ");
                first.append(MyWarp.inst().getLocalizationManager()
                        .getColorlessString("commands.list.by", sender));
                first.append(" ");
                first.append(ChatColor.ITALIC);

                if (warp.isCreator(sender)) {
                    first.append(MyWarp.inst().getLocalizationManager()
                            .getColorlessString("commands.list.you", sender));
                } else {
                    first.append(warp.getCreator());
                }
                // @(x, y, z)
                StringBuilder last = new StringBuilder();
                last.append(ChatColor.RESET);
                last.append("@(");
                last.append(warp.getWorld() + ", ");
                last.append(Math.round(warp.getX()));
                last.append(", ");
                last.append(warp.getY());
                last.append(", ");
                last.append(Math.round(warp.getZ()));
                last.append(")");
                return (MinecraftFontWidthCalculator.rightLeftAlign(first.toString(), last.toString()));
            }
        };

        try {
            cmdList.display(sender, results, args.getInteger(0, 1));
        } catch (NumberFormatException e) {
            throw new CommandException(MyWarp
                    .inst()
                    .getLocalizationManager()
                    .getString("commands.invalid-number", sender,
                            StringUtils.join(args.getCommand(), ' ')));
        }
    }

    @Command(aliases = { "point" }, usage = "[name]", desc = "commands.point.description", fee = Fee.POINT, permissions = { "mywarp.warp.basic.compass" })
    public void pointToWarp(CommandContext args, Player sender) throws CommandException {
        if (args.argsLength() == 0) {
            sender.setCompassTarget(sender.getWorld().getSpawnLocation());
            sender.sendMessage(MyWarp.inst().getLocalizationManager()
                    .getString("commands.point.reset", sender));
        } else {
            Warp warp = CommandUtils.getWarpForUsage(sender, args.getJoinedStrings(0));

            MyWarp.inst().getWarpManager().point(warp, sender);
            sender.sendMessage(MyWarp.inst().getLocalizationManager()
                    .getString("commands.point.set", sender, warp.getName()));
        }
    }

    @Command(aliases = { "search" }, flags = "p", usage = "<name>", desc = "commands.search.description", fee = Fee.SEARCH, min = 1, permissions = { "mywarp.warp.basic.search" })
    public void searchWarps(CommandContext args, CommandSender sender) throws CommandException {
        MatchList matches = MyWarp
                .inst()
                .getWarpManager()
                .getMatches(args.getJoinedStrings(0), sender instanceof Player ? (Player) sender : null,
                        args.hasFlag('p') ? new PopularityWarpComparator() : null);

        if (matches.exactMatches.size() == 0 && matches.matches.size() == 0) {
            sender.sendMessage(MyWarp.inst().getLocalizationManager()
                    .getString("commands.search.no-matches", sender, args.getJoinedStrings(0)));
        } else {
            sender.sendMessage(ChatColor.GOLD
                    + MyWarp.inst().getLocalizationManager()
                            .getString("commands.search.heading", sender, args.getJoinedStrings(0)));
            if (!matches.exactMatches.isEmpty()) {
                sender.sendMessage(ChatColor.GRAY
                        + MyWarp.inst().getLocalizationManager()
                                .getString("commands.search.exact-heading", sender) + ": " + ChatColor.WHITE
                        + CommandUtils.joinWarps(matches.exactMatches));
            }
            if (!matches.matches.isEmpty()) {
                sender.sendMessage(ChatColor.GRAY
                        + MyWarp.inst()
                                .getLocalizationManager()
                                .getString("commands.search.partital-heading", sender,
                                        matches.matches.size()) + ": " + ChatColor.WHITE
                        + CommandUtils.joinWarps(matches.matches));
            }
        }
    }

    @Command(aliases = { "welcome" }, usage = "<name>", desc = "commands.welcome.description", fee = Fee.WELCOME, min = 1, permissions = { "mywarp.warp.basic.welcome" })
    public void setWarpWelcome(CommandContext args, Player sender) throws CommandException {

        Warp warp = CommandUtils.getWarpForModification(sender, args.getJoinedStrings(0));

        WelcomeMessageHandler.initiateWelcomeMessageChange(sender, warp);
    }

    @Command(aliases = { "help" }, usage = "#", desc = "commands.help.description", fee = Fee.HELP, max = 1, permissions = { "mywarp.warp.basic.help" })
    public void showHelp(final CommandContext args, CommandSender sender) throws CommandException {
        PaginatedResult<Command> cmdList = new PaginatedResult<Command>(MyWarp.inst()
                .getLocalizationManager().getColorlessString("commands.help.heading", sender)
                + ", ", MyWarp.inst().getLocalizationManager()
                .getColorlessString("commands.help.note", sender)) {

            @Override
            public String format(Command cmd, CommandSender sender) {
                // /root sub|sub [flags] <args>
                StringBuilder ret = new StringBuilder();
                ret.append(ChatColor.GOLD);
                ret.append("/");
                ret.append(args.getCommand()[0]);
                ret.append(" ");
                ret.append(StringUtils.join(cmd.aliases(), '|'));
                ret.append(" ");
                ret.append(ChatColor.GRAY);
                ret.append(MyWarp.inst().getCommandsManager().getArguments(cmd, sender));
                return (ret.toString());
            }
        };

        try {
            cmdList.display(sender, MyWarp.inst().getCommandsManager().getUsableCommands(sender, "warp"),
                    args.getInteger(0, 1));
        } catch (NumberFormatException e) {
            throw new CommandException(MyWarp
                    .inst()
                    .getLocalizationManager()
                    .getString("commands.invalid-number", sender,
                            StringUtils.join(args.getCommand(), ' ')));
        }
    }

    @Command(aliases = { "update" }, usage = "<name>", desc = "commands.update.description", fee = Fee.UPDATE, min = 1, permissions = { "mywarp.warp.basic.update" })
    public void updateWarp(CommandContext args, Player sender) throws CommandException {
        Warp warp = CommandUtils.getWarpForModification(sender, args.getJoinedStrings(0));

        warp.setLocation(sender.getLocation());
        sender.sendMessage(MyWarp.inst().getLocalizationManager()
                .getString("commands.update.update-successful", sender, warp.getName()));
    }

    @Command(aliases = { "info", "stats" }, usage = "<name>", desc = "commands.info.description", fee = Fee.INFO, min = 1, permissions = { "mywarp.warp.basic.info" })
    public void showWarpInfo(CommandContext args, CommandSender sender) throws CommandException {
        Warp warp = CommandUtils.getWarpForUsage(sender, args.getJoinedStrings(0));
        StringBuilder infos = new StringBuilder();

        infos.append(ChatColor.GOLD);
        // color the warp depending on its visibility
        infos.append(MyWarp
                .inst()
                .getLocalizationManager()
                .getString(
                        "commands.info.heading",
                        sender,
                        (warp.isPublicAll() ? ChatColor.GREEN : ChatColor.RED) + warp.getName()
                                + ChatColor.GOLD));
        infos.append("\n");

        infos.append(ChatColor.GRAY);
        infos.append(MyWarp.inst().getLocalizationManager().getString("commands.info.created-by", sender));
        infos.append(" ");
        infos.append(ChatColor.WHITE);
        infos.append(warp.getCreator());
        if (warp.isCreator(sender)) {
            infos.append(" ");
            infos.append(MyWarp.inst().getLocalizationManager()
                    .getString("commands.info.created-by-you", sender));
        }
        infos.append("\n");

        infos.append(ChatColor.GRAY);
        infos.append(MyWarp.inst().getLocalizationManager().getString("commands.info.location", sender));
        infos.append(" ");
        infos.append(ChatColor.WHITE);
        infos.append(Math.round(warp.getX()));
        infos.append(", ");
        infos.append(warp.getY());
        infos.append(", ");
        infos.append(Math.round(warp.getZ()));
        infos.append(" ");
        infos.append(MyWarp.inst().getLocalizationManager()
                .getString("commands.info.in-world", sender, warp.getWorld()));
        infos.append("\n");

        if (warp.isModifiable(sender)) {
            infos.append(ChatColor.GRAY);
            infos.append(MyWarp.inst().getLocalizationManager()
                    .getString("commands.info.invited-players", sender));
            infos.append(" ");
            infos.append(ChatColor.WHITE);
            infos.append(warp.getAllInvitedPlayers().isEmpty() ? "-" : StringUtils.join(
                    warp.getAllInvitedPlayers(), ", "));
            infos.append("\n");

            infos.append(ChatColor.GRAY);
            infos.append(MyWarp.inst().getLocalizationManager()
                    .getString("commands.info.invited-groups", sender));
            infos.append(" ");
            infos.append(ChatColor.WHITE);
            infos.append(warp.getAllInvitedGroups().isEmpty() ? "-" : StringUtils.join(
                    warp.getAllInvitedGroups(), ", "));
            infos.append("\n");
        }

        infos.append(ChatColor.GRAY);
        infos.append(MyWarp.inst().getLocalizationManager().getString("commands.info.visits", sender));
        infos.append(" ");
        infos.append(ChatColor.WHITE);
        infos.append(warp.getVisits());

        sender.sendMessage(infos.toString());
    }
}
