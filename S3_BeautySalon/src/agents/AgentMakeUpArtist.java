package agents;

import OSPABA.*;
import OSPRNG.TriangularRNG;
import OSPRNG.UniformDiscreteRNG;
import simulation.*;
import managers.*;
import continualAssistants.*;

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

	public AgentMakeUpArtist(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
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
