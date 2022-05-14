package continualAssistants;

import OSPABA.*;
import OSPRNG.ExponentialRNG;
import simulation.*;
import agents.*;
import simulation.Participants.Customer;

import java.util.Random;

//meta! id="23"
public class SchedulerOfArrivalsOnCar extends Scheduler
{
	//private ExponentialRNG arrivalsGenerator;

	public SchedulerOfArrivalsOnCar(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
		//arrivalsGenerator = new ExponentialRNG((double) (3600/8), new Random(myAgent().getSeedGenerator().nextInt()));
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentEnviroment", id="24", type="Start"
	public void processStart(MessageForm message)
	{
		message.setCode(Mc.createNewCustomerOnCar);
		//hold(arrivalsGenerator.sample(),message);
		hold(myAgent().getCarArrivalsGenerator().sample(),message);
	}

	public void processCreateNewCustomerOnCar(MessageForm message){
		double lengthOfHold = myAgent().getCarArrivalsGenerator().sample();
		//aby planovalo dalsie prichody len do 17:00
		if ((lengthOfHold + mySim().currentTime()) <= 28800){
			//MessageForm copyMessage = message.createCopy();
			//hold(lengthOfHold,copyMessage);
			MessageForm copyMessage = message.createCopy();
			hold(myAgent().getCarArrivalsGenerator().sample(),copyMessage);
		}
		Customer customer = new Customer(_mySim.currentTime());
		customer.setArrivedOnCar(true);
		((MyMessage) message).setCustomer(customer);
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.createNewCustomerOnCar:{
				processCreateNewCustomerOnCar(message);
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
