package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;

import java.util.Random;

//meta! id="30"
public class AgentParking extends Agent
{
	private Random seedGenerator;

	//TODO parkovanie zapracovat az po rozbehani salonu
	public AgentParking(int id, Simulation mySim, Agent parent)
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
		//TODO
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerParking(Id.managerParking, mySim(), this);
		new ProcessParking(Id.processParking, mySim(), this);
		new ProcessLeaving(Id.processLeaving, mySim(), this);
		addOwnMessage(Mc.parking);
		addOwnMessage(Mc.leavingCarPark);
	}
	//meta! tag="end"
}
