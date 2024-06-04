package me.wolfyscript.utilities.util.particles;

import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;

import java.util.Map;

public class ParticleDataFixer {

    private static final Map<String, String> PARTICLE_RENAMES;

    static {
        PARTICLE_RENAMES = Map.ofEntries(
                Map.entry("EXPLOSION_NORMAL", "poof"),
                Map.entry("EXPLOSION_LARGE", "explosion"),
                Map.entry("EXPLOSION_HUGE", "explosion_emitter"),
                Map.entry("FIREWORKS_SPARK", "firework"),
                Map.entry("WATER_BUBBLE", "bubble"),
                Map.entry("WATER_SPLASH", "splash"),
                Map.entry("WATER_WAKE", "fishing"),
                Map.entry("SUSPENDED", "underwater"),
                Map.entry("SUSPENDED_DEPTH", "underwater"),
                Map.entry("CRIT_MAGIC", "enchanted_hit"),
                Map.entry("SMOKE_NORMAL", "smoke"),
                Map.entry("SMOKE_LARGE", "large_smoke"),
                Map.entry("SPELL", "effect"),
                Map.entry("SPELL_INSTANT", "instant_effect"),
                Map.entry("SPELL_MOB", "entity_effect"),
                Map.entry("SPELL_WITCH", "witch"),
                Map.entry("DRIP_WATER", "dripping_water"),
                Map.entry("DRIP_LAVA", "dripping_lava"),
                Map.entry("VILLAGER_ANGRY", "angry_villager"),
                Map.entry("VILLAGER_HAPPY", "happy_villager"),
                Map.entry("TOWN_AURA", "mycelium"),
                Map.entry("ENCHANTMENT_TABLE", "enchant"),
                Map.entry("REDSTONE", "dust"),
                Map.entry("SNOWBALL", "item_snowball"),
                Map.entry("SNOW_SHOVEL", "item_snowball"),
                Map.entry("SLIME", "item_slime"),
                Map.entry("ITEM_CRACK", "item"),
                Map.entry("BLOCK_CRACK", "block"),
                Map.entry("BLOCK_DUST", "block"),
                Map.entry("WATER_DROP", "rain"),
                Map.entry("MOB_APPEARANCE", "elder_guardian"),
                Map.entry("TOTEM", "totem_of_undying")
        );
    }

    public static String convertWhenNecessary(String particleEnumOrKey) {
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 5))) { // Apparently the enums were renamed in 1.20.5/1.20.6
            return PARTICLE_RENAMES.getOrDefault(particleEnumOrKey, particleEnumOrKey);
        }
        return particleEnumOrKey;
    }

}
