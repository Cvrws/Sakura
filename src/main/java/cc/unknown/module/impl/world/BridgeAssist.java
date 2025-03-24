package cc.unknown.module.impl.world;

import org.lwjgl.input.Keyboard;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.MotionEvent;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.Render3DForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

@ModuleInfo(name = "BridgeAssist", category = Category.WORLD)	
public class BridgeAssist extends Module {
    
    private final SliderValue delay = new SliderValue("Delay (ms)", this, 140, 0, 200, 1);
    private final BoolValue checkAngle = new BoolValue("Check Angle", this, false);
    private final BoolValue legit = new BoolValue("Legitimize", this, true);
    private final BoolValue holdShift = new BoolValue("Require Sneak", this, true);
    private final BoolValue slotSwap = new BoolValue("Block Switching", this, false);
    private final BoolValue blocksOnly = new BoolValue("Only Blocks", this, true);
    private final BoolValue backwards = new BoolValue("Only Backwards", this, true);

    private int slot;
    private boolean shouldBridge, isShifting = false;
    private StopWatch stopWatch = new StopWatch();
    
    @Override
    public void onEnable() {
        if (slotSwap.get()) slot = -1;
    }

    @Override
    public void onDisable() {
        PlayerUtil.setShift(false);
        stopWatch.reset();
        
        if (overAir()) PlayerUtil.setShift(false);
        
        if (slotSwap.get()) mc.thePlayer.inventory.currentItem = slot;
    }

    @Kisoji
    public final Listener<MotionEvent.Pre> onMotion = event -> {    	
        if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) return;
        
        boolean shift = delay.getValue() > 0;
        
        if (holdShift.get()) {
            if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
               shouldBridge = false;
               return;
            }
         }
        
		if (blocksOnly.get()) {
			ItemStack i = mc.thePlayer.getHeldItem();
			if (i == null || !(i.getItem() instanceof ItemBlock)) {
				if (isShifting) {
					isShifting = false;
				}
				return;
			}
		}
        
        if (backwards.get() && (mc.thePlayer.movementInput.moveForward > 0) && (mc.thePlayer.movementInput.moveStrafe == 0) || mc.thePlayer.movementInput.moveForward >= 0) {
            shouldBridge = false;
            isShifting = false;
            return;
        }
        
		if (checkAngle.get() && mc.thePlayer.rotationPitch < 0 || mc.thePlayer.rotationPitch > 90) {
			isShifting = false;
			return;
		}
        
		if ((legit.get() || mc.thePlayer.onGround) && mc.inGameHasFocus && mc.currentScreen == null) {

			if (overAir()) {
				if (shift) {
					stopWatch.setMillis(randomInt((int) delay.get(), (int) (delay.get() + 0.1)));
					stopWatch.reset();
				}

				isShifting = true;
				PlayerUtil.setShift(true);
				shouldBridge = true;
			} 
			
			else if (mc.thePlayer.isSneaking() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && holdShift.get()) {
				isShifting = false;
				shouldBridge = false;
				PlayerUtil.setShift(false);
			}
			
			else if (holdShift.get() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				isShifting = false;
				shouldBridge = false;
				PlayerUtil.setShift(false);
			}
			
			else if (mc.thePlayer.isSneaking() && (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && holdShift.get()) && (!shift || stopWatch.finished())) {
				isShifting = false;
				PlayerUtil.setShift(false);
				shouldBridge = true;
			}
			
			else if (mc.thePlayer.isSneaking() && !holdShift.get() && (!shift || stopWatch.finished())) {
				isShifting = false;
				PlayerUtil.setShift(false);
				shouldBridge = true;
			}
		} 
		
		else if (shouldBridge && mc.thePlayer.capabilities.isFlying) {
			PlayerUtil.setShift(false);
			shouldBridge = false;
		}
		
		else if (shouldBridge && overAir()) {
			isShifting = true;
			PlayerUtil.setShift(true);
		} 
		
		else {
			isShifting = false;
			PlayerUtil.setShift(false);
		}

    };
    
    @Kisoji
    public final Listener<Render3DForgeEvent> onRender3D = event -> {
        if (!isInGame()) return;
        
        if (slotSwap.get() && slot == -1) {
            slot = mc.thePlayer.inventory.currentItem;
        }
        
        int slot = InventoryUtil.findBlock();
        
        if (slot == -1) return;
        
        if (slotSwap.get() && shouldSkipBlockCheck()) {
            mc.thePlayer.inventory.currentItem = slot;
        }
        
        if (mc.currentScreen == null || mc.thePlayer.getHeldItem() == null) return;
    };

    private boolean shouldSkipBlockCheck() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        return heldItem == null || !(heldItem.getItem() instanceof ItemBlock);
    }
    
    private boolean overAir() {
        return mc.theWorld.isAirBlock(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.posY - 1.0D), MathHelper.floor_double(mc.thePlayer.posZ)));
    }
	
	private int randomInt(int x, int v) {
		return (int) (Math.random() * (x - v) + v);
	}
}
