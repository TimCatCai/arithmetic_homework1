package timcat;

public class People {
    private String name;
    private int time;
    private int status;

    public People(String name, int time, int status) {
        this.name = name;
        this.time = time;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
