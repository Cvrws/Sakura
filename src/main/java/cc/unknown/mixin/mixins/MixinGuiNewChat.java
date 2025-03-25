package cc.unknown.mixin.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.realmsclient.gui.ChatFormatting;

import cc.unknown.ui.menu.GuiNewChatHook;
import cc.unknown.util.client.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat extends Gui {

	@Shadow
	@Final
	private Minecraft mc;

	@Shadow
	public abstract int getLineCount();

	@Shadow
	private boolean isScrolled;

	@Shadow
	public abstract float getChatScale();

	@Shadow
	public abstract void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId);

	@Unique
	private float percentComplete;
	@Unique
	private int newLines;
	@Unique
	private long prevMillis = System.currentTimeMillis();

	@Unique
	private float animationPercent;
	@Unique
	private int lineBeingDrawn;
	@Unique
	private String lastMessage = "";
	@Unique
	private int sameMessageAmount, line;

	@Unique
	private ChatLine drawingChatLine = null;

	@Unique
	private void updatePercentage(long diff) {
		if (percentComplete < 1) {
			percentComplete += (4 / 1000) * (float) diff;
		}
		percentComplete = (float) MathUtil.clamp(percentComplete, 0, 1);
	}

	@Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getChatComponent()Lnet/minecraft/util/IChatComponent;"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void getChatLine(int updateCounter, CallbackInfo ci, int i, boolean bl, int j, int k, float f, float g, int l, int m, ChatLine chatLine, int n, double d, int o, int p, int q) {
		drawingChatLine = chatLine;
	}

	@Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
	private int redirectText(FontRenderer instance, String text, float x, float y, int color) {
		int lastOpacity = 0;

		if (lineBeingDrawn <= newLines) {
			int opacity = (color >> 24) & 0xFF;
			opacity *= animationPercent;
			lastOpacity = (color & ~(0xFF << 24)) | (opacity << 24);
		} else {
			lastOpacity = color;
		}

		return GuiNewChatHook.drawStringWithHead(drawingChatLine, text, x, y, lastOpacity);
	}

	@Overwrite
	public void printChatMessage(IChatComponent component) {

		if (component.getUnformattedText().equals(lastMessage)) {
			mc.ingameGUI.getChatGUI().deleteChatLine(line);
			sameMessageAmount++;
			lastMessage = component.getUnformattedText();
			component.appendText(ChatFormatting.WHITE + " [x" + sameMessageAmount + "]");
		} else {
			sameMessageAmount = 1;
			lastMessage = component.getUnformattedText();
		}

		line++;

		if (line > 256) {
			line = 0;
		}

		printChatMessageWithOptionalDeletion(component, line);
	}

	@Redirect(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
	public int getSize(List<?> instance) {
		return 0;
	}

	@Inject(method = "drawChat", at = @At("HEAD"), cancellable = true)
	private void modifyChatRendering(CallbackInfo ci) {
		long current = System.currentTimeMillis();
		long diff = current - prevMillis;
		prevMillis = current;
		updatePercentage(diff);
		float t = percentComplete;
		animationPercent = (float) MathUtil.clamp(1 - (--t) * t * t * t, 0, 1);
	}

	@Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
	private void translate(CallbackInfo ci) {
		float y = 0;

		if (!this.isScrolled) {
			y += (9 - 9 * animationPercent) * this.getChatScale();
		}

		GlStateManager.translate(0, y, 0);
	}

	@Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
	private void transparentBackground(int left, int top, int right, int bottom, int color) {
		drawRect(left, top, right, bottom, color);
	}

	@ModifyArg(method = "drawChat", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0, remap = false), index = 0)
	private int getLineBeingDrawn(int line) {
		lineBeingDrawn = line;
		return line;
	}

	@Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
	private void printChatMessageWithOptionalDeletion(CallbackInfo ci) {
		percentComplete = 0;
	}

	@ModifyVariable(method = "setChatLine", at = @At("STORE"), ordinal = 0)
	private List<IChatComponent> setNewLines(List<IChatComponent> original) {
		newLines = original.size() - 1;
		return original;
	}

	@Inject(method = "getChatComponent", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiNewChat;scrollPos:I"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private void getChatComponent(int mouseX, int mouseY, CallbackInfoReturnable<IChatComponent> cir, ScaledResolution scaledresolution, int i, float f, int j, int k, int l) {
		int line = k / mc.fontRendererObj.FONT_HEIGHT;
		if (line >= getLineCount()) {
			cir.setReturnValue(null);
		}
	}

	@Redirect(method = "deleteChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getChatLineID()I"))
	private int adeleteChatLine(ChatLine instance) {
		if (instance == null) {
			return -1;
		}
		return instance.getChatLineID();
	}
}