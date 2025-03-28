package cc.unknown.mixin.mixins;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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

import com.sun.jna.Platform;

import cc.unknown.Sakura;
import cc.unknown.event.impl.ClickMouseEvent;
import cc.unknown.event.impl.GameEvent;
import cc.unknown.mixin.impl.IMinecraft;
import cc.unknown.module.impl.player.NoClickDelay;
import cc.unknown.ui.menu.MainMenu;
import cc.unknown.util.Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.IStream;
import net.minecraft.entity.Entity;
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
	private boolean fullscreen;

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
	private boolean enableGLErrorChecking = true;
	
	@Shadow
	public GameSettings gameSettings;
	
	@Shadow
	public abstract void updateDisplay();

	@Shadow
	public abstract ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException;

	@Override
	public void setSession(final Session session) {
		this.session = session;
	}

	@Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
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
    
	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z"))
	public boolean nextMouse() {
		boolean next = Mouse.next();

		if(next) {
			ClickMouseEvent event = new ClickMouseEvent(Mouse.getEventButton());
			Sakura.instance.getEventBus().handle(event);
			
			if(event.isCancelled()) {
				next = nextMouse();
			}
		}

		return next;
	}
	
	@Inject(method = "clickMouse", at = @At("HEAD"))
	public void fixHitDelay(CallbackInfo ci) {
		if(Sakura.instance.getModuleManager().getModule(NoClickDelay.class).isEnabled()) {
			leftClickCounter = 0;
		}
	}
	
	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;updateEffects()V"))
	public void fixEffectRenderer(EffectRenderer effectRenderer) {
		try {
			effectRenderer.updateEffects();
		}
		catch(Exception e) {}
	}
	
	@Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
	public void fixKeyBinding(CallbackInfo callback) {
		for(KeyBinding keyBinding : gameSettings.keyBindings) {
			try {
				KeyBinding.setKeyBindState(keyBinding.getKeyCode(), keyBinding.getKeyCode() < 256 && Keyboard.isKeyDown(keyBinding.getKeyCode()));
			}
			catch (Exception e) {}
		}
	}
	
	
    @Inject(method = "startGame", at = @At("TAIL"))
    private void disableGlErrorChecking(CallbackInfo ci) {
        this.enableGLErrorChecking = false;
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
	
    @Inject(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", remap = false))
    private void resolveScreenState(CallbackInfo ci) {
        if (!this.fullscreen && Platform.isWindows()) {
            Display.setResizable(false);
            Display.setResizable(true);
        }
    }

    @Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void optimizedWorldSwapping() {}
    
    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V"))
    private void skipTwitchCode1(IStream instance) {}

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V"))
    private void skipTwitchCode2(IStream instance) {}
    
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;loadEntityShader(Lnet/minecraft/entity/Entity;)V"))
    private void keepShadersOnPerspectiveChange(EntityRenderer entityRenderer, Entity entityIn) {}
	
    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        if (worldClientIn != this.theWorld) {
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }
    }
    
	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/resources/SkinManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V"))
	public void splashSkinManager(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/chunk/storage/AnvilSaveConverter;<init>(Ljava/io/File;)V"))
	public void splashSaveLoader(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/audio/SoundHandler;<init>(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/settings/GameSettings;)V"))
	public void splashSoundHandler(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/audio/MusicTicker;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashMusicTicker(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/FontRenderer;<init>(Lnet/minecraft/client/settings/GameSettings;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/TextureManager;Z)V"))
	public void splashFontRenderer(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/util/MouseHelper;<init>()V"))
	public void splashMouseHelper(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/texture/TextureMap;<init>(Ljava/lang/String;)V"))
	public void splashTextureMap(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/resources/model/ModelManager;<init>(Lnet/minecraft/client/renderer/texture/TextureMap;)V"))
	public void splashModelManager(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/RenderItem;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/ModelManager;)V"))
	public void splashRenderItem(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/RenderManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/entity/RenderItem;)V"))
	public void splashRenderManager(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/ItemRenderer;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashItemRenderer(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/EntityRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V"))
	public void splashEntityRenderer(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;<init>(Lnet/minecraft/client/renderer/BlockModelShapes;Lnet/minecraft/client/settings/GameSettings;)V"))
	public void splashBlockRenderDispatcher(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/RenderGlobal;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashRenderGlobal(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashGuiAchivement(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/particle/EffectRenderer;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
	public void splashEffectRenderer(CallbackInfo callback) {
		updateDisplay();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiIngame;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashGuiIngame(CallbackInfo callback) {
		updateDisplay();
	}
}
