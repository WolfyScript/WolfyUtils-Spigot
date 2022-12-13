package com.wolfyscript.utilities.bukkit.world.items;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.common.nbt.NBTTagConfig;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigBoolean;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigByte;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigByteArray;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigCompound;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigDouble;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigFloat;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigInt;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigIntArray;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigList;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListCompound;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListDouble;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListFloat;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListInt;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListIntArray;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListLong;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListPrimitive;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigListString;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigLong;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigPrimitive;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigShort;
import com.wolfyscript.utilities.common.nbt.NBTTagConfigString;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.eval.operator.BoolOperatorConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderByteArrayConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderByteConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderDoubleConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderFloatConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntArrayConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntegerConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderLongConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderShortConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBTList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
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
        this.enchants = stack.getEnchantments().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getKey().toString(), entry -> new ValueProviderIntegerConst(wolfyUtils, entry.getValue())));

        this.nbt = readFromItemStack(new NBTItem(stack), "", null);
    }

    @Override
    public ItemStack constructItemStack() {
        return constructItemStack(new EvalContext());
    }

    @Override
    public ItemStack constructItemStack(EvalContext context) {
        Material type = Material.matchMaterial(itemId);
        if (type != null) {
            ItemStack itemStack = new ItemStack(type);
            itemStack.setAmount(amount.getValue(context));

            // Apply the NBT of the stack
            NBTItem nbtItem = new NBTItem(itemStack);
            applyCompound(nbtItem, getNbt(), context);
            itemStack = nbtItem.getItem();

            // Apply ItemMeta afterwards to override possible NBT Tags
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                //TODO: Adventure format
                meta.setDisplayName(name.getValue(context));
                meta.setLore(lore.stream().map(line -> line.getValue(context)).toList());

                for (Map.Entry<String, ValueProvider<Integer>> entry : enchants.entrySet()) {
                    Enchantment enchant = Enchantment.getByKey(NamespacedKey.fromString(entry.getKey()));
                    if (enchant != null) {
                        meta.addEnchant(enchant, entry.getValue().getValue(context), true);
                    }
                }

                meta.setCustomModelData(customModelData.getValue(context));
                meta.setUnbreakable(unbreakable.evaluate(context));
                itemStack.setItemMeta(meta);
            }
            return itemStack;
        }
        return null;
    }

    private NBTTagConfigCompound readFromItemStack(ReadableNBT currentCompound, String entryKey, NBTTagConfig parent) {
        NBTTagConfigCompound configCompound = new NBTTagConfigCompound(wolfyUtils, parent);
        Map<String, NBTTagConfig> children = new HashMap<>();
        for (String key : currentCompound.getKeys()) {
            NBTTagConfig childConfig = switch (currentCompound.getType(key)) {
                case NBTTagCompound -> readFromItemStack(currentCompound.getCompound(key), key, configCompound);
                case NBTTagList -> switch (currentCompound.getListType(key)) {
                    case NBTTagCompound -> {
                        NBTTagConfigListCompound compoundConfigList = new NBTTagConfigListCompound(wolfyUtils, parent, List.of());
                        ReadableNBTList<ReadWriteNBT> compoundList = currentCompound.getCompoundList(key);
                        List<NBTTagConfigCompound> elements = new ArrayList<>();
                        for (ReadWriteNBT listCompound : compoundList) {
                            elements.add(readFromItemStack(listCompound, "", compoundConfigList));
                        }
                        compoundConfigList.setValues(elements);
                        yield compoundConfigList;
                    }
                    case NBTTagInt ->
                            readPrimitiveList(currentCompound.getIntegerList(key), new NBTTagConfigListInt(wolfyUtils, configCompound, new ArrayList<>()), (listInt, integer) -> new NBTTagConfigInt(wolfyUtils, listInt, new ValueProviderIntegerConst(wolfyUtils, integer)));
                    case NBTTagIntArray ->
                            readPrimitiveList(currentCompound.getIntArrayList(key), new NBTTagConfigListIntArray(wolfyUtils, configCompound, new ArrayList<>()), (listIntArray, intArray) -> new NBTTagConfigIntArray(wolfyUtils, listIntArray, new ValueProviderIntArrayConst(wolfyUtils, intArray)));
                    case NBTTagLong ->
                            readPrimitiveList(currentCompound.getLongList(key), new NBTTagConfigListLong(wolfyUtils, configCompound, new ArrayList<>()), (listConfig, aLong) -> new NBTTagConfigLong(wolfyUtils, listConfig, new ValueProviderLongConst(wolfyUtils, aLong)));
                    case NBTTagFloat ->
                            readPrimitiveList(currentCompound.getFloatList(key), new NBTTagConfigListFloat(wolfyUtils, configCompound, new ArrayList<>()), (listConfig, aFloat) -> new NBTTagConfigFloat(wolfyUtils, listConfig, new ValueProviderFloatConst(wolfyUtils, aFloat)));
                    case NBTTagDouble ->
                            readPrimitiveList(currentCompound.getDoubleList(key), new NBTTagConfigListDouble(wolfyUtils, configCompound, new ArrayList<>()), (listConfig, aDouble) -> new NBTTagConfigDouble(wolfyUtils, listConfig, new ValueProviderDoubleConst(wolfyUtils, aDouble)));
                    case NBTTagString ->
                            readPrimitiveList(currentCompound.getStringList(key), new NBTTagConfigListString(wolfyUtils, configCompound, new ArrayList<>()), (listConfig, aString) -> new NBTTagConfigString(wolfyUtils, listConfig, new ValueProviderStringConst(wolfyUtils, aString)));
                    default -> null;
                };
                case NBTTagByte ->
                        new NBTTagConfigByte(wolfyUtils, configCompound, new ValueProviderByteConst(wolfyUtils, currentCompound.getByte(key)));
                case NBTTagByteArray ->
                        new NBTTagConfigByteArray(wolfyUtils, configCompound,new ValueProviderByteArrayConst(wolfyUtils, currentCompound.getByteArray(key)));
                case NBTTagShort ->
                        new NBTTagConfigShort(wolfyUtils, configCompound, new ValueProviderShortConst(wolfyUtils, currentCompound.getShort(key)));
                case NBTTagInt ->
                        new NBTTagConfigInt(wolfyUtils, configCompound, new ValueProviderIntegerConst(wolfyUtils, currentCompound.getInteger(key)));
                case NBTTagIntArray ->
                        new NBTTagConfigIntArray(wolfyUtils, configCompound, new ValueProviderIntArrayConst(wolfyUtils, currentCompound.getIntArray(key)));
                case NBTTagLong ->
                        new NBTTagConfigLong(wolfyUtils, configCompound, new ValueProviderLongConst(wolfyUtils, currentCompound.getLong(key)));
                case NBTTagFloat ->
                        new NBTTagConfigFloat(wolfyUtils, configCompound, new ValueProviderFloatConst(wolfyUtils, currentCompound.getFloat(key)));
                case NBTTagDouble ->
                        new NBTTagConfigDouble(wolfyUtils, configCompound, new ValueProviderDoubleConst(wolfyUtils, currentCompound.getDouble(key)));
                case NBTTagString ->
                        new NBTTagConfigString(wolfyUtils, configCompound, new ValueProviderStringConst(wolfyUtils, currentCompound.getString(key)));
                default -> null;
            };
            if (childConfig != null) {
                children.put(key, childConfig);
            }
        }
        configCompound.setChildren(children);
        return configCompound;
    }

    /**
     * Reads the elements of a NBTList and converts them, using the given function, to the NBTTagConfig.
     *
     * @param nbtList            The NBTList from the NBTItemAPI
     * @param configList         The instance of the NBTTagConfigList to load the elements into.
     * @param elementConstructor This constructs each element of list.
     * @param <T>                The primitive data type.
     * @param <VAL>              The type of the Element config.
     * @return The configList instance with the new elements.
     */
    private <T, VAL extends NBTTagConfigPrimitive<T>> NBTTagConfigListPrimitive<T, VAL> readPrimitiveList(ReadableNBTList<T> nbtList, NBTTagConfigListPrimitive<T, VAL> configList, BiFunction<NBTTagConfigListPrimitive<T, VAL>, T, VAL> elementConstructor) {
        configList.setValues(nbtList.toListCopy().stream().map(value -> elementConstructor.apply(configList, value)).toList());
        return configList;
    }

    private void applyCompound(NBTCompound compound, NBTTagConfigCompound config, EvalContext context) {
        for (Map.Entry<String, NBTTagConfig> entry : config.getChildren().entrySet()) {
            var tagConfig = entry.getValue();
            var tagName = entry.getKey();
            if (tagConfig instanceof NBTTagConfigCompound configCompound) {
                applyCompound(compound.addCompound(entry.getKey()), configCompound, context);
            } else if (tagConfig instanceof NBTTagConfigList<?> configList) {
                applyList(compound, tagName, configList, context);
            } else if (tagConfig instanceof NBTTagConfigByte configByte) {
                compound.setByte(entry.getKey(), configByte.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigByteArray configByteArray) {
                compound.setByteArray(entry.getKey(), configByteArray.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigShort configShort) {
                compound.setShort(entry.getKey(), configShort.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigInt configInt) {
                compound.setInteger(entry.getKey(), configInt.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigIntArray configIntArray) {
                compound.setIntArray(entry.getKey(), configIntArray.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigLong configLong) {
                compound.setLong(entry.getKey(), configLong.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigFloat configFloat) {
                compound.setFloat(entry.getKey(), configFloat.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigDouble configDouble) {
                compound.setDouble(entry.getKey(), configDouble.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigString configString) {
                compound.setString(entry.getKey(), configString.getValue().getValue(context));
            } else if (tagConfig instanceof NBTTagConfigBoolean configBoolean) {
                compound.setBoolean(entry.getKey(), configBoolean.getValue(context));
            }
        }
    }

    private void applyList(NBTCompound compound, String tagName, NBTTagConfigList<?> configList, EvalContext context) {
        if (configList instanceof NBTTagConfigListCompound configListCompound) {
            NBTCompoundList list = compound.getCompoundList(tagName);
            for (NBTTagConfigList.Element<NBTTagConfigCompound> element : configListCompound.getElements()) {
                applyCompound(list.addCompound(), element.getValue(), context);
            }
        } else if (configList instanceof NBTTagConfigListInt configListInt) {
            applyPrimitiveList(compound.getIntegerList(tagName), configListInt, context);
        } else if (configList instanceof NBTTagConfigListLong configListLong) {
            applyPrimitiveList(compound.getLongList(tagName), configListLong, context);
        } else if (configList instanceof NBTTagConfigListFloat configListFloat) {
            applyPrimitiveList(compound.getFloatList(tagName), configListFloat, context);
        } else if (configList instanceof NBTTagConfigListDouble configListDouble) {
            applyPrimitiveList(compound.getDoubleList(tagName), configListDouble, context);
        } else if (configList instanceof NBTTagConfigListString configListString) {
            applyPrimitiveList(compound.getStringList(tagName), configListString, context);
        } else if (configList instanceof NBTTagConfigListIntArray configListIntArray) {
            applyPrimitiveList(compound.getIntArrayList(tagName), configListIntArray, context);
        }
    }

    private <T> void applyPrimitiveList(NBTList<T> nbtList, NBTTagConfigList<? extends NBTTagConfigPrimitive<T>> configPrimitive, EvalContext context) {
        for (NBTTagConfigList.Element<? extends NBTTagConfigPrimitive<T>> element : configPrimitive.getElements()) {
            nbtList.add(element.getValue().getValue().getValue(context)); // This looks weird, but it will provide more options in the future.
        }
    }

    @Override
    public String toString() {
        return "BukkitItemStackConfig{" +
                "itemId='" + itemId + '\'' +
                ", name=" + name +
                ", lore=" + lore +
                ", amount=" + amount +
                ", repairCost=" + repairCost +
                ", damage=" + damage +
                ", unbreakable=" + unbreakable +
                ", customModelData=" + customModelData +
                ", enchants=" + enchants +
                ", nbt=" + nbt +
                "} ";
    }
}
