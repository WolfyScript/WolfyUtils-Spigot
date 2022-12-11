package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R3;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.wolfyscript.utilities.bukkit.nms.api.ItemUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemUtilImpl extends ItemUtil {


    protected ItemUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public String getItemStackJson(org.bukkit.inventory.ItemStack itemStack) {
        var nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.save(new NBTTagCompound()).toString();
    }

    @Override
    public org.bukkit.inventory.ItemStack getJsonItemStack(String json) {
        try {
            var nbtTagCompound = MojangsonParser.parse(json);
            var itemStack = ItemStack.a(nbtTagCompound);
            return CraftItemStack.asBukkitCopy(itemStack);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getItemStackBase64(org.bukkit.inventory.ItemStack itemStack) throws IOException {
        if (itemStack == null) return "null";
        var nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        var outputStream = new ByteArrayOutputStream();
        NBTCompressedStreamTools.a(nmsItemStack.save(new NBTTagCompound()), outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    @Override
    public org.bukkit.inventory.ItemStack getBase64ItemStack(String data) throws IOException {
        return getBase64ItemStack(Base64.getDecoder().decode(data));
    }

    @Override
    public org.bukkit.inventory.ItemStack getBase64ItemStack(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) return null;
        var inputStream = new ByteArrayInputStream(bytes);
        var nbtTagCompound = NBTCompressedStreamTools.a(inputStream);
        var itemStack = ItemStack.a(nbtTagCompound);
        if (itemStack != null) {
            return CraftItemStack.asBukkitCopy(itemStack);
        }
        return null;
    }
}
