package agents;

import OSPABA.*;
import OSPRNG.UniformContinuousRNG;
import OSPStat.Stat;
import simulation.*;
import managers.*;
import continualAssistants.*;

import java.util.Random;

//meta! id="30"
public class AgentParking extends Agent
{
	private Random seedGenerator;
	private UniformContinuousRNG speedOfWalkingGenerator;

	private ParkingStrategy chosenStrategy;
	private int numberOfBuiltParkingLines;
	//false: volne, true: obsadene
	private boolean[] arrayOfParkingSpots_A;
	private boolean[] arrayOfParkingSpots_A_ReservedByUser;
	private boolean[] arrayOfParkingSpots_B;
	private boolean[] arrayOfParkingSpots_B_ReservedByUser;
	private boolean[] arrayOfParkingSpots_C;
	private boolean[] arrayOfParkingSpots_C_ReservedByUser;

	//stats
	private int leavingBecauseOfUnsuccessfulParking;
	//spokojnosti s parkovanim
	private Stat customersSuccessRateValues;

	public AgentParking(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
		new ProcessFromEntranceToCar(Id.processFromEntranceToCar, mySim(), this);
		new ProcessNextParkingSpot(Id.processNextParkingSpot, mySim(), this);
		new ProcessToCrossroad(Id.processToCrossroad, mySim(), this);
		new ProcessToEntrance(Id.processToEntrance, mySim(), this);
		addOwnMessage(Mc.leavingProcessFinished);
		addOwnMessage(Mc.fromEntranceToCarProcessFinished);
		addOwnMessage(Mc.nextParkingSpotProcessFinished);
		addOwnMessage(Mc.toCrossroadProcessFinished);
		addOwnMessage(Mc.toEntranceProcessFinished);
		arrayOfParkingSpots_A_ReservedByUser = new boolean[15];
		arrayOfParkingSpots_B_ReservedByUser = new boolean[15];
		arrayOfParkingSpots_C_ReservedByUser = new boolean[15];
		arrayOfParkingSpots_A = new boolean[15];
		arrayOfParkingSpots_B = new boolean[15];
		arrayOfParkingSpots_C = new boolean[15];
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		arrayOfParkingSpots_A = new boolean[15];
		arrayOfParkingSpots_B = new boolean[15];
		arrayOfParkingSpots_C = new boolean[15];
		for (int i = 0; i < 15; i++){
			if (arrayOfParkingSpots_A_ReservedByUser[i]){
				arrayOfParkingSpots_A[i] = true;
			}
			if (arrayOfParkingSpots_B_ReservedByUser[i]){
				arrayOfParkingSpots_B[i] = true;
			}
			if (arrayOfParkingSpots_C_ReservedByUser[i]){
				arrayOfParkingSpots_C[i] = true;
			}
		}
		leavingBecauseOfUnsuccessfulParking = 0;
		customersSuccessRateValues = new Stat();
	}

	public Random getSeedGenerator() {
		return seedGenerator;
	}

	public void setSeedGenerator(Random seedGenerator) {
		this.seedGenerator = seedGenerator;
		prepareGenerators();
	}

	private void prepareGenerators(){
		speedOfWalkingGenerator = new UniformContinuousRNG(-0.7,0.7, new Random(seedGenerator.nextInt()));
	}

	public ParkingStrategy getChosenStrategy() {
		return chosenStrategy;
	}

	public void setChosenStrategy(ParkingStrategy chosenStrategy) {
		this.chosenStrategy = chosenStrategy;
	}

	public int getNumberOfBuiltParkingLines() {
		return numberOfBuiltParkingLines;
	}

	public void setNumberOfBuiltParkingLines(int numberOfBuiltParkingLines) {
		this.numberOfBuiltParkingLines = numberOfBuiltParkingLines;
	}

	public int getLeavingBecauseOfUnsuccessfulParking() {
		return leavingBecauseOfUnsuccessfulParking;
	}

	public void addToLeavingBecauseOfUnsuccessfulParking(){
		leavingBecauseOfUnsuccessfulParking++;
	}

	//ak sa podari uprava tak true
	public boolean reserveParkingSpotByUser(int row, int column, boolean reserve){
		int index = 14 - column;
		switch (row){
			case 0:{
				if(reserve){
					//ak chce obsadit to miesto
					if (!arrayOfParkingSpots_A[index] && !arrayOfParkingSpots_A_ReservedByUser[index]){
						//ak nie je obsadene tak moze vyblokovat uzivatelom
						arrayOfParkingSpots_A_ReservedByUser[index] = true;
						arrayOfParkingSpots_A[index] = true;
						return true;
					}
				}else{
					if (arrayOfParkingSpots_A[index] && arrayOfParkingSpots_A_ReservedByUser[index]){
						//ak je blokovane uzivatelom tak moze miesto odblokovat
						arrayOfParkingSpots_A_ReservedByUser[index] = false;
						arrayOfParkingSpots_A[index] = false;
						return true;
					}
				}
				break;
			}
			case 1:{
				if(reserve){
					//ak chce obsadit to miesto
					if (!arrayOfParkingSpots_B[index] && !arrayOfParkingSpots_B_ReservedByUser[index]){
						//ak nie je obsadene tak moze vyblokovat uzivatelom
						arrayOfParkingSpots_B_ReservedByUser[index] = true;
						arrayOfParkingSpots_B[index] = true;
						return true;
					}
				}else{
					if (arrayOfParkingSpots_B[index] && arrayOfParkingSpots_B_ReservedByUser[index]){
						//ak je blokovane uzivatelom tak moze miesto odblokovat
						arrayOfParkingSpots_B_ReservedByUser[index] = false;
						arrayOfParkingSpots_B[index] = false;
						return true;
					}
				}
				break;
			}
			case 2:{
				if(reserve){
					//ak chce obsadit to miesto
					if (!arrayOfParkingSpots_C[index] && !arrayOfParkingSpots_C_ReservedByUser[index]){
						//ak nie je obsadene tak moze vyblokovat uzivatelom
						arrayOfParkingSpots_C_ReservedByUser[index] = true;
						arrayOfParkingSpots_C[index] = true;
						return true;
					}
				}else{
					if (arrayOfParkingSpots_C[index] && arrayOfParkingSpots_C_ReservedByUser[index]){
						//ak je blokovane uzivatelom tak moze miesto odblokovat
						arrayOfParkingSpots_C_ReservedByUser[index] = false;
						arrayOfParkingSpots_C[index] = false;
						return true;
					}
				}
				break;
			}
		}
		return false;
	}

	public boolean[] getArrayOfParkingSpots_A() {
		return arrayOfParkingSpots_A;
	}

	public boolean[] getArrayOfParkingSpots_B() {
		return arrayOfParkingSpots_B;
	}

	public boolean[] getArrayOfParkingSpots_C() {
		return arrayOfParkingSpots_C;
	}

	public boolean[] getArrayOfParkingSpots_A_ReservedByUser() {
		return arrayOfParkingSpots_A_ReservedByUser;
	}

	public boolean[] getArrayOfParkingSpots_B_ReservedByUser() {
		return arrayOfParkingSpots_B_ReservedByUser;
	}

	public boolean[] getArrayOfParkingSpots_C_ReservedByUser() {
		return arrayOfParkingSpots_C_ReservedByUser;
	}

	public UniformContinuousRNG getSpeedOfWalkingGenerator() {
		return speedOfWalkingGenerator;
	}

	public Stat getCustomersSuccessRateValues() {
		return customersSuccessRateValues;
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerParking(Id.managerParking, mySim(), this);
		new ProcessParking(Id.processParking, mySim(), this);
		new ProcessLeaving(Id.processLeaving, mySim(), this);
		addOwnMessage(Mc.parking);
		addOwnMessage(Mc.leavingCarPark);
	}
	//meta! tag="end"
}
