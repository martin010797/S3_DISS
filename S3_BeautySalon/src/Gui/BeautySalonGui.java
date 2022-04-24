package Gui;

import Simulation.BeautySalonSimulator;
import Simulation.Events.Event;
import Simulation.Simulator;
import Simulation.TypeOfSimulation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

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
    private BeautySalonSimulator simulator;
    private String lastStatesValues;
    private String lastCalendar;
    private String lastStats;

    public BeautySalonGui() {
        frame = new JFrame("Beauty salon");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        simulator = new BeautySalonSimulator(1,100000000);
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
                if (simulator.getTypeOfSimulation() == TypeOfSimulation.MAX_SPEED){
                    simulator.setNumberOfReplications(Integer.parseInt(numberOfReplicationsTextField.getText()));
                }
                if (simulator.getTypeOfSimulation() == TypeOfSimulation.OBSERVE){
                    simulator.setNumberOfReplications(1);
                }
                simulator.setDeltaT(Integer.parseInt(deltaTTextField.getText()));
                simulator.setSleepLength(Integer.parseInt(lengthOfSleepTextField.getText()));
                fastSimulationRadioButton.setEnabled(false);
                slowSimulationRadioButton.setEnabled(false);
                chartOutputRadioButton.setEnabled(false);
                isPausedLabel.setVisible(false);
                simulator.setNumberOfMakeupArtists(Integer.parseInt(numberOfMakeupArtistsTextField.getText()));
                simulator.setNumberOfReceptionists(Integer.parseInt(numberOfReceptionistsTextField.getText()));
                if (simulator.getTypeOfSimulation() == TypeOfSimulation.MAX_WITH_CHART){
                    simulator.setNumberOfReplications(Integer.parseInt(numberOfReplicationsTextField.getText()));
                    statisticsTextPane.setText("");
                    createDatasets();
                    frame.setVisible(true);
                    simulator.simulate(10);
                }else {
                    simulator.setNumberOfHairstylists(Integer.parseInt(numberOfHairdressersTextField.getText()));
                    simulator.simulate();
                }
            }
        });
        stopSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulator.stopSimulation();
                fastSimulationRadioButton.setEnabled(true);
                slowSimulationRadioButton.setEnabled(true);
                chartOutputRadioButton.setEnabled(true);
                isPausedLabel.setVisible(false);
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
                if(simulator.setPaused(true)){
                    isPausedLabel.setVisible(true);
                }
            }
        });
        changeTheSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulator.setDeltaT(Integer.parseInt(deltaTTextField.getText()));
                simulator.setSleepLength(Integer.parseInt(lengthOfSleepTextField.getText()));
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
            }
        });
    }

    @Override
    public void refresh(Simulator simulator) {
        BeautySalonSimulator sim = (BeautySalonSimulator) simulator;
        TypeOfSimulation typeOfSimulation = sim.getTypeOfSimulation();
        if (typeOfSimulation == TypeOfSimulation.OBSERVE){
            //toto vykresluj iba ak je zapnute sledovanie simulacie
            simulationTimeLabel.setText(sim.getCurrentTime());

            String statesOfSystem = sim.getStatesOfSimulation();
            //aby vykreslovalo len ked nastala zmena nejakej hondoty
            if (!statesOfSystem.equals(lastStatesValues)) {
                statesOfSystemTextPane.setText(statesOfSystem);
                lastStatesValues = statesOfSystemTextPane.getText();
            }
            String calendar = sim.getCalendar();
            if (!calendar.equals(lastCalendar)){
                calendarTextPane.setText(calendar);
                lastCalendar = calendarTextPane.getText();
            }

            //v spracovanych nezobrazuje systemove udalosti
            if (sim.getLastProcessedEvent() != null){
                Event e = sim.getLastProcessedEvent();
                lastProcessedEventLabel.setText(sim.convertTimeOfSystem(e.getTime()) + "  " + e.getNameOfTheEvent());
            }

            String stats = sim.getStats();
            if (!stats.equals(lastStats)){
                statisticsTextPane.setText(stats);
                lastStats = statisticsTextPane.getText();
            }

            if (sim.isFinished()){
                fastSimulationRadioButton.setEnabled(true);
                slowSimulationRadioButton.setEnabled(true);
                chartOutputRadioButton.setEnabled(true);
            }
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

    private void setDeafultText(){
        lastProcessedEventLabel.setText("-");
        calendarTextPane.setText("");
        isPausedLabel.setVisible(false);
        simulationTimeLabel.setText(simulator.getCurrentTime());
        numberOfMakeupArtistsTextField.setText("1");
        numberOfReceptionistsTextField.setText("1");
        numberOfHairdressersTextField.setText("1");
        lengthOfSleepTextField.setText("400");
        deltaTTextField.setText("400");
        numberOfReplicationsTextField.setText("100000");
        String text = "Pocet ludi v radoch: -\n  Rad pred recepciou: -\n  Rad pred upravou ucesu: -" +
                "\n  Rad pred licenim: -\n  Rad pred platenim: - \nPocet prichodov zakaznikov: -" +
                "\nPocet obsluzenych zakaznikov: -\nStavy personalu: - \nStavy zakaznikov v systeme: -";
        statesOfSystemTextPane.setText(text);
        statisticsTextPane.setText("Statistiky");
        lastStatesValues = statesOfSystemTextPane.getText();
        lastCalendar = calendarTextPane.getText();
        lastStats = statisticsTextPane.getText();
    }
}
