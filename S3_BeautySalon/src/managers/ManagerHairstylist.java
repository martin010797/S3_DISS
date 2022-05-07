package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;
import simulation.Participants.Hairstylist;
import simulation.Participants.Receptionist;

import java.util.PriorityQueue;

//meta! id="35"
public class ManagerHairstylist extends Manager
{
	public ManagerHairstylist(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentBeautySalon", id="73", type="Request"
	public void processHairstyling(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		if (myAgent().isSomeHairstylistFree()){
			//vykonavanie sluzby ucesu
			PriorityQueue<Hairstylist> availableHairstylists = new PriorityQueue<>();
			for (int i = 0; i < myAgent().getListOfHairStylists().size(); i++){
				Hairstylist h = myAgent().getListOfHairStylists().get(i);
				if (!h.isWorking()) {
					availableHairstylists.add(h);
				}
			}
			if (availableHairstylists.size() == 1){
				myAgent().setSomeHairstylistFree(false);
			}
			Hairstylist chosenHairstylist = availableHairstylists.poll();
			chosenHairstylist.setWorking(true);
			customer.setChosenPersonnel(chosenHairstylist);
			//spustenie procesu pre uces
			message.setAddressee(myAgent().findAssistant(Id.processHairstyle));
			startContinualAssistant(message);
		}else {
			//postavenie do radu
			customer.setMessage(message);
			customer.setCurrentPosition(CurrentPosition.IN_QUEUE_FOR_HAIRSTYLE);
			myAgent().getHairstyleWaitingQueue().enqueue(customer);
			//statistiky o dlzke radu by sa mali zaznamenavat v queue
		}
	}

	//meta! sender="ProcessHairstyle", id="54", type="Finish"
	public void processFinish(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		Hairstylist hairstylist = ((Hairstylist) customer.getChosenPersonnel());

		//update pre kadernicku
		hairstylist.setWorking(false);
		myAgent().setSomeHairstylistFree(true);
		Double workedTime = hairstylist.getWorkedTimeTogether();
		hairstylist.setWorkedTimeTogether(workedTime + mySim().currentTime() - customer.getServiceStartTime());

		//update pre customera
		customer.setHairstyle(false);

		//respond pre agenta beauty simulator
		message.setCode(Mc.hairstyling);
		response(message);

		planNextHairstyle();
	}

	public void planNextHairstyle(){
		//pokial je niekto v rade tak si ho priradi niekto
		if (!myAgent().getHairstyleWaitingQueue().isEmpty()){
			Customer customer = myAgent().getHairstyleWaitingQueue().dequeue();
			MessageForm message = customer.getMessage();
			//statistiky o dlzke radu sa meraju same
			processHairstyling(message);
		}
	}

	public void processNumberOfCustomersInQueues(MessageForm message){
		((MyMessage) message).setNumberOfCustomersInHairstyleQueue(myAgent().getHairstyleWaitingQueue().size());
		message.setAddressee(mySim().findAgent(Id.agentBeautySalon));
		message.setSender(myAgent());
		call(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.numberOfCustomersInQueues:
			case Mc.numberOfCustomersInQueuesInit: {
				processNumberOfCustomersInQueues(message);
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
		case Mc.finish:
			processFinish(message);
		break;

		case Mc.hairstyling:
			processHairstyling(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentHairstylist myAgent()
	{
		return (AgentHairstylist)super.myAgent();
	}

}
