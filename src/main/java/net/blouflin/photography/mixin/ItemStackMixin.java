package net.blouflin.photography.mixin;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract ComponentMap getComponents();

    @Shadow public abstract Item getItem();

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable<Text> cir) {
        cir.cancel();
        Text text = this.getComponents().get(DataComponentTypes.CUSTOM_NAME);
        if (text != null) {
            cir.setReturnValue(text);
        } else {
            Text text2 = this.getComponents().get(DataComponentTypes.ITEM_NAME);
            if (this.getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
                if (this.getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().contains("isPhotographyCamera:1b")) {
                    cir.setReturnValue(Text.translatable("item.photography.camera"));
                } else if (this.getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().contains("isPhotographyEmptyMap:1b")) {
                    cir.setReturnValue(Text.translatable("item.photography.empty_map"));
                } else if (this.getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().contains("isPhotographyFilledMap:1b")) {
                    cir.setReturnValue(Text.translatable("item.photography.filled_map"));
                } else if (text2 != null) {
                    cir.setReturnValue(text2);
                } else {
                    cir.setReturnValue(this.getItem().getName(this.getItem().getDefaultStack()));
                }
            } else if (text2 != null) {
                cir.setReturnValue(text2);
            } else {
                cir.setReturnValue(this.getItem().getName(this.getItem().getDefaultStack()));
            }
        }
    }
}
