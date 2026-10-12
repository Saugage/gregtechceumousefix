package com.gtmousefix.mixin;

import com.gtmousefix.QuantumSync;
import gregtech.common.metatileentities.storage.MetaTileEntityQuantumTank;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes the Quantum Tank bucket slots showing stale contents when buckets are put in or taken out
 * quickly.
 * <p>
 * All bucket handling (draining/filling containers and moving them between the two slots) happens in the tank's
 * server-side {@code update()}, which runs after the player's container sync for the tick. The client never runs
 * that logic, so it keeps its predicted slot contents, and a vanilla container only resends a slot when it differs
 * from the copy it last sent. A slot that is emptied and refilled with the same stack between two comparisons is
 * therefore never corrected, leaving stale ("ghost") buckets or a missing output bucket until the GUI is reopened.
 * <p>
 * Whenever {@code update()} actually changes either bucket slot, both slots are resent directly to every player who
 * has this tank's GUI open (see {@link QuantumSync}).
 */
@Mixin(value = MetaTileEntityQuantumTank.class, remap = false)
public abstract class MixinQuantumTankUpdate {

    @Unique
    private ItemStack gtmousefix$importBefore = ItemStack.EMPTY;

    @Unique
    private ItemStack gtmousefix$exportBefore = ItemStack.EMPTY;

    @Inject(method = "update", at = @At("HEAD"))
    private void gtmousefix$captureSlotsBefore(CallbackInfo ci) {
        MetaTileEntityQuantumTank tank = (MetaTileEntityQuantumTank) (Object) this;
        World world = tank.getWorld();
        if (world == null || world.isRemote) {
            return;
        }
        this.gtmousefix$importBefore = tank.getImportItems().getStackInSlot(0).copy();
        this.gtmousefix$exportBefore = tank.getExportItems().getStackInSlot(0).copy();
    }

    @Inject(method = "update", at = @At("RETURN"))
    private void gtmousefix$resyncSlotsAfter(CallbackInfo ci) {
        MetaTileEntityQuantumTank tank = (MetaTileEntityQuantumTank) (Object) this;
        World world = tank.getWorld();
        if (world == null || world.isRemote) {
            return;
        }
        IItemHandler importItems = tank.getImportItems();
        IItemHandler exportItems = tank.getExportItems();
        ItemStack importAfter = importItems.getStackInSlot(0);
        ItemStack exportAfter = exportItems.getStackInSlot(0);
        if (ItemStack.areItemStacksEqual(this.gtmousefix$importBefore, importAfter) &&
                ItemStack.areItemStacksEqual(this.gtmousefix$exportBefore, exportAfter)) {
            return;
        }
        this.gtmousefix$importBefore = importAfter.copy();
        this.gtmousefix$exportBefore = exportAfter.copy();
        QuantumSync.resendToViewers(tank, importItems, exportItems);
    }
}
