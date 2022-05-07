package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;

//meta! id="60"
public class ProcessSkinCleaning extends Process
{
	public ProcessSkinCleaning(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentMakeUpArtist", id="61", type="Start"
	public void processStart(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		customer.setCurrentPosition(CurrentPosition.CLEANING_SKIN);
		customer.setServiceStartTime(mySim().currentTime());

		double lengthOfCleaning = myAgent().getCleaningTriangularGenerator().sample();
		//pozdrzanie
		message.setCode(Mc.cleaningProcessFinished);
		hold(lengthOfCleaning,message);
	}

	public void processCleaningFinished(MessageForm message){
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.cleaningProcessFinished:{
				processCleaningFinished(message);
				break;
			}
		}
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	@Override
	public void processMessage(MessageForm message)
	{
		switch (message.code())
		{
		case Mc.start:
			processStart(message);
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
