package models.algorithms;

import Threading.SortTask;
import Threading.SwapItem;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class BubbleSort extends SortTask {

    // this will setup the rectangles Array
    public BubbleSort(ArrayList<Rectangle> rectanglesArray) {
        super(rectanglesArray);
    }

    @Override
    protected void doSorting() {

        System.out.println("entered doSorting..");
        System.out.println("right now rectanglesArray is set to " + rectanglesArray);
        // iterate the array length - 1 to do comparison
        // this should be the index of the number to be compared
        for ( int f=0; f < rectanglesArray.size() - 1; f++ ){
            // move the number to be compared through the array length
            // as long as the index is less than the length after deducted first number index
            for ( int s = 0; s < rectanglesArray.size() - f - 1; s++ ){
                int secondRectHeight = Double.valueOf(rectanglesArray.get(s+1).getHeight()).intValue();
                int firstRectHeight =  Double.valueOf(rectanglesArray.get(s).getHeight()).intValue();
                if ( secondRectHeight > firstRectHeight ){
                    // return item to be swapped
//                    System.out.println("rectanglesArray.get(s) is set to :" + rectanglesArray.get(s));
                    SwapItem swapItem = new SwapItem(rectanglesArray.get(s+1), s+1);
                    swapItem.setSecondPos(s);
                    updateValue(swapItem);
                    waitOnFlag();
                }
            }
        }
    }

    @Override
    public Runnable getSwapCode(SwapItem swapItem) {

        return ()->{ // returns runnable
            try {
                if (swapItem != null) {
                    System.out.println("swapItem is not null: " + swapItem);
                    Rectangle rect = swapItem.getRectToSwap();
                    System.out.println("swapItem.getRectToSwap is not null: " + rect);

                }
                flag.set(true);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        };

    }

}
