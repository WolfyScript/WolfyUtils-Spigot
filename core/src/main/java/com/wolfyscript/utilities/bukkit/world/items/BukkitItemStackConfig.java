package com.wolfyscript.utilities.bukkit.world.items;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.common.nbt.NBTTagConfig;
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
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
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

        this.nbt = readFromItemStack(new NBTItem(stack), "", null);
    }

    private NBTTagConfigCompound readFromItemStack(NBTCompound currentCompound, String entryKey, NBTTagConfig parent) {
        NBTTagConfigCompound configCompound = new NBTTagConfigCompound(wolfyUtils, entryKey, parent);
        Map<String, NBTTagConfig> children = new HashMap<>();
        for (String key : currentCompound.getKeys()) {
            NBTTagConfig childConfig = switch (currentCompound.getType(key)) {
                case NBTTagCompound -> readFromItemStack(currentCompound.getCompound(key), key, configCompound);
                case NBTTagList -> switch (currentCompound.getListType(key)) {
                        case NBTTagCompound -> {
                            List<NBTTagConfigList.Element<NBTTagConfigCompound>> elements = new ArrayList<>();
                            NBTTagConfigListCompound compoundConfigList = new NBTTagConfigListCompound(wolfyUtils, elements, key, configCompound);
                            NBTCompoundList compoundList = currentCompound.getCompoundList(key);
                            for (NBTListCompound listCompound : compoundList) {
                                NBTTagConfigList.Element<NBTTagConfigCompound> element = new NBTTagConfigList.Element<>();
                                readFromItemStack(listCompound, "", compoundConfigList);
                                elements.add(element);
                            }
                            yield compoundConfigList;
                        }
                        case NBTTagInt -> readPrimitiveList(currentCompound.getIntegerList(key), new NBTTagConfigListInt(wolfyUtils, new ArrayList<>(), key, configCompound), (listInt, integer) -> new NBTTagConfigInt(wolfyUtils, new ValueProviderIntegerConst(wolfyUtils, integer), "", listInt));
                        case NBTTagIntArray ->  readPrimitiveList(currentCompound.getIntArrayList(key), new NBTTagConfigListIntArray(wolfyUtils, new ArrayList<>(), key, configCompound), (listIntArray, intArray) -> new NBTTagConfigIntArray(wolfyUtils, new ValueProviderIntArrayConst(wolfyUtils, intArray), "", listIntArray));
                        case NBTTagLong -> readPrimitiveList(currentCompound.getLongList(key), new NBTTagConfigListLong(wolfyUtils, new ArrayList<>(), key, configCompound), (listConfig, aLong) -> new NBTTagConfigLong(wolfyUtils, new ValueProviderLongConst(wolfyUtils, aLong), "", listConfig));
                        case NBTTagFloat -> readPrimitiveList(currentCompound.getFloatList(key), new NBTTagConfigListFloat(wolfyUtils, new ArrayList<>(), key, configCompound), (listConfig, aFloat) -> new NBTTagConfigFloat(wolfyUtils, new ValueProviderFloatConst(wolfyUtils, aFloat), "", listConfig));
                        case NBTTagDouble -> readPrimitiveList(currentCompound.getDoubleList(key), new NBTTagConfigListDouble(wolfyUtils, new ArrayList<>(), key, configCompound), (listConfig, aDouble) -> new NBTTagConfigDouble(wolfyUtils, new ValueProviderDoubleConst(wolfyUtils, aDouble), "", listConfig));
                        case NBTTagString -> readPrimitiveList(currentCompound.getStringList(key), new NBTTagConfigListString(wolfyUtils, new ArrayList<>(), key, configCompound), (listConfig, aString) -> new NBTTagConfigString(wolfyUtils, new ValueProviderStringConst(wolfyUtils, aString), "", listConfig));
                        default -> null;
                    };
                case NBTTagByte -> new NBTTagConfigByte(wolfyUtils, new ValueProviderByteConst(wolfyUtils, currentCompound.getByte(key)), key, configCompound);
                case NBTTagByteArray -> new NBTTagConfigByteArray(wolfyUtils, new ValueProviderByteArrayConst(wolfyUtils, currentCompound.getByteArray(key)), key, configCompound);
                case NBTTagShort -> new NBTTagConfigShort(wolfyUtils, new ValueProviderShortConst(wolfyUtils, currentCompound.getShort(key)), key, configCompound);
                case NBTTagInt -> new NBTTagConfigInt(wolfyUtils, new ValueProviderIntegerConst(wolfyUtils, currentCompound.getInteger(key)), key, configCompound);
                case NBTTagIntArray -> new NBTTagConfigIntArray(wolfyUtils, new ValueProviderIntArrayConst(wolfyUtils, currentCompound.getIntArray(key)), key, configCompound);
                case NBTTagLong -> new NBTTagConfigLong(wolfyUtils, new ValueProviderLongConst(wolfyUtils, currentCompound.getLong(key)), key, configCompound);
                case NBTTagFloat -> new NBTTagConfigFloat(wolfyUtils, new ValueProviderFloatConst(wolfyUtils, currentCompound.getFloat(key)), key, configCompound);
                case NBTTagDouble -> new NBTTagConfigDouble(wolfyUtils, new ValueProviderDoubleConst(wolfyUtils, currentCompound.getDouble(key)), key, configCompound);
                case NBTTagString -> new NBTTagConfigString(wolfyUtils, new ValueProviderStringConst(wolfyUtils, currentCompound.getString(key)), key, configCompound);
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
     * @param nbtList The NBTList from the NBTItemAPI
     * @param configList The instance of the NBTTagConfigList to load the elements into.
     * @param elementConstructor This constructs each element of list.
     * @return The configList instance with the new elements.
     * @param <T> The primitive data type.
     * @param <VAL> The type of the Element config.
     */
    private <T, VAL extends NBTTagConfigPrimitive<T>> NBTTagConfigListPrimitive<T, VAL> readPrimitiveList(NBTList<T> nbtList, NBTTagConfigListPrimitive<T, VAL> configList, BiFunction<NBTTagConfigListPrimitive<T, VAL>, T, VAL> elementConstructor) {
        configList.overrideElements(nbtList.stream().map(value -> {
            var element = new NBTTagConfigList.Element<VAL>();
            element.setValue(elementConstructor.apply(configList, value));
            return element;
        }).toList());
        return configList;
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
