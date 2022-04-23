package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;

//meta! id="34"
public class ManagerReceptionist extends Manager
{
	public ManagerReceptionist(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentBeautySalon", id="71", type="Request"
	public void processPayment(MessageForm message)
	{
	}

	//meta! sender="AgentBeautySalon", id="70", type="Request"
	public void processWriteOrder(MessageForm message)
	{
	}

	//meta! sender="ProcessWritingOrder", id="56", type="Finish"
	public void processFinishProcessWritingOrder(MessageForm message)
	{
	}

	//meta! sender="ProcessPayment", id="58", type="Finish"
	public void processFinishProcessPayment(MessageForm message)
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

		case Mc.finish:
			switch (message.sender().id())
			{
			case Id.processWritingOrder:
				processFinishProcessWritingOrder(message);
			break;

			case Id.processPayment:
				processFinishProcessPayment(message);
			break;
			}
		break;

		case Mc.payment:
			processPayment(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentReceptionist myAgent()
	{
		return (AgentReceptionist)super.myAgent();
	}

}
