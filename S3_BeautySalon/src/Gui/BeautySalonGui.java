package Gui;

import OSPABA.SimState;
import OSPABA.Simulation;
import OSPABA.ISimDelegate;
/*import Simulation.BeautySalonSimulator;
import Simulation.Events.Event;
import Simulation.Simulator;
import Simulation.TypeOfSimulation;*/
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import simulation.MySimulation;
import simulation.ParkingStrategy;
import simulation.Participants.*;
import simulation.TypeOfSimulation;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class BeautySalonGui implements ISimDelegate{
    private JFrame frame;

    private JPanel mainPanel;
    private JPanel chartPanel;
    private JButton startSimulationButton;
    private JLabel simulationTimeLabel;
    private JButton stopSimulationButton;
    private JButton pauseSimulationButton;
    private JRadioButton slowSimulationRadioButton;
    private JRadioButton fastSimulationRadioButton;
    private JLabel NumberOfReplicationsLabel;
    private JTextField numberOfReplicationsTextField;
    private JLabel lengthOfSleepLabel;
    private JTextField lengthOfSleepTextField;
    private JLabel deltaTLabel;
    private JTextField deltaTTextField;
    private JButton changeTheSpeedButton;
    private JTextField numberOfHairdressersTextField;
    private JTextField numberOfMakeupArtistsTextField;
    private JTextField numberOfReceptionistsTextField;
    private JLabel numberOfHairdressersLabel;
    private JLabel numberOfMakeupArtistsLabel;
    private JLabel numberOfReceptionistsLabel;
    private JTextPane statisticsTextPane;
    private JTextPane statesOfSystemTextPane;
    private JLabel isPausedLabel;
    private JLabel parkingSuccessRateLabel;
    private JLabel arrivedOnCarLabel;
    private JLabel unsuccesfulParkingCountLabel;
    private JRadioButton chartOutputRadioButton;
    private JTable parkingTable;
    private JComboBox parkingStrategyCombobox;
    private JComboBox numberOfParkingLinesComboBox;
    private JLabel parkingStrategyLabel;
    private JLabel numberOfParkingLinesLabel;
    private JLabel parkingSuccessRateValueLabel;

    private DefaultXYDataset datasetLineChart;
    private XYSeries lineChartXYSeries;
    private XYSeries lineChartXYSeriesUntil17;
    private JFreeChart lineChart;
    private MySimulation simulator;
    private String lastStatesValues;
    //private String lastCalendar;
    private String lastStats;

    //private ArrayList<MySimulation> simulations = new ArrayList<>();
    private Thread thread;
    private int currentRun;
    private String[][] parkingSpots;

    public BeautySalonGui() {
        frame = new JFrame("Beauty salon");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        simulator = new MySimulation();
        simulator.setTypeOfSimulation(TypeOfSimulation.OBSERVE);

        simulator.setDeltaT(400);
        simulator.setSleepLength(400);
        simulator.registerDelegate(this);

        createDatasets();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        ButtonGroup speedOfTheSimulationButtonGroup = new ButtonGroup();
        speedOfTheSimulationButtonGroup.add(fastSimulationRadioButton);
        speedOfTheSimulationButtonGroup.add(slowSimulationRadioButton);
        speedOfTheSimulationButtonGroup.add(chartOutputRadioButton);

        slowSimulationRadioButton.setSelected(true);
        NumberOfReplicationsLabel.setVisible(false);
        numberOfReplicationsTextField.setVisible(false);

        setDeafultText();

        /*for (int i = 0; i < 10; i++){
            MySimulation sim = new MySimulation();
            sim.registerDelegate(this);
            sim.setTypeOfSimulation(TypeOfSimulation.MAX_WITH_CHART);
            sim.setNumberOfMakeupArtists(Integer.parseInt(numberOfMakeupArtistsTextField.getText()));
            sim.setNumberOfReceptionists(Integer.parseInt(numberOfReceptionistsTextField.getText()));
            sim.setNumberOfHairstylists(i+1);

            simulations.add(sim);
        }*/

        createParkingTable();
        createComboBoxes();
        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!simulator.isRunning() && !simulator.isPaused()){
                    fastSimulationRadioButton.setEnabled(false);
                    slowSimulationRadioButton.setEnabled(false);
                    chartOutputRadioButton.setEnabled(false);
                    isPausedLabel.setVisible(false);
                    simulator.setNumberOfMakeupArtists(Integer.parseInt(numberOfMakeupArtistsTextField.getText()));
                    simulator.setNumberOfReceptionists(Integer.parseInt(numberOfReceptionistsTextField.getText()));
                    setParkingStrategy(simulator);
                    setNumberOfBuiltParkingLines(simulator);
                    if (simulator.getTypeOfSimulation() == TypeOfSimulation.MAX_WITH_CHART){
                        statisticsTextPane.setText("");
                        createDatasets();
                        frame.setVisible(true);
                        thread = new Thread(() -> {
                            currentRun = -1;
                            for (int i = 0; i < 10; i++){
                                currentRun++;
                                simulator.setNumberOfHairstylists(i+1);
                                simulator.simulate(Integer.parseInt(numberOfReplicationsTextField.getText()));
                            }
                        });
                        thread.setDaemon(true);
                        thread.start();
                    }else {
                        simulator.setNumberOfHairstylists(Integer.parseInt(numberOfHairdressersTextField.getText()));
                        if (simulator.getTypeOfSimulation() == TypeOfSimulation.OBSERVE){

                            simulator.setSimSpeed(
                                    Double.parseDouble(deltaTTextField.getText()),
                                    Double.parseDouble(lengthOfSleepTextField.getText()));
                            simulator.simulateAsync(1);
                        }else {
                            simulator.setMaxSimSpeed();
                            simulator.simulateAsync(Integer.parseInt(numberOfReplicationsTextField.getText()));
                        }
                    }
                }else if (simulator.isPaused()){
                    isPausedLabel.setVisible(false);
                    simulator.resumeSimulation();
                }
            }
        });
        stopSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulator.isRunning()){
                    if (thread != null && thread.isAlive()){
                        thread.stop();
                    }
                    if (simulator.isPaused()){
                        simulator.resumeSimulation();
                    }
                    simulator.stopSimulation();
                    fastSimulationRadioButton.setEnabled(true);
                    slowSimulationRadioButton.setEnabled(true);
                    chartOutputRadioButton.setEnabled(true);
                    isPausedLabel.setVisible(false);
                }
            }
        });
        fastSimulationRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NumberOfReplicationsLabel.setVisible(true);
                numberOfReplicationsTextField.setVisible(true);
                lengthOfSleepLabel.setVisible(false);
                lengthOfSleepTextField.setVisible(false);
                deltaTLabel.setVisible(false);
                deltaTTextField.setVisible(false);
                changeTheSpeedButton.setVisible(false);
                numberOfHairdressersLabel.setVisible(true);
                numberOfHairdressersTextField.setVisible(true);
                simulator.setTypeOfSimulation(TypeOfSimulation.MAX_SPEED);
                simulator.setMaxSimSpeed();
            }
        });
        slowSimulationRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NumberOfReplicationsLabel.setVisible(false);
                numberOfReplicationsTextField.setVisible(false);
                lengthOfSleepLabel.setVisible(true);
                lengthOfSleepTextField.setVisible(true);
                deltaTLabel.setVisible(true);
                deltaTTextField.setVisible(true);
                changeTheSpeedButton.setVisible(true);
                numberOfHairdressersLabel.setVisible(true);
                numberOfHairdressersTextField.setVisible(true);
                simulator.setTypeOfSimulation(TypeOfSimulation.OBSERVE);
            }
        });
        pauseSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulator.isRunning()){
                    simulator.pauseSimulation();
                    isPausedLabel.setVisible(true);
                }
            }
        });
        changeTheSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulator.setSimSpeed(
                        Double.parseDouble(deltaTTextField.getText()),
                        Double.parseDouble(lengthOfSleepTextField.getText()));
            }
        });
        chartOutputRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NumberOfReplicationsLabel.setVisible(true);
                numberOfReplicationsTextField.setVisible(true);
                lengthOfSleepLabel.setVisible(false);
                lengthOfSleepTextField.setVisible(false);
                deltaTLabel.setVisible(false);
                deltaTTextField.setVisible(false);
                changeTheSpeedButton.setVisible(false);
                numberOfHairdressersLabel.setVisible(false);
                numberOfHairdressersTextField.setVisible(false);
                simulator.setTypeOfSimulation(TypeOfSimulation.MAX_WITH_CHART);
                simulator.setMaxSimSpeed();
            }
        });
        parkingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int column = parkingTable.getSelectedColumn();
                int row = parkingTable.getSelectedRow();
                if (parkingTable.getValueAt(row, column) == " "){
                    boolean result = simulator.agentParking().reserveParkingSpotByUser(row,column,true);
                    if (result){
                        parkingTable.setValueAt("R", row, column);
                    }
                }else {
                    boolean result = simulator.agentParking().reserveParkingSpotByUser(row,column,false);
                    if (result){
                        parkingTable.setValueAt(" ", row, column);
                    }
                }
                parkingTable.clearSelection();
            }
        });
    }

    @Override
    public void simStateChanged(Simulation simulation, SimState simState) {
        switch (simState){
            case running:{
                break;
            }
            case stopped:{
                if (((MySimulation) simulation).getTypeOfSimulation() == TypeOfSimulation.MAX_WITH_CHART){
                    if (currentRun == 9){
                        fastSimulationRadioButton.setEnabled(true);
                        slowSimulationRadioButton.setEnabled(true);
                        chartOutputRadioButton.setEnabled(true);
                    }
                }else {
                    fastSimulationRadioButton.setEnabled(true);
                    slowSimulationRadioButton.setEnabled(true);
                    chartOutputRadioButton.setEnabled(true);
                }
                break;
            }
            case paused:{
                break;
            }
            case replicationStopped:{
                refresh(simulation);
            }
        }
    }

    @Override
    public void refresh(Simulation simulator) {
        MySimulation sim = (MySimulation) simulator;
        TypeOfSimulation typeOfSimulation = sim.getTypeOfSimulation();
        if (typeOfSimulation == TypeOfSimulation.OBSERVE){
            //toto vykresluj iba ak je zapnute sledovanie simulacie
            simulationTimeLabel.setText(sim.getCurrentTime());

            String statesOfSystem = getStatesOfSimulation(sim);
            //aby vykreslovalo len ked nastala zmena nejakej hondoty
            if (!statesOfSystem.equals(lastStatesValues)) {
                statesOfSystemTextPane.setText(statesOfSystem);
                lastStatesValues = statesOfSystemTextPane.getText();
            }

            showParkingSpots(sim);

            String stats = getStats(sim);
            if (!stats.equals(lastStats)){
                statisticsTextPane.setText(stats);
                lastStats = statisticsTextPane.getText();
            }
        }else if (typeOfSimulation == TypeOfSimulation.MAX_SPEED){
            if (sim.currentReplication() > 10){
                String stats = getGlobalStatsAndForCurrentReplication(sim);
                statisticsTextPane.setText(stats);
            }
        }else {
            //aj s grafom
            if (sim.currentReplication() == (sim.replicationCount() - 1)){
                String currentText = statisticsTextPane.getText();
                String newData = currentText + "Cela doba: \n  Priemerna dlzka radu: " +
                        (Math.round(sim.getLengthOfQueueReception().mean() * 100.0) / 100.0) +
                        "\n  Pocet kaderniciek: " + sim.getNumberOfHairstylists() + "\n";
                newData += "Do 17:00: \n  Priemerna dlzka radu: " +
                        (Math.round(sim.getLengthOfQueueReceptionUntil17().mean() * 100.0) / 100.0)+
                        "\n  Pocet kaderniciek: " + sim.getNumberOfHairstylists() + "\n";
                statisticsTextPane.setText(newData);
                addToChart(sim.getLengthOfQueueReception().mean(),sim.getNumberOfHairstylists());
                addToChartUntil17(sim.getLengthOfQueueReceptionUntil17().mean(),sim.getNumberOfHairstylists());
            }
        }
    }

    public String getStats(Simulation simulator){
        MySimulation sim = (MySimulation) simulator;
        String result = "Priemerny cas zakaznika v prevadzke: "
                + getTotalTimeFromSeconds(sim.agentBeautySalon().getTimeInSystem().mean())
                + "\n  " + sim.agentBeautySalon().getNumberOfServedCustomers() + " obsluzenych zakaznikov";
        result += "\nPriemerny cas cakania v rade na zadanie objednavky: "
                + getTotalTimeFromSeconds(sim.agentReceptionist().getWaitTimeForPlacingOrder().mean()) + "\n  "
                + sim.agentReceptionist().getNumberOfStartedOrders()+ " zadanych objednavok";

        result += "\nPriemerny pocet v rade pred recepciou: "
                + Math.round(sim.agentReceptionist().getLengthOfReceptionQueue().mean() * 100.0) / 100.0
                + "\nPriemerny pocet v rade pred licenim: "
                + Math.round(sim.agentMakeUpArtist().getMakeupWaitingQueue().lengthStatistic().mean() * 100.0) / 100.0
                + "\nPriemerny pocet v rade pred ucesom: "
                + Math.round(sim.agentHairstylist().getHairstyleWaitingQueue().lengthStatistic().mean() * 100.0) / 100.0;
        return result;
    }

    public String getStatesOfSimulation(Simulation simulator){
        MySimulation sim = (MySimulation) simulator;
        String result = "";
        result += "Pocet ludi v radoch: -\n  Rad pred recepciou: "
                + sim.agentReceptionist().getReceptionWaitingQueue().size() + " \n  " +
                "Rad pred upravou ucesu: " + sim.agentHairstylist().getHairstyleWaitingQueue().size()
                + " \n  Rad pred licenim: " + sim.agentMakeUpArtist().getMakeupWaitingQueue().size()
                + "\nPocet prichodov zakaznikov: " + sim.agentModel().getNumberOfArrivedCustomers()
                + "\nPocet zakaznikov, ktori vstupili dovnutra: " + sim.agentBeautySalon().getNumberOfArrivedCustomers()
                + "\nPocet zakaznikov, ktori nezaparkovali a odisli: "
                + sim.agentParking().getLeavingBecauseOfUnsuccessfulParking() +
                "\nPocet obsluzenych zakaznikov: " + sim.agentBeautySalon().getNumberOfServedCustomers()
                +" \nPocet odchodov po zatvaracke: " + sim.agentBeautySalon().getNumberOfLeavingCustomers()
                + "\nStavy personalu: ";
        result += "\n  Recepcia:";
        for (int i = 0; i < sim.agentReceptionist().getListOfReceptionists().size(); i++){
            Receptionist r = sim.agentReceptionist().getListOfReceptionists().get(i);
            String isWorkig = "";
            if (r.isWorking()){
                isWorkig = "Ano";
            }else {
                isWorkig = "Nie";
            }
            result += "\n    Recepcny c." + (i+1) + ":" + "\n      Odpracovany cas: " +
                    getTotalTimeFromSeconds(r.getWorkedTimeTogether()) + "\n      Pracuje: " + isWorkig;
        }
        result += "\n  Kadernicky:";
        for (int i = 0; i < sim.agentHairstylist().getListOfHairStylists().size(); i++){
            Hairstylist h = sim.agentHairstylist().getListOfHairStylists().get(i);
            String isWorkig = "";
            if (h.isWorking()){
                isWorkig = "Ano";
            }else {
                isWorkig = "Nie";
            }
            result += "\n    Kadernicka c." + (i+1) + ":" + "\n      Odpracovany cas: " +
                    getTotalTimeFromSeconds(h.getWorkedTimeTogether()) + "\n      Pracuje: " + isWorkig;
        }
        result += "\n  Kozmeticky:";
        for (int i = 0; i < sim.agentMakeUpArtist().getListOfMakeupArtists().size(); i++){
            MakeUpArtist m = sim.agentMakeUpArtist().getListOfMakeupArtists().get(i);
            String isWorkig = "";
            if (m.isWorking()){
                isWorkig = "Ano";
            }else {
                isWorkig = "Nie";
            }
            result += "\n    Kozmeticka c." + (i+1) + ":" + "\n      Odpracovany cas: " +
                    getTotalTimeFromSeconds(m.getWorkedTimeTogether()) + "\n      Pracuje: " + isWorkig;
        }
        result += "\nStavy zakaznikov v systeme: ";
        for (int i = 0; i < sim.agentModel().getListOfCustomersInSystem().size(); i++){
            Customer c = sim.agentModel().getListOfCustomersInSystem().get(i);
            if (c.getArriveTime() < sim.currentTime()){
                String wantedServices = "";
                if (c.isHairstyle()){
                    wantedServices += "Uces ";
                }
                if (c.isCleaning()){
                    wantedServices += "Cistenie pleti ";
                }
                if (c.isMakeup()){
                    wantedServices += "Licenie";
                }
                result += "\n  Zakaznik: \n    Cas prichodu: " + sim.convertTimeOfSystem(c.getArriveTime());
                if (c.getFinalParkingNumber() != -1){
                    result += "\n    Parkovacie miesto: " + c.getFinalParkingLine() + (c.getFinalParkingNumber()+1)
                            + "\n    Spokojnost s parkovanim: " + c.getCurrentCustomerSuccessRateValue();
                }
                result +=  "\n    Aktualne miesto v systeme: " +
                        sim.convertCurrentPosition(c.getCurrentPosition());
                if ((c.getCurrentPosition() == CurrentPosition.PARKING)
                        || (c.getCurrentPosition() == CurrentPosition.LEAVING)){
                    result += "\n    Miesto na parkovisku: "
                            + sim.convertCurrentParkingPosition(c.getCurrentParkingPosition());
                }
                result += "\n    Momentalne ma zaujem este o sluzby: " + wantedServices;
            }
        }
        return result;
    }

    private void showParkingSpots(Simulation simulator){
        MySimulation sim = (MySimulation) simulator;
        boolean[] lineA = sim.agentParking().getArrayOfParkingSpots_A();
        boolean[] lineB = sim.agentParking().getArrayOfParkingSpots_B();
        boolean[] lineC = sim.agentParking().getArrayOfParkingSpots_C();
        boolean[] lineReservedA = sim.agentParking().getArrayOfParkingSpots_A_ReservedByUser();
        boolean[] lineReservedB = sim.agentParking().getArrayOfParkingSpots_B_ReservedByUser();
        boolean[] lineReservedC = sim.agentParking().getArrayOfParkingSpots_C_ReservedByUser();
        for (int i = 0; i < 15; i++){
            int column = 14 - i;
            int row = 0;
            boolean parkingSpot = lineA[i];
            if (parkingSpot){
                if (lineReservedA[i]){
                    parkingTable.setValueAt("R", row, column);
                }else {
                    parkingTable.setValueAt("---", row, column);
                }
            }else {
                parkingTable.setValueAt(" ", row, column);
            }
        }
        for (int i = 0; i < 15; i++){
            int column = 14 - i;
            int row = 1;
            boolean parkingSpot = lineB[i];
            if (parkingSpot){
                if (lineReservedB[i]){
                    parkingTable.setValueAt("R", row, column);
                }else {
                    parkingTable.setValueAt("---", row, column);
                }
            }else {
                parkingTable.setValueAt(" ", row, column);
            }
        }
        for (int i = 0; i < 15; i++){
            int column = 14 - i;
            int row = 2;
            boolean parkingSpot = lineC[i];
            if (parkingSpot){
                if (lineReservedC[i]){
                    parkingTable.setValueAt("R", row, column);
                }else {
                    parkingTable.setValueAt("---", row, column);
                }
            }else {
                parkingTable.setValueAt(" ", row, column);
            }
        }
        double arrivedOnCar = sim.agentModel().getArrivedOnCar();
        double unsuccessfulParking = sim.agentParking().getLeavingBecauseOfUnsuccessfulParking();
        arrivedOnCarLabel.setText("Prislo na aute: "+ (int) arrivedOnCar);
        unsuccesfulParkingCountLabel.setText("Nezaparkovalo: " + (int) unsuccessfulParking);
        parkingSuccessRateLabel.setText("Uspesnost zaparkovania: "
                + Math.round(((arrivedOnCar - unsuccessfulParking)/arrivedOnCar)*100.0 * 100.0) / 100.0 + "%");
        parkingSuccessRateValueLabel.setText("Spokojnost so zaparkovanim: "
                + sim.agentParking().getCustomersSuccessRateValues().mean());
    }

    private String getTotalTimeFromSeconds(double pSeconds){
        int seconds = (int)pSeconds % 60;
        int minutes = ((int)pSeconds / 60) % 60;
        if (minutes == 59 && seconds == 59){
            minutes = minutes;
        }
        int hours = ((int)pSeconds / 60 / 60) % 24;
        String time = "";
        if (hours < 10){
            time += "0"+ hours + ":";
        }else {
            time += hours + ":";
        }
        if (minutes < 10){
            time += "0"+ minutes + ":";
        }else {
            time += minutes + ":";
        }
        if (seconds < 10){
            time += "0"+ seconds;
        }else {
            time += seconds;
        }
        return time;
    }

    private String getGlobalStatsAndForCurrentReplication(Simulation simulator){
        MySimulation sim = (MySimulation) simulator;
        //celkove statistiky
        String result = "Globalne statistiky: "
                + "\n  Cislo replikacie: " + sim.currentReplication()
                + "\n  Priemerne obsluzenych zakaznikov: " + Math.round(sim.getServedCustomers().mean() * 100.0) / 100.0
                + "\n  Priemerne odchodov po zatvaracke: " + Math.round(sim.getLeftAfterClosing().mean() * 100.0) / 100.0
                + "\n  Priemerne prislo na aute: " + Math.round(sim.getArrivedOnCar().mean() * 100.0) / 100.0
                + "\n  Priemerne nezaparkovalo: " + Math.round(sim.getUnsuccessfulParking().mean() * 100.0) / 100.0
                + "\n  Priemerny cas cakania v rade na zadanie objednavky: " +
                getTotalTimeFromSeconds(sim.getWaitTimeForPlacingOrder().mean())
                + "\n  Priemerna spokojnost zakaznika s parkovanim: "
                + Math.round(sim.getCustomersSuccesRates().mean() * 100.0) / 100.0
                + "\n  Priemerna uspesnost zaparkovania: "
                + Math.round(sim.getParkingSuccessRatePercentage().mean() * 100.0) / 100.0 + "%"
                + "\n"
                + "\n  Celkovo:" +
                "\n    Priemerny cas zakaznika v systeme: " + getTotalTimeFromSeconds(sim.getTimeInSystem().mean()) +
                "\n      Smerodajna odchylka: " + getTotalTimeFromSeconds(sim.getTimeInSystem().stdev()) +
                "\n      90% Interval spolahlivosti: <"
                + getTotalTimeFromSeconds(sim.getTimeInSystem().confidenceInterval_90()[0])
                + ", " + getTotalTimeFromSeconds(sim.getTimeInSystem().confidenceInterval_90()[1]) +
                ">\n    Priemerny pocet v rade pred recepciou: " +
                Math.round(sim.getLengthOfQueueReception().mean() * 100.0) / 100.0
                + "\n    Priemerny pocet v rade pred licenim: " +
                Math.round(sim.getLengthOfQueueMakeUp().mean() * 100.0) / 100.0
                + "\n    Priemerny pocet v rade pred ucesom: " +
                Math.round(sim.getLengthOfQueueHairstyle().mean() * 100.0) / 100.0;
        result += "\n  Iba do 17:00:" +
                "\n    Priemerny cas zakaznika v systeme: " +
                getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().mean()) +
                "\n      Smerodajna odchylka: " + getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().stdev())
                + "\n      90% Interval spolahlivosti: <" +
                getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().confidenceInterval_90()[0]) +
                ", " + getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().confidenceInterval_90()[1]) +
                ">\n    Priemerny pocet v rade pred recepciou: " +
                Math.round(sim.getLengthOfQueueReceptionUntil17().mean() * 100.0) / 100.0;
        //pre poslednu replikaciu
        result += "\n\nPosledna replikacia(celkovy cas): \n";
        result += "  Priemerny cas zakaznika v systeme: "
                + getTotalTimeFromSeconds(sim.agentBeautySalon().getTimeInSystem().mean())
                + "\n  " + sim.agentBeautySalon().getNumberOfServedCustomers() + " obsluzenych zakaznikov";
        result += "\n  Priemerny cas cakania v rade na zadanie objednavky: "
                + getTotalTimeFromSeconds(sim.agentReceptionist().getWaitTimeForPlacingOrder().mean())
                + "\n  " + sim.agentReceptionist().getNumberOfStartedOrders() + " zadanych objednavok";
        result += "\n  Priemerny pocet v rade pred recepciou: "
                + Math.round(sim.agentReceptionist().getLengthOfReceptionQueue().mean() * 100.0) / 100.0;
        if (sim.currentReplication() == (Integer.parseInt(numberOfReplicationsTextField.getText())-1)){
            writeResultsToFile(sim);
        }
        return result;
    }

    public void writeResultsToFile(Simulation simulator){
        MySimulation sim = (MySimulation) simulator;

        try (PrintWriter writer = new PrintWriter("results.csv")) {

            StringBuilder sb = new StringBuilder();
            //hlavicka
            sb.append("Replikacii");
            sb.append(',');
            sb.append("Nazov");
            sb.append(',');
            sb.append("Kadernicky");
            sb.append(',');
            sb.append("Kozmeticky");
            sb.append(',');
            sb.append("Recepcne");
            sb.append(',');
            sb.append("Strategia");
            sb.append(',');
            sb.append("Pocet radov");
            sb.append(',');
            sb.append("Obsluzenych");
            sb.append(',');
            sb.append("Odchodov po zatvaracke");
            sb.append(',');
            sb.append("Prislo na aute");
            sb.append(',');
            sb.append("Nezaparkovalo");
            sb.append(',');
            sb.append("Spokojnost s parkovanim");
            sb.append(',');
            sb.append("Uspesnost zaparkovania(%)");
            sb.append(',');
            sb.append("Cas cakania v rade na objednavku");
            sb.append(',');
            sb.append("Priem. cas zak. v systeme");
            sb.append(',');
            sb.append("Interval spolahlivosti");
            sb.append(',');
            sb.append("Pocet v rade pred recepciou");
            sb.append(',');
            sb.append("Pocet v rade pred licenim");
            sb.append(',');
            sb.append("Pocet v rade pred ucesom");
            sb.append(',');
            sb.append("Priem. cas zak. v systeme(17:00)");
            sb.append(',');
            sb.append("Interval spolahlivosti(17:00)");
            sb.append(',');
            sb.append("Pocet v rade pred recepciou(17:00)");
            sb.append('\n');

            //hodnoty
            sb.append(numberOfReplicationsTextField.getText());
            sb.append(',');
            sb.append("KD")
                    .append(sim.getNumberOfHairstylists())
                    .append(" KZ")
                    .append(numberOfMakeupArtistsTextField.getText())
                    .append(" RC")
                    .append(numberOfReceptionistsTextField.getText());
            sb.append(',');
            sb.append(sim.getNumberOfHairstylists());
            sb.append(',');
            sb.append(numberOfMakeupArtistsTextField.getText());
            sb.append(',');
            sb.append(numberOfReceptionistsTextField.getText());
            sb.append(',');
            sb.append(parkingStrategyCombobox.getSelectedIndex()+1);
            sb.append(',');
            sb.append(numberOfParkingLinesComboBox.getSelectedIndex()+1);
            sb.append(',');
            sb.append(Math.round(sim.getServedCustomers().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(Math.round(sim.getLeftAfterClosing().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(Math.round(sim.getArrivedOnCar().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(Math.round(sim.getUnsuccessfulParking().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(Math.round(sim.getCustomersSuccesRates().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(Math.round(sim.getParkingSuccessRatePercentage().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(getTotalTimeFromSeconds(sim.getWaitTimeForPlacingOrder().mean()));
            sb.append(',');
            sb.append(getTotalTimeFromSeconds(sim.getTimeInSystem().mean()));
            sb.append(',');
            sb.append("<")
                    .append(getTotalTimeFromSeconds(sim.getTimeInSystem().confidenceInterval_90()[0]))
                    .append("- ")
                    .append(getTotalTimeFromSeconds(sim.getTimeInSystem().confidenceInterval_90()[1]))
                    .append(">");
            sb.append(',');
            sb.append(Math.round(sim.getLengthOfQueueReception().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(Math.round(sim.getLengthOfQueueMakeUp().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(Math.round(sim.getLengthOfQueueHairstyle().mean() * 100.0) / 100.0);
            sb.append(',');
            sb.append(getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().mean()));
            sb.append(',');
            sb.append("<")
                    .append(getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().confidenceInterval_90()[0]))
                    .append("- ")
                    .append(getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().confidenceInterval_90()[1]))
                    .append(">");
            sb.append(',');
            sb.append(Math.round(sim.getLengthOfQueueReceptionUntil17().mean() * 100.0) / 100.0);
            sb.append('\n');

            writer.write(sb.toString());

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createDatasets(){
        datasetLineChart = new DefaultXYDataset();
        lineChartXYSeries = new XYSeries("waitingQueue");
        lineChartXYSeriesUntil17 = new XYSeries("waitingQueueUntil17");
        lineChart = ChartFactory.createXYLineChart(
                "Priemerne pocty cakajucich v rade na recepcii",
                "Pocet kaderniciek",
                "Priemerny pocet cakajucich v rade",
                datasetLineChart,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        //aby spravne skalovalo a nezahrnovalo stale 0
        NumberAxis yAxis = (NumberAxis) lineChart.getXYPlot().getRangeAxis();
        yAxis.setAutoRangeIncludesZero(false);

        ChartPanel panel = new ChartPanel(lineChart);
        chartPanel.removeAll();
        chartPanel.add(panel, BorderLayout.CENTER);
    }

    public void addToChart(double averageNumberOfCustomersInReceptionQueue, int numberOfHairstylists){
        lineChartXYSeries.add(numberOfHairstylists, averageNumberOfCustomersInReceptionQueue);
        datasetLineChart.addSeries(lineChartXYSeries.getKey(), lineChartXYSeries.toArray());
    }

    public void addToChartUntil17(double averageNumberOfCustomersInReceptionQueue, int numberOfHairstylists){
        lineChartXYSeriesUntil17.add(numberOfHairstylists, averageNumberOfCustomersInReceptionQueue);
        datasetLineChart.addSeries(lineChartXYSeriesUntil17.getKey(), lineChartXYSeriesUntil17.toArray());
    }

    private void setParkingStrategy(Simulation simulator){
        MySimulation sim = (MySimulation) simulator;
        switch (parkingStrategyCombobox.getSelectedIndex()){
            case 0:{
                sim.setChosenStrategy(ParkingStrategy.FIRST_STRATEGY);
                break;
            }
            case 1:{
                sim.setChosenStrategy(ParkingStrategy.SECOND_STRATEGY);
                break;
            }
            case 2:{
                sim.setChosenStrategy(ParkingStrategy.THIRD_STRATEGY);
                break;
            }
        }
    }

    private void setNumberOfBuiltParkingLines(Simulation simulator){
        MySimulation sim = (MySimulation) simulator;
        switch (numberOfParkingLinesComboBox.getSelectedIndex()){
            case 0:{
                sim.setNumberOfBuiltParkingLines(1);
                break;
            }
            case 1:{
                sim.setNumberOfBuiltParkingLines(2);
                break;
            }
            case 2:{
                sim.setNumberOfBuiltParkingLines(3);
                break;
            }
        }
    }

    private void createParkingTable(){
        parkingSpots = new String[][]{
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", "", " ", " ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}
        };
        parkingTable.setModel(new DefaultTableModel(
                parkingSpots,
                new String[]{"15","14","13","12","11","10","9","8","7","6","5","4","3","2","1"}
        ));
        TableColumnModel columnModel = parkingTable.getColumnModel();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 15; i++){
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void createComboBoxes(){
        numberOfParkingLinesComboBox.setModel(new DefaultComboBoxModel(new String[]{"1","2","3"}));
        parkingStrategyCombobox.setModel(new DefaultComboBoxModel(new String[]{"1 strategia", "2. strategia", "3. strategia"}));
    }

    private void setDeafultText(){
        //unsuccesfulParkingCountLabel.setText("-");
        isPausedLabel.setVisible(false);
        simulationTimeLabel.setText(simulator.getCurrentTime());
        numberOfMakeupArtistsTextField.setText("1");
        numberOfReceptionistsTextField.setText("1");
        numberOfHairdressersTextField.setText("1");
        lengthOfSleepTextField.setText("0.05");
        deltaTTextField.setText("50");
        numberOfReplicationsTextField.setText("10000");
        String text = "Pocet ludi v radoch: -\n  Rad pred recepciou: -\n  Rad pred upravou ucesu: -" +
                "\n  Rad pred licenim: -\n  Rad pred platenim: - \nPocet prichodov zakaznikov: -" +
                "\nPocet obsluzenych zakaznikov: -\nStavy personalu: - \nStavy zakaznikov v systeme: -";
        statesOfSystemTextPane.setText(text);
        statisticsTextPane.setText("Statistiky");
        lastStatesValues = statesOfSystemTextPane.getText();
        lastStats = statisticsTextPane.getText();
    }
}
