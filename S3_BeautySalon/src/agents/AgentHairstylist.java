package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;

//meta! id="35"
public class AgentHairstylist extends Agent
{
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

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerHairstylist(Id.managerHairstylist, mySim(), this);
		new ProcessHairstyle(Id.processHairstyle, mySim(), this);
		addOwnMessage(Mc.hairstyling);
	}
	//meta! tag="end"
}
