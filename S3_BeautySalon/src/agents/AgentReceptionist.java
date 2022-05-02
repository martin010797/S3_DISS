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

	//statistiky
	private WStat lengthOfReceptionQueue;
	//TODO s tymto zatial nic neriesime. Asi sa ani nepouzije. Bude sa posielat sprava o zatvaracke kde sa potom zoberu stats
	private WStat lengthOfReceptionQueueUntil17;
	private Stat waitTimeForPlacingOrder;

	private Random seedGenerator;
	//generatory
	private UniformContinuousRNG lengthOfOrderingGenerator;
	private UniformContinuousRNG lengthOfPaymentGenerator;
	//pre rozhodovanie ktore sluzby chce
	private Random hairstyleMakeupServicesGenerator;
	private Random cleaningChoiceGenerator;

	private List<Receptionist> listOfReceptionists;
	private int numberOfReceptionists;
	private boolean isSomeReceptionistFree;
	//pokial by nebolo potrebne priorineho frontu ale iba frontu tak sa moze pouzit SimQueue<> a nebolo by potrebne
	// rucne zbieranie statistik do wstat
	private PriorityQueue<Customer> receptionWaitingQueue;

	private int numberOfStartedOrders;

	public AgentReceptionist(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		listOfReceptionists = new ArrayList<>();
		addOwnMessage(Mc.orderingProcessFinished);
		addOwnMessage(Mc.paymentProcessFinished);
		addOwnMessage(Mc.numberOfCustomersInQueues);
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
		if (numberOfReceptionists > 0){
			isSomeReceptionistFree = true;
		}else {
			isSomeReceptionistFree = false;
		}
		numberOfStartedOrders = 0;
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

	public Random getCleaningChoiceGenerator() {
		return cleaningChoiceGenerator;
	}

	private void prepareGenerators(){
		lengthOfOrderingGenerator = new UniformContinuousRNG(-120.0,120.0,new Random(seedGenerator.nextInt()));
		lengthOfPaymentGenerator = new UniformContinuousRNG(-50.0,50.0, new Random(seedGenerator.nextInt()));
		hairstyleMakeupServicesGenerator = new Random(seedGenerator.nextInt());
		cleaningChoiceGenerator = new Random(seedGenerator.nextInt());
	}

	public List<Receptionist> getListOfReceptionists() {
		return listOfReceptionists;
	}

	public boolean isSomeReceptionistFree() {
		return isSomeReceptionistFree;
	}

	public void setSomeReceptionistFree(boolean someReceptionistFree) {
		isSomeReceptionistFree = someReceptionistFree;
	}

	public int getNumberOfStartedOrders() {
		return numberOfStartedOrders;
	}

	public void addToNumberOfStartedOrders() {
		this.numberOfStartedOrders++;
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
