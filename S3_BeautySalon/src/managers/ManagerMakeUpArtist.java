package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;

//meta! id="36"
public class ManagerMakeUpArtist extends Manager
{
	public ManagerMakeUpArtist(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentBeautySalon", id="79", type="Request"
	public void processMakeUp(MessageForm message)
	{
	}

	//meta! sender="ProcessMakeUp", id="63", type="Finish"
	public void processFinishProcessMakeUp(MessageForm message)
	{
	}

	//meta! sender="ProcessSkinCleaning", id="61", type="Finish"
	public void processFinishProcessSkinCleaning(MessageForm message)
	{
	}

	//meta! sender="AgentBeautySalon", id="75", type="Request"
	public void processSkinCleaning(MessageForm message)
	{
	}

	public void processNumberOfCustomersInQueues(MessageForm message){
		((MyMessage) message).setNumberOfCustomersInMakeUpQueue(myAgent().getMakeupWaitingQueue().size());
		message.setAddressee(mySim().findAgent(Id.agentBeautySalon));
		message.setSender(myAgent());
		call(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.numberOfCustomersInQueues:
			case Mc.numberOfCustomersInQueuesInit: {
				processNumberOfCustomersInQueues(message);
				break;
			}
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
		case Mc.skinCleaning:
			processSkinCleaning(message);
		break;

		case Mc.finish:
			switch (message.sender().id())
			{
			case Id.processMakeUp:
				processFinishProcessMakeUp(message);
			break;

			case Id.processSkinCleaning:
				processFinishProcessSkinCleaning(message);
			break;
			}
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
	public AgentMakeUpArtist myAgent()
	{
		return (AgentMakeUpArtist)super.myAgent();
	}

}
