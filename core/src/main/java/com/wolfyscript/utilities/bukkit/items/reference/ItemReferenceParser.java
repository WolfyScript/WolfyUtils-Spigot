package com.wolfyscript.utilities.bukkit.items.reference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ItemReferenceParser {

    int priority() default 0;

    Class<? extends ItemReference> type();

}
