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

package com.wolfyscript.utilities.util;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestNamespacedKey {

    @Test
    public void checkNamespaceAndKey() {
        BukkitNamespacedKey namespacedKey = new BukkitNamespacedKey("test_namespace", "test_key");
        Assertions.assertEquals("test_namespace", namespacedKey.getNamespace());
        Assertions.assertEquals("test_key", namespacedKey.getKey());

        BukkitNamespacedKey namespacedKey1 = new BukkitNamespacedKey("namespace", "root/folder/subfolder/obj");
        Assertions.assertEquals("namespace", namespacedKey1.getNamespace());
        BukkitNamespacedKey.Key key = namespacedKey1.getKeyComponent();
        Assertions.assertEquals("root", key.getRoot());
        Assertions.assertEquals("root/folder/subfolder", key.getFolder());
        Assertions.assertEquals("obj", key.getObject());
        Assertions.assertEquals("root/folder/subfolder/obj", key.toString());
        Assertions.assertEquals("root/folder/subfolder:obj", key.toString(":"));

        BukkitNamespacedKey namespacedKey2 = new BukkitNamespacedKey("namespace", "obj");
        BukkitNamespacedKey.Key key2 = namespacedKey2.getKeyComponent();
        Assertions.assertEquals("", key2.getRoot());
        Assertions.assertEquals("", key2.getFolder());
        Assertions.assertEquals("obj", key2.toString());
        Assertions.assertEquals("obj", key2.toString(":"));
        Assertions.assertEquals("obj", key2.toString("/"));
        Assertions.assertEquals(":obj", key2.toString(":", true));
        Assertions.assertEquals("/obj", key2.toString("/", true));

        BukkitNamespacedKey namespacedKey3 = new BukkitNamespacedKey("namespace", "root/obj");
        BukkitNamespacedKey.Key key3 = namespacedKey3.getKeyComponent();
        Assertions.assertEquals("root", key3.getRoot());
        Assertions.assertEquals("root", key3.getFolder());
        Assertions.assertEquals("root/obj", key3.toString());
        Assertions.assertEquals("root:obj", key3.toString(":"));
        Assertions.assertEquals("root/obj", key3.toString("/"));
        Assertions.assertEquals("root:obj", key3.toString(":", true));
        Assertions.assertEquals("root/obj", key3.toString("/", true));
    }

}
