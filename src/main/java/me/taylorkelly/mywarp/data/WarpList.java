package me.taylorkelly.mywarp.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import me.taylorkelly.mywarp.LanguageManager;
import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.WarpSettings;
import me.taylorkelly.mywarp.utils.MatchList;
import me.taylorkelly.mywarp.utils.WarpLogger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WarpList {
    private final MyWarp plugin;

    private HashMap<String, Warp> warpMap;
    private ConcurrentHashMap<String, Warp> welcomeMessage;

    private static final int CLEANUP_TIME = 30;

    public WarpList(MyWarp plugin) {
        this.plugin = plugin;

        welcomeMessage = new ConcurrentHashMap<String, Warp>();
        warpMap = plugin.getConnectionManager().getMap();
        WarpLogger.info(getSize() + " warps loaded");
    }

    public void addWarp(String name, Warp warp) {
        warpMap.put(name, warp);
        plugin.getConnectionManager().addWarp(warp);

        if (plugin.getMarkers() != null) {
            plugin.getMarkers().addWarp(warp);
        }
    }

    public void addWarpPrivate(String name, Player player) {
        Warp warp = new Warp(name, player, false);
        addWarp(name, warp);
    }

    public void addWarpPublic(String name, Player player) {
        Warp warp = new Warp(name, player);
        addWarp(name, warp);
    }

    public void deleteWarp(Warp warp) {
        warpMap.remove(warp.name);
        plugin.getConnectionManager().deleteWarp(warp);

        if (plugin.getMarkers() != null) {
            plugin.getMarkers().deleteWarp(warp);
        }
    }

    public MatchList getMatches(String name, Player player,
            Comparator<Warp> comperator) {
        TreeSet<Warp> exactMatches = new TreeSet<Warp>(comperator);
        TreeSet<Warp> matches = new TreeSet<Warp>(comperator);

        for (Warp warp : warpMap.values()) {
            if (player != null && !warp.playerCanWarp(player)) {
                continue;
            }
            if (WarpSettings.worldAccess && player != null
                    && !playerCanAccessWorld(player, warp.world)) {
                continue;
            }
            if (warp.name.equalsIgnoreCase(name)) {
                exactMatches.add(warp);
            } else if (warp.name.toLowerCase().contains(name.toLowerCase())) {
                matches.add(warp);
            }
        }
        if (exactMatches.size() > 1) {
            Iterator<Warp> iterator = exactMatches.iterator();
            while (iterator.hasNext()) {
                Warp warp = iterator.next();
                if (!warp.name.equals(name)) {
                    matches.add(warp);
                    iterator.remove();
                }
            }
        }
        return new MatchList(exactMatches, matches);
    }

    public String getMatchingCreator(Player player, String creator) {
        ArrayList<String> matches = new ArrayList<String>();

        for (Warp warp : warpMap.values()) {
            if (player != null && !warp.playerCanWarp(player)) {
                continue;
            }
            if (WarpSettings.worldAccess && player != null
                    && !playerCanAccessWorld(player, warp.world)) {
                continue;
            }
            if (warp.creator.equalsIgnoreCase(creator)) {
                return creator;
            }
            if (warp.creator.toLowerCase().contains(creator.toLowerCase())
                    && !matches.contains(warp.creator)) {
                matches.add(warp.creator);
            }
        }
        if (matches.size() == 1) {
            return matches.get(0);
        }
        return "";
    }

    public double getMaxWarps(Player player, String creator) {
        int count = 0;
        for (Warp warp : warpMap.values()) {
            if ((player != null && !warp.playerCanWarp(player))
                    || (creator != null && !warp.playerIsCreator(creator))) {
                continue;
            }
            if (WarpSettings.worldAccess && player != null
                    && !playerCanAccessWorld(player, warp.world)) {
                continue;
            }
            count++;
        }
        return count;
    }

    public TreeSet<Warp> getPublicWarps() {
        TreeSet<Warp> ret = new TreeSet<Warp>();

        for (Warp warp : warpMap.values()) {
            if (warp.publicAll) {
                ret.add(warp);
            }
        }
        return ret;
    }

    public int getSize() {
        return warpMap.size();
    }

    public Warp getWarp(String name) {
        return warpMap.get(name);
    }

    public void give(Warp warp, String giveeName) {
        warp.setCreator(giveeName);
        plugin.getConnectionManager().updateCreator(warp);

        if (plugin.getMarkers() != null) {
            plugin.getMarkers().updateWarp(warp);
        }
    }

    public void inviteGroup(Warp warp, String inviteeName) {
        warp.inviteGroup(inviteeName);
        plugin.getConnectionManager().updateGroupPermissions(warp);
    }

    public void invitePlayer(Warp warp, String inviteeName) {
        warp.invite(inviteeName);
        plugin.getConnectionManager().updatePermissions(warp);
    }

    public void notWaiting(Player player) {
        welcomeMessage.remove(player.getName());
    }

    private int numPrivateWarpsPlayer(Player player) {
        int size = 0;
        for (Warp warp : warpMap.values()) {
            boolean privateAll = !warp.publicAll;
            String creator = warp.creator;
            if (creator.equals(player.getName()) && privateAll)
                size++;
        }
        return size;
    }

    private int numPublicWarpsPlayer(Player player) {
        int size = 0;
        for (Warp warp : warpMap.values()) {
            boolean publicAll = warp.publicAll;
            String creator = warp.creator;
            if (creator.equals(player.getName()) && publicAll)
                size++;
        }
        return size;
    }

    private int numWarpsPlayer(Player player) {
        int size = 0;
        for (Warp warp : warpMap.values()) {
            String creator = warp.creator;
            if (creator.equals(player.getName()))
                size++;
        }
        return size;
    }

    public boolean playerCanAccessWorld(Player player, String worldName) {
        if (player.getWorld().getName().equals(worldName)
                && MyWarp.getWarpPermissions().canWarpInsideWorld(player)) {
            return true;
        }
        if (MyWarp.getWarpPermissions().canWarpToWorld(player, worldName)) {
            return true;
        }
        return false;
    }

    public boolean playerCanBuildPrivateWarp(Player player) {
        if (MyWarp.getWarpPermissions().disobeyPrivateLimit(player)) {
            return true;
        }
        return numPrivateWarpsPlayer(player) < MyWarp.getWarpPermissions()
                .maxPrivateWarps(player);
    }

    public boolean playerCanBuildPublicWarp(Player player) {
        if (MyWarp.getWarpPermissions().disobeyPublicLimit(player)) {
            return true;
        }
        return numPublicWarpsPlayer(player) < MyWarp.getWarpPermissions()
                .maxPublicWarps(player);
    }

    public boolean playerCanBuildWarp(Player player) {
        if (MyWarp.getWarpPermissions().disobeyTotalLimit(player)) {
            return true;
        }
        return numWarpsPlayer(player) < MyWarp.getWarpPermissions()
                .maxTotalWarps(player);
    }

    public void point(Warp warp, Player player) {
        player.setCompassTarget(warp.getLocation(plugin.getServer()));
    }

    public void privatize(Warp warp) {
        warp.publicAll = false;
        plugin.getConnectionManager().publicizeWarp(warp, false);

        if (plugin.getMarkers() != null) {
            plugin.getMarkers().deleteWarp(warp);
        }
    }

    public void publicize(Warp warp) {
        warp.publicAll = true;
        plugin.getConnectionManager().publicizeWarp(warp, true);

        if (plugin.getMarkers() != null) {
            plugin.getMarkers().addWarp(warp);
        }
    }

    /**
     * Sets the welcome message to the given message for the warp that is stored
     * under the given player in the welcomeMessages-Map. Threadsafe.
     * 
     * @param player
     *            the player
     * @param message
     *            the message
     */
    public void setWelcomeMessage(Player player, String message) {
        if (welcomeMessage.containsKey(player.getName())) {
            Warp warp = welcomeMessage.get(player.getName());

            // this method is almost always called asnyc so the warp needs to be
            // locked for changes
            synchronized (warp) {
                warp.welcomeMessage = message;
            }
            plugin.getConnectionManager().updateWelcomeMessage(warp);

            // sendMessage is threadsafe
            player.sendMessage(LanguageManager.getEffectiveString(
                    "warp.welcome.received", "%warp%", warp.name));
            player.sendMessage(ChatColor.AQUA + message);
        }
    }

    public void uninviteGroup(Warp warp, String inviteeName) {
        warp.uninviteGroup(inviteeName);
        plugin.getConnectionManager().updateGroupPermissions(warp);
    }

    public void uninvitePlayer(Warp warp, String inviteeName) {
        warp.uninvite(inviteeName);
        plugin.getConnectionManager().updatePermissions(warp);
    }

    public void updateLocation(Warp warp, Player player) {
        warp.setLocation(player.getLocation());
        plugin.getConnectionManager().updateLocation(warp);

        if (plugin.getMarkers() != null) {
            plugin.getMarkers().updateWarp(warp);
        }
    }

    public boolean waitingForWelcome(Player player) {
        return welcomeMessage.containsKey(player.getName());
    }

    public boolean warpExists(String warp) {
        return warpMap.containsKey(warp);
    }

    public TreeSet<Warp> warpsInvitedTo(Player player, String creator,
            String world, Comparator<Warp> comperator) {
        TreeSet<Warp> results = new TreeSet<Warp>(comperator);

        if (creator != null) {
            creator = getMatchingCreator(player, creator);
        }

        for (Warp warp : warpMap.values()) {
            if (player != null && !warp.playerCanWarp(player)) {
                continue;
            }
            if (WarpSettings.worldAccess && player != null
                    && !playerCanAccessWorld(player, warp.world)) {
                continue;
            }
            if (creator != null && !warp.creator.equals(creator)) {
                continue;
            }
            if (world != null && !warp.world.equals(world)) {
                continue;
            }
            results.add(warp);
        }
        return results;
    }

    public void warpTo(Warp warp, Player player) {
        if (warp.warp(player, plugin.getServer())) {
            warp.visits++;
            plugin.getConnectionManager().updateVisits(warp);
            player.sendMessage(ChatColor.AQUA
                    + warp.getSpecificWelcomeMessage(player));
        }
    }

    public void welcomeMessage(Warp warp, final Player player) {
        welcomeMessage.put(player.getName(), warp);
        plugin.getServer().getScheduler()
                .runTaskLaterAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        welcomeMessage.remove(player.getName());
                    }
                }, CLEANUP_TIME * 20);
    }
}
