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

package fr.iamacat.multithreading.mixin.plugins;

import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.condition;

import java.util.List;
import java.util.function.Predicate;

import com.falsepattern.lib.config.Config;
import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Mixin implements IMixin {

    // region common
    //MixinEntitySpawning(Side.COMMON,
       // condition(() -> GeneralConfig.MixinEntitySpawning && GeneralConfig.MixinEntitySpawning), "MixinEntitySpawning"),
    // region client
    // BlockRedstoneWireMixin(Side.CLIENT, condition(() -> GeneralConfig.extendBlockItem &&
    // GeneralConfig.extendRedstone), "redstone.BlockRedstoneWireMixin"),
    ;

    @Config.DefaultString("")
    public static String __NOTICE__;

    @Getter
    private final Side side;
    @Getter
    private final Predicate<List<ITargetedMod>> filter;
    @Getter
    private final String mixin;
}
