package com.gtmousefix.mixin;

import com.gtmousefix.IQuantumInputHandler;
import gregtech.api.gui.widgets.SlotWidget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Makes the Quantum Chest input slot a pass-through slot.
@Mixin(SlotWidget.WidgetSlotItemHandler.class)
public abstract class MixinWidgetSlotItemHandler extends SlotItemHandler {

    private MixinWidgetSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    // Replaces the stack given to {@code putStack} with whatever could not be inserted into the chest. The original
    // method then stores that remainder (normally empty) and runs the usual slot-changed handling.
    @ModifyVariable(method = "putStack", at = @At("HEAD"), argsOnly = true)
    private ItemStack gtmousefix$passThroughToQuantumChest(ItemStack stack) {
        IItemHandler handler = this.getItemHandler();
        if (!(handler instanceof IQuantumInputHandler) || stack.isEmpty()) {
            return stack;
        }
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            // Visual prediction only: the server decides what is stored and syncs the count.
            return ItemStack.EMPTY;
        }
        // The input handler's insertItem forwards to the chest's combined (virtual) inventory.
        return handler.insertItem(this.getSlotIndex(), stack, false);
    }
}
