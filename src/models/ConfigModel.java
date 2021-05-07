package models;

public class ConfigModel{

    public final int MAX_NUMBERS = 200;
    public final int MIN_NUMBERS = 5;

    public final int MAX_SPEED_INTERVAL = 2000;
    public final int MIN_SPEED_INTERVAL = 10;

    int numbersSize;
    String algorithmSelected;
    long speedInterval;


    public int getNumbersSize() {
        return numbersSize;
    }

    public void setNumbersSize(int numbersSize) {
        this.numbersSize = numbersSize;
    }

    public String getAlgorithmSelected() {
        return algorithmSelected;
    }
    public void setAlgorithmSelected(String algorithmSelected) {
        this.algorithmSelected = algorithmSelected;
    }

    public long getSpeedInterval() {
        return speedInterval;
    }
    public void setSpeedInterval(long speedInterval) {
        this.speedInterval = speedInterval;
    }


}
