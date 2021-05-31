package tfar.ae2wt;

import appeng.core.AEConfig;

public class WTConfig {

    public static double getPowerMultiplier(double range, boolean isOutOfRange) {
        if(!isOutOfRange) return AEConfig.instance().wireless_getDrainRate(range);
        return AEConfig.instance().wireless_getDrainRate(16 * getOutOfRangePowerMultiplier());
    }

    public static double getChargeRate() {
        return 32000;
    }

    public static double WUTChargeRateMultiplier() {
        return 1;
    }

    private static int getOutOfRangePowerMultiplier() {
        return 2;
    }
}