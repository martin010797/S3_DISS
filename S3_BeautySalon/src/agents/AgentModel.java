package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;
import simulation.Participants.Customer;

import java.util.ArrayList;
import java.util.List;

//meta! id="1"
public class AgentModel extends Agent
{
	private List<Customer> listOfCustomersInSystem;
	private int numberOfArrivedCustomers;
	private int arrivedOnCar;

	public AgentModel(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		addOwnMessage(Mc.init);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		listOfCustomersInSystem = new ArrayList<>();
		numberOfArrivedCustomers = 0;
		arrivedOnCar = 0;

		MyMessage message = new MyMessage(mySim());
		message.setCode(Mc.init);
		message.setAddressee(this);
		manager().notice(message);
	}

	public List<Customer> getListOfCustomersInSystem() {
		return listOfCustomersInSystem;
	}

	public int getNumberOfArrivedCustomers() {
		return numberOfArrivedCustomers;
	}

	public void addCustomerToStats(){
		numberOfArrivedCustomers++;
	}

	public int getArrivedOnCar() {
		return arrivedOnCar;
	}

	public void addArrivedCarToStats(){
		arrivedOnCar++;
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerModel(Id.managerModel, mySim(), this);
		addOwnMessage(Mc.customerArrived);
		addOwnMessage(Mc.serveCustomer);
		addOwnMessage(Mc.parking);
		addOwnMessage(Mc.leavingCarPark);
	}
	//meta! tag="end"
}
