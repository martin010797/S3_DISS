package agents;

import OSPABA.*;
import OSPDataStruct.SimQueue;
import OSPRNG.ExponentialRNG;
import OSPRNG.UniformContinuousRNG;
import OSPStat.Stat;
import OSPStat.WStat;
import simulation.*;
import managers.*;
import continualAssistants.*;
import simulation.Participants.Customer;
import simulation.Participants.Hairstylist;
import simulation.Participants.MakeUpArtist;
import simulation.Participants.Receptionist;

import java.util.*;

//meta! id="34"
public class AgentReceptionist extends Agent
{
	//TODO pridat generatory(nejak premysliet aj seed) asi by sa mohol do kazdeho agenta vlozit Random() co by bol
	//seed generator. Nastavil by sa v mySimulation po inite pre vsetkych agentov(alebo tych ktory budu nieco generovat)
	//pokial by nebolo potrebne priorineho frontu ale iba frontu tak sa moze pouzit SimQueue<> a nebolo by potrebne
	// rucne zbieranie statistik do wstat
	private PriorityQueue<Customer> receptionWaitingQueue;

	private WStat lengthOfReceptionQueue;
	private WStat lengthOfReceptionQueueUntil17;
	private Stat waitTimeForPlacingOrder;

	private List<Receptionist> listOfReceptionists;
	private int numberOfReceptionists;

	//TODO
	private Random seedGenerator;

	//generatory
	private UniformContinuousRNG lengthOfOrderingGenerator;
	private UniformContinuousRNG lengthOfPaymentGenerator;
	//pre rozhodovanie ktore sluzby chce
	private Random hairstyleMakeupServicesGenerator;

	public AgentReceptionist(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		listOfReceptionists = new ArrayList<>();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		addReceptionists();
		receptionWaitingQueue = new PriorityQueue<>();
		lengthOfReceptionQueue = new WStat(mySim());
		lengthOfReceptionQueueUntil17 = new WStat(mySim());
		waitTimeForPlacingOrder = new Stat();
	}

	private void addReceptionists(){
		listOfReceptionists.clear();
		for (int i = 0; i < numberOfReceptionists; i++){
			Receptionist receptionist = new Receptionist();
			listOfReceptionists.add(receptionist);
		}
	}

	public WStat getLengthOfReceptionQueue() {
		return lengthOfReceptionQueue;
	}

	public WStat getLengthOfReceptionQueueUntil17() {
		return lengthOfReceptionQueueUntil17;
	}

	public Stat getWaitTimeForPlacingOrder() {
		return waitTimeForPlacingOrder;
	}

	public PriorityQueue<Customer> getReceptionWaitingQueue() {
		return receptionWaitingQueue;
	}

	public int getNumberOfReceptionists() {
		return numberOfReceptionists;
	}

	public void setNumberOfReceptionists(int numberOfReceptionists) {
		this.numberOfReceptionists = numberOfReceptionists;
	}

	public void setSeedGenerator(Random seedGenerator) {
		this.seedGenerator = seedGenerator;
		prepareGenerators();
	}

	public Random getSeedGenerator() {
		return seedGenerator;
	}

	public UniformContinuousRNG getLengthOfOrderingGenerator() {
		return lengthOfOrderingGenerator;
	}

	public UniformContinuousRNG getLengthOfPaymentGenerator() {
		return lengthOfPaymentGenerator;
	}

	public Random getHairstyleMakeupServicesGenerator() {
		return hairstyleMakeupServicesGenerator;
	}

	private void prepareGenerators(){
		lengthOfOrderingGenerator = new UniformContinuousRNG(-120.0,120.0,new Random(seedGenerator.nextInt()));
		lengthOfPaymentGenerator = new UniformContinuousRNG(-50.0,50.0, new Random(seedGenerator.nextInt()));
		hairstyleMakeupServicesGenerator = new Random(seedGenerator.nextInt());
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerReceptionist(Id.managerReceptionist, mySim(), this);
		new ProcessPayment(Id.processPayment, mySim(), this);
		new ProcessWritingOrder(Id.processWritingOrder, mySim(), this);
		addOwnMessage(Mc.payment);
		addOwnMessage(Mc.writeOrder);
	}
	//meta! tag="end"
}
