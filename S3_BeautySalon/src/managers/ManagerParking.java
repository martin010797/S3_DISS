package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;

//meta! id="30"
public class ManagerParking extends Manager
{
	public ManagerParking(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentModel", id="66", type="Request"
	public void processParking(MessageForm message)
	{
	}

	//meta! sender="ProcessParking", id="45", type="Finish"
	public void processFinishProcessParking(MessageForm message)
	{
	}

	//meta! sender="ProcessLeaving", id="50", type="Finish"
	public void processFinishProcessLeaving(MessageForm message)
	{
	}

	//meta! sender="AgentModel", id="68", type="Request"
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

		case Mc.finish:
			switch (message.sender().id())
			{
			case Id.processParking:
				processFinishProcessParking(message);
			break;

			case Id.processLeaving:
				processFinishProcessLeaving(message);
			break;
			}
		break;

		case Mc.parking:
			processParking(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentParking myAgent()
	{
		return (AgentParking)super.myAgent();
	}

}
