package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;
import simulation.Participants.MakeUpArtist;
import simulation.Participants.Receptionist;

import java.util.PriorityQueue;

//meta! id="36"
public class ManagerMakeUpArtist extends Manager
{
	public ManagerMakeUpArtist(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentBeautySalon", id="79", type="Request"
	public void processMakeUp(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		if (myAgent().isSomeMakeupArtistsFree()){
			//vykonavanie sluzby licenia
			PriorityQueue<MakeUpArtist> availableMakeupArtists = new PriorityQueue<>();
			for (int i = 0; i < myAgent().getListOfMakeupArtists().size(); i++){
				MakeUpArtist m = myAgent().getListOfMakeupArtists().get(i);
				if (!m.isWorking()) {
					availableMakeupArtists.add(m);
				}
			}
			if (availableMakeupArtists.size() == 1){
				myAgent().setSomeMakeupArtistsFree(false);
			}
			MakeUpArtist chosenMakeupArtist = availableMakeupArtists.poll();
			chosenMakeupArtist.setWorking(true);
			customer.setChosenPersonnel(chosenMakeupArtist);
			message.setAddressee(myAgent().findAssistant(Id.processMakeUp));
			startContinualAssistant(message);
		}else {
			//postavenie do radu
			customer.setMessage(message);
			customer.setCurrentPosition(CurrentPosition.IN_QUEUE_FOR_MAKEUP);
			myAgent().getMakeupWaitingQueue().enqueue(customer);
			//statistiky o dlzke radu by sa mali zaznamenavat v queue
		}
	}

	//meta! sender="ProcessMakeUp", id="63", type="Finish"
	public void processFinishProcessMakeUp(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		MakeUpArtist makeUpArtist = ((MakeUpArtist) customer.getChosenPersonnel());

		//update pre kozmeticku
		makeUpArtist.setWorking(false);
		myAgent().setSomeMakeupArtistsFree(true);
		Double workedTime = makeUpArtist.getWorkedTimeTogether();
		makeUpArtist.setWorkedTimeTogether(workedTime + mySim().currentTime() - customer.getServiceStartTime());

		//update pre customera
		customer.setMakeup(false);

		planNextMakeUpOrCleaning();

		//respond pre agenta beauty simulator
		message.setCode(Mc.makeUp);
		response(message);
	}

	//meta! sender="ProcessSkinCleaning", id="61", type="Finish"
	public void processFinishProcessSkinCleaning(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		MakeUpArtist makeUpArtist = ((MakeUpArtist) customer.getChosenPersonnel());

		//update pre kozmeticku
		makeUpArtist.setWorking(false);
		myAgent().setSomeMakeupArtistsFree(true);
		Double workedTime = makeUpArtist.getWorkedTimeTogether();
		makeUpArtist.setWorkedTimeTogether(workedTime + mySim().currentTime() - customer.getServiceStartTime());

		//update pre customera
		customer.setCleaning(false);

		planNextMakeUpOrCleaning();

		//respond pre agenta beauty simulator
		message.setCode(Mc.skinCleaning);
		response(message);
	}

	//meta! sender="AgentBeautySalon", id="75", type="Request"
	public void processSkinCleaning(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		if (myAgent().isSomeMakeupArtistsFree()){
			//vykonavanie sluzby cistenia pleti
			PriorityQueue<MakeUpArtist> availableMakeupArtists = new PriorityQueue<>();
			for (int i = 0; i < myAgent().getListOfMakeupArtists().size(); i++){
				MakeUpArtist m = myAgent().getListOfMakeupArtists().get(i);
				if (!m.isWorking()) {
					availableMakeupArtists.add(m);
				}
			}
			if (availableMakeupArtists.size() == 1){
				myAgent().setSomeMakeupArtistsFree(false);
			}
			MakeUpArtist chosenMakeupArtist = availableMakeupArtists.poll();
			chosenMakeupArtist.setWorking(true);
			customer.setChosenPersonnel(chosenMakeupArtist);
			message.setAddressee(myAgent().findAssistant(Id.processSkinCleaning));
			startContinualAssistant(message);
		}else {
			//postavenie do radu
			customer.setMessage(message);
			customer.setCurrentPosition(CurrentPosition.IN_QUEUE_FOR_MAKEUP);
			myAgent().getMakeupWaitingQueue().enqueue(customer);
			//statistiky o dlzke radu by sa mali zaznamenavat v queue
		}
	}

	public void planNextMakeUpOrCleaning(){
		//pokial je niekto v rade tak si ho priradi nejaka kozmeticka
		if (!myAgent().getMakeupWaitingQueue().isEmpty()){
			Customer customer = myAgent().getMakeupWaitingQueue().dequeue();
			MessageForm message = customer.getMessage();
			//statistiky o dlzke radu sa meraju same
			if (customer.isCleaning()){
				//ma sa planovat cistenie pleti
				processSkinCleaning(message);
			}else {
				//ma sa planovat licenie
				processMakeUp(message);
			}
		}
	}

	public void processNumberOfCustomersInQueues(MessageForm message){
		((MyMessage) message).setNumberOfCustomersInMakeUpQueue(myAgent().getMakeupWaitingQueue().size());
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
		case Mc.skinCleaning:
			processSkinCleaning(message);
		break;

		case Mc.finish:
			switch (message.sender().id())
			{
			case Id.processMakeUp:
				processFinishProcessMakeUp(message);
			break;

			case Id.processSkinCleaning:
				processFinishProcessSkinCleaning(message);
			break;
			}
		break;

		case Mc.makeUp:
			processMakeUp(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentMakeUpArtist myAgent()
	{
		return (AgentMakeUpArtist)super.myAgent();
	}

}
