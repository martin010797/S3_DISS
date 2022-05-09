package simulation.Participants;

import OSPABA.MessageForm;

import java.util.ArrayList;

public class Customer implements Comparable<Customer>{
    private double arriveTime;
    private CurrentPosition currentPosition;

    private boolean hairstyle;
    private boolean makeup;
    private boolean cleaning;

    private boolean isPaying;

    private boolean arrivedOnCar;
    private int finalParkingNumber;
    private String finalParkingLine;

    private CurrentParkingPosition currentParkingPosition;
    private int currentParkingNumber;
    //spokojnost s parkovanim
    private int currentCustomerSuccessRateValue;

    //kvoli strategiam, aby vedel ktore rady uz overoval
    private ArrayList<String> processedLines;

    private MessageForm message;
    private Personnel chosenPersonnel;
    private double serviceStartTime;

    public Customer(double arriveTime){
        this.arriveTime = arriveTime;
        currentPosition = CurrentPosition.ARRIVED;
        hairstyle = false;
        makeup = false;
        cleaning = false;
        isPaying = false;
        arrivedOnCar = false;
        processedLines = new ArrayList<>();
        currentParkingNumber = -1;
        currentCustomerSuccessRateValue = 0;
        finalParkingNumber = -1;
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

    public int getFinalParkingNumber() {
        return finalParkingNumber;
    }

    public void setFinalParkingNumber(int finalParkingNumber) {
        this.finalParkingNumber = finalParkingNumber;
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

    public String getFinalParkingLine() {
        return finalParkingLine;
    }

    public void setFinalParkingLine(String finalParkingLine) {
        this.finalParkingLine = finalParkingLine;
    }

    public CurrentParkingPosition getCurrentParkingPosition() {
        return currentParkingPosition;
    }

    public void setCurrentParkingPosition(CurrentParkingPosition currentParkingPosition) {
        this.currentParkingPosition = currentParkingPosition;
    }

    public ArrayList<String> getProcessedLines() {
        return processedLines;
    }

    public int getCurrentParkingNumber() {
        return currentParkingNumber;
    }

    public void setCurrentParkingNumber(int currentParkingNumber) {
        this.currentParkingNumber = currentParkingNumber;
    }

    public void setProcessedLines(ArrayList<String> processedLines) {
        this.processedLines = processedLines;
    }

    public int getCurrentCustomerSuccessRateValue() {
        return currentCustomerSuccessRateValue;
    }

    public void setCurrentCustomerSuccessRateValue(int currentCustomerSuccessRateValue) {
        this.currentCustomerSuccessRateValue = currentCustomerSuccessRateValue;
    }

    public void increaseCurrentParkingNumber(){
        currentParkingNumber++;
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
