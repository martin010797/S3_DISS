package simulation;

import OSPABA.*;

public class Mc extends IdList
{
	//meta! userInfo="Generated code: do not modify", tag="begin"
	public static final int customerArrived = 1005;
	public static final int parking = 1006;
	public static final int leavingCarPark = 1008;
	public static final int serveCustomer = 1009;
	public static final int writeOrder = 1010;
	public static final int payment = 1011;
	public static final int hairstyling = 1012;
	public static final int skinCleaning = 1014;
	public static final int makeUp = 1015;
	//meta! tag="end"

	// 1..1000 range reserved for user
	public static final int init = 1;
	public static final int createNewCustomerOnFoot = 2;
	public static final int createNewCustomerOnCar = 3;
	public static final int orderingProcessFinished = 4;
	public static final int paymentProcessFinished = 5;
	public static final int numberOfCustomersInQueues = 6;
	public static final int numberOfCustomersInQueuesInit = 7;
	public static final int hairstyleProcessFinished = 8;
	public static final int makeUpProcessFinished = 9;
	public static final int cleaningProcessFinished = 10;
	public static final int tryServeCustomerFromQueue = 11;
	public static final int closingProcessFinished = 12;
	public static final int closingSalon = 13;
	public static final int leavingProcessFinished = 14;
	public static final int fromEntranceToCarProcessFinished = 15;
	public static final int nextParkingSpotProcessFinished = 16;
	public static final int toCrossroadProcessFinished = 17;
	public static final int toEntranceProcessFinished = 18;
}
