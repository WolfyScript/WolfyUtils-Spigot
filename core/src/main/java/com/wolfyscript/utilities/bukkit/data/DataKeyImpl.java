package com.wolfyscript.utilities.bukkit.data;

import com.wolfyscript.utilities.data.DataKey;
import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer;
import com.wolfyscript.utilities.gui.functions.ReceiverFunction;
import org.bukkit.inventory.meta.ItemMeta;

public class DataKeyImpl<T> implements DataKey<T> {

    private final ReceiverFunction<ItemMeta, T> fetcher;
    private final ReceiverBiConsumer<ItemMeta, T> applier;

    DataKeyImpl(ReceiverFunction<ItemMeta, T> fetcher, ReceiverBiConsumer<ItemMeta, T> applier) {
        this.fetcher = fetcher;
        this.applier = applier;
    }

    public ReceiverFunction<ItemMeta, T> getFetcher() {
        return fetcher;
    }

    public ReceiverBiConsumer<ItemMeta, T> getApplier() {
        return applier;
    }
}
