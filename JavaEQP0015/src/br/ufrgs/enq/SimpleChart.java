package br.ufrgs.enq;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import br.com.vrtech.iise.IISEClient;
import br.com.vrtech.iise.ThermoServer;

/**
 * A simple example for creating XY charts using JFreeChart.
 * 
 * <p>You can get JFreeChart at http://www.jfree.org/jfreechart.
 *  
 * @author rafael
 *
 */
@SuppressWarnings("serial")
public class SimpleChart extends JFrame {

	public SimpleChart() {
		super("Simple Chart");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container content = getContentPane();

		// A dataset will contain all data for the chart
		XYSeriesCollection dataset = new XYSeriesCollection();

		// With the ChartFactory we create an already configured chart.
		JFreeChart chart = ChartFactory.createXYLineChart("ELV da mistura Etano(1)/Propeno(2) a 100 F", "Fração molar de etano (x1/y1)", "Pressão do sistema (psi)",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		// Add the chart inside of a ChartPanel and pack()
		content.add(new ChartPanel(chart), BorderLayout.CENTER);
		pack();

		// Create the series (the lines) and add to the dataset
		XYSeries series0 = new XYSeries("Lei de Raoult", false);
		XYSeries series1 = new XYSeries("", false);
		XYSeries series2 = new XYSeries("Lei de Raoult Modificada", false);
		XYSeries series3 = new XYSeries("", false);
		XYSeries series4 = new XYSeries("Dados Experimentais", false);
		XYSeries series5 = new XYSeries("", false);
		dataset.addSeries(series0);
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		dataset.addSeries(series4);
		dataset.addSeries(series5);
		
		double[] Pexp = {227, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 722};
		double[] x1exp = {0.0, 0.048, 0.157, 0.26, 0.361, 0.461, 0.554, 0.643, 0.727, 0.809, 0.894, 0.93}; 
		double[] y1exp = {0.0, 0.118, 0.317, 0.447, 0.543, 0.626, 0.697, 0.759, 0.813, 0.863, 0.912, 0.93};

		// adding the data to the series
		//		int N = 200;
		//		for(int i=0; i<N; i++){
		//			double t = 2*Math.PI*i/N;
		//			series0.add(t, Math.sin(t));
		//			series1.add(t, Math.cos(t));
		//		}

		try{
			// Connect to a remote host (not needed when using the desktop version)
			String host = "lvpp-srv02.nuvem.ufrgs.br";
			//			String user = "fabricioferrarini@gmail.com";
			String user = "fabricioferrarini@gmail.com";
			String key = "e245df41-15f0-4547-8b4e-804456e0d0b4";
			IISEClient.connect(host, user, key);

			// Get a reference for the iiSE thermodynamics server
			ThermoServer thermo = IISEClient.getThermo();

			// Lets use the first mixture "slot" and two phase "slots".
			// There is no limit (other than the machine memory) for the number
			// of "slots" for mixtures and phases.
			// Most applications will use only 1 or 2 slots.
			int mix = 1;
			int liq = 1;
			int vap = 2;

			// Configure the mixture with the desired components.
			String comps[] = {"ethane", "propylene"};
			thermo.configureMixture(mix, comps);
			thermo.configurePhase(liq, ThermoServer.Liquid|ThermoServer.ActPureFugPvapIdeal, mix, "PR", null, "UNIFAC(Do)");

			// Set the state of the phases
			double T = 100.0;   String Tunit = "F";   // Temperature in deg C
			double P = 1.0;     String Punit = "psi"; // Pressure in bar

			//Calculo de Psat
			double Psat[] = thermo.getPureProperty(mix, ThermoServer.SaturationPressure, Punit, T, Tunit);


			System.out.println("x1"+" "+"y1"+" "+"Pbolha1"+" "+"Pbolha2");
			//Lei de Raoult
			double x1[] = new double [21];
			double Pbolha[] = new double [x1.length];
			double y1[] = new double [x1.length];
			double actliq[] = new double [x1.length];
			double Pbolha2[] = new double [x1.length];
			for (int i = 0; i < x1.length; i++) {
				double i2 = i/(x1.length-1.);
				x1[i] = i2;
				double x[] = {x1[i], 1-x1[i]};
				Pbolha[i]=x[0]*Psat[0] + x[1]*Psat[1];
				y1[i] = (x[0]*Psat[0])/Pbolha[i];
				double i3 = y1[i];
				thermo.setPhaseState(liq, T, P, x, Tunit, Punit);


				actliq = thermo.getPhaseProperty(liq, ThermoServer.ActivityCoefficient, "");
				Pbolha2[i]=x1[i]*Psat[0]*actliq[0] + (1-x1[i])*Psat[1]*actliq[1];

				System.out.println(x1[i]+"\t"+y1[i]+"\t"+Pbolha[i]+"\t"+Pbolha2[i]);

				series0.add(i2, Pbolha[i]);
				series1.add(i3, Pbolha[i]);
				series2.add(i2, Pbolha2[i]);
				series3.add(i3, Pbolha2[i]);
				
			}

		for (int i = 0; i < x1exp.length; i++) {
			series4.add(x1exp[i], Pexp[i]);
			series5.add(y1exp[i], Pexp[i]);
		}
			
		}


		catch (Exception e) {
			e.printStackTrace();
		}


		// Some additional configurations (comment the following lines to stay with the default
		// configuration)
		XYPlot plot = (XYPlot)chart.getPlot();
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		// Changing the line width of series0
//		r.setSeriesStroke(0, new BasicStroke(2.5f));
		
		// Only markers for series1
		r.setSeriesLinesVisible(4, false);
		r.setSeriesShapesVisible(4, true);
		r.setSeriesShapesFilled(4, false);
		
		r.setSeriesLinesVisible(5, false);
		r.setSeriesShapesVisible(5, true);
		r.setSeriesShapesFilled(5, false);
		
		// adjusting the plot range for the "x" axis
		plot.getDomainAxis().setAutoRange(false);
		plot.getDomainAxis().setRange(new Range(0.0, 1.0));
		
		// adjusting the plot range for the "y" axis
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(200, 800);
	}

	public static void main(String[] args) {
		SimpleChart chart = new SimpleChart();

		chart.setLocationRelativeTo(null);
		chart.setVisible(true);
	}
}