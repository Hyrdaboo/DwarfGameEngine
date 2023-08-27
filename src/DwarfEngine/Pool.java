package DwarfEngine;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Pool<V> {
	public static class PoolInstance<V> {
		final ArrayList<V> values = new ArrayList<>();
		int index = 0;

		V get(Supplier<V> creator) {
			if (index < values.size()) {
				return values.get(index++);
			}
			for (int i = 0; i < 32; i++) {
				values.add(creator.get());
			}
			return values.get(index++);
		}

		void sub(int i) {
			index -= i;
		}
	}

	private final Supplier<V> creator;
	private final ThreadLocal<PoolInstance<V>> storage = ThreadLocal.withInitial(PoolInstance::new);

	public Pool(Supplier<V> creator) {
		this.creator = creator;
	}

	public V get() {
		return storage.get().get(creator);
	}

	public PoolInstance<V> getI() {
		return storage.get();
	}

	public void sub(int i) {
		storage.get().sub(i);
	}
}
