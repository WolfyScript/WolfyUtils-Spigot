package com.wolfyscript.utilities.compatibility.plugins.denizen;

import com.denizenscript.denizen.scripts.containers.core.ItemScriptHelper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import java.io.IOException;
import java.util.Objects;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.compatibility.plugins.DenizenIntegrationImpl;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DenizenRefImpl extends APIReference {

    private final ItemStack displayItem;
    private final String itemScript;

    public DenizenRefImpl(ItemStack displayItem, String itemScript) {
        this.itemScript = itemScript;
        this.displayItem = displayItem;
    }

    private DenizenRefImpl(DenizenRefImpl ref) {
        this.itemScript = ref.itemScript;
        this.displayItem = ref.displayItem.clone();
    }

    @Override
    public ItemStack getLinkedItem() {
        return displayItem.clone();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return Objects.equals(ItemScriptHelper.getItemScriptNameText(itemStack), itemScript);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObjectFieldStart("denizen");
        gen.writeObjectField("display_item", displayItem);
        gen.writeStringField("script", itemScript);
        gen.writeEndObject();
    }

    @Override
    public APIReference clone() {
        return new DenizenRefImpl(this);
    }

    public static class Parser extends PluginParser<DenizenRefImpl> {

        public Parser() {
            super(DenizenIntegrationImpl.PLUGIN_NAME, "denizen");
        }

        @Override
        public @Nullable DenizenRefImpl construct(ItemStack itemStack) {
            String script = ItemScriptHelper.getItemScriptNameText(itemStack);
            if (script != null) {
                return new DenizenRefImpl(itemStack, script);
            }
            return null;
        }

        @Override
        public @Nullable DenizenRefImpl parse(JsonNode element) {
            if (element.isObject()) {
                ItemStack item = WolfyCoreBukkit.getInstance().getWolfyUtils().getJacksonMapperUtil().getGlobalMapper().convertValue(element.path("display_item"), ItemStack.class);
                if(!ItemUtils.isAirOrNull(item)) {
                    String script = element.path("script").asText("");
                    if (!script.isBlank()) {
                        return new DenizenRefImpl(item, script);
                    }
                }
            }
            return null;
        }
    }
}
