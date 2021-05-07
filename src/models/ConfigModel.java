package models;

public class ConfigModel{

    public final int MAX_NUMBERS = 100;
    public final int MIN_NUMBERS = 5;

    public final int MAX_SPEED_INTERVAL = 2000;
    public final int MIN_SPEED_INTERVAL = 10;

    private int numbersSize;
    private String algorithmSelected;
    private long speedInterval;
    private boolean isSorted;

    // Construstor with default configure values
	public ConfigModel() {
		this.numbersSize = 20;
		this.speedInterval = 10;
		this.isSorted = false;
	}

	public boolean isSorted() {
		return isSorted;
	}

	public void setSorted(boolean sorted) {
		isSorted = sorted;
	}

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
