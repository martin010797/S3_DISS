package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;
import simulation.Participants.CurrentPosition;
import simulation.Participants.Customer;

//meta! id="1"
public class ManagerModel extends Manager
{
	public ManagerModel(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentEnviroment", id="65", type="Notice"
	public void processCustomerArrived(MessageForm message)
	{
		if (((MyMessage) message).getCustomer() != null){
			Customer customer = ((MyMessage) message).getCustomer();
			//pridanie zakaznika do poctu a do aktualnych zakaznikov v systeme
			//TODO odkomentuje sa po doplnenia fungovania parkovania
			/*myAgent().addCustomerToStats();
			myAgent().getListOfCustomersInSystem().add(customer);
			if (customer.isArrivedOnCar()){
				customer.setCurrentPosition(CurrentPosition.PARKING);
				message.setCode(Mc.parking);
				message.setAddressee(mySim().findAgent(Id.agentParking));
				request(message);
			}else {
				message.setCode(Mc.serveCustomer);
				message.setAddressee(mySim().findAgent(Id.agentBeautySalon));
				request(message);
			}*/
			if (!customer.isArrivedOnCar()) {
				myAgent().addCustomerToStats();
				myAgent().getListOfCustomersInSystem().add(customer);
				message.setCode(Mc.serveCustomer);
				message.setAddressee(mySim().findAgent(Id.agentBeautySalon));
				request(message);
			}
		}
	}

	//meta! sender="AgentBeautySalon", id="69", type="Response"
	public void processServeCustomer(MessageForm message)
	{
		Customer customer = ((MyMessage) message).getCustomer();
		if (!customer.isArrivedOnCar()){
			//odchod zakaznika ktory neprisiel na aute tak zo systemu odchadza hned
			myAgent().getListOfCustomersInSystem().remove(customer);
		}else {
			//ti co prisli na aute musia este odist autom z parkoviska
			//TODO
		}
		//vypnutie symulacie ak uz nie je nikto v systeme
		if (((MySimulation) mySim()).getTypeOfSimulation() == TypeOfSimulation.OBSERVE
				&& mySim().currentTime() >= 28800
				&& myAgent().getListOfCustomersInSystem().isEmpty()){
			mySim().stopSimulation();
		}
	}

	//meta! sender="AgentParking", id="66", type="Response"
	public void processParking(MessageForm message)
	{
	}

	//meta! sender="AgentParking", id="68", type="Response"
	public void processLeavingCarPark(MessageForm message)
	{
		//TODO
		//vymazanie zakaznika zo systemu

		if (((MySimulation) mySim()).getTypeOfSimulation() == TypeOfSimulation.OBSERVE
				&& mySim().currentTime() >= 28800
				&& myAgent().getListOfCustomersInSystem().isEmpty()){
			mySim().stopSimulation();
		}
	}

	public void processInit(MessageForm message){
		message.setAddressee(mySim().findAgent(Id.agentEnviroment));
		notice(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.init:{
				processInit(message);
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
		case Mc.leavingCarPark:
			processLeavingCarPark(message);
		break;

		case Mc.serveCustomer:
			processServeCustomer(message);
		break;

		case Mc.parking:
			processParking(message);
		break;

		case Mc.customerArrived:
			processCustomerArrived(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentModel myAgent()
	{
		return (AgentModel)super.myAgent();
	}

}
