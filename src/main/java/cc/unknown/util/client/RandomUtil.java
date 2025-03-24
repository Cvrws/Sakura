package cc.unknown.util.client;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.util.MathHelper;

public enum RandomUtil {
	instance;

	private final Random RANDOM = new Random();
	private final SecureRandom SECURE_RANDOM = new SecureRandom();

	private double devRand1 = 0f, devRand2 = 0f, devRand3 = 0f, devRand4 = 0f;
	private int patternIndex = 0;

	private final int[] pattern1 = new int[] { 17, 79, 83, 24, 81, 86, 30, 67, 97, 25, 79, 101, 33, 68, 129, 69, 96, 24,
			77, 90, 27, 81, 104, 30, 68, 94, 29, 80, 96, 25, 72, 114, 25, 79, 100, 26, 75, 117, 22, 77, 134, 16, 75,
			126, 22, 76, 119, 21, 94, 120, 32, 56, 119, 27, 80, 151, 68, 134, 30, 68, 115, 25, 92, 128, 28, 78, 134, 28,
			71, 133, 33, 50, 136, 32, 65, 137, 26, 63, 143, 23, 78, 134, 29, 71, 136, 32, 68, 146, 31, 62, 138, 165, 17,
			67, 97, 25, 78, 100, 34, 67, 32, 68, 116, 105, 28, 53, 105, 32, 79, 115, 25, 61, 103, 24, 60, 109, 29, 74,
			16, 98, 18, 97, 18, 69, 25, 85, 110, 30, 82, 83, 26, 83, 124, 22, 62, 134, 20, 77, 92, 27, 88, 112, 23, 89,
			122, 33, 67, 19, 88, 30, 79, 134, 108, 108, 27, 78, 128, 23, 76, 134, 27, 66, 140, 25, 79, 120, 44, 62, 124,
			31, 82, 108, 41, 85, 117, 27, 93, 129, 19, 76, 136, 110, 142, 20, 67, 112, 34, 71, 114, 23, 93, 105, 47, 58,
			112, 27, 66, 117, 22, 108, 111, 29, 83, 123, 27, 90, 127, 22, 92, 134, 26, 93, 131, 34, 83, 115, 24, 94,
			119, 47, 73, 113, 30, 92, 115, 27, 92, 128, 30, 72, 125, 115, 128, 30, 66, 135, 29, 57, 127, 106, 145, 18,
			68, 162, 70, 131, 21, 70, 127, 23, 61, 148, 86, 105, 30, 95, 102, 32, 82, 125, 42, 50, 150, 100, 124, 29,
			64 };

	private final int[] pattern2 = new int[] { 52, 87, 74, 73, 80, 78, 88, 78, 72, 93, 88, 72, 74, 76, 93, 77, 96, 68,
			82, 75, 73, 76, 90, 177, 66, 74, 76, 91, 86, 88, 90, 94, 84, 85, 80, 83, 147, 103, 93, 80, 109, 76, 84, 177,
			82, 110, 88, 83, 90, 110, 63, 87, 83, 74, 93, 83, 100, 73, 72, 122, 83, 117, 83, 84, 88, 82, 87, 78, 115,
			80, 86, 97, 87, 92, 92, 93, 93, 95, 87, 97, 68, 108, 70, 68, 87, 71, 94, 67, 96, 75, 81, 81, 93, 141, 87,
			78, 96, 80, 91, 121, 78, 96, 88, 132, 73, 92, 83, 95, 155, 89, 88, 76, 85, 95, 88, 75, 83, 73, 90, 79, 125,
			89, 94, 150, 103, 71, 78, 98, 167, 77, 103, 87, 84, 82, 88, 96, 166, 95, 67, 83, 83, 67, 83, 78, 105, 73,
			94, 99, 72, 93, 85, 84, 100, 86, 83, 100, 67, 83, 85, 85, 98, 65, 66, 84, 84, 99, 67, 101, 83, 82, 117, 116,
			84, 66, 83, 101, 67, 83, 168, 83, 65, 134, 50, 84, 82, 84, 83, 83, 101, 99, 83, 102, 65, 67, 68, 66, 83, 67,
			152, 128, 68, 79, 76, 93, 74, 100, 88, 71, 75, 93, 72, 70, 83, 100, 84, 65, 96, 88, 71, 78, 84, 84, 68, 87,
			157, 65, 88, 68, 97, 68, 113, 57, 93, 83, 72, 69, 78, 67, 84, 67, 151, 100, 83, 83, 67, 91, 161, 72, 73,
			100, 84, 87, 95, 87, 80, 83, 67, 83, 67, 93, 90, 84, 72, 94, 125, 81, 111, 83, 70, 80, 153, 91, 73, 100, 83,
			186 };

	private final int[] pattern3 = new int[] { 16, 22, 14, 46, 18, 8, 8, 63, 25, 25, 12, 39, 26, 18, 6, 62, 26, 18, 21,
			40, 26, 8, 16, 46, 26, 20, 15, 50, 25, 10, 11, 43, 25, 11, 37, 39, 25, 12, 18, 54, 25, 25, 15, 41, 27, 9, 1,
			66, 26, 17, 21, 48, 27, 8, 6, 62, 28, 19, 13, 47, 26, 7, 14, 53, 27, 16, 29, 38, 27, 8, 6, 60, 27, 22, 19,
			45, 26, 10, 10, 62, 25, 20, 28, 22, 26, 19, 11, 57, 26, 16, 32, 36, 26, 9, 9, 66, 27, 19, 27, 38, 26, 9, 10,
			61, 26, 25, 15, 34, 26, 20, 10, 52, 26, 22, 28, 29, 27, 8, 3, 63, 26, 21, 27, 38, 26, 10, 11, 38, 27, 15,
			31, 39, 25, 13, 10, 45, 27, 14, 27, 40, 26, 10, 6, 51, 26, 18, 31, 27, 27, 11, 14, 47, 26, 23, 21, 35, 26,
			12, 13, 41, 26, 15, 31, 36, 27, 16, 9, 44, 27, 14, 30, 39, 25, 14, 10, 46, 28, 10, 24, 45, 26, 7, 5, 46, 26,
			20, 6, 50, 26, 8, 6, 51, 26, 17, 20, 40, 27, 25, 1, 32, 26, 20, 9, 46, 25, 15, 12, 30, 26, 11, 25, 46, 27,
			13, 10, 36, 27, 20, 15, 41, 26, 8, 6, 41, 26, 12, 29, 44, 26, 13, 11, 44, 26, 12, 27, 36, 26, 23, 4, 39, 26,
			24, 12, 47, 26, 9, 2, 65, 26, 16, 27, 34, 26, 25, 0, 53, 26, 16, 3, 47, 27, 16, 10, 41, 26, 18, 25, 38, 26,
			11, 10, 50, 27, 20, 20, 29, 26, 11, 7, 66, 26, 20, 18, 31, 26, 21, 21, 28, 26, 21, 29, 25, 27, 15, 12, 43,
			28, 11, 31, 32, 27, 23, 0, 49, 27, 20, 30, 30, 25, 32, 0, 50, 26, 12, 25, 34, 27, 11, 11, 44, 27, 23, 26,
			25, 27, 16, 11, 46, 26, 13, 32, 35, 28, 9, 5, 48, 26, 21, 29, 37, 26, 10, 7, 48, 27, 20, 21, 41, 24, 7, 18,
			46, 25, 22, 22, 33, 25, 10, 5, 59, 26, 21, 19, 29, 26, 11, 10, 46, 25, 22, 29, 31, 25, 11, 12, 50, 24, 20,
			28, 40, 25, 10, 4, 56, 25, 16, 36, 30, 24, 10, 9, 63, 25, 22, 22, 32, 25, 9, 8, 58 };

	private int devRandTick = 0;
	
	private final int minPattern = Arrays.stream(pattern3).min().orElse(0);
	private final int maxPattern = Arrays.stream(pattern3).max().orElse(100);

	public long randomization(String mode, int cps, float rand) {
		switch (mode) {
		case "None":
			return (long) (1000 / cps);
		case "Gaussian":
			return (long) (1000 / getGaussian(cps, rand));
		case "Deviation":
			return (long) (1000 / getDeviation(cps, rand));
		case "Simple":
			return (long) (1000 / getSimple(cps, rand));
		case "ButterFly":
			return (long) (1000 / getButterFly(cps, rand));
		case "Plus":
			return (long) (1000 / getPlus(cps, rand));
		case "Extra":
			return (long) (1000 / getExtra(cps, rand));
		case "Pattern":
			return (long) (1000 / getPattern(cps, rand));
		case "PatternPlus":
			return (long) (1000 / getPattern2(cps, rand));
		}
		return 0L;
	}

	private double getSimple(double cps, double rand) {
		double rnd = MathHelper.getRandomDoubleInRange(RANDOM, 0, 1);
		double normal = Math.sqrt(-2 * (Math.log(rnd) / Math.log(Math.E))) * Math.sin(2 * Math.PI * rnd);
		return cps + rand * normal;
	}

	private double getPlus(double cps, double rand) {
		double rnd = MathHelper.getRandomDoubleInRange(RANDOM, 0, 1);
		double normal = Math.sqrt(-2 * (Math.log(rnd) / Math.log(Math.E))) * Math.sin(2 * Math.PI * rnd);
		return cps + rand * normal + ((cps + SECURE_RANDOM.nextDouble() * rand) / 4);
	}

	private double getGaussian(double cps, double rand) {
		double rnd = RANDOM.nextDouble();
		double normal = Math.sqrt(-2 * Math.log(rnd)) * Math.sin(2 * Math.PI * RANDOM.nextDouble());
		return Math.max(1, cps + rand * normal + ((cps + SECURE_RANDOM.nextGaussian() * rand) / 4));
	}

	private double getDeviation(double cps, double rand) {
		double gaussian = SECURE_RANDOM.nextGaussian();
		double adjusted = cps + gaussian * rand;
		return Math.max(1, adjusted);
	}

	private double getExtra(double cps, double rand) {
		if (devRandTick % 30 == 0) {
			devRand1 = MathHelper.getRandomDoubleInRange(RANDOM, 0, 1);
			devRand2 = MathHelper.getRandomDoubleInRange(RANDOM, 0, 1);
			devRand3 = MathHelper.getRandomDoubleInRange(RANDOM, 0, 1);
			devRand4 = MathHelper.getRandomDoubleInRange(RANDOM, 0, 1);
		}

		double randOffset1 = 0D;
		double randOffset2 = 0D;

		switch (MathHelper.getRandomIntegerInRange(RANDOM, 1, 4)) {
		case 1:
			randOffset1 = devRand1;
			break;
		case 2:
			randOffset1 = devRand2;
			break;
		case 3:
			randOffset1 = devRand3;
			break;
		case 4:
			randOffset1 = devRand4;
			break;
		}
		switch (MathHelper.getRandomIntegerInRange(RANDOM, 1, 4)) {
		case 1:
			randOffset2 = devRand1;
			break;
		case 2:
			randOffset2 = devRand2;
			break;
		case 3:
			randOffset2 = devRand3;
			break;
		case 4:
			randOffset2 = devRand4;
			break;
		}

		double rand1 = getPlus(cps + ((-0.3 + randOffset1 * 0.6) * rand), rand * (0.5 + (randOffset1 * 0.3)));
		double rand2 = getSimple(cps + (randOffset2 * rand), rand * (0.2 + (randOffset2 * 0.4)));
		return (3 * rand1 + rand2) / 4;
	}

	private double getPattern(double cps, double rand) {
		patternIndex++;
		if (patternIndex >= pattern1.length) {
			patternIndex = 0;
		}
		return cps + (rand * pattern1[patternIndex] / 164.0);
	}

	private double getPattern2(double cps, double rand) {
		patternIndex++;
		if (patternIndex >= pattern2.length) {
			patternIndex = 0;
		}
		return cps + (rand * pattern2[patternIndex] / 180.0);
	}

	private double getButterFly(double cps, double rand) {
	    patternIndex++;
	    if (patternIndex >= pattern3.length) {
	        patternIndex = 0;
	    }

	    double legitimize = (double) (pattern3[patternIndex] - minPattern) / (maxPattern - minPattern);

	    return cps + (rand * legitimize * cps * 0.5);
	}
}
