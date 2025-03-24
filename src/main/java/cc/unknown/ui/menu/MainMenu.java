package cc.unknown.ui.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import cc.unknown.Sakura;
import cc.unknown.util.client.SystemUtil;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.FontUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.GuiModList;

public class MainMenu extends GuiScreen {

	private final Map<Integer, Consumer<GuiButton>> buttonActions = new HashMap<>();
	private RainSystem rainParticleSystem;

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();

		buttonActions.put(0, button -> mc.displayGuiScreen(new GuiSelectWorld(this)));
		buttonActions.put(1, button -> mc.displayGuiScreen(new GuiMultiplayer(this)));
		buttonActions.put(2, button -> mc.displayGuiScreen(new GuiModList(this)));
		buttonActions.put(3, button -> mc.displayGuiScreen(new AltManager()));
		buttonActions.put(4, button -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings)));

		final String[] keys = { "SinglePlayer", "MultiPlayer", "Mods", "Alt Manager", "Settings" };
		final int initHeight = height / 4 + 36;
		final int objHeight = 20;
		final int objWidth = 100;
		final int buttonSpacing = 22;
		final int xMid = width / 2 - objWidth / 2;

		for (int i = 0; i < keys.length; i++) {
			String translatedString = I18n.format(keys[i]);
			int yOffset = initHeight + i * buttonSpacing;
			this.buttonList.add(new Button(i, xMid, yOffset, objWidth, objHeight, translatedString));
		}

		rainParticleSystem = new RainSystem(width, height);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		buttonActions.getOrDefault(button.id, b -> { }).accept(button);
	}

	@Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        BackgroundUtil.renderBackground(this);

        String title = "§fSakura Client";
        String name = String.format("§fLogged in as §7%s", mc.getSession().getUsername());

        if (Sakura.instance.firstStart) {
            SystemUtil.playSound();
            Sakura.instance.firstStart = false;
        }

        FontUtil.getFontRenderer("comfortaa.ttf", 16).drawStringWithShadow(title, 2.0f, height - 10, -1);
        FontUtil.getFontRenderer("comfortaa.ttf", 16).drawStringWithShadow(name, width - FontUtil.getFontRenderer("comfortaa.ttf", 16).getStringWidth(name) - 2, height - 10, -1);

        if (Sakura.instance.getModuleManager().getModule(cc.unknown.module.impl.visual.MainMenu.class).particles.get()) {
	        rainParticleSystem.update();
	        rainParticleSystem.render();
        }
        
    	super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
