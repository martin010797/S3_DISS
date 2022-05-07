package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;

//meta! id="53"
public class ProcessHairstyle extends Process
{
	public ProcessHairstyle(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentHairstylist", id="54", type="Start"
	public void processStart(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		customer.setCurrentPosition(CurrentPosition.HAIR_STYLING);
		customer.setServiceStartTime(mySim().currentTime());

		double durationOfHairstyle;
		double choice = myAgent().getHairstyleTypeGenerator().nextDouble();
		if (choice < 0.4){
			//jednoduchy uces
			durationOfHairstyle = myAgent().getSimpleHairstyleGenerator().sample()*60;
		}else if(choice < 0.8){
			//zlozity uces
			try {
				durationOfHairstyle = myAgent().getComplicatedHairstyleGenerator().sample().intValue()*60;
			}catch (Exception e){
				e.printStackTrace();
				durationOfHairstyle = 100000000;
			}
		}else {
			//svadobny uces
			try {
				durationOfHairstyle = myAgent().getWeddingHairstyleGenerator().sample().intValue()*60;
			}catch (Exception e){
				e.printStackTrace();
				durationOfHairstyle = 100000000;
			}
		}
		//pozdrzanie
		message.setCode(Mc.hairstyleProcessFinished);
		hold(durationOfHairstyle,message);
	}

	public void processHairstyleProcessFinished(MessageForm message){
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.hairstyleProcessFinished:{
				processHairstyleProcessFinished(message);
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
	public AgentHairstylist myAgent()
	{
		return (AgentHairstylist)super.myAgent();
	}

}
