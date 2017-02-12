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

package me.taylorkelly.mywarp.bukkit;

import org.bukkit.Material;

/**
 * Provides information about Materials.
 */
class MaterialInfo {

  private MaterialInfo() {
  }

  //   For reference, all Materials added since 1.7 (taken directly from Spigot's source code):
  //
  //   ## 1.8
  //   SLIME_BLOCK(165),
  //   BARRIER(166),
  //   IRON_TRAPDOOR(167,TrapDoor.class),
  //   PRISMARINE(168),
  //   SEA_LANTERN(169),
  //   STANDING_BANNER(176, Banner.class),
  //   WALL_BANNER(177, Banner.class),
  //   DAYLIGHT_DETECTOR_INVERTED(178),
  //   RED_SANDSTONE(179),
  //   RED_SANDSTONE_STAIRS(180, Stairs.class),
  //   DOUBLE_STONE_SLAB2(181),
  //   STONE_SLAB2(182),
  //   SPRUCE_FENCE_GATE(183),
  //   BIRCH_FENCE_GATE(184),
  //   JUNGLE_FENCE_GATE(185),
  //   DARK_OAK_FENCE_GATE(186),
  //   ACACIA_FENCE_GATE(187),
  //   SPRUCE_FENCE(188),
  //   BIRCH_FENCE(189),
  //   JUNGLE_FENCE(190),
  //   DARK_OAK_FENCE(191),
  //   ACACIA_FENCE(192),
  //   SPRUCE_DOOR(193),
  //   BIRCH_DOOR(194),
  //   JUNGLE_DOOR(195),
  //   ACACIA_DOOR(196),
  //   DARK_OAK_DOOR(197),
  //
  //   ## 1.9
  //   END_ROD(198),
  //   CHORUS_PLANT(199),
  //   CHORUS_FLOWER(200),
  //   PURPUR_BLOCK(201),
  //   PURPUR_PILLAR(202),
  //   PURPUR_STAIRS(203, Stairs.class),
  //   PURPUR_DOUBLE_SLAB(204),
  //   PURPUR_SLAB(205),
  //   END_BRICKS(206),
  //   BEETROOT_BLOCK(207, Crops.class),
  //   GRASS_PATH(208),
  //   END_GATEWAY(209),
  //   COMMAND_REPEATING(210),
  //   COMMAND_CHAIN(211),
  //   FROSTED_ICE(212),
  //   STRUCTURE_BLOCK(255),
  //
  //   ## 1.10
  //   MAGMA(213),
  //   NETHER_WART_BLOCK(214)
  //   RED_NETHER_BRICK(215)
  //   BONE_BLOCK(216)
  //   STRUCTURE_VOID(217)
  //
  //   ## 1.11
  //   OBSERVER(218)
  //   WHITE_SHULKER_BOX(219, 1)
  //   ORANGE_SHULKER_BOX(220, 1)
  //   MAGENTA_SHULKER_BOX(221, 1)
  //   LIGHT_BLUE_SHULKER_BOX(222, 1)
  //   YELLOW_SHULKER_BOX(223, 1)
  //   LIME_SHULKER_BOX(224, 1)
  //   PINK_SHULKER_BOX(225, 1)
  //   GRAY_SHULKER_BOX(226, 1)
  //   SILVER_SHULKER_BOX(227, 1)
  //   CYAN_SHULKER_BOX(228, 1)
  //   PURPLE_SHULKER_BOX(229, 1)
  //   BLUE_SHULKER_BOX(230, 1)
  //   BROWN_SHULKER_BOX(231, 1)
  //   GREEN_SHULKER_BOX(232, 1)
  //   RED_SHULKER_BOX(233, 1)
  //   BLACK_SHULKER_BOX(234, 1)

  /**
   * Returns whether a regular entity (without any status effects) can stand <i>within</i> a block of the given material
   * without taking any damage from doing so.
   *
   * @param material the material to check
   * @return {@code true} if an entity can safely stand within a block of the given material
   */
  static boolean canEntitySafelyStandWithin(Material material) {
    switch (material) {
      // -- 1.7 (and before)
      case LAVA:
      case STATIONARY_LAVA:
      case FIRE:
        return false;
      default:
        return !material.isSolid();
    }
  }

  /**
   * Returns whether a regular entity (without any status effects) can stand <i>on</i> a block of the given material
   * without taking any damage from doing so.
   *
   * @param material the material to check
   * @return {@code true} if an entity can safely stand within a block of the given material
   */
  static boolean canEntitySafelyStandOn(Material material) {
    switch (material) {
      // FALSE
      // -- 1.7 (and before)
      case CACTUS:
        // -- 1.10
      case MAGMA:
        return false;
      // TRUE
      // -- 1.7 (and before)
      case WATER:
      case STATIONARY_WATER:
        return true;
      default:
        return material.isSolid();
    }
  }

  /**
   * Returns whether a block of the given material is smaller than a normal full block.
   *
   * @param material the material to check
   * @return {@code true} if this particular block is smaller than a normal block
   */
  static boolean isNotFullHeight(Material material) {
    switch (material) {
      // -- 1.7 (and before)
      case BED_BLOCK:
      case STEP:
      case WOOD_STAIRS:
      case CHEST:
      case COBBLESTONE_STAIRS:
      case CAKE_BLOCK:
      case TRAP_DOOR:
      case BRICK_STAIRS:
      case SMOOTH_STAIRS:
      case NETHER_BRICK_STAIRS:
      case ENCHANTMENT_TABLE:
      case BREWING_STAND:
      case CAULDRON:
      case WOOD_STEP:
      case SANDSTONE_STAIRS:
      case ENDER_CHEST:
      case SPRUCE_WOOD_STAIRS:
      case BIRCH_WOOD_STAIRS:
      case JUNGLE_WOOD_STAIRS:
      case SKULL:
      case TRAPPED_CHEST:
      case DAYLIGHT_DETECTOR:
      case QUARTZ_STAIRS:
      case ACACIA_STAIRS:
      case DARK_OAK_STAIRS:
        // -- 1.8
      case IRON_TRAPDOOR:
      case DAYLIGHT_DETECTOR_INVERTED:
      case RED_SANDSTONE_STAIRS:
        // -- 1.9
      case PURPUR_STAIRS:
      case PURPUR_SLAB:
        return true;
      default:
        return false;
    }
  }
}
