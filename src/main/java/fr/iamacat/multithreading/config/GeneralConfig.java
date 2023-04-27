/*
 * FalseTweaks
 * Copyright (C) 2022 FalsePattern
 * All Rights Reserved
 * The above copyright notice, this permission notice and the word "SNEED"
 * shall be included in all copies or substantial portions of the Software.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.iamacat.multithreading.config;

import com.falsepattern.lib.config.Config;
import com.falsepattern.lib.config.ConfigurationManager;

import fr.iamacat.multithreading.Tags;

@Config(modid = Tags.MODID)
public class GeneralConfig {
    static {
        ConfigurationManager.selfInit();
    }

    @Config.Comment("Enable/Disable multithreaded mob spawning This includes:\n" + "- Multithreaded mob spawning\n"
        + "-Better tps when mob spawning on heavily modpacks\n"
        + "TPS impact: none")
    @Config.DefaultBoolean(true)
    public static boolean MixinEntitySpawning;
  //  @Config.Comment("Increases the max signal strength of redstone.\n"
  //      + "Only has effect with both extendBlockItem and extendRedstone enabled.\n"
  //      + "Vanilla value is 15.")
   // @Config.RangeInt(min = 15, max = 127)
   // @Config.DefaultInt(15)
   // public static int maxRedstone;
    /*
     * @Config.Comment(
     * "WARNING: THIS CONFIG IS EXTREMELY SENSITIVE TO CHANGES, DISABLING STUFF ONCE THEY HAVE BEEN ENABLED\n" +
     * "CAN CORRUPT YOUR WORLD! ONLY TOUCH THIS FILE IF YOU KNOW WHAT YOU'RE DOING!\n" +
     * "This file should only be edited by modpack developers to finetune it for the modpack. Once an option\n" +
     * "has been enabled, it modifies the chunk save data format, which is (partially) compatible with\n" +
     * "vanilla, but removing the mod might cause block/item corruption for higher IDs.\n" +
     * "Additional note: Every client and server must have the exact same config, this also affects netcode.")
     * @Config.DefaultString("")
     */
    public static String __NOTICE__;
}
