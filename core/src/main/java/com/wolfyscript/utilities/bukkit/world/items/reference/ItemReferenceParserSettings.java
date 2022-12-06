package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.google.inject.Guice;
import com.google.inject.Injector;
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

        public static <R extends ItemReference> ItemReference.AbstractParser<R> constructParser(NamespacedKey id, Class<R> itemReferenceType) {
            ItemReferenceParserSettings annotation = itemReferenceType.getAnnotation(ItemReferenceParserSettings.class);
            if (annotation == null) {
                // Annotation is not specified! No parser!
                return null;
            }
            if (annotation.parser() != (Class<ItemReference.Parser<?>>)(Object) ItemReference.Parser.class) {
                // Specified Custom Parse class!
                try {
                    Constructor<?> constructor = annotation.parser().getConstructor();
                    ItemReference.AbstractParser<?> unTypedParser = (ItemReference.AbstractParser<?>) constructor.newInstance();
                    if (unTypedParser.type != itemReferenceType) {
                        // Invalid type!
                        return null;
                    }

                    final ItemReference.Parser<R> parser = (ItemReference.Parser<R>) unTypedParser;
                    final int priority = annotation.priority();
                    final String plugin = annotation.plugin();

                    if (plugin == null) {
                        return new ItemReference.AbstractParser<>(id, priority, itemReferenceType) {
                            @Override
                            public Optional<R> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack) {
                                return parser.parseFromStack(wolfyUtils, stack);
                            }
                        };
                    }
                    return new ItemReference.AbstractParser<>(id, priority, itemReferenceType) {
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

    }

}
