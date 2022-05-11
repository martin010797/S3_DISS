package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.CurrentParkingPosition;
import simulation.Participants.Customer;

//meta! id="49"
public class ProcessLeaving extends Process
{
	private final double TO_CROSSROAD_A = 13;
	private final double TO_CROSSROAD_B = 10;
	private final double TO_CROSSROAD_C = 8;
	private final double SPEED_OF_CAR = 20;
	private final double SPEED_OF_CARS_ON_CARPARK = 12;

	public ProcessLeaving(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentParking", id="50", type="Start"
	public void processStart(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		customer.setCurrentParkingPosition(CurrentParkingPosition.LEAVING);
		double lengthOfDriving = 0;
		switch (customer.getCurrentParkingPosition()){
			case LINE_A:{
				lengthOfDriving += (TO_CROSSROAD_A)/(((SPEED_OF_CAR*1000)/60)/60);
				break;
			}
			case LINE_B:{
				lengthOfDriving += (TO_CROSSROAD_B)/(((SPEED_OF_CAR*1000)/60)/60);
				break;
			}
			case LINE_C:{
				lengthOfDriving += (TO_CROSSROAD_C)/(((SPEED_OF_CAR*1000)/60)/60);
				break;
			}
		}
		if (customer.getFinalParkingNumber() == -1){
			//odchadza lebo nezaparkoval
			message.setCode(Mc.leavingProcessFinished);
			hold(lengthOfDriving,message);
		}else {
			//odchadza po obsluzeni
			//vypocet ako daleko to ma od svojho miesta ku krizovatke na konci radu
			lengthOfDriving += (14-customer.getFinalParkingNumber())/(((SPEED_OF_CARS_ON_CARPARK*1000)/60)/60);
			message.setCode(Mc.leavingProcessFinished);
			//uvolnenie miesta
			switch (customer.getFinalParkingLine()){
				case "A":{
					myAgent().getArrayOfParkingSpots_A()[customer.getFinalParkingNumber()] = false;
					break;
				}
				case "B":{
					myAgent().getArrayOfParkingSpots_B()[customer.getFinalParkingNumber()] = false;
					break;
				}
				case "C":{
					myAgent().getArrayOfParkingSpots_C()[customer.getFinalParkingNumber()] = false;
					break;
				}
			}
			hold(lengthOfDriving,message);
		}
	}

	public void processLeavingProcessFinished(MessageForm message){
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.leavingProcessFinished:{
				processLeavingProcessFinished(message);
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
