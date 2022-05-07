package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;
import simulation.Participants.Receptionist;

import java.util.PriorityQueue;

//meta! id="34"
public class ManagerReceptionist extends Manager
{
	public ManagerReceptionist(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentBeautySalon", id="71", type="Request"
	public void processPayment(MessageForm message)
	{
		if (myAgent().isSomeReceptionistFree()){
			//zaciatok platby
			PriorityQueue<Receptionist> availableReceptionists = new PriorityQueue<>();
			for (int i = 0; i < myAgent().getListOfReceptionists().size(); i++){
				Receptionist r = myAgent().getListOfReceptionists().get(i);
				if (!r.isWorking()){
					availableReceptionists.add(r);
				}
			}
			if (availableReceptionists.size() == 1){
				myAgent().setSomeReceptionistFree(false);
			}
			Receptionist chosenReceptionist = availableReceptionists.poll();
			chosenReceptionist.setWorking(true);
			((MyMessage) message).getCustomer().setChosenPersonnel(chosenReceptionist);
			//spustenie procesu pre vybavovanie platby
			message.setAddressee(myAgent().findAssistant(Id.processPayment));
			startContinualAssistant(message);
		}else {
			//postavenie do radu
			Customer customer = ((MyMessage) message).getCustomer();
			customer.setMessage(message);
			customer.setCurrentPosition(CurrentPosition.IN_QUEUE_FOR_PAY);
			myAgent().getReceptionWaitingQueue().add(customer);
			//priradenie statistik o dlzke radu
			myAgent().getLengthOfReceptionQueue().addSample(myAgent().getReceptionWaitingQueue().size());
		}
	}

	//meta! sender="AgentBeautySalon", id="70", type="Request"
	public void processWriteOrder(MessageForm message)
	{
		//v sprave posielane kolko je v radoch ludi
		if (myAgent().isSomeReceptionistFree() &&
				(((MyMessage) message).getNumberOfCustomersInHairstyleQueue()
						+ ((MyMessage) message).getNumberOfCustomersInMakeUpQueue()) <= 10){
			//spracovavanie objednavky
			PriorityQueue<Receptionist> availableReceptionists = new PriorityQueue<>();
			for (int i = 0; i < myAgent().getListOfReceptionists().size(); i++){
				Receptionist r = myAgent().getListOfReceptionists().get(i);
				if (!r.isWorking()){
					availableReceptionists.add(r);
				}
			}
			if (availableReceptionists.size() == 1){
				myAgent().setSomeReceptionistFree(false);
			}
			Receptionist chosenReceptionist = availableReceptionists.poll();
			chosenReceptionist.setWorking(true);
			((MyMessage) message).getCustomer().setChosenPersonnel(chosenReceptionist);
			//spustenie procesu pre vybavovanie objednavky
			message.setAddressee(myAgent().findAssistant(Id.processWritingOrder));
			startContinualAssistant(message);
		}else {
			//postavenie do radu
			Customer customer = ((MyMessage) message).getCustomer();
			customer.setMessage(message);
			customer.setCurrentPosition(CurrentPosition.IN_QUEUE_FOR_ORDERING);
			myAgent().getReceptionWaitingQueue().add(customer);
			//priradenie do statistik o dlzke radu
			myAgent().getLengthOfReceptionQueue().addSample(myAgent().getReceptionWaitingQueue().size());
		}
	}

	//meta! sender="ProcessWritingOrder", id="56", type="Finish"
	public void processFinishProcessWritingOrder(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		Receptionist receptionist = ((Receptionist) customer.getChosenPersonnel());

		//update pre recepcnu
		receptionist.setWorking(false);
		myAgent().setSomeReceptionistFree(true);
		Double workedTime = receptionist.getWorkedTimeTogether();
		receptionist.setWorkedTimeTogether(workedTime + mySim().currentTime() - customer.getServiceStartTime());

		//respond pre agenta beauty simulator
		message.setCode(Mc.writeOrder);
		response(message);

		planNextWritingOrderOrPayment();
	}

	//meta! sender="ProcessPayment", id="58", type="Finish"
	public void processFinishProcessPayment(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		Receptionist receptionist = ((Receptionist) customer.getChosenPersonnel());

		//update pre recepcnu
		receptionist.setWorking(false);
		myAgent().setSomeReceptionistFree(true);
		Double workedTime = receptionist.getWorkedTimeTogether();
		receptionist.setWorkedTimeTogether(workedTime + mySim().currentTime() - customer.getServiceStartTime());

		//respond pre agenta beauty simulator
		message.setCode(Mc.payment);
		response(message);

		planNextWritingOrderOrPayment();
	}

	public void planNextWritingOrderOrPayment(){
		//planovanie dalsieho vybavovania objednavky/platby
		//pokial je niekto v rade pred recepciou nech si ho priradi niekto z recepcie
		if (!myAgent().getReceptionWaitingQueue().isEmpty() && myAgent().isSomeReceptionistFree()){
			if (myAgent().getReceptionWaitingQueue().peek().isPaying()){
				//ak je platiaci zakaznik tak sa vytvori zaciatok platby
				Customer customer = myAgent().getReceptionWaitingQueue().poll();
				MessageForm message = customer.getMessage();
				//priradenie do statistik o dlzke radu
				myAgent().getLengthOfReceptionQueue().addSample(myAgent().getReceptionWaitingQueue().size());
				processPayment(message);
			}else {
				//ak nie je platiaci(chce spisat objednavku) tak musi overit ci je kapacita radov mensia ako 11
				//riesnie cez call spravy
				MyMessage message = new MyMessage(mySim());
				message.setCode(Mc.numberOfCustomersInQueues);
				message.setAddressee(mySim().findAgent(Id.agentBeautySalon));
				message.setSender(myAgent());
				call(message);
			}
		}
	}

	public void processNumberOfMessagesInQueues(MessageForm message){
		//vratilo sa kolko zakaznikov je v radoch
		if (((MyMessage) message).getNumberOfCustomersInMakeUpQueue() != -1 &&
				((MyMessage) message).getNumberOfCustomersInHairstyleQueue() != -1){
			//pokial je menej ako 11
			if ((((MyMessage) message).getNumberOfCustomersInHairstyleQueue() +
					((MyMessage) message).getNumberOfCustomersInMakeUpQueue()) < 11){
				//moze sa spracovat objednavka
				if (!myAgent().getReceptionWaitingQueue().isEmpty() &&
						!myAgent().getReceptionWaitingQueue().peek().isPaying()){
					Customer customer = myAgent().getReceptionWaitingQueue().poll();
					MessageForm m = customer.getMessage();
					//priradenie do statistik o dlzke radu
					myAgent().getLengthOfReceptionQueue().addSample(myAgent().getReceptionWaitingQueue().size());
					((MyMessage) m).setNumberOfCustomersInMakeUpQueue(
							((MyMessage) message).getNumberOfCustomersInMakeUpQueue());
					((MyMessage) m).setNumberOfCustomersInHairstyleQueue(
							((MyMessage) message).getNumberOfCustomersInHairstyleQueue());
					processWriteOrder(m);
				}
			}
		}
	}

	public void processTryServeCustomerFromQueue(MessageForm message){
		planNextWritingOrderOrPayment();
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.numberOfCustomersInQueues:{
				processNumberOfMessagesInQueues(message);
				break;
			}
			case Mc.tryServeCustomerFromQueue:{
				processTryServeCustomerFromQueue(message);
				break;
			}
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
		case Mc.writeOrder:
			processWriteOrder(message);
		break;

		case Mc.finish:
			switch (message.sender().id())
			{
			case Id.processWritingOrder:
				processFinishProcessWritingOrder(message);
			break;

			case Id.processPayment:
				processFinishProcessPayment(message);
			break;
			}
		break;

		case Mc.payment:
			processPayment(message);
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
