package com.wolfyscript.utilities.bukkit.items.reference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    Class<? extends ItemReference.Parser> customParser() default ItemReference.Parser.class;

    class Creator {

        private static <R extends ItemReference> ItemReference.Parser<R> constructDefaultParser(int priority, Class<R> itemReferenceType) {
            try {
                Method parseMethod = itemReferenceType.getMethod("parseFromStack", ItemStack.class);
                if (!Modifier.isStatic(parseMethod.getModifiers())) {
                    return null;
                }
                if (parseMethod.getReturnType() == itemReferenceType) {
                    return null;
                }
                final Method finalParseMethod = parseMethod;
                return new ItemReference.Parser<>(priority, itemReferenceType) {
                    @Override
                    public R parseFromStack(ItemStack stack) {
                        try {
                            // We can do this because we check the return type above
                            return (R) finalParseMethod.invoke(null, stack);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            } catch (NoSuchMethodException ignored) {
                // parse method is not defined!
            }
            return null;
        }

        public static <R extends ItemReference> ItemReference.Parser<R> constructParser(Class<R> itemReferenceType) {
            ItemReferenceParserSettings annotation = itemReferenceType.getAnnotation(ItemReferenceParserSettings.class);
            if (annotation == null) {
                // Fallback to default constructor parser!
                return constructDefaultParser(0, itemReferenceType);
            }
            if (annotation.customParser() != ItemReference.Parser.class) {
                // Specified Custom Parse class!
                try {
                    Constructor<?> constructor = annotation.customParser().getConstructor();
                    ItemReference.Parser<?> parser = (ItemReference.Parser<?>) constructor.newInstance();
                    if (parser.type != itemReferenceType) {
                        // Invalid type!
                        return null;
                    }
                    // We made sure that the type is the same, so can cast!
                    return (ItemReference.Parser<R>) parser;
                } catch (NoSuchMethodException e) {
                    // Constructor not defined!
                    e.printStackTrace();
                } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    // Error creating parser!
                    e.printStackTrace();
                }
                return null;
            }
            // Construct default parser
            return constructDefaultParser(annotation.priority(), itemReferenceType);
        }

    }

}
