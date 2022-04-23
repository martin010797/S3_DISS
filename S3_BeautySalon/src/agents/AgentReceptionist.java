package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;

//meta! id="34"
public class AgentReceptionist extends Agent
{
	public AgentReceptionist(int id, Simulation mySim, Agent parent)
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
		new ManagerReceptionist(Id.managerReceptionist, mySim(), this);
		new ProcessPayment(Id.processPayment, mySim(), this);
		new ProcessWritingOrder(Id.processWritingOrder, mySim(), this);
		addOwnMessage(Mc.payment);
		addOwnMessage(Mc.writeOrder);
	}
	//meta! tag="end"
}
