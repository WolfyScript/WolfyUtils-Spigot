package com.wolfyscript.utilities.bukkit.world.items;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.common.nbt.NBTTagConfig;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigCompound;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigList;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListCompound;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListDouble;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListFloat;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListInt;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListIntArray;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListLong;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListString;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigPrimitive;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.operator.BoolOperatorConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntegerConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BukkitItemStackConfig extends ItemStackConfig<ItemStack> {

    @JsonCreator
    public BukkitItemStackConfig(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("itemId") String itemId) {
        super(wolfyUtils, itemId);
    }

    public BukkitItemStackConfig(WolfyUtils wolfyUtils, ItemStack stack) {
        super(wolfyUtils, stack.getType().getKey().toString());

        this.amount = new ValueProviderIntegerConst(wolfyUtils, stack.getAmount());
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (meta.hasLore()) {
                this.lore = meta.getLore().stream().map(s -> new ValueProviderStringConst(wolfyUtils, s)).collect(Collectors.toList());
            }
            this.unbreakable = new BoolOperatorConst(wolfyUtils, meta.isUnbreakable());
            this.customModelData = new ValueProviderIntegerConst(wolfyUtils, meta.getCustomModelData());
        }
        this.enchants = stack.getEnchantments().entrySet().stream().collect(Collectors.toMap(entry-> entry.getKey().getKey().toString(), entry -> new ValueProviderIntegerConst(wolfyUtils, entry.getValue())));

        NBTItem nbtItem = new NBTItem(stack);
        for (String key : nbtItem.getKeys()) {
            nbtItem.getType(key); // TODO
        }

    }

    @Override
    public ItemStack constructItemStack() {
        Material type = Material.matchMaterial(itemId);
        if (type != null) {
            ItemStack itemStack = new ItemStack(type);
            itemStack.setAmount(amount.getValue());

            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                //TODO: Adventure format
                meta.setDisplayName(name.getValue());
                meta.setLore(lore.stream().map(ValueProvider::getValue).toList());

                for (Map.Entry<String, ValueProvider<Integer>> entry : enchants.entrySet()) {
                    Enchantment enchant = Enchantment.getByKey(NamespacedKey.fromString(entry.getKey()));
                    if (enchant != null) {
                        meta.addEnchant(enchant, entry.getValue().getValue(), true);
                    }
                }

                meta.setCustomModelData(customModelData.getValue());
                meta.setUnbreakable(unbreakable.evaluate(new EvalContext()));
                itemStack.setItemMeta(meta);

                // Apply the NBT of the stack
                NBTItem nbtItem = new NBTItem(itemStack);
                applyCompound(nbtItem, getNbt());
                itemStack = nbtItem.getItem();
            }
            return itemStack;
        }
        return null;
    }

    private void applyCompound(NBTCompound compound, NBTTagConfigCompound config) {
        for (Map.Entry<String, NBTTagConfig> entry : config.getChildren().entrySet()) {
            var tagConfig = entry.getValue();
            var tagName = entry.getKey();
            if (tagConfig instanceof NBTTagConfigCompound configCompound) {
                applyCompound(compound.addCompound(entry.getKey()), configCompound);
            } else if (tagConfig instanceof NBTTagConfigList<?> configList) {
                applyList(compound, tagName, configList);
            }
        }
    }

    private void applyList(NBTCompound compound, String tagName, NBTTagConfigList<?> configList) {
        if (configList instanceof NBTTagConfigListCompound configListCompound) {
            NBTCompoundList list = compound.getCompoundList(tagName);
            for (NBTTagConfigList.Element<NBTTagConfigCompound> element : configListCompound.getElements()) {
                applyCompound(list.addCompound(), element.getValue());
            }
        } else if (configList instanceof NBTTagConfigListInt configListInt) {
           applyPrimitiveList(compound.getIntegerList(tagName), configListInt);
        } else if (configList instanceof NBTTagConfigListLong configListLong) {
            applyPrimitiveList(compound.getLongList(tagName), configListLong);
        } else if (configList instanceof NBTTagConfigListFloat configListFloat) {
            applyPrimitiveList(compound.getFloatList(tagName), configListFloat);
        } else if (configList instanceof NBTTagConfigListDouble configListDouble) {
            applyPrimitiveList(compound.getDoubleList(tagName), configListDouble);
        } else if (configList instanceof NBTTagConfigListString configListString) {
            applyPrimitiveList(compound.getStringList(tagName), configListString);
        } else if (configList instanceof NBTTagConfigListIntArray configListIntArray) {
            applyPrimitiveList(compound.getIntArrayList(tagName), configListIntArray);
        }
    }

    private <T> void applyPrimitiveList(NBTList<T> nbtList, NBTTagConfigList<? extends NBTTagConfigPrimitive<T>> configPrimitive) {
        for (NBTTagConfigList.Element<? extends NBTTagConfigPrimitive<T>> element : configPrimitive.getElements()) {
            nbtList.add(element.getValue().getValue().getValue()); // This looks weird, but it will provide more options in the future.
        }
    }


}
