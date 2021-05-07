package models.algorithms.commons;


import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * The type Visualizer.
 */
public class NumbersList  {


    /**
     * Get rainbow colored numbers map hash map.
     *
     * @param numbersArray the numbers array
     * @return the hash map
     */
    public HashMap<Integer, HashMap<Integer, String>> getRainbowColoredNumbersMap(int[] numbersArray){
        HashMap<Integer, HashMap<Integer, String>> rainbowColoredNumbersMap = new HashMap<>();

        int size = numbersArray.length;
        int[] numbersArrayCopy = numbersArray.clone(); // assign colors with original array copy
        double jump = 360.0 / (size*1.0);

        // sort the array and assign rainbow color
        Arrays.sort(numbersArrayCopy);
        for (int i = 0; i < size; i++) {
            HashMap<Integer, String> numberColorMap = new HashMap<>(); // color map
            Color color = Color.hsb((float) (jump * i), 1.0f, 1.0f);

            // colored map
            numberColorMap.put(
                    numbersArrayCopy[i],
                    toHexString(color)
            );

            // add to hashmap
            int [] foundIndexes = findIndex(numbersArray, numbersArrayCopy[i]);
            for ( int index : foundIndexes)
                rainbowColoredNumbersMap.put(index, numberColorMap);
        }
        return rainbowColoredNumbersMap;
    }

    // https://www.educative.io/edpresso/how-to-generate-random-numbers-in-java
    public int[] getRandomNumbers(int size, double drawingWrapperHeight) {
        int[] arr = new int[size];
        Random randNum = new Random();
        System.out.println("bound is : " +  drawingWrapperHeight );
        for (int i = 0; i < size; i++) {
            // https://stackoverflow.com/questions/5827023/java-random-giving-negative-numbers
            arr[i] = randNum.nextInt(  Double.valueOf(drawingWrapperHeight).intValue() );
        }

        return arr;
    }


    // Helpers

    /**
     * Find index int [ ].
     *
     * @param arr the arr
     * @param t   the t
     * @return the int [ ]
     */
// Function to find the index of an element
    public static int[] findIndex(int[] arr, int t)
    {
        int len = arr.length;
        return IntStream.range(0, len)
                .filter(i -> t == arr[i])
                .toArray();
    }

    /**
     * To hex string string.
     *
     * @param color the color
     * @return the string
     */
    public static String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed()     * 255)) << 24;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
        return String.format("#%08X", (r + g + b + a));
    }

}
