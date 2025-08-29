package com.niycco.mixin;

import com.niycco.utils.EmojiRegistry;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatInputSuggestor.SuggestionWindow.class)
public abstract class SuggestionWindowMixin {
    @Shadow
    @Final
    ChatInputSuggestor field_21615;


    @Inject(method = "complete", at = @At("TAIL"))
    private void overwirteCompete(CallbackInfo ci) {
        ChatInputSuggestorAccessor inputSuggestor = (ChatInputSuggestorAccessor) this.field_21615;
        if (inputSuggestor == null) return;
        TextFieldWidget textFieldWidget = inputSuggestor.getTextField();
        for (String s : EmojiRegistry.EMOJI_NAMES){
            if (textFieldWidget.getText().contains(":" + s + ":")){
                textFieldWidget.eraseCharacters(-2);
            }
        }

    }
}
