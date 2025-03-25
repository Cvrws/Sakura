package cc.unknown.module.impl.combat;

import cc.unknown.event.Kisoji;
import cc.unknown.event.impl.buz.Listener;
import cc.unknown.event.impl.forge.Render3DForgeEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.RandomUtil;
import cc.unknown.util.client.StopWatch;
import cc.unknown.util.player.PlayerUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.ModeValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

@ModuleInfo(name = "AutoClicker", category = Category.COMBAT)
public class AutoClicker extends Module {
			
	private final SliderValue leftCps = new SliderValue("LeftCPS", this, 10, 1, 25);
	private final SliderValue randomizeAmount = new SliderValue("Amount", this, 1.5f, 0f, 4f, 0.1f, () -> !this.randomization.is("Normal"));
	private final BoolValue cpsMultiplicator = new BoolValue("CPSMultiplicator", this, false);
	private final SliderValue multiplicator = new SliderValue("LeftMult", this, 10, 0, 300, cpsMultiplicator::get);
	
	private final BoolValue breakBlocks = new BoolValue("BreakBlocks", this, true);
	private final ModeValue randomization = new ModeValue("Randomization", this, "Normal", "Normal", "Extra", "Extra+");


	private final StopWatch stopWatch = new StopWatch();

	@Override
	public void onEnable() {
		stopWatch.reset();
	}
	
	@Override
	public void onDisable() {
		stopWatch.reset();
	}

	@Kisoji
	public final Listener<Render3DForgeEvent> onRenderHand = event -> {
		mc.leftClickCounter = 0;
		if (mc.currentScreen != null || !mc.inGameHasFocus) return;

		if (mc.gameSettings.keyBindAttack.isKeyDown()) {
			if (breakBlocks.get() && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
				return;
			}
			
			if (cpsMultiplicator.get()) {
			    int totalCps = (int) leftCps.getValue() + (int) multiplicator.getValue();
			    int delay = (int) RandomUtil.instance.randomization(randomization.getMode(), totalCps, randomizeAmount.getValue());
			    
			    if (stopWatch.finished(delay)) {
			    	PlayerUtil.leftClick(true);
			        stopWatch.reset();
			    }
			}
			
			if (stopWatch.finished(RandomUtil.instance.randomization(randomization.getMode(), (int) leftCps.getValue(), randomizeAmount.getValue()))) {
				PlayerUtil.leftClick(true);
				stopWatch.reset();
			}
		}
	};
}