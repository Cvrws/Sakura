package cc.unknown.util.waveycapes.config;

import cc.unknown.util.waveycapes.CapeMovement;
import cc.unknown.util.waveycapes.CapeStyle;
import cc.unknown.util.waveycapes.WindMode;

public class Config {
    public static final WindMode windMode = WindMode.NONE;
    public static final CapeStyle capeStyle = CapeStyle.SMOOTH;
    public static final CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;
    public static final int gravity = 25;
    public static final int heightMultiplier = 6;
}