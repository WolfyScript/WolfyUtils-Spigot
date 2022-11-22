/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util;

import com.wolfyscript.utilities.bukkit.items.CustomItem;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collector;


public class RandomCollection<E> extends com.wolfyscript.utilities.utils.RandomCollection<E> {

    @Deprecated
    public RandomCollection() {
        super();
    }

    /**
     * When using this constructor the specified Random will be used whenever an item is selected.
     *
     * @param random The random to use when selecting an item.
     */
    @Deprecated
    public RandomCollection(Random random) {
        super(random);
    }

    public static Collector<CustomItem, RandomCollection<CustomItem>, RandomCollection<CustomItem>> getCustomItemCollector() {
        return Collector.of(RandomCollection::new, (rdmCollection, customItem) -> rdmCollection.add(customItem.getWeight(), customItem.clone()), RandomCollection::addAll);
    }

    public static <T> Collector<T, RandomCollection<T>, RandomCollection<T>> getCollector(BiConsumer<RandomCollection<T>, T> accumulator) {
        return Collector.of(RandomCollection::new, accumulator, RandomCollection::addAll);
    }

    @Override
    public RandomCollection<E> addAll(com.wolfyscript.utilities.utils.RandomCollection<E> randomCollection) {
        return (RandomCollection<E>) super.addAll(randomCollection);
    }

    @Override
    public RandomCollection<E> add(double weight, E result) {
        return (RandomCollection<E>) super.add(weight, result);
    }
}
