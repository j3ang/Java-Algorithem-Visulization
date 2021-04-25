package models;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Visualizer extends MainModel {


    Session session;


    // Constructor
    public Visualizer() {
        this.session = Session.getInstace(new ConfigModel());
    }

    public ArrayList<double[]> getRectSize( int size){
        ArrayList<double[]> rectParams = new ArrayList<>();
        size = session.getConfig().getNumbersSize();
        return rectParams;
    }

    public HashMap<Integer, Color> getRaindowColorMap(int[] numbersArray){
        HashMap<Integer, Color> colors = new HashMap<>(); // create array of colors
        int size = numbersArray.length;
        int[] numbersArrayCopy = numbersArray.clone(); // assign colors with original array copy
        double jump = 360.0 / (size*1.0);

        // sort the array and assign rainbow color
        Arrays.sort(numbersArrayCopy);
        for (int i = 0; i < size; i++) {
            colors.put(
                    Double.valueOf(numbersArrayCopy[i]).intValue(),
                    Color.hsb((float) (jump * i), 1.0f, 1.0f)
            );
        }
        return colors;
    }

    public  ArrayList<Rectangle> getRectangles(){
        int[] generatedNumbers = session.getGeneratedNumbers();
        double rectWidth = ( Session.getDrawingWrapperWidth() /  session.getConfig().getNumbersSize()) ;
        ArrayList<Rectangle> rectArr = new ArrayList<>(); // rectArr to contain generated rectangles
        HashMap rainbowColorsMap = new Visualizer().getRaindowColorMap(generatedNumbers); //  getRaindowColorMap

        for ( int i = 0; i < session.getConfig().getNumbersSize(); i++){
            int x = Double.valueOf(740 - rectWidth*(i+1)).intValue();
            Rectangle rect = new Rectangle(
                    x , 0,
                    rectWidth ,
                    session.getGeneratedNumbers()[i]
            );
            Color color = (Color) rainbowColorsMap.get(Double.valueOf(session.getGeneratedNumbers()[i]).intValue());
            rect.setFill(color);
            rectArr.add(rect);
        }

        return rectArr;
    }


}
