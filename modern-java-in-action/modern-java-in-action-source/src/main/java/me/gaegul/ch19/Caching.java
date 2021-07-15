package me.gaegul.ch19;

import scala.collection.immutable.Range;

import java.util.HashMap;
import java.util.Map;

public class Caching {

    private static final Map<Range, Integer> numberOfNodes = new HashMap<>();

    public static void main(String[] args) {
        Range range = new Range(0,1,1);
        numberOfNodes.put(range, 1234);
        computeNumberOfNodesUsingCache(range);
        System.out.println(numberOfNodes);
    }

    public static Integer computeNumberOfNodes(Range range) {
        Integer result = numberOfNodes.get(range);
        if (result != null) {
            return result;
        }

        result = computeNumberOfNodes(range);
        numberOfNodes.put(range, result);
        return result;
    }

    public static Integer computeNumberOfNodesUsingCache(Range range) {
        return numberOfNodes.computeIfAbsent(range, Caching::computeNumberOfNodes);
    }
}
