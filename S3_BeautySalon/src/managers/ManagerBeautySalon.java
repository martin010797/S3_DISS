package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;

//meta! id="26"
public class ManagerBeautySalon extends Manager
{
	public ManagerBeautySalon(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentReceptionist", id="71", type="Response"
	public void processPayment(MessageForm message)
	{
	}

	//meta! sender="AgentModel", id="69", type="Request"
	public void processServeCustomer(MessageForm message)
	{
		myAgent().addArrivedCustomerToStats();
		message.setCode(Mc.writeOrder);
		message.setAddressee(mySim().findAgent(Id.agentReceptionist));
		request(message);
	}

	//meta! sender="AgentHairstylist", id="73", type="Response"
	public void processHairstyling(MessageForm message)
	{
	}

	//meta! sender="AgentMakeUpArtist", id="79", type="Response"
	public void processMakeUp(MessageForm message)
	{
	}

	//meta! sender="AgentReceptionist", id="70", type="Response"
	public void processWriteOrder(MessageForm message)
	{
	}

	//meta! sender="AgentMakeUpArtist", id="75", type="Response"
	public void processSkinCleaning(MessageForm message)
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
		case Mc.writeOrder:
			processWriteOrder(message);
		break;

		case Mc.skinCleaning:
			processSkinCleaning(message);
		break;

		case Mc.serveCustomer:
			processServeCustomer(message);
		break;

		case Mc.payment:
			processPayment(message);
		break;

		case Mc.hairstyling:
			processHairstyling(message);
		break;

		case Mc.makeUp:
			processMakeUp(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentBeautySalon myAgent()
	{
		return (AgentBeautySalon)super.myAgent();
	}

}
