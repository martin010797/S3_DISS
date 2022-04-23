package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;

//meta! id="2"
public class ManagerEnviroment extends Manager
{
	public ManagerEnviroment(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="SchedulerOfArrivalsOnFoot", id="22", type="Finish"
	public void processFinishSchedulerOfArrivalsOnFoot(MessageForm message)
	{
	}

	//meta! sender="SchedulerOfArrivalsOnCar", id="24", type="Finish"
	public void processFinishSchedulerOfArrivalsOnCar(MessageForm message)
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
		case Mc.finish:
			switch (message.sender().id())
			{
			case Id.schedulerOfArrivalsOnFoot:
				processFinishSchedulerOfArrivalsOnFoot(message);
			break;

			case Id.schedulerOfArrivalsOnCar:
				processFinishSchedulerOfArrivalsOnCar(message);
			break;
			}
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentEnviroment myAgent()
	{
		return (AgentEnviroment)super.myAgent();
	}

}
