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
		Customer customer = ((MyMessage) message).getCustomer();
		if (myAgent().getChosenStrategy() == ParkingStrategy.THIRD_STRATEGY){
			//rozhodnutie sa kolko miest bude tento zakaznik prechadzat kym zaparkuje
			double value = myAgent().getSkippedParkingSpotsGenerator().nextDouble();
			if (value < 0.4){
				//s 40 percentnou pravdepodobnostou najprv prejde v rade vzdy 7 miest az potom hlada miesto
				customer.setSkipNumberOfParkingSpots(7);
			}else if (value < 0.6){
				//s 20 percentou pravdepobnostou najprv prejde v rade 10 miest a az potom parkuje
				customer.setSkipNumberOfParkingSpots(10);
			}else {
				//40 percent ze parkuje od prveho miesta
				customer.setSkipNumberOfParkingSpots(0);
			}
		}
		customer.setCurrentParkingPosition(CurrentParkingPosition.ARRIVAL_ROAD);
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
						if (!customer.getProcessedLines().contains("C")){
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
				switch (customer.getCurrentParkingPosition()){
					case LINE_A:{
						if(myAgent().getNumberOfBuiltParkingLines() == 1){
							//je vybudovany len tento rad tak prechadza miesta
							if (!customer.getProcessedLines().contains("A")){
								customer.getProcessedLines().add("A");
							}
							message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
							startContinualAssistant(message);
						}else {
							//je vybudovanych viac radov
							if (myAgent().getNumberOfBuiltParkingLines() == 2){
								//vybudovane 2
								if (customer.getProcessedLines().contains("B")){
									//ak uz bol v B tak prechadza miesta v A
									if (!customer.getProcessedLines().contains("A")){
										customer.getProcessedLines().add("A");
									}
									message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
									startContinualAssistant(message);
								}else {
									//este nebol v B
									if (customer.getProcessedLines().contains("A")){
										//v A uz bol tak prechadza k B
										message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
										startContinualAssistant(message);
									}else {
										//v A ani B este nebol tak sa rozhoduje ci prejde na dalsiu krizovatku
										//na dalsiu krizovatku prechadza s 50 percentnou pravdepodobnostou
										double value = myAgent().getGoToNextLineGenerator().nextDouble();
										if (value < 0.5){
											//vojde do radu A
											customer.getProcessedLines().add("A");
											message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
											startContinualAssistant(message);
										}else {
											//ide k radu B
											message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
											startContinualAssistant(message);
										}
									}
								}
							}else {
								//vybudovane 3
								if (customer.getProcessedLines().contains("B")
										&& customer.getProcessedLines().contains("C")){
									//su presiel uz B aj C tak vchadza do A
									if (!customer.getProcessedLines().contains("A")){
										customer.getProcessedLines().add("A");
									}
									message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
									startContinualAssistant(message);
								}else {
									//niektory rad z dalsich este nepresiel
									if (customer.getProcessedLines().contains("A")){
										//v A uz bol tak prechadza k dalsej krizovatke
										message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
										startContinualAssistant(message);
									}else {
										//v A este nebol tak sa rozhoduje ci prejde k dalsej krizovatke alebo parkuje tu
										//na dalsiu krizovatku prechadza s 50 percentnou pravdepodobnostou
										double value = myAgent().getGoToNextLineGenerator().nextDouble();
										if (value < 0.5){
											//vojde do radu A
											customer.getProcessedLines().add("A");
											message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
											startContinualAssistant(message);
										}else {
											//ide k radu B
											message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
											startContinualAssistant(message);
										}
									}
								}
							}
						}
						/*if (customer.getProcessedLines().contains("A")){
							//ak uz bol v rade A
							//prechod k radu B
							message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
							startContinualAssistant(message);
						}else {
							//este nebol v rade A
							//bude prechadzat miesta v rade A
							customer.getProcessedLines().add("A");
							message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
							startContinualAssistant(message);
						}*/
						break;
					}
					case LINE_B:{
						if (myAgent().getNumberOfBuiltParkingLines() == 2){
							//dalsia rada nie je vybudovana tak vchadza do B
							if (!customer.getProcessedLines().contains("B")){
								customer.getProcessedLines().add("B");
							}
							message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
							startContinualAssistant(message);
						}else {
							//je aj dalsia rada
							if (customer.getProcessedLines().contains("C")){
								//v rade C uz bol tak vchadza do B
								if (!customer.getProcessedLines().contains("B")){
									customer.getProcessedLines().add("B");
								}
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}else {
								//este nebol v C
								if (customer.getProcessedLines().contains("B")){
									//v B uz bol tak prechadza k C
									message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
									startContinualAssistant(message);
								}else {
									//v B ani C este nebol tak sa rozhoduje ci ide k dalsej krizovatke alebo parkuje tu
									//na dalsiu krizovatku prechadza s 50 percentnou pravdepodobnostou
									double value = myAgent().getGoToNextLineGenerator().nextDouble();
									if (value < 0.5){
										//vojde do radu B
										customer.getProcessedLines().add("B");
										message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
										startContinualAssistant(message);
									}else {
										//ide k radu C
										message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
										startContinualAssistant(message);
									}
								}
							}
						}
						/*if (customer.getProcessedLines().contains("B")){
							//ak uz predtym bol v rade B
							//prechod k radu C
							message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
							startContinualAssistant(message);
						}else {
							//este nebol v rade B
							//bude prechadzat miesta v rade B
							customer.getProcessedLines().add("B");
							message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
							startContinualAssistant(message);
						}*/
						break;
					}
					case LINE_C:{
						//uz aj tak musi prejst cez parkovacie miesta ked sa dostal sem
						if (!customer.getProcessedLines().contains("C")){
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
			case THIRD_STRATEGY:{
				//TODO
				switch (customer.getCurrentParkingPosition()){
					case LINE_A:{
						if (customer.getProcessedLines().contains("A")){
							//ak uz bol v rade A
							//prechod k radu B
							message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
							startContinualAssistant(message);
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
							//prechod k radu C
							message.setAddressee(myAgent().findAssistant(Id.processToCrossroad));
							startContinualAssistant(message);
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
						if (!customer.getProcessedLines().contains("C")){
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
				switch (customer.getCurrentParkingPosition()){
					case LINE_A:{
						if (!myAgent().getArrayOfParkingSpots_A()[customer.getCurrentParkingNumber()]){
							//je volne parkovacie miesto
							//rozhoduje sa ci zaparkuje podla pozicie v parkovisku
							boolean parkHere = decideToParkByPosition(customer);
							if (parkHere){
								//parkuje
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
								//ide na dalsie miesto
								//kontorla ci je na poslednom mieste nie je potrebna lebo na volnom by urcite zaparkoval
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
						}else {
							//nie je volne
							if (customer.getCurrentParkingNumber() == 14){
								//je na poslednom mieste v rade tak bud obchadzka alebo odchod
								if (myAgent().getNumberOfBuiltParkingLines() == 1){
									//odchod lebo presiel cely rad A a je vybudovany iba tento rad
									myAgent().addToLeavingBecauseOfUnsuccessfulParking();
									message.setAddressee(myAgent().findAssistant(Id.processLeaving));
									startContinualAssistant(message);
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
							//je volne parkovacie miesto
							//rozhoduje sa ci zaparkuje podla pozicie v parkovisku
							boolean parkHere = decideToParkByPosition(customer);
							if (parkHere){
								//parkuje
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
								//ide na dalsie miesto
								//kontorla ci je na poslednom mieste nie je potrebna lebo na volnom by urcite zaparkoval
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
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
							//je volne parkovacie miesto
							//rozhoduje sa ci zaparkuje podla pozicie v parkovisku
							boolean parkHere = decideToParkByPosition(customer);
							if (parkHere){
								//parkuje
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
								//ide na dalsie miesto
								//kontorla ci je na poslednom mieste nie je potrebna lebo na volnom by urcite zaparkoval
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
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
			case THIRD_STRATEGY:{
				//TODO
				switch (customer.getCurrentParkingPosition()){
					case LINE_A:{
						if (!myAgent().getArrayOfParkingSpots_A()[customer.getCurrentParkingNumber()]){
							//je volne parkovacie miesto tak sa rozhoduje ci tam zaparkuje
							boolean parkHere = decideToParkByCustomerPreferences(customer);
							if (parkHere){
								//parkuje
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
								//ide na dalsie miesto
								//zakaznik si zapamata ze videl volne miesto a nezaparkoval tam
								customer.setGoToParkingRowAgain("A");
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
						}else {
							//nie je volne
							//TODO tu budu zmeny ak budem menit sposob fungovania strategie
							if (customer.getCurrentParkingNumber() == 14){
								//je na poslednom mieste v rade tak bud obchadzka alebo odchod
								if (myAgent().getNumberOfBuiltParkingLines() == 1){
									//odchod lebo presiel cely rad A a je vybudovany iba tento rad
									myAgent().addToLeavingBecauseOfUnsuccessfulParking();
									message.setAddressee(myAgent().findAssistant(Id.processLeaving));
									startContinualAssistant(message);
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
							//je volne parkovacie miesto tak sa rozhoduje ci tam zaparkuje
							boolean parkHere = decideToParkByCustomerPreferences(customer);
							if (parkHere){
								//parkuje
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
								//ide na dalsie miesto
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
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
							//je volne parkovacie miesto tak sa rozhoduje ci tam zaparkuje
							boolean parkHere = decideToParkByCustomerPreferences(customer);
							if (parkHere){
								//parkuje
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
								//ide na dalsie miesto
								message.setAddressee(myAgent().findAssistant(Id.processNextParkingSpot));
								startContinualAssistant(message);
							}
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
		}
	}

	private boolean decideToParkByCustomerPreferences(Customer customer){
		if (customer.getCurrentParkingNumber() >= customer.getSkipNumberOfParkingSpots()){
			return true;
		}else {
			return false;
		}
	}

	private boolean decideToParkByPosition(Customer customer){
		if (customer.getCurrentParkingNumber() < 5){
			//nachadza sa do piateho miesta tak zaparkuje na 10 percent
			double value = myAgent().getSpotsOneToFiveGenerator().nextDouble();
			if (value < 0.1){
				return true;
			}
		}else if (customer.getCurrentParkingNumber() < 10){
			//nachadza sa do desiateho miesta tak zaparkuje na 30 percent
			double value = myAgent().getSpotsSixToTenGenerator().nextDouble();
			if (value < 0.3){
				return true;
			}
		}else if (customer.getCurrentParkingNumber() < 13){
			//nachadza sa do 13 parkovacieho miesta tak zaparkuje na 50 percent
			double value = myAgent().getSpotsElevenToThirteenGenerator().nextDouble();
			if (value < 0.5){
				return true;
			}
		}else {
			//posledne dve miesta tak parkuje urcite
			return true;
		}
		return false;
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
