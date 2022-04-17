package network.frostless.glacier.utils;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Weighted random collection
 * @param <E> The type of the elements
 * @implNote Author <a href="https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java">StackOverflow</a>
 */
public class RandomCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}
