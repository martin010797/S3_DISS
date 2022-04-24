package simulation.Participants;

public class Personnel implements Comparable<Personnel>{
    protected double workedTimeTogether;
    protected boolean isWorking;

    public Personnel() {
        workedTimeTogether = 0;
        isWorking = false;
    }

    @Override
    public int compareTo(Personnel o) {
        if (this.workedTimeTogether < o.workedTimeTogether){
            return -1;
        }else if(this.workedTimeTogether > o.workedTimeTogether){
            return 1;
        }else {
            return 0;
        }
    }

    public double getWorkedTimeTogether() {
        return workedTimeTogether;
    }

    public void setWorkedTimeTogether(double workedTimeTogether) {
        this.workedTimeTogether = workedTimeTogether;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }
}
