package cc.unknown.ui.menu;

import static cc.unknown.util.render.ColorUtil.gray;
import static cc.unknown.util.render.ColorUtil.green;
import static cc.unknown.util.render.ColorUtil.red;
import static cc.unknown.util.render.ColorUtil.yellow;

import java.awt.Color;
import java.io.IOException;

import cc.unknown.mixin.impl.IMinecraft;
import cc.unknown.util.alt.MicrosoftAccount;
import cc.unknown.util.alt.UsernameGenerator;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.render.FontUtil;
import cc.unknown.util.structure.Vector2d;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Session;

public class AltManager extends GuiScreen {
    private static TextField usernameBox;
    public static String status = yellow + "Idle...";
    
    @Override
    public void initGui() {
    	status = yellow + "Idle...";
    	this.buttonList.clear();
        int boxWidth = 200;
        int boxHeight = 24;
        int padding = 4;
        float buttonWidth = (boxWidth - padding * 2) / 3.0F;

        Vector2d position = new Vector2d(this.width / 2 - boxWidth / 2, this.height / 2 - 24);
        usernameBox = new TextField(0, this.fontRendererObj, (int) position.x, (int) position.y, (int) boxWidth, (int) boxHeight);
    	this.buttonList.add(new Button(1, (int) position.x, (int) position.y + boxHeight + padding, (int) boxWidth, (int) boxHeight, "Generar un nick random"));
    	this.buttonList.add(new Button(2, (int) (position.x + padding - 32), (int) (position.y + (boxHeight + padding) * 2), (int) buttonWidth, (int) boxHeight, "Login"));
    	this.buttonList.add(new Button(3, (int) (position.x + (buttonWidth + padding) * 1 - 30), (int) (position.y + (boxHeight + padding) * 2), (int) buttonWidth, (int) boxHeight, "Browser Login"));
    	this.buttonList.add(new Button(4, (int) (position.x + (buttonWidth + padding) * 2 - 30), (int) (position.y + (boxHeight + padding) * 2), (int) buttonWidth, (int) boxHeight, "Cookie Login"));
    	this.buttonList.add(new Button(5, (int) (position.x + (buttonWidth + padding) * 3 - 30), (int) (position.y + (boxHeight + padding) * 2), (int) buttonWidth, (int) boxHeight, "Atràs"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BackgroundUtil.renderBackground(this);

        usernameBox.drawTextBox();
        GlStateManager.pushMatrix();

	     int backgroundHeight = (int) (FontUtil.getFontRenderer("comfortaa.ttf", 16).getHeight() + 5);
	     int backgroundY = (int) (height / 2 - 35 - (backgroundHeight / 2));
	
	     FontUtil.getFontRenderer("comfortaa.ttf", 16).drawCenteredString(status, width / 2, (int) (backgroundY + backgroundHeight / 2 - FontUtil.getFontRenderer("consolas.ttf", 16).getHeight() / 2), Color.WHITE.getRGB());
	     this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));
	     GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	usernameBox.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	usernameBox.textboxKeyTyped(typedChar, keyCode);
        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(1));
        }
        
        if (keyCode == 1) {
        	mc.displayGuiScreen(new GuiMainMenu());
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
        String username = usernameBox.getText();

    	switch (button.id) {
        case 1:        	
        	String name = UsernameGenerator.generate();
        	if (name != null && UsernameGenerator.validate(name)) {
        		usernameBox.setText(name);
        	}
        	status = gray + "Te gusta este nombre > " + red + name + gray + "?";
        	break;
        case 2:
        	if (username.isEmpty()) status = gray + "Debes ingresar un ign primero.";
            if (UsernameGenerator.validate(username)) {            	
            	((IMinecraft) mc).setSession(new Session(username.toString(), "none", "none", "mojang"));
            	status = gray + "Logeado como > " + green + username;
            }
        	break;
        case 3:
        	status = gray + "Abriendo navegador...";
        	
            MicrosoftAccount.create();
        	break;
        case 4:
        	mc.displayGuiScreen(new CookieMenu());
            break;
        case 5:
        	mc.displayGuiScreen(new GuiMainMenu());
        	break;
        }
    }
}