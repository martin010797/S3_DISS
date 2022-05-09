package continualAssistants;

import OSPABA.*;
import simulation.*;
import agents.*;
import OSPABA.Process;
import simulation.Participants.CurrentParkingPosition;
import simulation.Participants.Customer;

//meta! id="129"
public class ProcessToCrossroad extends Process
{
	private final double WIDTH_OF_BUILDING = 35;
	private final double TO_CROSSROAD_A = 13;
	private final double TO_CROSSROAD_B = 10;
	private final double TO_CROSSROAD_C = 8;
	private final double SPEED_OF_CAR = 20;

	public ProcessToCrossroad(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentParking", id="130", type="Start"
	public void processStart(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		if (customer.getCurrentParkingPosition() == CurrentParkingPosition.ARRIVAL_ROAD){
			//prichod k prvej krizovatke od prichodu auta
			double lengthOfDrive = (WIDTH_OF_BUILDING + TO_CROSSROAD_A)/(((SPEED_OF_CAR*1000)/60)/60);
			message.setCode(Mc.toCrossroadProcessFinished);
			hold(lengthOfDrive,message);
		}else if(customer.getCurrentParkingPosition() == CurrentParkingPosition.LINE_A){
			if (customer.getCurrentParkingNumber() == 14){
				//ak je na konci radu a chce robit obchadzku
				customer.setCurrentParkingPosition(CurrentParkingPosition.DETOUR);
				int successRate = customer.getCurrentCustomerSuccessRateValue();
				successRate += 50;
				customer.setCurrentCustomerSuccessRateValue(successRate);
				customer.setCurrentParkingNumber(-1);
				double lengthOfDrive =
						(TO_CROSSROAD_A + WIDTH_OF_BUILDING + TO_CROSSROAD_A)/(((SPEED_OF_CAR*1000)/60)/60);
				message.setCode(Mc.toCrossroadProcessFinished);
				hold(lengthOfDrive,message);
			}else {
				//je na krizovatke pri rade A tak prechadza k B.
				// Tato kontrola asi nie je ani potrebna lebo sa kontroluje inde
				if (myAgent().getNumberOfBuiltParkingLines() > 1){
					//prechod od krizovatky radu A ku krizovatke radu B
					double lengthOfDrive = (TO_CROSSROAD_B)/(((SPEED_OF_CAR*1000)/60)/60);
					message.setCode(Mc.toCrossroadProcessFinished);
					hold(lengthOfDrive,message);
				}
			}
		}else if (customer.getCurrentParkingPosition() == CurrentParkingPosition.LINE_B){
			if (customer.getCurrentParkingNumber() == 14){
				//ak je na konci radu a chce robit obchadzku
				customer.setCurrentParkingPosition(CurrentParkingPosition.DETOUR);
				int successRate = customer.getCurrentCustomerSuccessRateValue();
				successRate += 50;
				customer.setCurrentCustomerSuccessRateValue(successRate);
				customer.setCurrentParkingNumber(-1);
				double lengthOfDrive =
						(TO_CROSSROAD_B + TO_CROSSROAD_A + WIDTH_OF_BUILDING + TO_CROSSROAD_A)
								/(((SPEED_OF_CAR*1000)/60)/60);
				message.setCode(Mc.toCrossroadProcessFinished);
				hold(lengthOfDrive,message);
			}else {
				//je na krizovatke pri rade A tak prechadza k B.
				// Tato kontrola asi nie je ani potrebna lebo sa kontroluje inde
				if (myAgent().getNumberOfBuiltParkingLines() > 2){
					//prechod od krizovatky radu B ku krizovatke radu C
					double lengthOfDrive = (TO_CROSSROAD_C)/(((SPEED_OF_CAR*1000)/60)/60);
					message.setCode(Mc.toCrossroadProcessFinished);
					hold(lengthOfDrive,message);
				}
			}
		}else if (customer.getCurrentParkingPosition() == CurrentParkingPosition.LINE_C){
			//od konca radu k prvej krizovatke
			// Tato kontrola asi nie je ani potrebna lebo sa kontroluje inde
			if (customer.getCurrentParkingNumber() == 14){
				//ak je na konci radu a chce robit obchadzku
				customer.setCurrentParkingPosition(CurrentParkingPosition.DETOUR);
				int successRate = customer.getCurrentCustomerSuccessRateValue();
				successRate += 50;
				customer.setCurrentCustomerSuccessRateValue(successRate);
				customer.setCurrentParkingNumber(-1);
				double lengthOfDrive =
						(TO_CROSSROAD_C + TO_CROSSROAD_B + TO_CROSSROAD_A + WIDTH_OF_BUILDING + TO_CROSSROAD_A)
								/(((SPEED_OF_CAR*1000)/60)/60);
				message.setCode(Mc.toCrossroadProcessFinished);
				hold(lengthOfDrive,message);
			}
		}
	}

	public void processToCrossroadProcessFinished(MessageForm message){
		Customer customer = ((MyMessage) message).getCustomer();
		//nastavenie spravnej pozicie na parkovisku
		switch (customer.getCurrentParkingPosition()){
			case ARRIVAL_ROAD:
			case DETOUR: {
				customer.setCurrentParkingPosition(CurrentParkingPosition.LINE_A);
				break;
			}
			case LINE_A:{
				customer.setCurrentParkingPosition(CurrentParkingPosition.LINE_B);
				break;
			}
			case LINE_B:{
				customer.setCurrentParkingPosition(CurrentParkingPosition.LINE_C);
				break;
			}
		}
		assistantFinished(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.toCrossroadProcessFinished:{
				processToCrossroadProcessFinished(message);
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
