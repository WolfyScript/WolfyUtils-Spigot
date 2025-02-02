package me.wolfyscript.utilities.api.nms.inventory;

import javassist.*;
import javassist.bytecode.SignatureAttribute;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.generated.PermissionReference;
import me.wolfyscript.utilities.util.Reflection;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class InjectGUIInventory {

    private static final String GENERATOR_PACKAGE = "me.wolfyscript.utilities.api.nms.inventory.generated";

    private static final Class<?> CONTAINER_CLASS;

    private static final Map<Class<? extends Inventory>, Class<?>> MODIFIED_CLASSES = new HashMap<>();
    private static final Map<Class<?>, Function<CtClass, CtConstructor>> CREATE_EXTRA_CONSTRUCTOR_MAP = new HashMap<>();


    static {
        CONTAINER_CLASS = Reflection.getNMS("world", "IInventory");
    }

    public static <C extends CustomCache, T extends GUIInventory<C> & Inventory> T patchInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, Inventory inventory, @Nullable String originalTitle) {
        Class<? extends Inventory> inventoryClass = inventory.getClass();

        if (!(inventory instanceof GUIInventory<?>)) {
            Class<?> modifiedClass = MODIFIED_CLASSES.computeIfAbsent(inventoryClass, aClass -> {
                // Inject GUIInventory into the class
                try {
                    return inject(ClassPool.getDefault(), aClass);
                } catch (NotFoundException | CannotCompileException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Constructor<?> constructor;
            try {
                // Special inventory type
                constructor = modifiedClass.getConstructor(GuiHandler.class, GuiWindow.class, CONTAINER_CLASS);
                return (T) constructor.newInstance(guiHandler, window, inventoryClass.getMethod("getInventory").invoke(inventory));
            } catch (NoSuchMethodException e) {
                // Custom inventory - need to check which constructor to use
                try {
                    InventoryType type = inventory.getType();

                    if (type != InventoryType.CHEST) {
                        // Using custom inventory type
                        if (originalTitle == null || Objects.equals(originalTitle, type.getDefaultTitle())) {
                            // using the (InventoryHolder owner, InventoryType type) constructor
                            constructor = modifiedClass.getConstructor(GuiHandler.class, GuiWindow.class, InventoryHolder.class, InventoryType.class);
                            return (T) constructor.newInstance(guiHandler, window, inventory.getHolder(), type);
                        } else {
                            // using the (InventoryHolder owner, InventoryType type, String title) constructor
                            constructor = modifiedClass.getConstructor(GuiHandler.class, GuiWindow.class, InventoryHolder.class, InventoryType.class, String.class);
                            return (T) constructor.newInstance(guiHandler, window, inventory.getHolder(), type, originalTitle);
                        }
                    } else if (originalTitle == null || Objects.equals(originalTitle, type.getDefaultTitle())) {
                        // using the (InventoryHolder owner, int size) constructor
                        constructor = modifiedClass.getConstructor(GuiHandler.class, GuiWindow.class, InventoryHolder.class, int.class);
                        return (T) constructor.newInstance(guiHandler, window, inventory.getHolder(), inventory.getSize());
                    } else {
                        // using the (InventoryHolder owner, int size, String title) constructor
                        constructor = modifiedClass.getConstructor(GuiHandler.class, GuiWindow.class, InventoryHolder.class, int.class, String.class);
                        return (T) constructor.newInstance(guiHandler, window, inventory.getHolder(), inventory.getSize(), originalTitle);
                    }
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Injects GUIInventory into the specified inventory class.
     *
     * @param classPool
     * @param originalClass
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    public static Class<?> inject(ClassPool classPool, Class<?> originalClass) throws NotFoundException, CannotCompileException, IOException {
        classPool.insertClassPath(new LoaderClassPath(InjectGUIInventory.class.getClassLoader()));

        final String guiClassName = "GUI" + originalClass.getSimpleName();
        final CtClass wrappedInventory = classPool.makeClass(GENERATOR_PACKAGE + "." + guiClassName);

        classPool.importPackage("net.minecraft.server." + Reflection.getVersion());
        classPool.importPackage("me.wolfyscript.utilities.api.inventory.gui");
        classPool.importPackage("me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache");
        classPool.importPackage("me.wolfyscript.utilities.api.nms.inventory.GUIInventory");
        classPool.importPackage("me.wolfyscript.utilities.api.inventory.gui.GuiWindow");
        classPool.importPackage("me.wolfyscript.utilities.api.inventory.gui.GuiHandler");
        classPool.importPackage(GENERATOR_PACKAGE);

        SignatureAttribute.ClassSignature classSignature = new SignatureAttribute.ClassSignature(
                // <C>
                new SignatureAttribute.TypeParameter[]{
                        new SignatureAttribute.TypeParameter("C", new SignatureAttribute.ClassType(CustomCache.class.getName()), null)
                },
                // extends <name>
                new SignatureAttribute.ClassType(originalClass.getName()),
                // GUIInventory<C>
                new SignatureAttribute.ClassType[]{
                        new SignatureAttribute.ClassType(GUIInventory.class.getName(), new SignatureAttribute.TypeArgument[]{
                                new SignatureAttribute.TypeArgument(new SignatureAttribute.TypeVariable("C"))
                        })
                }
        );
        wrappedInventory.setGenericSignature(classSignature.encode());

        CtClass wrapperGUIInterface = classPool.get(GUIInventory.class.getName());
        wrappedInventory.setInterfaces(new CtClass[]{wrapperGUIInterface});
        wrappedInventory.setSuperclass(classPool.get(originalClass.getName()));

        SignatureAttribute.TypeVariable typeVar = new SignatureAttribute.TypeVariable("C");

        // GuiWindow<C>
        SignatureAttribute.ClassType guiWindowType = new SignatureAttribute.ClassType(GuiWindow.class.getName(), new SignatureAttribute.TypeArgument[]{
                new SignatureAttribute.TypeArgument(typeVar)
        });
        // GuiHandler<C>
        SignatureAttribute.ClassType guiHandlerType = new SignatureAttribute.ClassType(GuiHandler.class.getName(), new SignatureAttribute.TypeArgument[]{
                new SignatureAttribute.TypeArgument(typeVar)
        });

        // private final GuiWindow<C> wolfyutils$window;
        CtField guiWindowField = new CtField(classPool.get(GuiWindow.class.getName()), "wolfyutils$window", wrappedInventory);
        guiWindowField.setGenericSignature(guiWindowType.encode());
        guiWindowField.setModifiers(Modifier.setPrivate(Modifier.FINAL)); // private final
        wrappedInventory.addField(guiWindowField);
        // private final GuiHandler<C> wolfyutils$guiHandler;
        CtField guiHandlerField = new CtField(classPool.get(GuiHandler.class.getName()), "wolfyutils$guiHandler", wrappedInventory);
        guiHandlerField.setGenericSignature(guiHandlerType.encode()); //
        guiHandlerField.setModifiers(Modifier.setPrivate(Modifier.FINAL)); // private final
        wrappedInventory.addField(guiHandlerField);

        CtMethod guiWindowGetter = CtNewMethod.make("public GuiWindow getWindow() {\n    return this.wolfyutils$window;\n}", wrappedInventory);
        SignatureAttribute.MethodSignature ms = new SignatureAttribute.MethodSignature(null, null, guiWindowType, null);
        guiWindowGetter.setGenericSignature(ms.encode());
        wrappedInventory.addMethod(guiWindowGetter);

        CtMethod guiHandlerGetter = CtNewMethod.make("public GuiHandler getGuiHandler() {\n    return this.wolfyutils$guiHandler;\n}", wrappedInventory);
        SignatureAttribute.MethodSignature ms2 = new SignatureAttribute.MethodSignature(null, null, guiHandlerType, null);
        guiHandlerGetter.setGenericSignature(ms2.encode());
        wrappedInventory.addMethod(guiHandlerGetter);

        CtClass guiWindowClass = classPool.get(GuiWindow.class.getName());
        CtClass guiHandlerClass = classPool.get(GuiHandler.class.getName());

        for (Constructor<?> constructor : originalClass.getConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            StringBuilder bodyBuilder = new StringBuilder("{\n    super(");
            StringBuilder signatureBuilder = new StringBuilder("public ");
            signatureBuilder.append(guiClassName).append('(');

            // GuiHandler var0,
            signatureBuilder.append(guiHandlerClass.getName()).append(" var0").append(", ");
            // GuiWindow var1
            signatureBuilder.append(guiWindowClass.getName()).append(" var1");

            for (int i = 0; i < parameters.length; i++) {
                String name = "var" + (i+2);
                if (i != 0) {
                    bodyBuilder.append(", ");
                }
                bodyBuilder.append(name);
                // <Class Name> var<i>
                signatureBuilder.append(", ");
                signatureBuilder.append(parameters[i].getName()).append(' ').append(name);
            }
            signatureBuilder.append(") ");
            bodyBuilder.append(");\n");
            bodyBuilder.append("    this.wolfyutils$guiHandler = var0;\n");
            bodyBuilder.append("    this.wolfyutils$window = var1;\n");
            bodyBuilder.append('}');

            CtConstructor generatedConstructor = CtNewConstructor.make(signatureBuilder.toString() + bodyBuilder.toString(), wrappedInventory);
            wrappedInventory.addConstructor(generatedConstructor);
        }

        wrappedInventory.writeFile(WolfyUtilCore.getInstance().getDataFolder().getPath() + "/generated_classes");
        return wrappedInventory.toClass(PermissionReference.class);
    }

}
