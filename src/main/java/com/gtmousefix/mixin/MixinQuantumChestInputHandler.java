package com.gtmousefix.mixin;

import com.gtmousefix.IQuantumInputHandler;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Tags the anonymous input handler created by {@code MetaTileEntityQuantumChest#createImportItemHandler}
 * so that {@link MixinWidgetSlotItemHandler} can recognise slots backed by it.
 * <p>
 * That handler's {@code insertItem} already forwards to the chest's combined (virtual) inventory.
 */
@Mixin(targets = "gregtech.common.metatileentities.storage.MetaTileEntityQuantumChest$1", remap = false)
public abstract class MixinQuantumChestInputHandler implements IQuantumInputHandler {}
