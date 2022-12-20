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

package com.wolfyscript.utilities.bukkit.world.items;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Streams;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.ItemsAdderIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.itemsadder.CustomStack;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.itemsadder.ItemsAdderRef;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.bukkit.world.inventory.InventoryUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.AbstractItemBuilder;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.meta.CustomItemTagMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.Meta;
import com.wolfyscript.utilities.bukkit.world.items.meta.MetaSettings;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.bukkit.world.items.references.VanillaRef;
import com.wolfyscript.utilities.bukkit.world.items.references.WolfyUtilitiesRef;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleLocation;
import com.wolfyscript.utilities.json.jackson.JacksonUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * This Object acts as a wrapper for the {@link APIReference} with additional options that make it possible to manipulate the behaviour of the wrapped reference.
 * <br>
 * The {@link APIReference} can be any kind of reference, a simple {@link ItemStack} ({@link VanillaRef}) or an item from another API.
 * </p>
 * <p>
 * For most additional features the CustomItem has to be registered into the {@link BukkitRegistries#getCustomItems()}.
 * <br>
 * To make sure the CustomItem can be detected later on, it must be created via any of the {@link #create()} methods.
 * <br>
 * These methods will include an extra {@link PersistentDataContainer} entry to identify the item later on!
 * </p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CustomItem extends AbstractItemBuilder<CustomItem> implements Keyed {

    public static final org.bukkit.NamespacedKey PERSISTENT_KEY_TAG = new org.bukkit.NamespacedKey(WolfyCoreBukkit.getInstance(), "custom_item");
    private static final Map<String, APIReference.Parser<?>> API_REFERENCE_PARSER = new HashMap<>();

    @Nullable
    public static APIReference.Parser<?> getApiReferenceParser(String id) {
        return API_REFERENCE_PARSER.get(id);
    }

    /**
     * Register a new {@link APIReference.Parser} that can parse ItemStacks and keys from another plugin to a usable {@link APIReference}
     *
     * @param parser an {@link APIReference.Parser} instance.
     */
    public static void registerAPIReferenceParser(APIReference.Parser<?> parser) {
        if (parser instanceof APIReference.PluginParser pluginParser) {
            if (!WolfyCoreBukkit.getInstance().getCompatibilityManager().getPlugins().isPluginEnabled(pluginParser.getPluginName())) {
                return;
            }
            pluginParser.init(Bukkit.getPluginManager().getPlugin(pluginParser.getPluginName()));
        }
        API_REFERENCE_PARSER.put(parser.getId(), parser);
        if (!parser.getAliases().isEmpty()) {
            parser.getAliases().forEach(s -> API_REFERENCE_PARSER.putIfAbsent(s, parser));
        }
    }

    @JsonIgnore
    private final Material type;

    /**
     * This namespacedKey can either be null or non-null.
     * <p>
     * If it's non-null, the item is saved and the variables of this Object will be persistent </p>
     * when converted to ItemStack via {@link #create()}.
     * <p>
     * If it is null, the item isn't saved and the variables of this Object will get lost when {@link #create()} is called!
     */
    @JsonIgnore
    private NamespacedKey namespacedKey;
    @JsonIgnore
    private final Material craftRemain;

    @JsonAlias("api_reference")
    private final APIReference apiReference;

    @JsonAlias("custom_data")
    private final CustomData.DeprecatedCustomDataWrapper customDataMap = new CustomData.DeprecatedCustomDataWrapper(this);
    @JsonIgnore
    private final Map<NamespacedKey, CustomItemData> indexedData = new HashMap<>();

    @JsonAlias("equipment_slots")
    private final List<EquipmentSlot> equipmentSlots;
    private boolean consumed;
    private boolean blockVanillaEquip;
    private boolean blockPlacement;
    private boolean blockVanillaRecipes;
    @JsonAlias("rarity_percentage")
    private double rarityPercentage;
    private String permission;
    private MetaSettings nbtChecks;
    private APIReference replacement;
    @JsonAlias("fuel")
    private FuelSettings fuelSettings;
    @JsonAlias("durability_cost")
    private int durabilityCost;
    @JsonAlias("particles")
    private ParticleContent particleContent;
    private ActionSettings actionSettings = new ActionSettings();
    private final CustomBlockSettings blockSettings;

    @JsonIgnore
    private boolean checkOldMetaSettings = true;

    @JsonCreator
    public CustomItem(@JsonProperty("apiReference") @JsonAlias({"item", "api_reference"}) APIReference apiReference) {
        super(CustomItem.class);
        this.apiReference = apiReference;

        this.namespacedKey = null;
        this.fuelSettings = new FuelSettings();
        setMetaSettings(new MetaSettings());
        this.permission = "";
        this.rarityPercentage = apiReference.getWeight() > 0 ? apiReference.getWeight() : 1.0d;
        for (CustomData.Provider<?> customData : WolfyUtilCore.getInstance().getRegistries().getCustomItemData().values()) {
            addCustomData(customData.getNamespacedKey(), customData.createData());
        }
        this.equipmentSlots = new ArrayList<>();
        this.particleContent = new ParticleContent();
        this.blockSettings = new CustomBlockSettings();
        this.blockPlacement = false;
        this.blockVanillaEquip = false;
        this.blockVanillaRecipes = false;

        this.consumed = true;
        this.replacement = null;
        this.durabilityCost = 0;
        this.type = getItemStack() != null ? getItemStack().getType() : Material.AIR;
        this.craftRemain = getCraftRemain();
    }

    @JsonAnySetter
    protected void setOldProperties(String fieldKey, JsonNode value) throws JsonProcessingException {
        if (fieldKey.equals("advanced")) {
            checkOldMetaSettings = value.asBoolean();
        } else if (fieldKey.equals("metaSettings") || fieldKey.equals("meta")) {
            //Since the new system has its new field we need to update old appearances to the new system.
            JsonNode node = value.isTextual() ? JacksonUtil.getObjectMapper().readTree(value.asText()) : value;
            final List<Meta> checks;
            if (!node.has(MetaSettings.CHECKS_KEY)) {
                checks = new ArrayList<>();
                if (checkOldMetaSettings) {
                    //Convert old meta to new format.
                    node.fields().forEachRemaining(entry -> {
                        if (entry.getValue() instanceof ObjectNode entryVal) {
                            String key = entry.getKey().toLowerCase(Locale.ROOT);
                            NamespacedKey namespacedKey = key.contains(":") ? BukkitNamespacedKey.of(key) : BukkitNamespacedKey.wolfyutilties(key);
                            if (namespacedKey != null) {
                                entryVal.put("key", String.valueOf(namespacedKey));
                                Meta meta = JacksonUtil.getObjectMapper().convertValue(entryVal, Meta.class);
                                if (meta != null && !meta.getOption().equals(MetaSettings.Option.IGNORE) && !meta.getOption().equals(MetaSettings.Option.EXACT)) {
                                    checks.add(meta);
                                }
                            }
                        }
                    });
                }
            } else {
                checks = JacksonUtil.getObjectMapper().convertValue(node.get(MetaSettings.CHECKS_KEY), new TypeReference<>() {
                });
            }
            var nbtChecks = new MetaSettings();
            checks.forEach(nbtChecks::addCheck);
            setMetaSettings(nbtChecks);
        }
    }

    /**
     * Creates a CustomItem with a Vanilla Reference to the itemstack
     *
     * @param itemStack the itemstack this CustomItem will be linked to
     */
    public CustomItem(ItemStack itemStack) {
        this(new VanillaRef(itemStack));
    }

    /**
     * Creates a CustomItem with a Vanilla Reference to an itemstack of the material
     *
     * @param material the material of the itemstack this CustomItem will be linked to
     */
    public CustomItem(Material material) {
        this(new ItemStack(material));
    }

    /**
     * @param customItem A new deep copy of the passed in CustomItem.
     */
    private CustomItem(CustomItem customItem) {
        super(CustomItem.class);
        this.apiReference = customItem.apiReference.clone();

        this.namespacedKey = customItem.getNamespacedKey();
        this.fuelSettings = customItem.fuelSettings.clone();
        this.blockSettings = customItem.blockSettings.copy();
        this.nbtChecks = customItem.nbtChecks;
        this.permission = customItem.permission;
        this.rarityPercentage = customItem.rarityPercentage;
        this.customDataMap.clear();
        for (Map.Entry<NamespacedKey, CustomData> entry : customItem.customDataMap.entrySet()) {
            this.customDataMap.put(entry.getKey(), entry.getValue().clone());
        }
        this.equipmentSlots = new ArrayList<>(customItem.equipmentSlots);
        this.particleContent = customItem.particleContent;
        this.blockPlacement = customItem.blockPlacement;
        this.blockVanillaEquip = customItem.blockVanillaEquip;
        this.blockVanillaRecipes = customItem.blockVanillaRecipes;

        this.consumed = customItem.consumed;
        this.replacement = customItem.replacement;
        this.durabilityCost = customItem.durabilityCost;
        this.type = getItemStack() != null ? getItemStack().getType() : Material.AIR;
        this.craftRemain = getCraftRemain();
    }

    /**
     * Clones the CustomItem and all the containing data.
     *
     * @return An exact deep copy of this CustomItem instance.
     */
    @Override
    public CustomItem clone() {
        return new CustomItem(this);
    }

    /**
     * <p>
     * This will create a <b>new</b> {@link CustomItem} instance with the specified APIReference.
     * </p>
     * <p>
     * This means:
     * If the reference points to an actual CustomItem, the returned CustomItem will override it's custom values and can then be registered.
     * They are then dependent on the linked CustomItem.
     * If the reference points to any other API such as Oraxen, MMOItems, etc. it is linked to these APIs and can be registered.
     * They are then dependent on the linked API.
     * </p>
     *
     * @param reference The reference to link the item to.
     * @return A new CustomItem instance with the specified APIReference.
     */
    public static CustomItem with(APIReference reference) {
        if (reference == null) return null;
        return new CustomItem(reference);
    }

    /**
     * <p>
     * This method tries to get the actual {@link CustomItem} of the {@link APIReference}!
     * </p>
     * <p>
     * If the reference points to an actual registered CustomItem ({@link WolfyUtilitiesRef}) then that item is returned.<br>
     * If the reference points to any other API such as Oraxen, MMOItems, etc. it redirects to the {@link #with(APIReference)} method.<br>
     * </p>
     * <p>
     * <b>
     * !Warning: If you want to create a CustomItem that is linked to another CustomItem and overrides it, use {@link #with(APIReference)} instead!
     * </b>
     * </p>
     *
     * @param reference The reference that points to an API Item.
     * @return The actual CustomItem of the APIReference.
     */
    @Nullable
    public static CustomItem of(APIReference reference) {
        if (reference == null) return null;
        return reference instanceof WolfyUtilitiesRef ? WolfyUtilCore.getInstance().getRegistries().getCustomItems().get(((WolfyUtilitiesRef) reference).getNamespacedKey()) : with(reference);
    }

    /**
     * Get the CustomItem via ItemStack.
     * It checks for the PersistentData containing the NamespacedKey of WolfyUtilities.
     * When that isn't found it checks for ItemsAdder and Oraxen values saved in the Items NBT.
     *
     * @param itemStack the ItemStack to check
     * @return the CustomItem linked to the specific API this Item is from.
     */
    public static CustomItem getReferenceByItemStack(ItemStack itemStack) {
        if (itemStack != null) {
            APIReference apiReference = API_REFERENCE_PARSER.values().stream().sorted(APIReference.Parser::compareTo).map(parser -> parser.construct(itemStack)).filter(Objects::nonNull).findFirst().orElse(null);
            if (apiReference != null) {
                apiReference.setAmount(itemStack.getAmount());
                return new CustomItem(apiReference);
            }
            return new CustomItem(itemStack);
        }
        return null;
    }

    /**
     * This method returns the original CustomItem from the ItemStack.
     * This only works if the itemStack contains a NamespacedKey corresponding to a CustomItem
     * that is saved!
     * <p>
     * If you need access to the original CustomItem variables use this method.
     * <p>
     * If you want to detect what plugin this ItemStack is from and use it's corresponding Reference use {@link #getReferenceByItemStack(ItemStack)} instead!
     *
     * @param itemStack
     * @return CustomItem the ItemStack is linked to, only if it is saved, else returns null
     */
    @Nullable
    public static CustomItem getByItemStack(ItemStack itemStack) {
        return WolfyUtilCore.getInstance().getRegistries().getCustomItems().getByItemStack(itemStack).orElse(null);
    }

    /**
     * @param itemMeta The ItemMeta to get the key from.
     * @return The CustomItems {@link BukkitNamespacedKey} from the ItemMeta; or null if the ItemMeta doesn't contain a key.
     */
    public static NamespacedKey getKeyOfItemMeta(ItemMeta itemMeta) {
        var container = itemMeta.getPersistentDataContainer();
        if (container.has(PERSISTENT_KEY_TAG, PersistentDataType.STRING)) {
            return BukkitNamespacedKey.of(container.get(PERSISTENT_KEY_TAG, PersistentDataType.STRING));
        }
        return null;
    }

    public boolean hasNamespacedKey() {
        return namespacedKey != null;
    }

    @Nullable
    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    /**
     * The replacement can be any of {@link APIReference} and it will replace this item when it is removed from the inventory using {@link #remove(ItemStack, int, Inventory, Location, boolean)}.
     *
     * @return True if this item has an replacement that is not AIR, else false.
     */
    public boolean hasReplacement() {
        return replacement != null && !replacement.getLinkedItem().getType().equals(Material.AIR);
    }

    /**
     * The replacement can be any of {@link APIReference} and it will replace this item when it is removed from the inventory using {@link #remove(ItemStack, int, Inventory, Location, boolean)}.
     *
     * @return The {@link APIReference} of the custom replacement.
     */
    @Nullable
    public APIReference getReplacement() {
        return hasReplacement() ? replacement : null;
    }

    /**
     * The replacement can be any of {@link APIReference} and it will replace this item when it is removed from the inventory using {@link #remove(ItemStack, int, Inventory, Location, boolean)}.
     *
     * @param replacement The replacement for this item.
     */
    public void setReplacement(@Nullable APIReference replacement) {
        this.replacement = replacement;
    }

    /**
     * @return The durability that is removed from the item when removed from an inventory using {@link #remove(ItemStack, int, Inventory, Location, boolean)}
     */
    public int getDurabilityCost() {
        return durabilityCost;
    }

    public void setDurabilityCost(int durabilityCost) {
        this.durabilityCost = durabilityCost;
    }

    /**
     * @return True if the item is removed by calling {@link #remove(ItemStack, int, Inventory, Location, boolean)}.
     */
    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public MetaSettings getMetaSettings() {
        return nbtChecks;
    }

    public void setMetaSettings(MetaSettings nbtChecks) {
        if (nbtChecks.isEmpty()) {
            //Add the CustomItemTag check, so the item will be checked correctly, but only if the item hasn't got any other checks already.
            nbtChecks.addCheck(new CustomItemTagMeta());
        }
        this.nbtChecks = nbtChecks;
    }

    /**
     * @return The burn time of the item inside the furnace.
     * @deprecated Use {@link #getFuelSettings()} and {@link FuelSettings#getBurnTime()}
     */
    @Deprecated
    @JsonIgnore
    public int getBurnTime() {
        return fuelSettings.getBurnTime();
    }

    /**
     * @param burnTime The burn time in ticks.
     * @deprecated Use {@link #getFuelSettings()} and {@link FuelSettings#setBurnTime(int)}
     */
    @Deprecated
    @JsonIgnore
    public void setBurnTime(int burnTime) {
        fuelSettings.setBurnTime(burnTime);
    }

    /**
     * @return The blocks in which the item can be used as fuel
     * @deprecated Use {@link #getFuelSettings()} and {@link FuelSettings#getAllowedBlocks()}
     */
    @Deprecated
    @JsonIgnore
    public List<Material> getAllowedBlocks() {
        return fuelSettings.getAllowedBlocks();
    }

    /**
     * @param allowedBlocks The blocks in which the item can be used as fuel
     * @deprecated Use {@link #getFuelSettings()} and {@link FuelSettings#setAllowedBlocks(List)}
     */
    @Deprecated
    @JsonIgnore
    public void setAllowedBlocks(List<Material> allowedBlocks) {
        fuelSettings.setAllowedBlocks(allowedBlocks);
    }

    /**
     * @return The EquipmentSlots this item can be equipped to.
     */
    public List<EquipmentSlot> getEquipmentSlots() {
        return equipmentSlots;
    }

    /**
     * @return True if the item has a custom {@link EquipmentSlot} it can be equipped to.
     */
    public boolean hasEquipmentSlot() {
        return !getEquipmentSlots().isEmpty();
    }

    public boolean hasEquipmentSlot(EquipmentSlot slot) {
        return hasEquipmentSlot() && getEquipmentSlots().contains(slot);
    }

    public void addEquipmentSlots(EquipmentSlot... slots) {
        for (EquipmentSlot slot : slots) {
            if (!equipmentSlots.contains(slot)) {
                equipmentSlots.add(slot);
            }
        }
    }

    public void removeEquipmentSlots(EquipmentSlot... slots) {
        equipmentSlots.removeAll(Arrays.asList(slots));
    }

    /**
     * Returns if this item is blocked to be equipped.
     * If true the item cannot be equipped even if it is a chestplate or other equipment.
     *
     * @return true if the item is blocked from equipping, else false
     */
    public boolean isBlockVanillaEquip() {
        return blockVanillaEquip;
    }

    public void setBlockVanillaEquip(boolean blockVanillaEquip) {
        this.blockVanillaEquip = blockVanillaEquip;
    }

    /**
     * Returns if this item is blocked in vanilla recipes.
     * This requires CustomCrafting to work.
     *
     * @return true if this item is blocked in vanilla recipes, else false
     */
    public boolean isBlockVanillaRecipes() {
        return blockVanillaRecipes;
    }

    public void setBlockVanillaRecipes(boolean blockVanillaRecipes) {
        this.blockVanillaRecipes = blockVanillaRecipes;
    }

    /**
     * BlockPlacement indicates if the item can be placed by a player or not.
     * If true the placement is blocked and the item cannot be placed.
     * If false the item can be placed.
     *
     * @return true if the placement is blocked, false otherwise
     */
    public boolean isBlockPlacement() {
        return blockPlacement;
    }

    public void setBlockPlacement(boolean blockPlacement) {
        this.blockPlacement = blockPlacement;
    }

    public FuelSettings getFuelSettings() {
        return fuelSettings;
    }

    public void setFuelSettings(FuelSettings fuelSettings) {
        this.fuelSettings = fuelSettings;
    }

    /**
     * Checks if the ItemStack is a similar to this CustomItem.
     * This method checks all the available ItemMeta on similarity and uses the meta options
     * when they are available.
     * Use {@link #isSimilar(ItemStack, boolean)} to only check for Material and Amount!
     *
     * @param otherItem the ItemStack that should be checked
     * @return true if the ItemStack is exactly the same as this CustomItem's ItemStack
     */
    public boolean isSimilar(ItemStack otherItem) {
        return isSimilar(otherItem, true);
    }

    /**
     * Checks if the ItemStack is similar to this CustomItem.
     * <p>If exactMeta is false it only checks for Material and amount.
     * <p>If exactMeta is true it checks all the available ItemMeta and uses the meta options
     * when they are available.
     *
     * @param otherItem the ItemStack that should be checked
     * @param exactMeta if the ItemMeta should be checked. If false only checks Material and Amount!
     * @return true if the ItemStack is equal to this CustomItems ItemStack
     */
    public boolean isSimilar(ItemStack otherItem, boolean exactMeta) {
        return isSimilar(otherItem, exactMeta, false);
    }

    /**
     * Checks if the ItemStack is similar to this CustomItem.
     *
     * <p>
     * <strong>Exact Meta:</strong>
     *     <ul>
     *         <li>false - Only checks for Material and amount (if ignoreAmount isn't enabled!).</li>
     *         <li>true - Checks all the available ItemMeta and uses the meta options when they are available.</li>
     *     </ul>
     * </p>
     *
     * <br>
     *
     * @param otherItem    the ItemStack that should be checked
     * @param exactMeta    if the ItemMeta should be checked. If false only checks Material and Amount!
     * @param ignoreAmount If true ignores the amount check.
     * @return true if the ItemStack is equal to this CustomItems ItemStack
     */
    public boolean isSimilar(ItemStack otherItem, boolean exactMeta, boolean ignoreAmount) {
        if (otherItem != null && otherItem.getType().equals(this.type) && (ignoreAmount || otherItem.getAmount() >= getAmount())) {
            if (hasNamespacedKey()) {
                return getMetaSettings().check(this, new ItemBuilder(otherItem));
            } else if (getApiReference() instanceof VanillaRef && (!hasItemMeta() && !exactMeta)) {
                return true;
            }
            return getApiReference().isValidItem(otherItem);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomItem that)) return false;
        return Double.compare(that.rarityPercentage, rarityPercentage) == 0 &&
                durabilityCost == that.durabilityCost &&
                consumed == that.consumed &&
                blockPlacement == that.blockPlacement &&
                blockVanillaEquip == that.blockVanillaEquip &&
                blockVanillaRecipes == that.blockVanillaRecipes &&
                Objects.equals(customDataMap, that.customDataMap) &&
                Objects.equals(namespacedKey, that.namespacedKey) &&
                Objects.equals(replacement, that.replacement) &&
                Objects.equals(fuelSettings, that.fuelSettings) &&
                Objects.equals(permission, that.permission) &&
                Objects.equals(equipmentSlots, that.equipmentSlots) &&
                Objects.equals(apiReference, that.apiReference) &&
                Objects.equals(particleContent, that.particleContent) &&
                Objects.equals(nbtChecks, that.nbtChecks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomDataMap(), getNamespacedKey(), getReplacement(), getPermission(), getRarityPercentage(), getFuelSettings(), getBlockSettings(), getDurabilityCost(), isConsumed(), blockPlacement, isBlockVanillaEquip(), isBlockVanillaRecipes(), getEquipmentSlots(), getApiReference(), getParticleContent(), getMetaSettings());
    }

    /**
     * Other than {@link #create()} it returns the real item and no copy!
     * Any changes made to this item may change the source Item!
     *
     * @return the linked item of the API reference
     */
    @Override
    public ItemStack getItemStack() {
        return apiReference.getLinkedItem();
    }

    /**
     * Gets a copy of the current version of the external linked item.<br>
     * The item can be linked to Vanilla, WolfyUtilities, ItemsAdder, Oraxen, MythicMobs or MMOItems
     *
     * @return the item from the external API that is linked to this object
     */
    public ItemStack create() {
        return create(getAmount());
    }

    /**
     * Gets a copy of the current version of the external linked item.<br>
     * The item can be linked to Vanilla, WolfyUtilities, ItemsAdder, Oraxen, MythicMobs or MMOItems.
     * <p>
     * If this CustomItem has an NamespacedKey it will include it in the NBT of the returned item!
     * </p>
     *
     * @param amount Modifies the amount of the returned ItemStack.
     * @return the item from the external API that is linked to this object
     */
    public ItemStack create(int amount) {
        var itemStack = apiReference.getLinkedItem().clone();
        if (this.hasNamespacedKey()) {
            var itemMeta = itemStack.getItemMeta();
            itemMeta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(WolfyCoreBukkit.getInstance(), "custom_item"), PersistentDataType.STRING, namespacedKey.toString());
            itemStack.setItemMeta(itemMeta);
        }
        if (amount > 0) {
            itemStack.setAmount(amount);
        }
        return itemStack;
    }

    /**
     * Returns the id item with the default stacksize of this instance!
     *
     * @return ItemStack that visually represents the namespacekey
     * @see #getIDItem(int)
     */
    @Deprecated
    public ItemStack getIDItem() {
        return getIDItem(getAmount());
    }

    /**
     * This item should only be used to visualize the namespacedkey!
     * It doesn't include a NBT Tag with the namspacekey and non of the WU features!
     *
     * @param amount The stacksize of the item
     * @return ItemStack that visually represents the namespacekey
     */
    @Deprecated
    public ItemStack getIDItem(int amount) {
        var itemStack = apiReference.getIdItem();
        if (amount > 0) {
            itemStack.setAmount(amount);
        }
        return itemStack;
    }

    public APIReference getApiReference() {
        return apiReference;
    }

    /**
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @deprecated Renamed to {@link #remove(ItemStack, int, Inventory, Location)} to better show it's functionality.
     */
    @Deprecated
    public void consumeItem(ItemStack input, int totalAmount, Inventory inventory, Location location) {
        remove(input, totalAmount, inventory, location);
    }

    /**
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @deprecated Renamed to {@link #remove(ItemStack, int, Inventory)} to better show it's functionality.
     */
    @Deprecated
    public void consumeItem(ItemStack input, int totalAmount, Inventory inventory) {
        remove(input, totalAmount, inventory);
    }

    /**
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @deprecated Renamed to {@link #remove(ItemStack, int, Location)} to better show it's functionality.
     */
    @Deprecated
    public ItemStack consumeItem(ItemStack input, int totalAmount, Location location) {
        return remove(input, totalAmount, location);
    }

    /**
     * @param input The input ItemStack, that is going to be edited.
     * @deprecated Replaced by {@link #removeUnStackableItem(ItemStack)}
     */
    @Deprecated
    public void consumeUnstackableItem(ItemStack input) {
        removeUnStackableItem(input);
    }

    /**
     * Removes the specified amount from the input ItemStack inside an inventory!
     * <p>
     * This method will directly edit the input ItemStack (Change it's type, amount, etc.) and won't return a result value!
     *
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or input ItemStack amount > 1)<b>:</b><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement/craft remain:
     * <ul>
     *     <li><b>Player is not null: </b>Tries to add the item/s to the players inventory. If there is no space it will drop the item at the position of the player.</li>
     *     <li><b>Player is null:</b>
     *          <ul>
     *              <li>
     *                  <b>Location is null, Inventory is not null:</b> Tries to add the item/s to the inventory.<br>
     *                  If there is no space, it tries to get the location of the inventory to drop the item/s there instead.<br>
     *                  In case the inventory has no location, the item/s are not dropped and will be lost! Be careful with this!
     *              </li>
     *              <li><b>Location is not null: </b>Drops the items at that location.</li>
     *              <li><b>Location and Inventory is null: </b>Item/s are neither added to an inventory or dropped.</li>
     *          </ul>
     *     </li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and input ItemStack amount == 1)<b>:</b><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input              The input ItemStack, that is also going to be edited.
     * @param totalAmount        The amount of this custom item that should be removed from the input.
     * @param inventory          The optional inventory to add the replacements to. (Only for stackable items)
     * @param player             The player to give the items to. If the players' inventory has space the craft remains are added. (Only for stackable items)
     * @param location           The location where the replacements should be dropped. (Only for stackable items)
     * @param replaceWithRemains If the Item should be replaced by the default craft remains (Only for un-stackable items).
     */
    public void remove(ItemStack input, int totalAmount, @Nullable Inventory inventory, @Nullable Player player, @Nullable Location location, boolean replaceWithRemains) {
        if (this.type.getMaxStackSize() == 1 && input.getAmount() == 1) {
            removeUnStackableItem(input, replaceWithRemains);
        } else {
            if (this.isConsumed()) {
                int amount = input.getAmount() - getAmount() * totalAmount;
                input.setAmount(amount);
            }
            applyStackableReplacement(totalAmount, replaceWithRemains, player, inventory, location);
        }
    }

    /**
     * Removes the specified amount from the input ItemStack inside an inventory!
     * <p>
     * This method will directly edit the input ItemStack (Change it's type, amount, etc.) and won't return a result value!
     *
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or input ItemStack amount > 1)<b>:</b><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement/craft remain:
     * <ul>
     *      <li>
     *          <b>Location is null, Inventory is not null:</b> Tries to add the item/s to the inventory.<br>
     *          If there is no space, it tries to get the location of the inventory to drop the item/s there instead.<br>
     *          In case the inventory has no location, the item/s are not dropped and will be lost! Be careful with this!
     *      </li>
     *      <li><b>Location is not null: </b>Drops the items at that location.</li>
     *      <li><b>Location and Inventory is null: </b>Item/s are neither added to an inventory or dropped.</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and input ItemStack amount == 1)<b>:</b><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input              The input ItemStack, that is also going to be edited.
     * @param totalAmount        The amount of this custom item that should be removed from the input.
     * @param inventory          The optional inventory to add the replacements to. (Only for stackable items)
     * @param location           The location where the replacements should be dropped. (Only for stackable items)
     * @param replaceWithRemains If the Item should be replaced by the default craft remains (Only for un-stackable items).
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory, Location location, boolean replaceWithRemains) {
        remove(input, totalAmount, inventory, null, location, replaceWithRemains);
    }

    /**
     * Removes the specified amount from the input ItemStack inside an inventory!
     * <p>
     * This method will directly edit the input ItemStack (Change it's type, amount, etc.) and won't return a result value!
     *
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or input ItemStack amount > 1)<b>:</b><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement/craft remain:
     * <ul>
     *      <li>
     *          <b>Location is null, Inventory is not null:</b> Tries to add the item/s to the inventory.<br>
     *          If there is no space, it tries to get the location of the inventory to drop the item/s there instead.<br>
     *          In case the inventory has no location, the item/s are not dropped and will be lost! Be careful with this!
     *      </li>
     *      <li><b>Location is not null: </b>Drops the items at that location.</li>
     *      <li><b>Location and Inventory is null: </b>Item/s are neither added to an inventory or dropped.</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and input ItemStack amount == 1)<b>:</b><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory, Location location) {
        remove(input, totalAmount, inventory, location, true);
    }

    /**
     * Removes the specified amount from the input ItemStack inside an inventory!
     * <p>
     * This method will directly edit the input ItemStack (Change it's type, amount, etc.) and won't return a result value!
     *
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or input ItemStack amount > 1)<b>:</b><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement/craft remain:
     * <ul>
     *      <li>
     *          <b>Inventory is not null:</b> Tries to add the item/s to the inventory.<br>
     *          If there is no space, it tries to get the location of the inventory to drop the item/s there instead.<br>
     *          In case the inventory has no location, the item/s are not dropped and will be lost! Be careful with this!
     *      </li>
     *      <li><b>Inventory is null: </b>Item/s are neither added to an inventory or dropped.</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and input ItemStack amount == 1)<b>:</b><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory) {
        remove(input, totalAmount, inventory, null);
    }

    /**
     * Removes the specified amount from the input ItemStack inside a inventory!
     * <p>
     * This method will directly edit the input ItemStack and won't return a result value.
     *
     * <p>
     * <strong>Stackable:</strong><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement:
     * <ul>
     *     <li><b>If location is not null,</b> then it will drop the items at that location.</li>
     *     <li><b>If location is null,</b> then the replacement items are neither dropped nor added to the inventory!</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <strong>Un-stackable:</strong><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack)} method and replaces the item with it's craft remains if available.
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @return The original input {@link ItemStack} that was directly edited by the method.
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public ItemStack remove(ItemStack input, int totalAmount, Location location) {
        remove(input, totalAmount, null, location);
        return input;
    }

    private void applyStackableReplacement(int totalAmount, boolean replaceWithRemains, @Nullable Player player, @Nullable Inventory inventory, @Nullable Location location) {
        ItemStack replacement = isConsumed() && replaceWithRemains && craftRemain != null ? new ItemStack(craftRemain) : null;
        if (this.hasReplacement()) {
            assert getReplacement() != null;
            replacement = new CustomItem(getReplacement()).create();
        }
        if (replacement != null) {
            replacement.setAmount(replacement.getAmount() * totalAmount);
            if (player != null) {
                var playerInv = player.getInventory();
                if (InventoryUtils.hasInventorySpace(playerInv, replacement)) {
                    playerInv.addItem(replacement);
                    return;
                }
                location = player.getLocation();
            }
            if (location == null) {
                if (inventory == null) return;
                if (InventoryUtils.hasInventorySpace(inventory, replacement)) {
                    inventory.addItem(replacement);
                    return;
                }
                location = inventory.getLocation();
            }
            if (location != null && location.getWorld() != null) {
                location.getWorld().dropItemNaturally(location.add(0.5, 1.0, 0.5), replacement);
            }
        }
    }

    /**
     * Removes the input as an un-stackable item.
     * <p>
     * Items that have craft remains by default will be replaced with the according {@link Material} <br>
     * Like Buckets, Potions, Stew/Soup.
     * </p>
     * <p>
     * If this CustomItem has a custom replacement then the input will be replaced with that.
     * </p>
     * <br>
     *
     * @param input The input ItemStack, that is going to be edited.
     */
    public void removeUnStackableItem(ItemStack input) {
        removeUnStackableItem(input, true);
    }

    /**
     * Removes the input as an un-stackable item.
     * <p>
     * Items that have craft remains by default will be replaced with the according {@link Material} <br>
     * Like Buckets, Potions, Stew/Soup.
     * </p>
     * <p>
     * If this CustomItem has a custom replacement then the input will be replaced with that.
     * </p>
     * <br>
     *
     * @param input              The input ItemStack, that is going to be edited.
     * @param replaceWithRemains If the item should be replaced by it's remains if removed. Not including custom replacement options!
     */
    public void removeUnStackableItem(ItemStack input, boolean replaceWithRemains) {
        if (this.isConsumed()) {
            if (craftRemain != null && replaceWithRemains) {
                input.setType(craftRemain);
                input.setItemMeta(Bukkit.getItemFactory().getItemMeta(craftRemain));
            } else {
                input.setAmount(0);
            }
        }
        if (this.hasReplacement()) {
            ItemStack replace = new CustomItem(this.getReplacement()).create();
            input.setType(replace.getType());
            input.setItemMeta(replace.getItemMeta());
            input.setData(replace.getData());
            input.setAmount(replace.getAmount());
        } else if (this.getDurabilityCost() != 0) {
            var itemBuilder = new ItemBuilder(input);
            if (itemBuilder.hasCustomDurability()) {
                itemBuilder.setCustomDamage(itemBuilder.getCustomDamage() + this.getDurabilityCost());
                return;
            }
            var itemMeta = input.getItemMeta();
            if (itemMeta instanceof Damageable) {
                int damage = ((Damageable) itemMeta).getDamage() + this.getDurabilityCost();
                if (damage > create().getType().getMaxDurability()) {
                    input.setAmount(0);
                } else {
                    ((Damageable) itemMeta).setDamage(damage);
                }
            }
            input.setItemMeta(itemMeta);
        }
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or stack count > 1)<b>:</b><br>
     * The stack is shrunk by the specified amount (<strong><code>{@link #getAmount()} * totalAmount</code></strong>)
     * <p>
     * If this CustomItem has a custom replacement:<br>
     * This calls the stackReplacement function with the shrunken stack and this CustomItem.
     * It is meant for applying the stackable replacement items.<br>
     * For default behaviour see {@link #shrink(ItemStack, int, boolean, Inventory, Player, Location)} and {@link #shrinkUnstackableItem(ItemStack, boolean)}
     * </p>
     * </p>
     * <p>
     * <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and stack count == 1)<b>:</b><br>
     * Redirects to {@link #removeUnStackableItem(ItemStack, boolean)}<br>
     * </p>
     * </p>
     * <br>
     *
     * @param stack            The input ItemStack, that is also going to be edited.
     * @param count            The amount of this custom item that should be removed from the input.
     * @param useRemains       If the Item should be replaced by the default craft remains.
     * @param stackReplacement Behaviour of how to apply the replacements of stackable items.
     * @return  The manipulated stack, default remain, or custom remains.
     */
    public ItemStack shrink(@NotNull ItemStack stack, int count, boolean useRemains, @NotNull BiFunction<CustomItem, ItemStack, ItemStack> stackReplacement) {
        if (this.type.getMaxStackSize() == 1 && stack.getAmount() == 1) {
            return shrinkUnstackableItem(stack, useRemains);
        }
        if (this.isConsumed()) {
            int amount = stack.getAmount() - getAmount() * count;
            if (amount <= 0) {
                stack = new ItemStack(Material.AIR);
            } else {
                stack.setAmount(amount);
            }
            return stackReplacement.apply(this, stack);
        }
        return stack;
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or stack count > 1)<b>:</b><br>
     * The stack is shrunk by the specified amount (<strong><code>{@link #getAmount()} * totalAmount</code></strong>)
     * <p>
     * If this CustomItem has a custom replacement:<br>
     * <ul>
     *   <li><b>Location: </b>Used as the drop location for remaining items. <br>May be overridden by options below.</li>
     *   <li>
     *     <b>Player: </b>Adds items to the players inventory.
     *     <br>Remaining items are still in the pool for the next options below.
     *     <br>Player location is used as the drop location for remaining items.</li>
     *   <li>
     *     <b>Inventory:</b> Adds items to the inventory.
     *     <br>Remaining items are still in the pool for the next options below.
     *     <br>If location not available yet: uses inventory location as drop location for remaining items.
     *   </li>
     * </ul>
     * All remaining items that cannot be added to player or the other inventory are dropped at the specified location.<br>
     * <b>Warning! If you do not provide a location via <code>player</code>, <code>inventory</code>, or <code>inventory</code>, then the remaining items are discarded!</b><br>
     * For custom behaviour see {@link #shrink(ItemStack, int, boolean, BiFunction)}.
     *
     * </p>
     * </p>
     * <p>
     * <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and stack count == 1)<b>:</b><br>
     * Redirects to {@link #removeUnStackableItem(ItemStack, boolean)}<br>
     * </p>
     * </p>
     * <br>
     *
     * @param stack      The input ItemStack, that is also going to be edited.
     * @param count      The amount of this custom item that should be removed from the input.
     * @param useRemains If the Item should be replaced by the default craft remains.
     * @param inventory  The optional inventory to add the replacements to. (Only for stackable items)
     * @param player     The player to give the items to. If the players' inventory has space the craft remains are added. (Only for stackable items)
     * @param location   The location where the replacements should be dropped. (Only for stackable items)
     * @return  The manipulated stack, default remain, or custom remains.
     */
    public ItemStack shrink(ItemStack stack, int count, boolean useRemains, @Nullable final Inventory inventory, @Nullable final Player player, @Nullable final Location location) {
        return shrink(stack, count, useRemains, (customItem, resultStack) -> {
            ItemStack replacement = isConsumed() && useRemains && craftRemain != null ? new ItemStack(craftRemain) : null;
            if (this.hasReplacement()) {
                assert getReplacement() != null;
                replacement = new CustomItem(getReplacement()).create();
            }
            if (!ItemUtils.isAirOrNull(replacement)) {
                int replacementAmount = replacement.getAmount() * count;
                if (ItemUtils.isAirOrNull(resultStack)) {
                    int returnableAmount = Math.min(replacement.getMaxStackSize(), replacementAmount);
                    replacementAmount -= returnableAmount;
                    resultStack = replacement.clone();
                    resultStack.setAmount(replacementAmount);
                }
                if (replacementAmount > 0) {
                    replacement.setAmount(replacementAmount);
                    Location loc = location;
                    if (player != null) {
                        replacement = player.getInventory().addItem(replacement).get(0);
                        loc = player.getLocation();
                    }
                    if (inventory != null && replacement != null) {
                        replacement = inventory.addItem(replacement).get(0);
                        if (loc == null) loc = inventory.getLocation();
                    }
                    if (loc != null && replacement != null && loc.getWorld() != null) {
                        loc.getWorld().dropItemNaturally(loc.add(0.5, 1.0, 0.5), replacement);
                    }
                }
            }
            return resultStack;
        });
    }

    /**
     * Shrinks the specified stack and returns the manipulated or replaced item!
     * <p>
     *     This firstly checks for custom replacements (remains) and sets it as the result.<br>
     *     Then handles damaging of the stack, if there is a specified durability cost.<br>
     *     In case the stack breaks due damage it is replaced by the result, specified earlier.
     * </p>
     *
     * @param stack         The stack to shrink
     * @param useRemains    If the Item should be replaced by the default craft remains.
     * @return The manipulated (damaged) stack, default remain, or custom remains.
     */
    public ItemStack shrinkUnstackableItem(ItemStack stack, boolean useRemains) {
        ItemStack result = new ItemStack(Material.AIR);
        if (this.hasReplacement()) {
            result = this.getReplacement() == null ? new ItemStack(Material.AIR) : new CustomItem(this.getReplacement()).create();
        } else if (this.isConsumed() && craftRemain != null && useRemains) {
            result = new ItemStack(craftRemain);
        }
        if (this.getDurabilityCost() != 0) {
            // handle custom durability
            var itemBuilder = new ItemBuilder(stack);
            if (itemBuilder.hasCustomDurability()) {
                int damage = itemBuilder.getCustomDamage() + this.getDurabilityCost();
                if (damage > itemBuilder.getCustomDurability()) {
                    return result;
                }
                itemBuilder.setCustomDamage(damage);
                return itemBuilder.create();
            }
            // handle vanilla durability
            if (stack.getItemMeta() instanceof Damageable itemMeta) {
                int damage = itemMeta.getDamage() + this.getDurabilityCost();
                if (damage > type.getMaxDurability()) {
                    return result;
                }
                itemMeta.setDamage(damage);
                stack.setItemMeta(itemMeta);
                return stack;
            }
        }
        return result;
    }

    private Material getCraftRemain() {
        var item = getItemStack();
        if (!ItemUtils.isAirOrNull(item) && item.getType().isItem()) {
            Material replaceType = item.getType().getCraftingRemainingItem();
            if (replaceType != null) return replaceType;
            return switch (item.getType().name()) {
                case "LAVA_BUCKET", "MILK_BUCKET", "WATER_BUCKET", "COD_BUCKET", "SALMON_BUCKET", "PUFFERFISH_BUCKET", "TROPICAL_FISH_BUCKET" ->
                        Material.BUCKET;
                case "POTION" -> Material.GLASS_BOTTLE;
                case "BEETROOT_SOUP", "MUSHROOM_STEW", "RABBIT_STEW" -> Material.BOWL;
                default -> null;
            };
        }
        return null;
    }

    /**
     * @return True if this item requires permission to be used, else false.
     */
    public boolean hasPermission() {
        return !permission.isEmpty();
    }

    /**
     * Gets the permission string of this CustomItem.
     *
     * @return The permission string of this item
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission String.
     *
     * @param permission The new permission string
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * @return The weight of the item. Can be changed by the {@link APIReference} of the item.
     */
    public double getWeight() {
        return rarityPercentage;
    }

    /**
     * @param weight The weight of the item. Can be changed by the {@link APIReference} of the item.
     */
    public void setWeight(double weight) {
        setRarityPercentage(weight);
    }

    /**
     * @return The weight of the item. Can be changed by the {@link APIReference} of the item.
     * @deprecated replaced with {@link #getWeight()}
     */
    @Deprecated
    public double getRarityPercentage() {
        return getWeight();
    }

    /**
     * @param rarityPercentage The weight of the item. Can be changed by the {@link APIReference} of the item.
     * @deprecated replaced with {@link #setWeight(double)}
     */
    @Deprecated
    public void setRarityPercentage(double rarityPercentage) {
        this.rarityPercentage = rarityPercentage;
    }

    @JsonSetter("data")
    private void setDataList(List<CustomItemData> data) {
        for (CustomItemData itemData : data) {
            addOrReplaceData(itemData);
        }
    }

    @JsonGetter("data")
    private List<CustomItemData> getDataList() {
        return indexedData.values().stream().toList();
    }

    public <T extends CustomItemData> T addOrReplaceData(T data) {
        Class<T> type = (Class<T>) data.getClass();
        return type.cast(indexedData.put(getKeyForData(type), data));
    }

    public CustomItemData addDataIfAbsent(CustomItemData data) {
        return indexedData.putIfAbsent(data.getNamespacedKey(), data);
    }

    public CustomItemData computeDataIfAbsent(NamespacedKey id, Function<NamespacedKey, CustomItemData> mappingFunction) {
        return indexedData.computeIfAbsent(id, mappingFunction);
    }

    public <T extends CustomItemData> T computeDataIfAbsent(Class<T> type, Function<NamespacedKey, T> mappingFunction) {
        return type.cast(indexedData.computeIfAbsent(getKeyForData(type), mappingFunction));
    }

    public CustomItemData computeDataIfPresent(NamespacedKey id, BiFunction<NamespacedKey, CustomItemData, CustomItemData> remappingFunction) {
        return indexedData.computeIfPresent(id, remappingFunction);
    }

    public <T extends CustomItemData> T computeDataIfPresent(Class<T> type, BiFunction<NamespacedKey, T, T> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        NamespacedKey key = getKeyForData(type);
        T oldValue = type.cast(indexedData.get(key));
        if (oldValue != null) {
            T newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                indexedData.put(key, newValue);
                return newValue;
            } else {
                indexedData.remove(key);
                return null;
            }
        }
        return null;
    }

    public Optional<CustomItemData> getData(NamespacedKey id) {
        return Optional.ofNullable(indexedData.get(id));
    }

    public <T extends CustomItemData> Optional<T> getData(Class<T> type) {
        return getData(getKeyForData(type)).map(type::cast);
    }

    private static NamespacedKey getKeyForData(Class<? extends CustomItemData> type) {
        return WolfyCoreBukkit.getInstance().getRegistries().getCustomItemDataTypeRegistry().getKey(type);
    }

    /**
     * Used to deserialize the old CustomData content.<br>
     * This is replaced by a better modular system {@link CustomItemData}
     */
    @Deprecated
    @JsonAlias("custom_data")
    @JsonSetter("customDataMap")
    private void setCustomDataMap(JsonNode dataNode) {
        if (dataNode == null || dataNode.isNull()) return;
        WolfyUtilCore core = WolfyUtilCore.getInstance();
        BukkitRegistries registries = core.getRegistries();
        Iterator<Map.Entry<String, JsonNode>> itr = dataNode.fields();
        while (itr.hasNext()) {
            Map.Entry<String, JsonNode> entry = itr.next();
            var namespacedKey = entry.getKey().contains(":") ? BukkitNamespacedKey.of(entry.getKey()) : /* Backwards compatibility */ registries.getCustomItemData().keySet().parallelStream().filter(key -> key.getKey().equals(entry.getKey())).findFirst().orElse(null);
            if (namespacedKey != null) {
                CustomData.Provider<?> provider = registries.getCustomItemData().get(namespacedKey);
                if (provider != null) {
                    CustomData data = provider.createData();
                    try {
                        data.readFromJson(this, entry.getValue(), core.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper().getDeserializationContext());
                        this.customDataMap.put(namespacedKey, data);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Deprecated
    @JsonGetter("customDataMap")
    public Map<NamespacedKey, CustomData> getCustomDataMap() {
        return customDataMap;
    }

    @Deprecated
    public CustomData getCustomData(NamespacedKey namespacedKey) {
        return customDataMap.get(namespacedKey);
    }

    @Deprecated
    public void addCustomData(NamespacedKey namespacedKey, CustomData customData) {
        this.customDataMap.put(namespacedKey, customData);
    }

    @JsonGetter
    @NotNull
    public ParticleContent getParticleContent() {
        return particleContent;
    }

    @JsonAlias("particles")
    @JsonSetter
    public void setParticleContent(ObjectNode particlesNode) {
        if (particlesNode == null) {
            this.particleContent = new ParticleContent();
            return;
        }
        this.particleContent = Objects.requireNonNullElse(JacksonUtil.getObjectMapper().convertValue(particlesNode, ParticleContent.class), new ParticleContent());

        if (Streams.stream(particlesNode.fieldNames()).anyMatch(s -> ParticleLocation.valueOf(s.toUpperCase(Locale.ROOT)).isDeprecated())) { //Old version. Conversion required.
            var playerSettings = Objects.requireNonNullElse(this.particleContent.getPlayer(), new ParticleContent.PlayerSettings()); //Create or get player settings
            particlesNode.fields().forEachRemaining(entry -> {
                ParticleLocation loc = ParticleLocation.valueOf(entry.getKey());
                JsonNode value = entry.getValue();
                if (value.isObject() && value.has("effect")) {
                    var animation = WolfyUtilCore.getInstance().getRegistries().getParticleAnimations().get(JacksonUtil.getObjectMapper().convertValue(value.get("effect"), BukkitNamespacedKey.class));
                    if (animation != null) {
                        loc.applyOldPlayerAnimation(playerSettings, animation);
                    }
                }
            });
        }
    }

    @JsonGetter
    public ActionSettings getActionSettings() {
        return actionSettings;
    }

    @JsonSetter
    public void setActionSettings(ActionSettings actionSettings) {
        this.actionSettings = actionSettings == null ? new ActionSettings() : actionSettings;
    }

    public CustomBlockSettings getBlockSettings() {
        return blockSettings;
    }

    /**
     * @return Always true due to system changes!
     * @deprecated This feature was removed. This method is still here in case anyone used it.
     */
    @Deprecated(forRemoval = true)
    public boolean isAdvanced() {
        return true;
    }

    /**
     * @deprecated This feature was removed. This method is still here in case anyone used it.
     */
    @Deprecated(forRemoval = true)
    public void setAdvanced(boolean advanced) {
    }

    /**
     * Gets the amount of the linked ItemStack or if the custom amount
     * is bigger than 0 gets the custom amount.
     *
     * @return actual amount of CustomItem
     */
    public int getAmount() {
        return getApiReference().getAmount();
    }

    /**
     * Sets the amount of the linked item.
     *
     * @param amount The new amount of the item.
     */
    public void setAmount(int amount) {
        getApiReference().setAmount(amount);
    }

    public boolean isBlock() {
        return type.isBlock() || (getApiReference() instanceof ItemsAdderRef iaRef && WolfyCoreBukkit.getInstance().getCompatibilityManager().getPlugins()
                .evaluateIfAvailable("ItemsAdder", ItemsAdderIntegration.class, ia -> ia.getStackInstance(iaRef.getItemID()).map(CustomStack::isBlock).orElse(false)));
    }

    @Override
    public String toString() {
        return "CustomItem{" +
                "customDataMap=" + customDataMap +
                ", namespacedKey=" + namespacedKey +
                ", craftRemain=" + craftRemain +
                ", consumed=" + consumed +
                ", replacement=" + replacement +
                ", durabilityCost=" + durabilityCost +
                ", permission='" + permission + '\'' +
                ", rarityPercentage=" + rarityPercentage +
                ", fuelSettings=" + fuelSettings +
                ", blockPlacement=" + blockPlacement +
                ", blockVanillaEquip=" + blockVanillaEquip +
                ", blockVanillaRecipes=" + blockVanillaRecipes +
                ", equipmentSlots=" + equipmentSlots +
                ", apiReference=" + apiReference +
                ", particleContent=" + particleContent +
                ", metaSettings=" + nbtChecks +
                "} " + super.toString();
    }
}