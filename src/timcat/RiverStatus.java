package timcat;

public class RiverStatus {
    private final int farmer;
    private final int wolf;
    private final int sheep;
    private final int cabbage;
    private final String message;
    private boolean isVisited;

    public RiverStatus(int farmer, int wolf, int sheep, int cabbage, String message) {
        this.farmer = farmer;
        this.wolf = wolf;
        this.sheep = sheep;
        this.cabbage = cabbage;
        this.message = message;
        this.isVisited = false;
    }

    public int getFarmer() {
        return farmer;
    }

    public int getWolf() {
        return wolf;
    }

    public int getSheep() {
        return sheep;
    }

    public int getCabbage() {
        return cabbage;
    }

    public String getMessage() {
        return message;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }
}
