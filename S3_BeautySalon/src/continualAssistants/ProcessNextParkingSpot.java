package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.Customer;

//meta! id="131"
public class ProcessNextParkingSpot extends Process
{
	private final double WIDTH_OF_BUILDING = 35;
	private final int NUMBER_OF_PARKING_SPOTS = 15;
	private final double SPEED_OF_CAR = 12;

	public ProcessNextParkingSpot(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentParking", id="132", type="Start"
	public void processStart(MessageForm message)
	{
		double lengthOfDriveToNextParkingSpot =
				(WIDTH_OF_BUILDING / NUMBER_OF_PARKING_SPOTS)/(((SPEED_OF_CAR*1000)/60)/60);
		message.setCode(Mc.nextParkingSpotProcessFinished);
		hold(lengthOfDriveToNextParkingSpot,message);
	}

	public void processNextParkingSpotProcessFinished(MessageForm message){
		((MyMessage) message).getCustomer().increaseCurrentParkingNumber();
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.nextParkingSpotProcessFinished:{
				processNextParkingSpotProcessFinished(message);
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
