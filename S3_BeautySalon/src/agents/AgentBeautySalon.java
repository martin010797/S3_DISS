package agents;

import OSPABA.*;
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
	//TODO toot vsetko sa asi popresuva do konkretnych agentov
	//private List<Hairstylist> listOfHairStylists;
	//private List<MakeUpArtist> listOfMakeupArtists;
	/*private List<Receptionist> listOfReceptionists;
	private int numberOfReceptionists;*/
	//private int numberOfMakeupArtists;
	//private int numberOfHairstylists;

	private int numberOfArrivedCustomers;
	private int numberOfServedCustomers;
	private int numberOfServedCustomersUntil17;
	private int numberOfLeavingCustomers;

	private List<Customer> listOfCustomersInSalon;

	private MessageForm processedMessage;

	public AgentBeautySalon(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		//listOfHairStylists = new ArrayList<>();
		//listOfMakeupArtists = new ArrayList<>();
		//listOfReceptionists = new ArrayList<>();
		listOfCustomersInSalon = new ArrayList<>();

		addOwnMessage(Mc.numberOfCustomersInQueues);
		addOwnMessage(Mc.numberOfCustomersInQueuesInit);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		addPersonnel();
		numberOfArrivedCustomers = 0;
		numberOfServedCustomers = 0;
		numberOfServedCustomersUntil17 = 0;
		numberOfLeavingCustomers = 0;
	}

	private void addPersonnel(){
		//listOfReceptionists.clear();
		//listOfHairStylists.clear();
		//listOfMakeupArtists.clear();
		/*for (int i = 0; i < numberOfReceptionists; i++){
			Receptionist receptionist = new Receptionist();
			listOfReceptionists.add(receptionist);
		}*/
		/*for (int i = 0; i < numberOfMakeupArtists; i++){
			MakeUpArtist makeUpArtist = new MakeUpArtist();
			listOfMakeupArtists.add(makeUpArtist);
		}*/
		/*for (int i = 0; i < numberOfHairstylists; i++){
			Hairstylist hairstylist = new Hairstylist();
			listOfHairStylists.add(hairstylist);
		}*/
	}

	/*public int getNumberOfReceptionists() {
		return numberOfReceptionists;
	}

	public void setNumberOfReceptionists(int numberOfReceptionists) {
		this.numberOfReceptionists = numberOfReceptionists;
	}*/

	/*public int getNumberOfMakeupArtists() {
		return numberOfMakeupArtists;
	}

	public void setNumberOfMakeupArtists(int numberOfMakeupArtists) {
		this.numberOfMakeupArtists = numberOfMakeupArtists;
	}*/

	/*public int getNumberOfHairstylists() {
		return numberOfHairstylists;
	}

	public void setNumberOfHairstylists(int numberOfHairstylists) {
		this.numberOfHairstylists = numberOfHairstylists;
	}*/

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
