package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;

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
	}

	//meta! sender="AgentModel", id="69", type="Request"
	public void processServeCustomer(MessageForm message)
	{
		myAgent().addArrivedCustomerToStats();
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
	}

	//meta! sender="AgentMakeUpArtist", id="79", type="Response"
	public void processMakeUp(MessageForm message)
	{
	}

	//meta! sender="AgentReceptionist", id="70", type="Response"
	public void processWriteOrder(MessageForm message)
	{
	}

	//meta! sender="AgentMakeUpArtist", id="75", type="Response"
	public void processSkinCleaning(MessageForm message)
	{
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
