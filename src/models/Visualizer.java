package models;


import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.IntStream;

public class Visualizer extends MainModel {

    Session session;
    // Constructor
    public Visualizer() {
        this.session = Session.getInstace(new ConfigModel());
    }

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


    // Helpers

    // Function to find the index of an element
    public static int[] findIndex(int[] arr, int t)
    {
        int len = arr.length;
        return IntStream.range(0, len)
                .filter(i -> t == arr[i])
                .toArray();
    }


    public static String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed()     * 255)) << 24;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
        return String.format("#%08X", (r + g + b + a));
    }


}
