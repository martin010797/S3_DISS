package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;

//meta! id="57"
public class ProcessPayment extends Process
{
	public ProcessPayment(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentReceptionist", id="58", type="Start"
	public void processStart(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		customer.setCurrentPosition(CurrentPosition.PAYING);
		customer.setServiceStartTime(mySim().currentTime());

		//pozdrzanie
		double lengthOfPayment = 180 + myAgent().getLengthOfPaymentGenerator().sample();
		message.setCode(Mc.paymentProcessFinished);
		hold(lengthOfPayment,message);
	}

	public void processPaymentProcessFinished(MessageForm message){
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.paymentProcessFinished:{
				processPaymentProcessFinished(message);
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
	public AgentReceptionist myAgent()
	{
		return (AgentReceptionist)super.myAgent();
	}

}
