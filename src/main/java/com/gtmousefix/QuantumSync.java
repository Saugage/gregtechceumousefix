package com.gtmousefix;

import gregtech.api.gui.impl.ModularUIContainer;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

// Helper for resending Quantum Chest / Tank slots to a client.
public final class QuantumSync {

    private QuantumSync() {}

    /**
     * Sends the current contents of every slot in {@code container} backed by {@code handler} to {@code player},
     * and updates the container's cached copy of those slots so its normal sync stays consistent.
     */
    public static void resendSlots(Container container, EntityPlayerMP player, IItemHandler handler) {
        for (Slot slot : container.inventorySlots) {
            if (slot instanceof SlotItemHandler && ((SlotItemHandler) slot).getItemHandler() == handler) {
                ItemStack stack = slot.getStack();
                container.inventoryItemStacks.set(slot.slotNumber, stack.copy());
                player.connection.sendPacket(new SPacketSetSlot(container.windowId, slot.slotNumber, stack));
            }
        }
    }

    /**
     * Resends every slot backed by one of {@code handlers} to each player who currently has {@code machine}'s GUI
     * open. Server side only.
     */
    public static void resendToViewers(MetaTileEntity machine, IItemHandler... handlers) {
        World world = machine.getWorld();
        if (world == null || world.isRemote) {
            return;
        }
        for (EntityPlayer player : world.playerEntities) {
            if (!(player instanceof EntityPlayerMP)) {
                continue;
            }
            Container container = player.openContainer;
            if (!(container instanceof ModularUIContainer) ||
                    ((ModularUIContainer) container).getModularUI().holder != machine.getHolder()) {
                continue;
            }
            for (IItemHandler handler : handlers) {
                resendSlots(container, (EntityPlayerMP) player, handler);
            }
        }
    }
}
