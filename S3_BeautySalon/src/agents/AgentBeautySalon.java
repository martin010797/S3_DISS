package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;

//meta! id="26"
public class AgentBeautySalon extends Agent
{
	public AgentBeautySalon(int id, Simulation mySim, Agent parent)
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
		new ManagerBeautySalon(Id.managerBeautySalon, mySim(), this);
		addOwnMessage(Mc.payment);
		addOwnMessage(Mc.serveCustomer);
		addOwnMessage(Mc.hairstyling);
		addOwnMessage(Mc.makeUp);
		addOwnMessage(Mc.writeOrder);
		addOwnMessage(Mc.skinCleaning);
	}
	//meta! tag="end"
}
