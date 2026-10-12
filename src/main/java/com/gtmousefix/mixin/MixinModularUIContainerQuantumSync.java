package com.gtmousefix.mixin;

import com.gtmousefix.QuantumSync;
import gregtech.api.gui.impl.ModularUIContainer;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.common.metatileentities.storage.MetaTileEntityQuantumChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Resends the Quantum Chest output slot to the clicking player when a click changes it.
 * <p>
 * Items put into an empty chest are inserted straight into its combined inventory, which fills the output slot
 * immediately while the click is being processed. The client predicted nothing for that slot, but vanilla then
 * records the new server state as already sent (and suppresses the slot packet after a confirmed click), so the
 * output slot stayed empty on the client until the GUI was reopened or the slot was clicked.
 * <p>
 * The chest's own {@code update()} is covered separately by {@link MixinQuantumChestOutput}.
 */
@Mixin(ModularUIContainer.class)
public abstract class MixinModularUIContainerQuantumSync {

    @Unique
    private ItemStack gtmousefix$exportBeforeClick = ItemStack.EMPTY;

    @Unique
    private IItemHandler gtmousefix$quantumExport(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return null;
        }
        Object holder = ((ModularUIContainer) (Object) this).getModularUI().holder;
        if (!(holder instanceof IGregTechTileEntity)) {
            return null;
        }
        MetaTileEntity mte = ((IGregTechTileEntity) holder).getMetaTileEntity();
        return mte instanceof MetaTileEntityQuantumChest ? mte.getExportItems() : null;
    }

    @Inject(method = "slotClick", at = @At("HEAD"))
    private void gtmousefix$captureExportBeforeClick(int slotId, int dragType, ClickType clickType,
                                                     EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        IItemHandler export = gtmousefix$quantumExport(player);
        if (export != null) {
            this.gtmousefix$exportBeforeClick = export.getStackInSlot(0).copy();
        }
    }

    @Inject(method = "slotClick", at = @At("RETURN"))
    private void gtmousefix$resyncExportAfterClick(int slotId, int dragType, ClickType clickType,
                                                   EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        IItemHandler export = gtmousefix$quantumExport(player);
        if (export != null &&
                !ItemStack.areItemStacksEqual(this.gtmousefix$exportBeforeClick, export.getStackInSlot(0))) {
            QuantumSync.resendSlots((ModularUIContainer) (Object) this, (EntityPlayerMP) player, export);
        }
    }
}
