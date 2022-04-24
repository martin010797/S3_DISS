package continualAssistants;

import OSPABA.*;
import OSPRNG.ExponentialRNG;
import simulation.*;
import agents.*;
import simulation.Participants.Customer;

//meta! id="21"
public class SchedulerOfArrivalsOnFoot extends Scheduler
{
	private ExponentialRNG arrivalsGenerator = new ExponentialRNG((double) (3600/5));

	public SchedulerOfArrivalsOnFoot(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentEnviroment", id="22", type="Start"
	public void processStart(MessageForm message)
	{
		message.setCode(Mc.createNewCustomerOnFoot);
		hold(arrivalsGenerator.sample(), message);
	}

	public void processCreateNewCustomerOnFoot(MessageForm message){
		MessageForm copyMessage = message.createCopy();
		hold(arrivalsGenerator.sample(),copyMessage);
		((MyMessage) message).setCustomer(new Customer(_mySim.currentTime()));
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.createNewCustomerOnFoot:{
				processCreateNewCustomerOnFoot(message);
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
	public AgentEnviroment myAgent()
	{
		return (AgentEnviroment)super.myAgent();
	}

}
