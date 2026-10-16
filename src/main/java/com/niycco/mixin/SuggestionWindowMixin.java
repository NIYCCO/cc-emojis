package com.niycco.mixin;

import com.niycco.utils.EmojiRegistry;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandSuggestions.SuggestionsList.class)
public abstract class SuggestionWindowMixin {
    @Shadow
    @Final
    CommandSuggestions this$0;


    @Inject(method = "useSuggestion", at = @At("TAIL"))
    private void overwirteCompete(CallbackInfo ci) {
        ChatInputSuggestorAccessor inputSuggestor = (ChatInputSuggestorAccessor) this.this$0;
        if (inputSuggestor == null) return;
        EditBox textFieldWidget = inputSuggestor.getTextField();
        for (String s : EmojiRegistry.EMOJI_NAMES){
            if (textFieldWidget.getValue().contains(":" + s + ":")){
                textFieldWidget.deleteChars(-2);
            }
        }

    }
}
