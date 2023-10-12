package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

@Retention(RetentionPolicy.RUNTIME)
public @interface StackIdentifierParserSettings {

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
    Class<? extends StackIdentifierParser<?>> parser();

    String plugin() default "";

    @interface ParseMethod {


    }

    class Builder {

        public static <I extends StackIdentifier> StackIdentifierParser<I> create(NamespacedKey id, Class<I> identifierType) {
            StackIdentifierParserSettings annotation = identifierType.getAnnotation(StackIdentifierParserSettings.class);
            if (annotation == null) return null;

            Set<Method> potentialParserMethods = WolfyUtilCore.getInstance().getReflections().getMethodsAnnotatedWith(ParseMethod.class);
            for (Method potentialParserMethod : potentialParserMethods) {
                if (Arrays.equals(potentialParserMethod.getParameterTypes(), new Class[]{ ItemStack.class })) {

                }
            }

            return null;
        }

    }


}
