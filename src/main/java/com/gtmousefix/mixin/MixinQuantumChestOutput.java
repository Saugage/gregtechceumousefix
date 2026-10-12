package com.gtmousefix.mixin;

import com.gtmousefix.QuantumSync;
import gregtech.api.gui.impl.ModularUIContainer;
import gregtech.common.metatileentities.storage.MetaTileEntityQuantumChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes the Quantum Chest output slot showing stale contents when items are taken out very quickly.
 * <p>
 * The output slot is a single staging slot that the chest refills from its virtual storage in the server-side
 * {@code update()}. That refill happens after the player's container sync for the tick, and a vanilla container only
 * resends a slot when it differs from the copy it last sent. When a click empties the slot and {@code update()}
 * refills it with the same stack before the next comparison, the server sees "no change" and never tells the client,
 * so the client keeps a stale (empty) output slot and its inventory view drifts until the GUI is reopened.
 * <p>
 * Whenever {@code update()} actually changes the output slot, this resends it to every player who has this chest's
 * GUI open, bypassing the change detection, and updates the container's cached copy to match.
 */
@Mixin(value = MetaTileEntityQuantumChest.class, remap = false)
public abstract class MixinQuantumChestOutput {

    @Unique
    private ItemStack gtmousefix$exportBefore = ItemStack.EMPTY;

    @Inject(method = "update", at = @At("HEAD"))
    private void gtmousefix$captureExportBefore(CallbackInfo ci) {
        MetaTileEntityQuantumChest chest = (MetaTileEntityQuantumChest) (Object) this;
        World world = chest.getWorld();
        if (world == null || world.isRemote) {
            return;
        }
        this.gtmousefix$exportBefore = chest.getExportItems().getStackInSlot(0).copy();
    }

    @Inject(method = "update", at = @At("RETURN"))
    private void gtmousefix$resyncExportSlot(CallbackInfo ci) {
        MetaTileEntityQuantumChest chest = (MetaTileEntityQuantumChest) (Object) this;
        World world = chest.getWorld();
        if (world == null || world.isRemote) {
            return;
        }
        IItemHandler export = chest.getExportItems();
        ItemStack after = export.getStackInSlot(0);
        if (ItemStack.areItemStacksEqual(this.gtmousefix$exportBefore, after)) {
            return;
        }
        this.gtmousefix$exportBefore = after.copy();

        for (EntityPlayer player : world.playerEntities) {
            if (!(player instanceof EntityPlayerMP)) {
                continue;
            }
            Container container = player.openContainer;
            if (!(container instanceof ModularUIContainer) ||
                    ((ModularUIContainer) container).getModularUI().holder != chest.getHolder()) {
                continue;
            }
            QuantumSync.resendSlots(container, (EntityPlayerMP) player, export);
        }
    }
}
