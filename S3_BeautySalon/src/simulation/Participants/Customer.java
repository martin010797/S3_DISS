package simulation.Participants;

import OSPABA.MessageForm;
import simulation.MyMessage;

public class Customer implements Comparable<Customer>{
    private double arriveTime;
    private CurrentPosition currentPosition;

    private boolean hairstyle;
    private boolean makeup;
    private boolean cleaning;

    private boolean isPaying;

    private boolean arrivedOnCar;
    private int parkingPosition;

    private MessageForm message;
    private Personnel chosenPersonnel;
    private double serviceStartTime;

    //TODO asi nakoniec nebude potrebne
    private boolean leavingUnserved;

    public Customer(double arriveTime){
        this.arriveTime = arriveTime;
        currentPosition = CurrentPosition.ARRIVED;
        hairstyle = false;
        makeup = false;
        cleaning = false;
        isPaying = false;
        arrivedOnCar = false;
        leavingUnserved = false;
    }

    public CurrentPosition getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(CurrentPosition currentPosition) {
        this.currentPosition = currentPosition;
    }

    public double getArriveTime() {
        return arriveTime;
    }

    public boolean isHairstyle() {
        return hairstyle;
    }

    public void setHairstyle(boolean hairstyle) {
        this.hairstyle = hairstyle;
    }

    public boolean isMakeup() {
        return makeup;
    }

    public void setMakeup(boolean makeup) {
        this.makeup = makeup;
    }

    public boolean isCleaning() {
        return cleaning;
    }

    public void setCleaning(boolean cleaning) {
        this.cleaning = cleaning;
    }

    public boolean isPaying() {
        return isPaying;
    }

    public void setPaying(boolean paying) {
        isPaying = paying;
    }

    public boolean isArrivedOnCar() {
        return arrivedOnCar;
    }

    public void setArrivedOnCar(boolean arrivedOnCar) {
        this.arrivedOnCar = arrivedOnCar;
    }

    public int getParkingPosition() {
        return parkingPosition;
    }

    public void setParkingPosition(int parkingPosition) {
        this.parkingPosition = parkingPosition;
    }

    public void setArriveTime(double arriveTime) {
        this.arriveTime = arriveTime;
    }

    public MessageForm getMessage() {
        return message;
    }

    public void setMessage(MessageForm message) {
        this.message = message;
    }

    public Personnel getChosenPersonnel() {
        return chosenPersonnel;
    }

    public void setChosenPersonnel(Personnel chosenPersonnel) {
        this.chosenPersonnel = chosenPersonnel;
    }

    public double getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(double serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public boolean isLeavingUnserved() {
        return leavingUnserved;
    }

    public void setLeavingUnserved(boolean leavingUnserved) {
        this.leavingUnserved = leavingUnserved;
    }

    @Override
    public int compareTo(Customer o) {
        if (this.isPaying && !o.isPaying){
            return -1;
        }else if(!this.isPaying && o.isPaying){
            return 1;
        }else {
            return 0;
        }
    }
}
