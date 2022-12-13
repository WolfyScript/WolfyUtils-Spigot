package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ItemReferenceParserSettings {

    /**
     * The priority of this parser.<br>
     * Parser with higher priority are called before Parsers with lower priority.<br>
     * Each time it chooses the next parser, if and only if the current parser returns null.<br>
     * The moment it returns a non-null value that value is used.
     *
     * @return The priority of this parser.
     */
    short priority() default 0;

    /**
     * The class of the custom Reference Parser
     *
     * @return The Parser for this ItemReference Type
     */
    Class<? extends ItemReference.Parser<?>> parser();

    String plugin() default "";

    class Creator {

        public static <R extends ItemReference> AbstractParser<R> constructParser(NamespacedKey id, Class<R> itemReferenceType) {
            ItemReferenceParserSettings annotation = itemReferenceType.getAnnotation(ItemReferenceParserSettings.class);
            if (annotation == null) {
                // Annotation is not specified! No parser!
                return null;
            }
            if (annotation.parser() != (Class<ItemReference.Parser<?>>)(Object) ItemReference.Parser.class) {
                // Specified Custom Parse class!
                try {
                    Constructor<?> constructor = annotation.parser().getConstructor();

                    final ItemReference.Parser<R> parser = (ItemReference.Parser<R>) constructor.newInstance();
                    final int priority = annotation.priority();
                    final String plugin = annotation.plugin();

                    if (plugin == null || plugin.isEmpty()) {
                        return new AbstractParser<>(id, priority, itemReferenceType) {
                            @Override
                            public Optional<R> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack) {
                                return parser.parseFromStack(wolfyUtils, stack);
                            }
                        };
                    }
                    return new AbstractParser<>(id, priority, itemReferenceType) {
                        @Override
                        public Optional<R> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack) {
                            if (!((WolfyCoreBukkit)wolfyUtils.getCore()).getCompatibilityManager().getPlugins().isPluginEnabled(plugin)) {
                                return Optional.empty();
                            }
                            return parser.parseFromStack(wolfyUtils, stack);
                        }
                    };
                } catch (NoSuchMethodException | SecurityException e) {
                    // Constructor not defined!
                    e.printStackTrace();
                } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    // Error creating parser!
                    e.printStackTrace();
                }
                return null;
            }
            return null;
        }

        /**
         * Parser used to receive the correct {@link ItemReference} from {@link ItemStack}s.
         *
         * @param <T> The type of the {@link ItemReference}
         */
        public abstract static class AbstractParser<T extends ItemReference> implements Keyed, Comparable<AbstractParser<?>> {

            protected final NamespacedKey id;
            protected final int priority;
            final Class<T> type;

            protected AbstractParser(NamespacedKey id, int priority, Class<T> type) {
                this.id = id;
                this.priority = priority;
                this.type = type;
            }

            public abstract Optional<T> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack);

            public int getPriority() {
                return priority;
            }

            @Override
            public NamespacedKey getNamespacedKey() {
                return id;
            }

            @Override
            public int compareTo(@NotNull ItemReferenceParserSettings.Creator.AbstractParser<?> other) {
                return -1 * Integer.compare(getPriority(), other.getPriority());
            }
        }

    }

}
