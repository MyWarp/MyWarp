package me.taylorkelly.mywarp.permissions;

import org.bukkit.entity.Player;

public interface IPermissionsHandler {
	boolean hasPermission(Player player, String node);
	
	boolean playerHasGroup(Player player, String group);
}
