package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;
import simulation.Participants.CurrentParkingPosition;
import simulation.Participants.Customer;

//meta! id="30"
public class ManagerParking extends Manager
{
	public ManagerParking(int id, Simulation mySim, Agent myAgent)
	{
		super(id, mySim, myAgent);
		init();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication

		if (petriNet() != null)
		{
			petriNet().clear();
		}
	}

	//meta! sender="AgentModel", id="66", type="Request"
	public void processParking(MessageForm message)
	{
		((MyMessage) message).getCustomer().setCurrentParkingPosition(CurrentParkingPosition.ARRIVAL_ROAD);
		message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
		startContinualAssistant(message);
	}

	//meta! sender="ProcessParking", id="45", type="Finish"
	public void processFinishProcessParking(MessageForm message)
	{
		//asi sa nebude pozuivat
	}

	//meta! sender="ProcessLeaving", id="50", type="Finish"
	public void processFinishProcessLeaving(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		if (customer.getFinalParkingNumber() == -1){
			//nezaparkoval a odchadza
			message.setCode(Mc.parking);
			response(message);
		}else {
			//vyparkoval a odchadza
			message.setCode(Mc.leavingCarPark);
			response(message);
		}
	}

	//meta! sender="AgentModel", id="68", type="Request"
	public void processLeavingCarPark(MessageForm message)
	{
		message.setAddressee(myAgent().findAssistant(Id.processFromEntranceToCar));
		startContinualAssistant(message);
	}

	public void processFinishProcessToEntrance(MessageForm message)
	{
		myAgent().getCustomersSuccessRateValues().addSample(
				((MyMessage) message).getCustomer().getCurrentCustomerSuccessRateValue());
		message.setCode(Mc.parking);
		response(message);
	}

	public void processFinishProcessToCrossroad(MessageForm message)
	{
		//Tu sa vykonava rozhodnutie ci vchadza do radu alebo ide do dalshieho radu
		Customer customer = ((MyMessage) message).getCustomer();
		switch (myAgent().getChosenStrategy()){
			case FIRST_STRATEGY:{
				//len postupne prechadza vsetky miesta a na prvom volnom zaparkuje
				switch (customer.getCurrentParkingPosition()){
					case LINE_A:{
						if (customer.getProcessedLines().contains("A")){
							//ak uz predtym bol v rade A
							boolean isSomeSpotFree = false;
							for (int i = 0; i < 5; i++){
								if (!myAgent().getArrayOfParkingSpots_A()[i]){
									isSomeSpotFree = true;
									//break;
								}
							}
							if (isSomeSpotFree){
								//vchadza sem znova len za predpokladu ze vidi na prvych piatich miestach volne meisto
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}else {
								//prechod k radu B
								message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
								startContinualAssistant(message);
							}
						}else {
							//este nebol v rade A
							//bude prechadzat miesta v rade A
							customer.getProcessedLines().add("A");
							message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
							startContinualAssistant(message);
						}
						break;
					}
					case LINE_B:{
						if (customer.getProcessedLines().contains("B")){
							//ak uz predtym bol v rade B
							boolean isSomeSpotFree = false;
							for (int i = 0; i < 5; i++){
								if (!myAgent().getArrayOfParkingSpots_B()[i]){
									isSomeSpotFree = true;
									//break;
								}
							}
							if (isSomeSpotFree){
								//vchadza sem znova len za predpokladu ze vidi na prvych piatich miestach volne meisto
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}else {
								//prechod k radu C
								message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
								startContinualAssistant(message);
							}
						}else {
							//este nebol v rade B
							//bude prechadzat miesta v rade B
							customer.getProcessedLines().add("B");
							message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
							startContinualAssistant(message);
						}
						break;
					}
					case LINE_C:{
						//uz aj tak musi prejst cez parkovacie miesta ked sa dostal sem
						if (customer.getProcessedLines().contains("C")){
							//pridanie ze uz sa kontroloval rad C
							customer.getProcessedLines().add("C");
						}
						//bude prechadzat miesta v rade C
						message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
						startContinualAssistant(message);
						break;
					}
				}
				break;
			}
			case SECOND_STRATEGY:{
				//TODO ked bude strategia vymyslena
				break;
			}
			case THIRD_STRATEGY:{
				//TODO ked bude strategia vymyslena
				break;
			}
		}
	}

	public void processFinishProcessFromEntranceToCar(MessageForm message)
	{
		message.setAddressee(myAgent().findAssistant(Id.processLeaving));
		startContinualAssistant(message);
	}

	public void processFinishProcessNextParkingSpot(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		switch (myAgent().getChosenStrategy()){
			case FIRST_STRATEGY:{
				//tato strategia hlada prve volne miesto tak hned zaparkuje ked ho najde
				switch (customer.getCurrentParkingPosition()){
					case LINE_A:{
						if (!myAgent().getArrayOfParkingSpots_A()[customer.getCurrentParkingNumber()]){
							//je volne parkovacie miesto tak parkuje a spusta proces pre presun ku vchodu
							customer.setFinalParkingLine("A");
							customer.setFinalParkingNumber(customer.getCurrentParkingNumber());
							myAgent().getArrayOfParkingSpots_A()[customer.getCurrentParkingNumber()] = true;
							//nastavenie spokojnosti s parkovanim
							int successRate = customer.getCurrentCustomerSuccessRateValue();
							successRate += (15 - customer.getCurrentParkingNumber());
							customer.setCurrentCustomerSuccessRateValue(successRate);
							message.setAddressee(myAgent().findAssistant(Id.processToEntrance));
							startContinualAssistant(message);
						}else {
							//nie je volne
							if (customer.getCurrentParkingNumber() == 14){
								//je na poslednom mieste v rade tak bud obchadzka alebo odchod
								if (myAgent().getNumberOfBuiltParkingLines() == 1){
									//odchod lebo presiel cely rad A a je vybudovany iba tento rad
									myAgent().addToLeavingBecauseOfUnsuccessfulParking();
									message.setAddressee(myAgent().findAssistant(Id.processLeaving));
									startContinualAssistant(message);
									/*message.setCode(Mc.parking);
									response(message);*/
								}else if (myAgent().getNumberOfBuiltParkingLines() == 2){
									if (customer.getProcessedLines().contains("B")){
										//presiel uz aj A aj B a dalsie nie su vybudovane tak odchadza
										myAgent().addToLeavingBecauseOfUnsuccessfulParking();
										message.setCode(Mc.parking);
										response(message);
									}else {
										//obchadzka
										message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
										startContinualAssistant(message);
									}
								}else {
									if (customer.getProcessedLines().contains("B")
											&& customer.getProcessedLines().contains("C")){
										//presiel vsetky rady tak odchod
										myAgent().addToLeavingBecauseOfUnsuccessfulParking();
										message.setAddressee(myAgent().findAssistant(Id.processLeaving));
										startContinualAssistant(message);
										/*message.setCode(Mc.parking);
										response(message);*/
									}else{
										//obchadzka
										message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
										startContinualAssistant(message);
									}
								}
							}else {
								//nie je na poslednom mieste v rade tak prechadza na dalsie miesto
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
						}
						break;
					}
					case LINE_B:{
						if (!myAgent().getArrayOfParkingSpots_B()[customer.getCurrentParkingNumber()]){
							//je volne parkovacie miesto tak parkuje a spusta proces pre presun ku vchodu
							customer.setFinalParkingLine("B");
							customer.setFinalParkingNumber(customer.getCurrentParkingNumber());
							myAgent().getArrayOfParkingSpots_B()[customer.getCurrentParkingNumber()] = true;
							//nastavenie spokojnosti s parkovanim
							int successRate = customer.getCurrentCustomerSuccessRateValue();
							successRate += 15 + (15 - customer.getCurrentParkingNumber());
							customer.setCurrentCustomerSuccessRateValue(successRate);
							message.setAddressee(myAgent().findAssistant(Id.processToEntrance));
							startContinualAssistant(message);
						}else {
							//nie je volne
							if (customer.getCurrentParkingNumber() == 14){
								//je na poslednom mieste v rade tak bud obchadzka alebo odchod
								if (myAgent().getNumberOfBuiltParkingLines() == 2){
									if (customer.getProcessedLines().contains("A")){
										//presiel uz aj A aj B a dalsie nie su vybudovane tak odchadza
										myAgent().addToLeavingBecauseOfUnsuccessfulParking();
										message.setAddressee(myAgent().findAssistant(Id.processLeaving));
										startContinualAssistant(message);
										/*message.setCode(Mc.parking);
										response(message);*/
									}else {
										//obchadzka
										message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
										startContinualAssistant(message);
									}
								}else {
									if (customer.getProcessedLines().contains("A")
											&& customer.getProcessedLines().contains("C")){
										//presiel vsetky rady tak odchod
										myAgent().addToLeavingBecauseOfUnsuccessfulParking();
										message.setAddressee(myAgent().findAssistant(Id.processLeaving));
										startContinualAssistant(message);
										/*message.setCode(Mc.parking);
										response(message);*/
									}else{
										//obchadzka
										message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
										startContinualAssistant(message);
									}
								}
							}else {
								//nie je na poslednom mieste v rade tak prechadza na dalsie miesto
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
						}
						break;
					}
					case LINE_C:{
						if (!myAgent().getArrayOfParkingSpots_C()[customer.getCurrentParkingNumber()]){
							//je volne parkovacie miesto tak parkuje a spusta proces pre presun ku vchodu
							customer.setFinalParkingLine("C");
							customer.setFinalParkingNumber(customer.getCurrentParkingNumber());
							myAgent().getArrayOfParkingSpots_C()[customer.getCurrentParkingNumber()] = true;
							//nastavenie spokojnosti s parkovanim
							int successRate = customer.getCurrentCustomerSuccessRateValue();
							successRate += 30 + (15 - customer.getCurrentParkingNumber());
							customer.setCurrentCustomerSuccessRateValue(successRate);
							message.setAddressee(myAgent().findAssistant(Id.processToEntrance));
							startContinualAssistant(message);
						}else {
							//nie je volne
							if (customer.getCurrentParkingNumber() == 14){
								//je na poslednom mieste v rade tak bud obchadzka alebo odchod
								//je v rade C tak nemusim kontrolovat pocet vybudovanych radov
								if (customer.getProcessedLines().contains("A")
										&& customer.getProcessedLines().contains("B")){
									//odchod
									myAgent().addToLeavingBecauseOfUnsuccessfulParking();
									message.setAddressee(myAgent().findAssistant(Id.processLeaving));
									startContinualAssistant(message);
									/*message.setCode(Mc.parking);
									response(message);*/
								}else {
									//obchadzka
									message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
									startContinualAssistant(message);
								}
							}else {
								//nie je na poslednom mieste v rade tak prechadza na dalsie miesto
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
						}
						break;
					}
				}
				break;
			}
			case SECOND_STRATEGY:{
				break;
			}
			case THIRD_STRATEGY:{
				break;
			}
		}
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
		}
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	public void init()
	{
	}

	@Override
	public void processMessage(MessageForm message)
	{
		switch (message.code())
		{
		case Mc.leavingCarPark:
			processLeavingCarPark(message);
		break;

		case Mc.finish:
			switch (message.sender().id())
			{
			case Id.processParking:
				processFinishProcessParking(message);
			break;

			case Id.processLeaving:
				processFinishProcessLeaving(message);
			break;

			case Id.processToEntrance:
				processFinishProcessToEntrance(message);
				break;

			case Id.processToCrossroad:
				processFinishProcessToCrossroad(message);
				break;

			case Id.processFromEntranceToCar:
				processFinishProcessFromEntranceToCar(message);
				break;

			case Id.processNextParkingSpot:
				processFinishProcessNextParkingSpot(message);
				break;
			}
		break;

		case Mc.parking:
			processParking(message);
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
