package agents;

import OSPABA.*;
import OSPDataStruct.SimQueue;
import OSPRNG.EmpiricPair;
import OSPRNG.EmpiricRNG;
import OSPRNG.UniformDiscreteRNG;
import OSPStat.WStat;
import simulation.*;
import managers.*;
import continualAssistants.*;
import simulation.Participants.Customer;
import simulation.Participants.Hairstylist;
import simulation.Participants.Receptionist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//meta! id="35"
public class AgentHairstylist extends Agent
{
	private Random seedGenerator;
	private Random hairstyleTypeGenerator;
	private UniformDiscreteRNG simpleHairstyleGenerator;
	private EmpiricRNG complicatedHairstyleGenerator;
	private EmpiricRNG weddingHairstyleGenerator;

	private List<Hairstylist> listOfHairStylists;
	private int numberOfHairstylists;
	private boolean isSomeHairstylistFree;

	private SimQueue<Customer> hairstyleWaitingQueue;

	public AgentHairstylist(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		listOfHairStylists = new ArrayList<>();

		addOwnMessage(Mc.numberOfCustomersInQueues);
		addOwnMessage(Mc.numberOfCustomersInQueuesInit);
		addOwnMessage(Mc.hairstyleProcessFinished);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		hairstyleWaitingQueue = new SimQueue<>(new WStat(mySim()));
		addHairstylists();
		if (numberOfHairstylists > 0){
			isSomeHairstylistFree = true;
		}else {
			isSomeHairstylistFree = false;
		}
	}

	private void addHairstylists(){
		listOfHairStylists.clear();
		for (int i = 0; i < numberOfHairstylists; i++){
			Hairstylist hairstylist = new Hairstylist();
			listOfHairStylists.add(hairstylist);
		}
	}

	public Random getSeedGenerator() {
		return seedGenerator;
	}

	public void setSeedGenerator(Random seedGenerator) {
		this.seedGenerator = seedGenerator;
		prepareGenerators();
	}

	private void prepareGenerators(){
		hairstyleTypeGenerator = new Random(seedGenerator.nextInt());
		simpleHairstyleGenerator = new UniformDiscreteRNG(10,30, new Random(seedGenerator.nextInt()));
		complicatedHairstyleGenerator = new EmpiricRNG(
				new Random(seedGenerator.nextInt()),
				new EmpiricPair(
						new UniformDiscreteRNG(30,60,new Random(seedGenerator.nextInt()))
						,0.4),
				new EmpiricPair(new UniformDiscreteRNG(61,120,new Random(seedGenerator.nextInt()))
						,0.6));
		weddingHairstyleGenerator = new EmpiricRNG(
				new Random(seedGenerator.nextInt()),
				new EmpiricPair(
						new UniformDiscreteRNG(50,60,new Random(seedGenerator.nextInt()))
						,0.2),
				new EmpiricPair(new UniformDiscreteRNG(61,100,new Random(seedGenerator.nextInt()))
						,0.3),
				new EmpiricPair(new UniformDiscreteRNG(101,150,new Random(seedGenerator.nextInt()))
						,0.5));
	}

	public Random getHairstyleTypeGenerator() {
		return hairstyleTypeGenerator;
	}

	public UniformDiscreteRNG getSimpleHairstyleGenerator() {
		return simpleHairstyleGenerator;
	}

	public EmpiricRNG getComplicatedHairstyleGenerator() {
		return complicatedHairstyleGenerator;
	}

	public EmpiricRNG getWeddingHairstyleGenerator() {
		return weddingHairstyleGenerator;
	}

	public int getNumberOfHairstylists() {
		return numberOfHairstylists;
	}

	public void setNumberOfHairstylists(int numberOfHairstylists) {
		this.numberOfHairstylists = numberOfHairstylists;
	}

	public boolean isSomeHairstylistFree() {
		return isSomeHairstylistFree;
	}

	public void setSomeHairstylistFree(boolean someHairstylistFree) {
		isSomeHairstylistFree = someHairstylistFree;
	}

	public List<Hairstylist> getListOfHairStylists() {
		return listOfHairStylists;
	}

	public SimQueue<Customer> getHairstyleWaitingQueue() {
		return hairstyleWaitingQueue;
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerHairstylist(Id.managerHairstylist, mySim(), this);
		new ProcessHairstyle(Id.processHairstyle, mySim(), this);
		addOwnMessage(Mc.hairstyling);
	}
	//meta! tag="end"
}
