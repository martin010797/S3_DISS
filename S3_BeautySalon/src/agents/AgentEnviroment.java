package agents;

import OSPABA.*;
import OSPRNG.ExponentialRNG;
import simulation.*;
import managers.*;
import continualAssistants.*;

import java.util.Random;

//meta! id="2"
public class AgentEnviroment extends Agent
{
	private Random seedGenerator;
	private ExponentialRNG carArrivalsGenerator;
	private ExponentialRNG footArrivalsGenerator;

	public AgentEnviroment(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		addOwnMessage(Mc.init);
		addOwnMessage(Mc.createNewCustomerOnFoot);
		addOwnMessage(Mc.createNewCustomerOnCar);
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
		carArrivalsGenerator = new ExponentialRNG((double) (3600/8), new Random(seedGenerator.nextInt()));
		footArrivalsGenerator = new ExponentialRNG((double) (3600/5), new Random(seedGenerator.nextInt()));
	}

	public ExponentialRNG getCarArrivalsGenerator() {
		return carArrivalsGenerator;
	}

	public ExponentialRNG getFootArrivalsGenerator() {
		return footArrivalsGenerator;
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerEnviroment(Id.managerEnviroment, mySim(), this);
		new SchedulerOfArrivalsOnFoot(Id.schedulerOfArrivalsOnFoot, mySim(), this);
		new SchedulerOfArrivalsOnCar(Id.schedulerOfArrivalsOnCar, mySim(), this);
	}
	//meta! tag="end"
}
