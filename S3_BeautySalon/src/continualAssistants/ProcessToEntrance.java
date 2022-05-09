package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.CurrentParkingPosition;
import simulation.Participants.Customer;

//meta! id="133"
public class ProcessToEntrance extends Process
{
	private final double WIDTH_OF_BUILDING = 35;
	private final double TO_CROSSROAD_B = 10;
	private final double TO_CROSSROAD_C = 8;

	public ProcessToEntrance(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentParking", id="134", type="Start"
	public void processStart(MessageForm message)
	{
		//TODO este overit ten pohyb chodcov ci to je takto spravne
		Customer customer = ((MyMessage) message).getCustomer();
		customer.setCurrentParkingPosition(CurrentParkingPosition.WALKING_TO_ENTRANCE);
		double distanceToWalk = 0;
		distanceToWalk += (14 - customer.getFinalParkingNumber()) * (WIDTH_OF_BUILDING/15);
		switch (customer.getFinalParkingLine()){
			case "A":{
				break;
			}
			case "B":{
				distanceToWalk += TO_CROSSROAD_B;
				break;
			}
			case "C":{
				distanceToWalk += TO_CROSSROAD_C;
				break;
			}
		}
		double speedOfWalking = 2.5 + myAgent().getSpeedOfWalkingGenerator().sample();
		double lengthOfWalking = distanceToWalk / speedOfWalking;
		message.setCode(Mc.toEntranceProcessFinished);
		hold(lengthOfWalking,message);
	}

	public void processToEntranceProcessFinished(MessageForm message){
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.toEntranceProcessFinished:{
				processToEntranceProcessFinished(message);
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
	public AgentParking myAgent()
	{
		return (AgentParking)super.myAgent();
	}

}
