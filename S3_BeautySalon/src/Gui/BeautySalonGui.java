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
import simulation.Participants.Customer;
import simulation.Participants.Hairstylist;
import simulation.Participants.MakeUpArtist;
import simulation.Participants.Receptionist;
import simulation.TypeOfSimulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JLabel calendarLabel;
    private JLabel lastProcesseedEventTextLabel;
    private JLabel lastProcessedEventLabel;
    private JTextPane calendarTextPane;
    private JRadioButton chartOutputRadioButton;

    private DefaultXYDataset datasetLineChart;
    private XYSeries lineChartXYSeries;
    private XYSeries lineChartXYSeriesUntil17;
    private JFreeChart lineChart;
    private MySimulation simulator;
    private String lastStatesValues;
    //private String lastCalendar;
    private String lastStats;

    public BeautySalonGui() {
        frame = new JFrame("Beauty salon");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        /*simulator = new BeautySalonSimulator(1,100000000);
        simulator.setTypeOfSimulation(TypeOfSimulation.OBSERVE);
        simulator.setDeltaT(400);
        simulator.setSleepLength(400);
        simulator.registerDelegate(this);*/
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
        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!simulator.isRunning() && !simulator.isPaused()){
                    //simulator.setDeltaT(Integer.parseInt(deltaTTextField.getText()));
                    //simulator.setSleepLength(Integer.parseInt(lengthOfSleepTextField.getText()));
                    fastSimulationRadioButton.setEnabled(false);
                    slowSimulationRadioButton.setEnabled(false);
                    chartOutputRadioButton.setEnabled(false);
                    isPausedLabel.setVisible(false);
                    simulator.setNumberOfMakeupArtists(Integer.parseInt(numberOfMakeupArtistsTextField.getText()));
                    simulator.setNumberOfReceptionists(Integer.parseInt(numberOfReceptionistsTextField.getText()));
                    if (simulator.getTypeOfSimulation() == TypeOfSimulation.MAX_WITH_CHART){
                        //simulator.setNumberOfReplications(Integer.parseInt(numberOfReplicationsTextField.getText()));
                        statisticsTextPane.setText("");
                        createDatasets();
                        frame.setVisible(true);
                        //TODO
                        //Vyriesit ako spustat desat krat kvoli grafu
                        //simulator.simulate(10);
                    }else {
                        simulator.setNumberOfHairstylists(Integer.parseInt(numberOfHairdressersTextField.getText()));
                        //neviem ako s uknocenim simulacneho behu este. Ci sa deafultne nastavi ovela dlhsi beh a pauzne
                        // sa niekde v agentoch ked bude cas vacsi ako 17:00 a len sa dobehnu zakaznici ktori su v systeme
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
                /*simulator.setSimSpeed(
                        Double.parseDouble(deltaTTextField.getText()),
                        Double.parseDouble(lengthOfSleepTextField.getText()));*/
            }
        });
        pauseSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulator.isRunning()){
                    simulator.pauseSimulation();
                    isPausedLabel.setVisible(true);
                }
                //TODO
                /*if(simulator.setPaused(true)){
                    isPausedLabel.setVisible(true);
                }*/
            }
        });
        changeTheSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //simulator.setDeltaT(Integer.parseInt(deltaTTextField.getText()));
                //simulator.setSleepLength(Integer.parseInt(lengthOfSleepTextField.getText()));
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
    }

    @Override
    public void simStateChanged(Simulation simulation, SimState simState) {
        switch (simState){
            case running:{
                break;
            }
            case stopped:{
                fastSimulationRadioButton.setEnabled(true);
                slowSimulationRadioButton.setEnabled(true);
                chartOutputRadioButton.setEnabled(true);
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

            String stats = getStats(sim);
            if (!stats.equals(lastStats)){
                statisticsTextPane.setText(stats);
                lastStats = statisticsTextPane.getText();
            }

           /* if (sim.isFinished()){
                fastSimulationRadioButton.setEnabled(true);
                slowSimulationRadioButton.setEnabled(true);
                chartOutputRadioButton.setEnabled(true);
            }*/
        }else if (typeOfSimulation == TypeOfSimulation.MAX_SPEED){
            if (sim.currentReplication() > 10){
                String stats = getGlobalStatsAndForCurrentReplication(sim);
                statisticsTextPane.setText(stats);
            }
        }else {
            //aj s grafom
        }
        //TODO
        /*BeautySalonSimulator sim = (BeautySalonSimulator) simulator;
        TypeOfSimulation typeOfSimulation = sim.getTypeOfSimulation();
        if (typeOfSimulation == TypeOfSimulation.OBSERVE){
        }else if (typeOfSimulation == TypeOfSimulation.MAX_SPEED){
            String stats = sim.getGlobalStatsAndForCurrentReplication();
            //if (!stats.equals(lastStats)){
                statisticsTextPane.setText(stats);
                //lastStats = statisticsTextPane.getText();
            //}
            if (sim.isFinished()){
                fastSimulationRadioButton.setEnabled(true);
                slowSimulationRadioButton.setEnabled(true);
                chartOutputRadioButton.setEnabled(true);
            }
        }else {
            //aj s grafom
            String currentText = statisticsTextPane.getText();
            String newData = currentText + "Cela doba: \n  Priemerna dlzka radu: " +
                    (Math.round(sim.getGlobalAverageLengthOfReceptionQueue() * 100.0) / 100.0) +
                    "\n  Pocet kaderniciek: " + sim.getNumberOfHairstylists() + "\n";
            newData += "Do 17:00: \n  Priemerna dlzka radu: " +
                    (Math.round(sim.getGlobalUntil17AverageLengthOfReceptionQueue() * 100.0) / 100.0)+
                    "\n  Pocet kaderniciek: " + sim.getNumberOfHairstylists() + "\n";
            statisticsTextPane.setText(newData);
            addToChart(sim.getGlobalAverageLengthOfReceptionQueue(),sim.getNumberOfHairstylists());
            addToChartUntil17(sim.getGlobalUntil17AverageLengthOfReceptionQueue(),sim.getNumberOfHairstylists());
            if (sim.isFinished()){
                fastSimulationRadioButton.setEnabled(true);
                slowSimulationRadioButton.setEnabled(true);
                chartOutputRadioButton.setEnabled(true);
            }
        }*/
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
                + Math.round(sim.agentReceptionist().getLengthOfReceptionQueue().mean() * 100.0) / 100.0;
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
                + "\nPocet zakaznikov, ktori vstupili dovnutra: " + sim.agentBeautySalon().getNumberOfArrivedCustomers() +
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
                result += "\n  Zakaznik: \n    Cas prichodu: " + sim.convertTimeOfSystem(c.getArriveTime()) +
                        "\n    Aktualne miesto v systeme: " +
                        sim.convertCurrentPosition(c.getCurrentPosition()) +
                        "\n    Momentalne ma zaujem este o sluzby: " + wantedServices;
            }
        }
        return result;
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
        String result = "Globalne statistiky: \n  Celkovo: \n    Cislo replikacie: " + sim.currentReplication() +
                "\n    Priemerny cas zakaznika v systeme: " + getTotalTimeFromSeconds(sim.getTimeInSystem().mean()) +
                "\n      Smerodajna odchylka: " + getTotalTimeFromSeconds(sim.getTimeInSystem().stdev()) +
                "\n      90% Interval spolahlivosti: <"
                + getTotalTimeFromSeconds(sim.getTimeInSystem().confidenceInterval_90()[0])
                + ", " + getTotalTimeFromSeconds(sim.getTimeInSystem().confidenceInterval_90()[1]) +
                ">\n    Priemerny pocet v rade pred recepciou: " +
                Math.round(sim.getLengthOfQueueReception().mean() * 100.0) / 100.0 +
                "\n    Priemerny cas cakania v rade na zadanie objednavky: " +
                getTotalTimeFromSeconds(sim.getWaitTimeForPlacingOrder().mean());
        result += "\n  Iba do 17:00: \n    Cislo replikacie: " + sim.currentReplication() +
                "\n    Priemerny cas zakaznika v systeme: " +
                getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().mean()) +
                "\n      Smerodajna odchylka: " + getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().stdev())
                + "\n      90% Interval spolahlivosti: <" +
                getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().confidenceInterval_90()[0]) +
                ", " + getTotalTimeFromSeconds(sim.getTimeInSystemUntil17().confidenceInterval_90()[1]) +
                ">\n    Priemerny pocet v rade pred recepciou(ESTE NEFUNKCNE): " +
                Math.round(sim.getLengthOfQueueReceptionUntil17().mean() * 100.0) / 100.0 +
                "\n    Priemerny cas cakania v rade na zadanie objednavky: " +
                getTotalTimeFromSeconds(sim.getWaitTimeForPlacingOrder().mean());
        //pre poslednu replikaciu
        result += "\nPosledna replikacia(celkovy cas): \n";
        result += "  Priemerny cas zakaznika v systeme: "
                + getTotalTimeFromSeconds(sim.agentBeautySalon().getTimeInSystem().mean())
                + "\n  " + sim.agentBeautySalon().getNumberOfServedCustomers() + " obsluzenych zakaznikov";
        result += "\n  Priemerny cas cakania v rade na zadanie objednavky: "
                + getTotalTimeFromSeconds(sim.agentReceptionist().getWaitTimeForPlacingOrder().mean())
                + "\n  " + sim.agentReceptionist().getNumberOfStartedOrders() + " zadanych objednavok";
        result += "\n  Priemerny pocet v rade pred recepciou: "
                + Math.round(sim.agentReceptionist().getLengthOfReceptionQueue().mean() * 100.0) / 100.0;
        return result;
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

    private void setDeafultText(){
        lastProcessedEventLabel.setText("-");
        calendarTextPane.setText("");
        isPausedLabel.setVisible(false);
        simulationTimeLabel.setText(simulator.getCurrentTime());
        numberOfMakeupArtistsTextField.setText("1");
        numberOfReceptionistsTextField.setText("1");
        numberOfHairdressersTextField.setText("1");
        lengthOfSleepTextField.setText("0.05");
        deltaTTextField.setText("50");
        numberOfReplicationsTextField.setText("100000");
        String text = "Pocet ludi v radoch: -\n  Rad pred recepciou: -\n  Rad pred upravou ucesu: -" +
                "\n  Rad pred licenim: -\n  Rad pred platenim: - \nPocet prichodov zakaznikov: -" +
                "\nPocet obsluzenych zakaznikov: -\nStavy personalu: - \nStavy zakaznikov v systeme: -";
        statesOfSystemTextPane.setText(text);
        statisticsTextPane.setText("Statistiky");
        lastStatesValues = statesOfSystemTextPane.getText();
        //lastCalendar = calendarTextPane.getText();
        lastStats = statisticsTextPane.getText();
    }
}
