package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;

//meta! id="55"
public class ProcessWritingOrder extends Process
{
	public ProcessWritingOrder(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentReceptionist", id="56", type="Start"
	public void processStart(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		customer.setCurrentPosition(CurrentPosition.ORDERING);
		customer.setServiceStartTime(mySim().currentTime());
		//statistiky
		myAgent().getWaitTimeForPlacingOrder().addSample(mySim().currentTime() - customer.getArriveTime());
		myAgent().addToNumberOfStartedOrders();

		//vyber objednavky
		double generatedValue = myAgent().getHairstyleMakeupServicesGenerator().nextDouble();
		if (generatedValue < 0.2){
			//iba uces
			customer.setHairstyle(true);
			customer.setCleaning(false);
			customer.setMakeup(false);
		}else{
			//sem idu vsetci co chcu makeup
			if (generatedValue < 0.35){
				customer.setHairstyle(false);
				customer.setMakeup(true);
			}else {
				//tu chcu aj aj
				customer.setHairstyle(true);
				customer.setMakeup(true);
			}
			double cleaningGeneratedValue = myAgent().getCleaningChoiceGenerator().nextDouble();
			//ak chcu aj cistenie pleti
			if (cleaningGeneratedValue < 0.35){
				customer.setCleaning(true);
			}else {
				customer.setCleaning(false);
			}
		}

		//pozdrzanie
		double lengthOfOrdering = 200 + myAgent().getLengthOfOrderingGenerator().sample();
		message.setCode(Mc.orderingProcessFinished);
		hold(lengthOfOrdering,message);
	}

	public void processOrderingProcessFinished(MessageForm message){
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.orderingProcessFinished:{
				processOrderingProcessFinished(message);
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
