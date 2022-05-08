package agents;

import OSPABA.*;
import OSPStat.Stat;
import simulation.*;
import managers.*;
import continualAssistants.*;
import simulation.Participants.Customer;
import simulation.Participants.Hairstylist;
import simulation.Participants.MakeUpArtist;
import simulation.Participants.Receptionist;

import java.util.ArrayList;
import java.util.List;

//meta! id="26"
public class AgentBeautySalon extends Agent
{
	private int numberOfArrivedCustomers;
	private int numberOfServedCustomers;
	private int numberOfServedCustomersUntil17;
	private int numberOfLeavingCustomers;

	private List<Customer> listOfCustomersInSalon;

	private MessageForm processedMessage;

	private Stat timeInSystem;
	private Stat timeInSystemUntil17;

	private boolean plannedClosing;

	public AgentBeautySalon(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		new ProcessClosing(Id.processClosing, mySim(), this);
		listOfCustomersInSalon = new ArrayList<>();

		addOwnMessage(Mc.numberOfCustomersInQueues);
		addOwnMessage(Mc.numberOfCustomersInQueuesInit);
		addOwnMessage(Mc.closingProcessFinished);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		numberOfArrivedCustomers = 0;
		numberOfServedCustomers = 0;
		numberOfServedCustomersUntil17 = 0;
		numberOfLeavingCustomers = 0;
		timeInSystem = new Stat();
		timeInSystemUntil17 = new Stat();
		plannedClosing = false;
	}

	public int getNumberOfArrivedCustomers() {
		return numberOfArrivedCustomers;
	}

	public int getNumberOfServedCustomers() {
		return numberOfServedCustomers;
	}

	public int getNumberOfServedCustomersUntil17() {
		return numberOfServedCustomersUntil17;
	}

	public int getNumberOfLeavingCustomers() {
		return numberOfLeavingCustomers;
	}

	public void addArrivedCustomerToStats(){
		numberOfArrivedCustomers++;
	}

	public List<Customer> getListOfCustomersInSalon() {
		return listOfCustomersInSalon;
	}

	public void addServedCustomerToStats(){
		numberOfServedCustomers++;
	}

	public void addServedCustomerUntil17ToStats(){
		numberOfServedCustomersUntil17++;
	}

	public void addLeavingCustomerToStats(){
		numberOfLeavingCustomers++;
	}

	public MessageForm getProcessedMessage() {
		return processedMessage;
	}

	public void setProcessedMessage(MessageForm processedMessage) {
		this.processedMessage = processedMessage;
	}

	public Stat getTimeInSystem() {
		return timeInSystem;
	}

	public Stat getTimeInSystemUntil17() {
		return timeInSystemUntil17;
	}

	public boolean isPlannedClosing() {
		return plannedClosing;
	}

	public void setPlannedClosing(boolean plannedClosing) {
		this.plannedClosing = plannedClosing;
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerBeautySalon(Id.managerBeautySalon, mySim(), this);
		addOwnMessage(Mc.payment);
		addOwnMessage(Mc.serveCustomer);
		addOwnMessage(Mc.hairstyling);
		addOwnMessage(Mc.makeUp);
		addOwnMessage(Mc.writeOrder);
		addOwnMessage(Mc.skinCleaning);
	}
	//meta! tag="end"
}
