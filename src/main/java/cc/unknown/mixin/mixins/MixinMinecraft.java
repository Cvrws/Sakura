package cc.unknown.mixin.mixins;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Sakura;
import cc.unknown.event.impl.GameEvent;
import cc.unknown.mixin.impl.IMinecraft;
import cc.unknown.module.impl.player.NoClickDelay;
import cc.unknown.ui.menu.MainMenu;
import cc.unknown.util.Accessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Util;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {

	@Shadow
	@Mutable
	@Final
	private Session session;

	@Shadow
	public int leftClickCounter;

	@Shadow
	public PlayerControllerMP playerController;

	@Shadow
	public WorldClient theWorld;

	@Shadow
	public EntityPlayerSP thePlayer;

	@Shadow
	public MovingObjectPosition objectMouseOver;

	@Shadow
	public EntityRenderer entityRenderer;

	@Shadow
	public GuiScreen currentScreen;

	@Shadow
	@Final
	public DefaultResourcePack mcDefaultResourcePack;

	@Shadow
	public abstract ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException;

	@Override
	public void setSession(final Session session) {
		this.session = session;
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
	private void injectStartGame(CallbackInfo ci) {
		Sakura.instance.init();
	}

	@Inject(method = "shutdownMinecraftApplet", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
	private void shutdownMinecraftApplet(CallbackInfo ci) {
		Sakura.instance.stop();
	}

	@Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1))
	private void loopEvent(CallbackInfo ci) {
		Sakura.instance.getEventBus().handle(new GameEvent());
	}

    @Inject(method = "displayGuiScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", shift = At.Shift.AFTER))
    private void handleDisplayGuiScreen(CallbackInfo callbackInfo) {
        if (currentScreen instanceof net.minecraft.client.gui.GuiMainMenu || (currentScreen != null && currentScreen.getClass().getName().startsWith("net.labymod") && currentScreen.getClass().getSimpleName().equals("ModGuiMainMenu"))) {
            currentScreen = new MainMenu();

            ScaledResolution sr = new ScaledResolution(Accessor.mc);
            currentScreen.setWorldAndResolution(Accessor.mc, sr.getScaledWidth(), sr.getScaledHeight());
        }
    }

	@Overwrite
	public void clickMouse() {
		if (this.leftClickCounter <= 0) {
			this.thePlayer.swingItem();

			if (this.objectMouseOver == null) {
				// logger.error("Null returned as 'hitResult', this shouldn't happen!");

				NoClickDelay noDelay = (NoClickDelay) Sakura.instance.getModuleManager().getModule(NoClickDelay.class);

				if (noDelay.isEnabled()) {
					if (this.playerController.isNotCreative()) {
						this.leftClickCounter = 0;
					}
				} else {
					if (this.playerController.isNotCreative()) {
						this.leftClickCounter = 10;
					}
				}
			} else {
				switch (this.objectMouseOver.typeOfHit) {
				case ENTITY:
					this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
					break;

				case BLOCK:
					final BlockPos blockpos = this.objectMouseOver.getBlockPos();

					if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
						this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
						break;
					}

				default:
					if (this.playerController.isNotCreative()) {
						this.leftClickCounter = 10;
					}
				}
			}
		}
	}
	
	@Overwrite
	public void setWindowIcon() {
		setWindowIcon("sakura/icon/icon16.png", "sakura/icon/icon32.png");
	}

	@Unique
	public void setWindowIcon(String icon16, String icon32) {
		if (Util.getOSType() == Util.EnumOS.OSX) {
			return;
		}

		try (InputStream input16 = mcDefaultResourcePack.getInputStream(new ResourceLocation(icon16));
				InputStream input32 = mcDefaultResourcePack.getInputStream(new ResourceLocation(icon32))) {

			if (input16 != null && input32 != null) {
				Display.setIcon(new ByteBuffer[] { readImageToBuffer(input16), readImageToBuffer(input32) });
			}
		} catch (IOException e) { }
	}

	@Inject(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", shift = At.Shift.AFTER))
	private void createDisplay(CallbackInfo callbackInfo) {
		Display.setTitle("Loading Sakura...");
	}

	@Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
	public void impl$cancelSystemGC() {
	}
}
