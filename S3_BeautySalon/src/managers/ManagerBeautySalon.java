package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;
import simulation.Participants.Customer;

//meta! id="26"
public class ManagerBeautySalon extends Manager
{
	public ManagerBeautySalon(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentReceptionist", id="71", type="Response"
	public void processPayment(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		myAgent().getListOfCustomersInSalon().remove(customer);
		//stats
		myAgent().addServedCustomerToStats();
		//TODO este sa uvidi ako sa spravia nakoniec statistiky do 17:00(mozno vytvorenie procesu)
		if (mySim().currentTime() <= 28800){
			myAgent().addServedCustomerUntil17ToStats();
			myAgent().getTimeInSystemUntil17().addSample(mySim().currentTime() - customer.getArriveTime());
		}
		myAgent().getTimeInSystem().addSample(mySim().currentTime() - customer.getArriveTime());
		message.setCode(Mc.serveCustomer);
		response(message);
	}

	//meta! sender="AgentModel", id="69", type="Request"
	public void processServeCustomer(MessageForm message)
	{
		myAgent().addArrivedCustomerToStats();
		myAgent().getListOfCustomersInSalon().add(((MyMessage) message).getCustomer());
		message.setCode(Mc.writeOrder);
		message.setAddressee(mySim().findAgent(Id.agentReceptionist));
		myAgent().setProcessedMessage(message);
		//zistovanie poctu zakaznikov vo frontoch
		MyMessage m = new MyMessage(mySim());
		m.setCode(Mc.numberOfCustomersInQueuesInit);
		m.setAddressee(mySim().findAgent(Id.agentHairstylist));
		call(m);
		//request(message);
	}

	//meta! sender="AgentHairstylist", id="73", type="Response"
	public void processHairstyling(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		if (customer.isCleaning()){
			//chce aj cistenie pleti
			message.setCode(Mc.skinCleaning);
			message.setAddressee(mySim().findAgent(Id.agentMakeUpArtist));
		}else if (customer.isMakeup()){
			//chce iba makeup
			message.setCode(Mc.makeUp);
			message.setAddressee(mySim().findAgent(Id.agentMakeUpArtist));
		}else {
			//chcel iba uces a ide platit

			//oznamenie nech skusi zobrat niekoho z radu pred recepciou(pretoze sa znizil pocet ludi v radoch)
			MyMessage m = new MyMessage(mySim());
			m.setCode(Mc.tryServeCustomerFromQueue);
			m.setAddressee(mySim().findAgent(Id.agentReceptionist));
			notice(m);

			customer.setPaying(true);
			message.setCode(Mc.payment);
			message.setAddressee(mySim().findAgent(Id.agentReceptionist));
		}
		request(message);
	}

	//meta! sender="AgentMakeUpArtist", id="79", type="Response"
	public void processMakeUp(MessageForm message)
	{
		//oznamenie nech skusi zobrat niekoho z radu pred recepciou(pretoze sa znizil pocet ludi v radoch)
		MyMessage m = new MyMessage(mySim());
		m.setCode(Mc.tryServeCustomerFromQueue);
		m.setAddressee(mySim().findAgent(Id.agentReceptionist));
		notice(m);

		Customer customer = ((MyMessage) message).getCustomer();
		//zakaznik ide platit
		customer.setPaying(true);
		message.setCode(Mc.payment);
		message.setAddressee(mySim().findAgent(Id.agentReceptionist));
		request(message);
	}

	//meta! sender="AgentReceptionist", id="70", type="Response"
	public void processWriteOrder(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		if (!customer.isHairstyle() && !customer.isCleaning() && !customer.isMakeup()){
			//zakaznik odchadza lebo nestihol zadat objednavku pred 17:00
			myAgent().addLeavingCustomerToStats();
			message.setCode(Mc.serveCustomer);
			response(message);
		}else {
			//poslania spravy pre spravnu sluzbu
			if (customer.isHairstyle()){
				//chce uces
				message.setCode(Mc.hairstyling);
				message.setAddressee(mySim().findAgent(Id.agentHairstylist));
				request(message);
			}else {
				//nechce uces to znamena ze urcite chce aspon licenie
				message.setAddressee(mySim().findAgent(Id.agentMakeUpArtist));
				if (customer.isCleaning()){
					//chce aj cistenie a to sa vykonava pred licenim
					message.setCode(Mc.skinCleaning);
				}else {
					//chce iba licenie bez cistenia
					message.setCode(Mc.makeUp);
				}
				request(message);
			}
		}
	}

	//meta! sender="AgentMakeUpArtist", id="75", type="Response"
	public void processSkinCleaning(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		//malo by platit vzdy ze chce licenie po cisteni pleti
		if (customer.isMakeup()){
			message.setCode(Mc.makeUp);
			message.setAddressee(mySim().findAgent(Id.agentMakeUpArtist));
			request(message);
		}
	}

	public void processNumberOfCustomersInQueueForReception(MessageForm message){
		message.setAddressee(mySim().findAgent(Id.agentHairstylist));
		call(message);
	}

	public void processNumberOfCustomersInQueueForHairstylist(MessageForm message){
		message.setAddressee(mySim().findAgent(Id.agentMakeUpArtist));
		call(message);
	}

	public void processNumberOfCustomersInQueueForMakeUpArtis(MessageForm message){
		message.setAddressee(mySim().findAgent(Id.agentReceptionist));
		call(message);
	}

	public void processNumberOfCustomersInQueueForHairstylistInit(MessageForm message){
		if (myAgent().getProcessedMessage() != null){
			//nastavim sprave pocet ludi v rade pred upravovou vlasov
			((MyMessage) myAgent().getProcessedMessage()).setNumberOfCustomersInHairstyleQueue(
					((MyMessage) message).getNumberOfCustomersInHairstyleQueue()
			);
			//zistim druhu dlzku radu
			message.setAddressee(mySim().findAgent(Id.agentMakeUpArtist));
			call(message);
		}
	}

	public void processNumberOfCustomersInQueueForMakeUpArtisInit(MessageForm message){
		if (myAgent().getProcessedMessage() != null){
			//nastavim sprave pocet ludi v rade pred makeupom
			((MyMessage) myAgent().getProcessedMessage()).setNumberOfCustomersInMakeUpQueue(
					((MyMessage) message).getNumberOfCustomersInMakeUpQueue()
			);
			//viem obidve dlzky radov tak mozem posielat request pre recepciu
			MessageForm m = myAgent().getProcessedMessage();
			request(m);
			myAgent().setProcessedMessage(null);
		}
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.numberOfCustomersInQueues: {
				switch (message.sender().id())
				{
					case Id.agentReceptionist:
						processNumberOfCustomersInQueueForReception(message);
						break;

					case Id.agentHairstylist:
						processNumberOfCustomersInQueueForHairstylist(message);
						break;
					case Id.agentMakeUpArtist:
						processNumberOfCustomersInQueueForMakeUpArtis(message);
						break;
				}
				break;
			}
			case Mc.numberOfCustomersInQueuesInit:{
				switch (message.sender().id())
				{
					case Id.agentHairstylist:
						processNumberOfCustomersInQueueForHairstylistInit(message);
						break;
					case Id.agentMakeUpArtist:
						processNumberOfCustomersInQueueForMakeUpArtisInit(message);
						break;
				}
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

		case Mc.skinCleaning:
			processSkinCleaning(message);
		break;

		case Mc.serveCustomer:
			processServeCustomer(message);
		break;

		case Mc.payment:
			processPayment(message);
		break;

		case Mc.hairstyling:
			processHairstyling(message);
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
	public AgentBeautySalon myAgent()
	{
		return (AgentBeautySalon)super.myAgent();
	}

}
