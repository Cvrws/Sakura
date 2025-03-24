package cc.unknown.util.client;

import java.security.SecureRandom;

import cc.unknown.util.Accessor;
import io.netty.util.internal.ThreadLocalRandom;

public class MathUtil implements Accessor {

	public static Number nextRandom(Number origin, Number bound) {
		if (origin.equals(bound))
			return origin;

		if (origin instanceof Integer && bound instanceof Integer) {
			return ThreadLocalRandom.current().nextInt((Integer) origin, (Integer) bound);
		} else if (origin instanceof Long && bound instanceof Long) {
			return ThreadLocalRandom.current().nextLong((Long) origin, (Long) bound);
		} else if (origin instanceof Float && bound instanceof Float) {
			return (float) ThreadLocalRandom.current().nextDouble((Float) origin, (Float) bound);
		} else if (origin instanceof Double && bound instanceof Double) {
			return ThreadLocalRandom.current().nextDouble((Double) origin, (Double) bound);
		} else {
			throw new IllegalArgumentException(
					"Unsupported number types: " + origin.getClass() + " and " + bound.getClass());
		}
	}

	public static Number nextSecure(Number origin, Number bound) {
		if (origin.equals(bound)) return origin;
		SecureRandom secureRandom = new SecureRandom();

		if (origin instanceof Integer && bound instanceof Integer) {
			return origin.intValue() + secureRandom.nextInt(bound.intValue() - origin.intValue());
		} else if (origin instanceof Double && bound instanceof Double) {
			return origin.doubleValue() + secureRandom.nextDouble() * (bound.doubleValue() - origin.doubleValue());
		} else {
			throw new IllegalArgumentException(
					"Unsupported number types for secure random: " + origin.getClass() + " and " + bound.getClass());
		}
	}

	public static Number lerp(final Number a, final Number b, final Number c) {
		if (a instanceof Integer && b instanceof Integer && c instanceof Integer) {
			return a.intValue() + c.intValue() * (b.intValue() - a.intValue());
		} else if (a instanceof Double && b instanceof Double && c instanceof Double) {
			return a.doubleValue() + c.doubleValue() * (b.doubleValue() - a.doubleValue());
		} else if (a instanceof Float && b instanceof Float && c instanceof Float) {
			return a.floatValue() + c.floatValue() * (b.floatValue() - a.floatValue());
		} else if (a instanceof Long && b instanceof Long && c instanceof Long) {
			return a.longValue() + c.longValue() * (b.longValue() - a.longValue());
		} else {
			throw new IllegalArgumentException(
					"Unsupported number types for: " + a.getClass() + ", " + b.getClass() + " and " + c.getClass());
		}
	}

	public static double wrappedDifference(double number1, double number2) {
		return Math.min(Math.abs(number1 - number2), Math.min(Math.abs(number1 - 360) - Math.abs(number2 - 0),
				Math.abs(number2 - 360) - Math.abs(number1 - 0)));
	}

	public static double clamp(double min, double max, double n) {
		return Math.max(min, Math.min(max, n));
	}

	public static long getSafeRandom(long min, long max) {
		double randomPercent = nextRandom(0.7, 1.3).doubleValue();
		long delay = (long) (randomPercent * nextRandom(min, max + 1).longValue());
		return delay;
	}

    public static double incValue(double val, double inc) {
        double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }
  
    public static int randomizeInt(float min, float max) {
        return (int) randomizeDouble(min, max);
    }
    
    public static float randomizeFloat(float min, float max) {
        return (float) randomizeDouble(min, max);
    }

    public static double randomizeDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }
    
    public static double getRandomGaussian(double average) {
        return ThreadLocalRandom.current().nextGaussian() * average;
    }
    
    public static float calculateGaussianValue(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }
}