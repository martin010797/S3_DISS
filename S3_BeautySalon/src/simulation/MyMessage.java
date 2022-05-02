package simulation;

import OSPABA.*;
import simulation.Participants.Customer;
import simulation.Participants.Personnel;

public class MyMessage extends MessageForm
{
	private Personnel personnel;
	private Customer customer;
	private int numberOfCustomersInHairstyleQueue;
	private int numberOfCustomersInMakeUpQueue;

	public MyMessage(Simulation sim)
	{
		super(sim);
		numberOfCustomersInMakeUpQueue = -1;
		numberOfCustomersInHairstyleQueue = -1;
	}

	public MyMessage(MyMessage original)
	{
		super(original);
		// copy() is called in superclass
	}

	@Override
	public MessageForm createCopy()
	{
		return new MyMessage(this);
	}

	@Override
	protected void copy(MessageForm message)
	{
		super.copy(message);
		MyMessage original = (MyMessage)message;
		// Copy attributes
		personnel = original.personnel;
		customer = original.customer;
		numberOfCustomersInHairstyleQueue = original.numberOfCustomersInHairstyleQueue;
		numberOfCustomersInMakeUpQueue = original.numberOfCustomersInMakeUpQueue;
	}

	public Personnel getPersonnel() {
		return personnel;
	}

	public void setPersonnel(Personnel personnel) {
		this.personnel = personnel;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getNumberOfCustomersInHairstyleQueue() {
		return numberOfCustomersInHairstyleQueue;
	}

	public void setNumberOfCustomersInHairstyleQueue(int numberOfCustomersInHairstyleQueue) {
		this.numberOfCustomersInHairstyleQueue = numberOfCustomersInHairstyleQueue;
	}

	public int getNumberOfCustomersInMakeUpQueue() {
		return numberOfCustomersInMakeUpQueue;
	}

	public void setNumberOfCustomersInMakeUpQueue(int numberOfCustomersInMakeUpQueue) {
		this.numberOfCustomersInMakeUpQueue = numberOfCustomersInMakeUpQueue;
	}
}
