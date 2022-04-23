package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;

//meta! id="1"
public class ManagerModel extends Manager
{
	public ManagerModel(int id, Simulation mySim, Agent myAgent)
	{
		super(id, mySim, myAgent);
		init();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication

		if (petriNet() != null)
		{
			petriNet().clear();
		}
	}

	//meta! sender="AgentEnviroment", id="65", type="Notice"
	public void processCustomerArrived(MessageForm message)
	{
	}

	//meta! sender="AgentBeautySalon", id="69", type="Response"
	public void processServeCustomer(MessageForm message)
	{
	}

	//meta! sender="AgentParking", id="66", type="Response"
	public void processParking(MessageForm message)
	{
	}

	//meta! sender="AgentParking", id="68", type="Response"
	public void processLeavingCarPark(MessageForm message)
	{
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
		}
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	public void init()
	{
	}

	@Override
	public void processMessage(MessageForm message)
	{
		switch (message.code())
		{
		case Mc.leavingCarPark:
			processLeavingCarPark(message);
		break;

		case Mc.serveCustomer:
			processServeCustomer(message);
		break;

		case Mc.parking:
			processParking(message);
		break;

		case Mc.customerArrived:
			processCustomerArrived(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentModel myAgent()
	{
		return (AgentModel)super.myAgent();
	}

}
