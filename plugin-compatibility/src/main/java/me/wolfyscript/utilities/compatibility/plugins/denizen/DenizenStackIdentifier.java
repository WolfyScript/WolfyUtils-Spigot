package me.wolfyscript.utilities.compatibility.plugins.denizen;

import com.denizenscript.denizen.scripts.containers.core.ItemScriptHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class DenizenStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("denizen");
    private final ItemStack displayItem;
    private final String itemScript;

    public DenizenStackIdentifier(ItemStack displayItem, String itemScript) {
        this.displayItem = displayItem;
        this.itemScript = itemScript;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        ItemStack stack = displayItem.clone();
        stack.setAmount(context.amount());
        return stack;
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        return Objects.equals(ItemScriptHelper.getItemScriptNameText(other), itemScript);
    }

    @Override
    public DenizenRefImpl convert(double weight, int amount) {
        DenizenRefImpl ref = new DenizenRefImpl(displayItem, itemScript);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<DenizenStackIdentifier>, LegacyParser<DenizenStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<DenizenStackIdentifier> from(ItemStack itemStack) {
            String script = ItemScriptHelper.getItemScriptNameText(itemStack);
            if (script != null) {
                return Optional.of(new DenizenStackIdentifier(itemStack, script));
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }

        @Override
        public DisplayConfiguration displayConfig() {
            return new DisplayConfiguration.SimpleDisplayConfig(
                    Component.text("Denizen").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.COMMAND_BLOCK)
            );
        }

        @Override
        public Optional<DenizenStackIdentifier> from(JsonNode legacyData) {
            if (legacyData.isObject()) {
                ItemStack item = WolfyUtilCore.getInstance().getWolfyUtils().getJacksonMapperUtil().getGlobalMapper().convertValue(legacyData.path("display_item"), ItemStack.class);
                if(!ItemUtils.isAirOrNull(item)) {
                    String script = legacyData.path("script").asText("");
                    if (!script.isBlank()) {
                        return Optional.of(new DenizenStackIdentifier(item, script));
                    }
                }
            }
            return Optional.empty();
        }
    }

}
