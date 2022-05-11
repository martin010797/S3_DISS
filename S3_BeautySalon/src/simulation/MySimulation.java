package simulation;

//import Gui.ISimDelegate;
import OSPABA.*;
import OSPStat.Stat;
import agents.*;
import simulation.Participants.CurrentParkingPosition;
import simulation.Participants.CurrentPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MySimulation extends Simulation
{
	private List<ISimDelegate> delegates;
	private TypeOfSimulation typeOfSimulation;

	private int deltaT;
	private int sleepLength;
	private int numberOfReceptionists;
	private int numberOfMakeupArtists;
	private int numberOfHairstylists;

	private ParkingStrategy chosenStrategy;
	private int numberOfBuiltParkingLines;

	//globalne statistiky
	private Stat timeInSystem;
	private Stat lengthOfQueueReception;
	private Stat waitTimeForPlacingOrder;
	private Stat timeInSystemUntil17;
	private Stat lengthOfQueueReceptionUntil17;
	//spokojnosti s parkovanim
	private Stat customersSuccesRates;
	//kolko percent zakazikov zaparkovalo
	private Stat parkingSuccessRatePercentage;
	private Stat servedCustomers;
	private Stat leftAfterClosing;
	private Stat unsuccessfulParking;
	private Stat arrivedOnCar;

	private Random seedGenerator;

	public MySimulation()
	{
		init();
		seedGenerator = new Random();
		agentHairstylist().setSeedGenerator(seedGenerator);
		agentEnviroment().setSeedGenerator(seedGenerator);
		agentMakeUpArtist().setSeedGenerator(seedGenerator);
		agentParking().setSeedGenerator(seedGenerator);
		agentReceptionist().setSeedGenerator(seedGenerator);
		delegates = new ArrayList<>();
		numberOfReceptionists = 0;
		numberOfHairstylists = 0;
		numberOfMakeupArtists = 0;
	}

	@Override
	public void prepareSimulation()
	{
		super.prepareSimulation();
		// Create global statistcis
		timeInSystem = new Stat();
		lengthOfQueueReception = new Stat();
		waitTimeForPlacingOrder = new Stat();
		timeInSystemUntil17 = new Stat();
		lengthOfQueueReceptionUntil17 = new Stat();
		customersSuccesRates = new Stat();
		parkingSuccessRatePercentage = new Stat();
		servedCustomers = new Stat();
		leftAfterClosing = new Stat();
		unsuccessfulParking = new Stat();
		arrivedOnCar = new Stat();
	}

	@Override
	public void prepareReplication()
	{
		//agentBeautySalon().setNumberOfReceptionists(numberOfReceptionists);
		agentReceptionist().setNumberOfReceptionists(numberOfReceptionists);
		//agentBeautySalon().setNumberOfHairstylists(numberOfHairstylists);
		agentHairstylist().setNumberOfHairstylists(numberOfHairstylists);
		//agentBeautySalon().setNumberOfMakeupArtists(numberOfMakeupArtists);
		agentMakeUpArtist().setNumberOfMakeupArtists(numberOfMakeupArtists);
		agentParking().setChosenStrategy(chosenStrategy);
		agentParking().setNumberOfBuiltParkingLines(numberOfBuiltParkingLines);
		super.prepareReplication();
		// Reset entities, queues, local statistics, etc...
	}

	@Override
	public void replicationFinished()
	{
		// Collect local statistics into global, update UI, etc...
		timeInSystem.addSample(agentBeautySalon().getTimeInSystem().mean());
		timeInSystemUntil17.addSample(agentBeautySalon().getTimeInSystemUntil17().mean());
		agentReceptionist().getLengthOfReceptionQueue().addSample(agentReceptionist().getReceptionWaitingQueue().size());
		lengthOfQueueReception.addSample(agentReceptionist().getLengthOfReceptionQueue().mean());
		waitTimeForPlacingOrder.addSample(agentReceptionist().getWaitTimeForPlacingOrder().mean());
		lengthOfQueueReceptionUntil17.addSample(agentReceptionist().getLengthOfQueueUntil17());
		customersSuccesRates.addSample(agentParking().getCustomersSuccessRateValues().mean());
		double arriveOnCar = agentModel().getArrivedOnCar();
		double unsuccessfulPark = agentParking().getLeavingBecauseOfUnsuccessfulParking();
		parkingSuccessRatePercentage.addSample(((arriveOnCar - unsuccessfulPark)/arriveOnCar)*100.0);
		servedCustomers.addSample(agentBeautySalon().getNumberOfServedCustomers());
		leftAfterClosing.addSample(agentBeautySalon().getNumberOfLeavingCustomers());
		unsuccessfulParking.addSample(agentParking().getLeavingBecauseOfUnsuccessfulParking());
		arrivedOnCar.addSample(agentModel().getArrivedOnCar());
		super.replicationFinished();
		if (typeOfSimulation == TypeOfSimulation.MAX_SPEED){
			refreshGui();
		}
	}

	@Override
	public void simulationFinished()
	{
		// Dysplay simulation results
		super.simulationFinished();
		if (typeOfSimulation == TypeOfSimulation.MAX_WITH_CHART){
			refreshGui();
		}
	}

	private void refreshGui(){
		for (ISimDelegate delegate : delegates) {
			delegate.refresh(this);
		}
	}

	public String getCurrentTime() {
		return convertTimeOfSystem(currentTime());
	}

	public String convertTimeOfSystem(double pTime){
		int seconds = (int)pTime % 60;
		int minutes = ((int)pTime / 60) % 60;
		if (minutes == 59 && seconds == 59){
			minutes = minutes;
		}
		int hours = (9 + (int)pTime / 60 / 60) % 24;
		String time = "";
		if (hours < 10){
			time += "0"+ hours + ":";
		}else {
			time += hours + ":";
		}
		if (minutes < 10){
			time += "0"+ minutes + ":";
		}else {
			time += minutes + ":";
		}
		if (seconds < 10){
			time += "0"+ seconds;
		}else {
			time += seconds;
		}
		return time;
	}

	public String convertCurrentPosition(CurrentPosition currentPosition){
		switch (currentPosition){
			case PAYING:{
				return "Platba";
			}
			case ARRIVED:{
				return "Prichod";
			}
			case MAKE_UP:{
				return "Licenie";
			}
			case ORDERING:{
				return "Zadavanie objednavky";
			}
			case HAIR_STYLING:{
				return "Uprava ucesu";
			}
			case CLEANING_SKIN:{
				return "Cistenie pleti";
			}
			case IN_QUEUE_FOR_PAY:{
				return "Rad platba";
			}
			case IN_QUEUE_FOR_MAKEUP:{
				return "Rad licenie";
			}
			case IN_QUEUE_FOR_ORDERING:{
				return "Rad pre objednavku";
			}
			case IN_QUEUE_FOR_HAIRSTYLE:{
				return "Rad uprava ucesu";
			}
			case PARKING:{
				return "Parkovanie";
			}
			case LEAVING:{
				return "Odchod autom";
			}
			default:{
				return "Nezname";
			}
		}
	}

	public String convertCurrentParkingPosition(CurrentParkingPosition currentParkingPosition){
		switch (currentParkingPosition){
			case DETOUR:{
				return "Obchadzka";
			}
			case ARRIVAL_ROAD:{
				return "Prijazdova cesta";
			}
			case LINE_A:{
				return "Rad A";
			}
			case LINE_B:{
				return "Rad B";
			}
			case LINE_C:{
				return "Rad C";
			}
			case LEAVING:{
				return "Odchadza";
			}
			case WALKING_TO_CAR:{
				return "Kraca k autu";
			}
			case WALKING_TO_ENTRANCE:{
				return "Kraca ku vchodu";
			}
			default:{
				return "Nezname";
			}
		}
	}

	public TypeOfSimulation getTypeOfSimulation() {
		return typeOfSimulation;
	}

	public void setTypeOfSimulation(TypeOfSimulation typeOfSimulation) {
		this.typeOfSimulation = typeOfSimulation;
	}

	public void setDeltaT(int deltaT) {
		this.deltaT = deltaT;
	}

	public void setSleepLength(int sleepLength) {
		this.sleepLength = sleepLength;
	}

	public void setNumberOfReceptionists(int numberOfReceptionists) {
		this.numberOfReceptionists = numberOfReceptionists;
	}

	public void setNumberOfMakeupArtists(int numberOfMakeupArtists) {
		this.numberOfMakeupArtists = numberOfMakeupArtists;
	}

	public void setNumberOfHairstylists(int numberOfHairstylists) {
		this.numberOfHairstylists = numberOfHairstylists;
	}

	public int getNumberOfHairstylists() {
		return numberOfHairstylists;
	}

	public Stat getTimeInSystem() {
		return timeInSystem;
	}

	public Stat getLengthOfQueueReception() {
		return lengthOfQueueReception;
	}

	public Stat getWaitTimeForPlacingOrder() {
		return waitTimeForPlacingOrder;
	}

	public Stat getTimeInSystemUntil17() {
		return timeInSystemUntil17;
	}

	public Stat getLengthOfQueueReceptionUntil17() {
		return lengthOfQueueReceptionUntil17;
	}

	public void setChosenStrategy(ParkingStrategy chosenStrategy) {
		this.chosenStrategy = chosenStrategy;
	}

	public void setNumberOfBuiltParkingLines(int numberOfBuiltParkingLines) {
		this.numberOfBuiltParkingLines = numberOfBuiltParkingLines;
	}

	public Stat getCustomersSuccesRates() {
		return customersSuccesRates;
	}

	public Stat getParkingSuccessRatePercentage() {
		return parkingSuccessRatePercentage;
	}

	public Stat getServedCustomers() {
		return servedCustomers;
	}

	public Stat getLeftAfterClosing() {
		return leftAfterClosing;
	}

	public Stat getUnsuccessfulParking() {
		return unsuccessfulParking;
	}

	public Stat getArrivedOnCar() {
		return arrivedOnCar;
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		setAgentModel(new AgentModel(Id.agentModel, this, null));
		setAgentEnviroment(new AgentEnviroment(Id.agentEnviroment, this, agentModel()));
		setAgentBeautySalon(new AgentBeautySalon(Id.agentBeautySalon, this, agentModel()));
		setAgentParking(new AgentParking(Id.agentParking, this, agentModel()));
		setAgentReceptionist(new AgentReceptionist(Id.agentReceptionist, this, agentBeautySalon()));
		setAgentHairstylist(new AgentHairstylist(Id.agentHairstylist, this, agentBeautySalon()));
		setAgentMakeUpArtist(new AgentMakeUpArtist(Id.agentMakeUpArtist, this, agentBeautySalon()));
	}

	private AgentModel _agentModel;

public AgentModel agentModel()
	{ return _agentModel; }

	public void setAgentModel(AgentModel agentModel)
	{_agentModel = agentModel; }

	private AgentEnviroment _agentEnviroment;

public AgentEnviroment agentEnviroment()
	{ return _agentEnviroment; }

	public void setAgentEnviroment(AgentEnviroment agentEnviroment)
	{_agentEnviroment = agentEnviroment; }

	private AgentBeautySalon _agentBeautySalon;

public AgentBeautySalon agentBeautySalon()
	{ return _agentBeautySalon; }

	public void setAgentBeautySalon(AgentBeautySalon agentBeautySalon)
	{_agentBeautySalon = agentBeautySalon; }

	private AgentParking _agentParking;

public AgentParking agentParking()
	{ return _agentParking; }

	public void setAgentParking(AgentParking agentParking)
	{_agentParking = agentParking; }

	private AgentReceptionist _agentReceptionist;

public AgentReceptionist agentReceptionist()
	{ return _agentReceptionist; }

	public void setAgentReceptionist(AgentReceptionist agentReceptionist)
	{_agentReceptionist = agentReceptionist; }

	private AgentHairstylist _agentHairstylist;

public AgentHairstylist agentHairstylist()
	{ return _agentHairstylist; }

	public void setAgentHairstylist(AgentHairstylist agentHairstylist)
	{_agentHairstylist = agentHairstylist; }

	private AgentMakeUpArtist _agentMakeUpArtist;

public AgentMakeUpArtist agentMakeUpArtist()
	{ return _agentMakeUpArtist; }

	public void setAgentMakeUpArtist(AgentMakeUpArtist agentMakeUpArtist)
	{_agentMakeUpArtist = agentMakeUpArtist; }
	//meta! tag="end"
}
