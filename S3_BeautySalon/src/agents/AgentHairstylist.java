package agents;

import OSPABA.*;
import OSPRNG.EmpiricPair;
import OSPRNG.EmpiricRNG;
import OSPRNG.UniformDiscreteRNG;
import simulation.*;
import managers.*;
import continualAssistants.*;

import java.util.Random;

//meta! id="35"
public class AgentHairstylist extends Agent
{
	private Random seedGenerator;

	private Random hairstyleTypeGenerator;
	private UniformDiscreteRNG simpleHairstyleGenerator;
	private EmpiricRNG complicatedHairstyleGenerator;
	private EmpiricRNG weddingHairstyleGenerator;

	/*private Random cleaningChoiceGenerator;
	private TriangularRNG cleaningTriangularGenerator;
	private Random makeupTypeGenerator;
	private UniformDiscreteRNG simpleMakeupGenerator;
	private UniformDiscreteRNG complicatedMakeupGenerator;*/

	public AgentHairstylist(int id, Simulation mySim, Agent parent)
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

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerHairstylist(Id.managerHairstylist, mySim(), this);
		new ProcessHairstyle(Id.processHairstyle, mySim(), this);
		addOwnMessage(Mc.hairstyling);
	}
	//meta! tag="end"
}
