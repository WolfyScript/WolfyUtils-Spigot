package me.wolfyscript.utilities.compatibility.plugins.executableitems;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.compatibility.plugins.ExecutableItemsIntegration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

public class ExecutableItemsRef extends APIReference {

    private final ExecutableItemsManagerInterface manager;
    private final String id;

    public ExecutableItemsRef(ExecutableItemsManagerInterface manager, String id) {
        this.id = id;
        this.manager = manager;
    }

    private ExecutableItemsRef(ExecutableItemsRef other) {
        this.id = other.id;
        this.manager = other.manager;
    }

    @Override
    public ItemStack getLinkedItem() {
        return manager.getExecutableItem(id).map(item -> item.buildItem(amount, Optional.empty())).orElseGet(()-> new ItemStack(Material.AIR));
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return manager.getExecutableItem(itemStack).map(exeItem -> exeItem.getId().equals(id)).orElse(false);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("executableitems", id);
    }

    @Override
    protected StackIdentifier convert() {
        return new ExecutableItemsStackIdentifier(manager, id);
    }

    @Override
    public APIReference clone() {
        return new ExecutableItemsRef(this);
    }

    public static class Parser extends APIReference.PluginParser<ExecutableItemsRef> {

        private final ExecutableItemsManagerInterface manager;

        public Parser(ExecutableItemsManagerInterface manager) {
            super(ExecutableItemsIntegration.PLUGIN_NAME, "executableitems");
            this.manager = manager;
        }

        @Override
        public @Nullable ExecutableItemsRef construct(ItemStack itemStack) {
            return manager.getExecutableItem(itemStack).map(exeItem -> new ExecutableItemsRef(manager, exeItem.getId())).orElse(null);
        }

        @Override
        public @Nullable ExecutableItemsRef parse(JsonNode element) {
            return new ExecutableItemsRef(manager, element.asText());
        }
    }
}
