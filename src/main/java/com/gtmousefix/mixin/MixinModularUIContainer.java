package com.gtmousefix.mixin;

import gregtech.api.gui.impl.ModularUIContainer;
import gregtech.api.util.PerTickIntCounter;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModularUIContainer.class)
public class MixinModularUIContainer {

    // PerTickIntCounter always returns 0
    @Redirect(
            method = "transferStackInSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lgregtech/api/util/PerTickIntCounter;get(Lnet/minecraft/world/World;)I",
                    remap = false))
    private int gtmousefix$ignoreTransferCap(PerTickIntCounter counter, World world) {
        return 0;
    }
}
