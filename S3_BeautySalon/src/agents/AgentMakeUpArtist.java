package agents;

import OSPABA.*;
import OSPDataStruct.SimQueue;
import OSPRNG.TriangularRNG;
import OSPRNG.UniformDiscreteRNG;
import OSPStat.WStat;
import simulation.*;
import managers.*;
import continualAssistants.*;
import simulation.Participants.Customer;
import simulation.Participants.Hairstylist;
import simulation.Participants.MakeUpArtist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//meta! id="36"
public class AgentMakeUpArtist extends Agent
{
	private Random seedGenerator;
	private Random cleaningChoiceGenerator;
	private TriangularRNG cleaningTriangularGenerator;
	private Random makeupTypeGenerator;
	private UniformDiscreteRNG simpleMakeupGenerator;
	private UniformDiscreteRNG complicatedMakeupGenerator;

	private List<MakeUpArtist> listOfMakeupArtists;
	private int numberOfMakeupArtists;
	private boolean isSomeMakeupArtistsFree;

	private SimQueue<Customer> makeupWaitingQueue;

	public AgentMakeUpArtist(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		listOfMakeupArtists = new ArrayList<>();

		addOwnMessage(Mc.numberOfCustomersInQueues);
		addOwnMessage(Mc.numberOfCustomersInQueuesInit);
		addOwnMessage(Mc.makeUpProcessFinished);
		addOwnMessage(Mc.cleaningProcessFinished);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		makeupWaitingQueue = new SimQueue<>(new WStat(mySim()));
		addMakeupArtists();
		if (numberOfMakeupArtists > 0){
			isSomeMakeupArtistsFree = true;
		}else {
			isSomeMakeupArtistsFree = false;
		}
	}

	private void addMakeupArtists(){
		listOfMakeupArtists.clear();
		for (int i = 0; i < numberOfMakeupArtists; i++){
			MakeUpArtist makeUpArtist = new MakeUpArtist();
			listOfMakeupArtists.add(makeUpArtist);
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
		cleaningChoiceGenerator = new Random(seedGenerator.nextInt());
		cleaningTriangularGenerator = new TriangularRNG(
				360.0,
				540.0,
				900.0,
				new Random(seedGenerator.nextInt()));
		makeupTypeGenerator = new Random(seedGenerator.nextInt());
		simpleMakeupGenerator = new UniformDiscreteRNG(10,25,new Random(seedGenerator.nextInt()));
		complicatedMakeupGenerator = new UniformDiscreteRNG(20,100, new Random(seedGenerator.nextInt()));
	}

	public Random getCleaningChoiceGenerator() {
		return cleaningChoiceGenerator;
	}

	public TriangularRNG getCleaningTriangularGenerator() {
		return cleaningTriangularGenerator;
	}

	public Random getMakeupTypeGenerator() {
		return makeupTypeGenerator;
	}

	public UniformDiscreteRNG getSimpleMakeupGenerator() {
		return simpleMakeupGenerator;
	}

	public UniformDiscreteRNG getComplicatedMakeupGenerator() {
		return complicatedMakeupGenerator;
	}

	public int getNumberOfMakeupArtists() {
		return numberOfMakeupArtists;
	}

	public void setNumberOfMakeupArtists(int numberOfMakeupArtists) {
		this.numberOfMakeupArtists = numberOfMakeupArtists;
	}

	public boolean isSomeMakeupArtistsFree() {
		return isSomeMakeupArtistsFree;
	}

	public void setSomeMakeupArtistsFree(boolean someMakeupArtistsFree) {
		isSomeMakeupArtistsFree = someMakeupArtistsFree;
	}

	public List<MakeUpArtist> getListOfMakeupArtists() {
		return listOfMakeupArtists;
	}

	public SimQueue<Customer> getMakeupWaitingQueue() {
		return makeupWaitingQueue;
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerMakeUpArtist(Id.managerMakeUpArtist, mySim(), this);
		new ProcessMakeUp(Id.processMakeUp, mySim(), this);
		new ProcessSkinCleaning(Id.processSkinCleaning, mySim(), this);
		addOwnMessage(Mc.makeUp);
		addOwnMessage(Mc.skinCleaning);
	}
	//meta! tag="end"
}
