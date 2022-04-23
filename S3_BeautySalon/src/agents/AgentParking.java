package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;

//meta! id="30"
public class AgentParking extends Agent
{
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
