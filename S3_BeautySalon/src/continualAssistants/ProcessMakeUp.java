package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;

//meta! id="62"
public class ProcessMakeUp extends Process
{
	public ProcessMakeUp(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentMakeUpArtist", id="63", type="Start"
	public void processStart(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		customer.setCurrentPosition(CurrentPosition.MAKE_UP);
		customer.setServiceStartTime(mySim().currentTime());

		double choice = myAgent().getMakeupTypeGenerator().nextDouble();
		double lengthOfMakeup;
		if (choice < 0.3){
			//jednoduche licenie
			lengthOfMakeup = myAgent().getSimpleMakeupGenerator().sample()*60;
		}else {
			//zlozite
			lengthOfMakeup = myAgent().getComplicatedMakeupGenerator().sample()*60;
		}
		//pozdrzanie
		message.setCode(Mc.makeUpProcessFinished);
		hold(lengthOfMakeup,message);
	}

	public void processMekeUpProcessFinished(MessageForm message){
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.makeUpProcessFinished:{
				processMekeUpProcessFinished(message);
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
