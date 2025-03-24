package cc.unknown.util.structure.lists;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class SHashSet<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CAPACITY = 16;

    private transient Object[] table;
    private int size = 0;
    private static final Object TOMBSTONE = new Object();

    public SHashSet() {
        this.table = new Object[DEFAULT_CAPACITY];
    }

    public SHashSet(int initialCapacity) {
        if (initialCapacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");
        this.table = new Object[initialCapacity];
    }

    public boolean add(T element) {
        if (contains(element)) return false;
        ensureCapacity(size + 1);
        int index = indexFor(element);
        while (table[index] != null && table[index] != TOMBSTONE) {
            index = (index + 1) % table.length;
        }
        table[index] = element;
        size++;
        return true;
    }

    public boolean remove(T element) {
        int index = indexFor(element);
        while (table[index] != null) {
            if (Objects.equals(table[index], element)) {
                table[index] = TOMBSTONE;
                size--;
                return true;
            }
            index = (index + 1) % table.length;
        }
        return false;
    }

    public boolean contains(T element) {
        int index = indexFor(element);
        while (table[index] != null) {
            if (table[index] != TOMBSTONE && Objects.equals(table[index], element)) {
                return true;
            }
            index = (index + 1) % table.length;
        }
        return false;
    }

    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Stream<T> stream() {
        return Arrays.stream((T[]) table).filter(obj -> obj != null && obj != TOMBSTONE);
    }

    private int indexFor(T element) {
        return Math.abs(Objects.hashCode(element)) % table.length;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > table.length * LOAD_FACTOR) {
            resize();
        }
    }

    private void resize() {
        Object[] oldTable = table;
        table = new Object[oldTable.length * 2];
        size = 0;
        for (Object obj : oldTable) {
            if (obj != null && obj != TOMBSTONE) add((T) obj);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int count = 0;

            @Override
            public boolean hasNext() {
                while (count < table.length && (table[count] == null || table[count] == TOMBSTONE)) {
                    count++;
                }
                return count < table.length;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) table[count++];
            }
        };
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (Object obj : table) {
            if (obj != null && obj != TOMBSTONE) action.accept((T) obj);
        }
    }
}
