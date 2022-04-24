package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;

//meta! id="1"
public class AgentModel extends Agent
{
	public AgentModel(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		addOwnMessage(Mc.init);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		MyMessage message = new MyMessage(mySim());
		message.setCode(Mc.init);
		message.setAddressee(this);
		manager().notice(message);
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerModel(Id.managerModel, mySim(), this);
		addOwnMessage(Mc.customerArrived);
		addOwnMessage(Mc.serveCustomer);
		addOwnMessage(Mc.parking);
		addOwnMessage(Mc.leavingCarPark);
	}
	//meta! tag="end"
}
