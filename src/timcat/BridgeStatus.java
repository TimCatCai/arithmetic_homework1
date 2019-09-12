package timcat;

public class BridgeStatus {
    private final People [] peoples;
    private final int [] defaultTime;
    private final String message;

    public BridgeStatus(int [] status, String message) {
        this.peoples = new People[status.length];
        this.message = message;
        defaultTime = new int[]{1, 2, 5, 10};
        for(int i = 0; i < peoples.length; i++) {
            peoples[i] = new People("a" + i, defaultTime[i], status[i]);
        }
    }

    public BridgeStatus(int [] status, int [] defaultTime, String message) {
        this.message = message;
        if(status.length != defaultTime.length){
            throw new IllegalArgumentException("参数数组的长度不一致");
        }
        this.peoples = new People[status.length];
        this.defaultTime = defaultTime;
        for(int i = 0; i < peoples.length; i++) {
            peoples[i] = new People("a" + i, defaultTime[i], status[i]);
        }
    }
    public People[] getPeoples() {
        return peoples;
    }

    public String getMessage() {
        return message;
    }
}
