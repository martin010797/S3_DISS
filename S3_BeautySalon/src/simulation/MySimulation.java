package simulation;

//import Gui.ISimDelegate;
import OSPABA.*;
import OSPStat.Stat;
import agents.*;
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

	//globalne statistiky
	private Stat timeInSystem;
	private Stat lengthOfQueueReception;
	private Stat waitTimeForPlacingOrder;
	private Stat timeInSystemUntil17;
	private Stat lengthOfQueueReceptionUntil17;

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
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Reset entities, queues, local statistics, etc...
		//TODO upravit aby nastavovalo v konkretnych agentoch!!
		//agentBeautySalon().setNumberOfReceptionists(numberOfReceptionists);
		agentReceptionist().setNumberOfReceptionists(numberOfReceptionists);
		agentBeautySalon().setNumberOfHairstylists(numberOfHairstylists);
		agentBeautySalon().setNumberOfMakeupArtists(numberOfMakeupArtists);
	}

	@Override
	public void replicationFinished()
	{
		// Collect local statistics into global, update UI, etc...
		super.replicationFinished();
		if (typeOfSimulation == TypeOfSimulation.MAX_SPEED){
			//TODO asi nie je treba
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

	//Po prerobeni na delegata z frameworku pouzit lambda funkcie
	//TODO mozem hodit sem refresh gui?
	/*@Override
	protected void updateAnimator() {
		super.updateAnimator();
		if (typeOfSimulation == TypeOfSimulation.OBSERVE){
			refreshGui();
		}
	}*/

	private void refreshGui(){
		for (ISimDelegate delegate : delegates) {
			delegate.refresh(this);
		}
	}

	/*public void registerDelegate(ISimDelegate delegate){
		delegates.add(delegate);
	}*/

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

	private String convertCurrentPosition(CurrentPosition currentPosition){
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
