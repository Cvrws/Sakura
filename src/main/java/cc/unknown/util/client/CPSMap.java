package cc.unknown.util.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CPSMap {
	private static List<Long> clicks = new ArrayList<Long>();
	public static boolean preventDoubleclicks;

	public static void addClick() {
		clicks.add(Long.valueOf(System.currentTimeMillis()));
	}

	public static int getClicks() {
		Iterator<Long> iterator = clicks.iterator();
		while (iterator.hasNext()) {
			if (((Long) iterator.next()).longValue() < System.currentTimeMillis() - 1000L)
				iterator.remove();
		}
		return clicks.size();
	}
}
