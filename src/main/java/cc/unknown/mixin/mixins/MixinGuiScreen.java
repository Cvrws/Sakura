package cc.unknown.mixin.mixins;

import java.io.IOException;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui {

	@Shadow
	protected Minecraft mc;

	@Shadow
	private GuiButton selectedButton;

	@Shadow
	public int width;

	@Shadow
	public int height;

	@Shadow
	protected FontRenderer fontRendererObj;

	@Shadow
	protected List<GuiLabel> labelList = Lists.<GuiLabel>newArrayList();

	@Shadow
	protected List<GuiButton> buttonList = Lists.<GuiButton>newArrayList();

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    protected void onActionPerformed(GuiButton button, CallbackInfo ci) {
        injectedActionPerformed(button);
        ci.cancel();
    }
    
    protected void injectedActionPerformed(GuiButton button) { }

	@Shadow
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseButton == 0) {
			for (int i = 0; i < this.buttonList.size(); ++i) {
				GuiButton guibutton = (GuiButton) this.buttonList.get(i);

				if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
					this.selectedButton = guibutton;
					guibutton.playPressSound(this.mc.getSoundHandler());
					this.injectedActionPerformed(guibutton);
				}
			}
		}
	}

	@Shadow
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			this.mc.displayGuiScreen((GuiScreen) null);

			if (this.mc.currentScreen == null) {
				this.mc.setIngameFocus();
			}
		}
	}

	@Shadow
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		for (int i = 0; i < this.buttonList.size(); ++i) {
			((GuiButton) this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY);
		}

		for (int j = 0; j < this.labelList.size(); ++j) {
			((GuiLabel) this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY);
		}
	}
}
