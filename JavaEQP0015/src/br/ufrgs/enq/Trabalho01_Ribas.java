package br.ufrgs.enq;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

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

import org.jfree.util.ShapeUtilities;

import br.com.vrtech.iise.IISEClient;
import br.com.vrtech.iise.ThermoServer;

@SuppressWarnings("serial")
public class Trabalho01_Ribas extends JFrame {

	public Trabalho01_Ribas() {

		try {
			// Connect to a remote host (not needed when using the desktop version)
			String host = "lvpp-srv02.nuvem.ufrgs.br";
			// String user = "fabricioferrarini@gmail.com";
			String user = "fabricioferrarini@gmail.com";
			String key = "e245df41-15f0-4547-8b4e-804456e0d0b4";
			IISEClient.connect(host, user, key);

			// Get a reference for the iiSE thermodynamics server
			ThermoServer thermo = IISEClient.getThermo();

			// Lets use the first mixture "slot" and two phase "slots".
			// There is no limit (other than the machine memory) for the number of "slots" for mixtures and phases.
			// Most applications will use only 1 or 2 slots.
			int mix = 1;
			int liq = 1;
			int vap = 2;

			// Configure the mixture with the desired components.
			String comps[] = { "ethane", "propylene" };
			thermo.configureMixture(mix, comps);
			thermo.configurePhase(liq, ThermoServer.Liquid | ThermoServer.ActPureFugPvapIdeal, mix, "PR", null,
					"UNIFAC(Do)");

			// Set the state of the phases
			double T = 100;
			String Tunit = "F"; // Temperature in deg C
			double P = 1;
			String Punit = "psi"; // Pressure in bar

			// Psat
			double Psat[] = thermo.getPureProperty(mix, ThermoServer.SaturationPressure, Punit, T, Tunit);

			// Results to show in the Console
			System.out.println("x1" + " " + "y1" + " " + "Pbolha1" + " " + "x1" + " " + "y2" + " " + "Pbolha2");
			
			// New variables
			double x1[] = new double[21];
			double Pbolha[] = new double[x1.length];
			double y1[] = new double[x1.length];
			double y2[] = new double[x1.length];
			double actliq[] = new double[x1.length];
			double Pbolha2[] = new double[x1.length];

			// Frame options
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Container content = getContentPane();
			content.setLayout(new GridLayout(0,2));
			
			// A dataset will contain all data for the chart
			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeriesCollection dataset1 = new XYSeriesCollection();

			// With the ChartFactory we create an already configured chart.
			JFreeChart chart = ChartFactory.createXYLineChart("VLE Etano(1)/Propeno(2)", "x1,y1", "Pressure(psi)",
					dataset, PlotOrientation.VERTICAL, true, true, false);
			JFreeChart chart1 = ChartFactory.createXYLineChart("VLE Etano(1)/Propeno(2)", "x1", "y1",
					dataset1, PlotOrientation.VERTICAL, true, true, false);
			
			// Add the chart inside of a ChartPanel and set the location
			content.add(new ChartPanel(chart), BorderLayout.WEST);
			content.add(new ChartPanel(chart1), BorderLayout.EAST);

			// Create the series (the lines) and add to the both dataset's
			XYSeries series0 = new XYSeries("Lei de Raoult", false);
			XYSeries series1 = new XYSeries("Lei de Raoult", false);
			XYSeries series2 = new XYSeries("Lei de Raoult Mod", false);
			XYSeries series3 = new XYSeries("Lei de Raoult Mod", false);
			XYSeries series4 = new XYSeries("Dados Exp", false);
			XYSeries series5 = new XYSeries("Dados Exp", false);
			XYSeries series6 = new XYSeries("Dados Exp", false);
			XYSeries series7 = new XYSeries("Lei de Raoult", false);
			XYSeries series8 = new XYSeries("Lei de Raolut Mod", false);
			XYSeries series9 = new XYSeries("", false);
			dataset.addSeries(series0);
			dataset.addSeries(series1);
			dataset.addSeries(series2);
			dataset.addSeries(series3);
			dataset.addSeries(series4);
			dataset.addSeries(series5);
			dataset1.addSeries(series6);
			dataset1.addSeries(series7);
			dataset1.addSeries(series8);
			dataset1.addSeries(series9);

			// Loop to calculate Raoult and Raoult-Mod for each x1 and y1
			for (int i = 0; i < x1.length; i++) {
				double i2 = i / (x1.length - 1.);
				x1[i] = i2;
				double x[] = { x1[i], 1 - x1[i] };
				Pbolha[i] = x[0] * Psat[0] + x[1] * Psat[1];
				double Pbol1 = Pbolha[i];
				y1[i] = (x[0] * Psat[0]) / Pbolha[i];
				double i3 = y1[i];

				thermo.setPhaseState(liq, T, P, x, Tunit, Punit);

				actliq = thermo.getPhaseProperty(liq, ThermoServer.ActivityCoefficient, "");
				Pbolha2[i] = x1[i] * Psat[0] * actliq[0] + (1 - x1[i]) * Psat[1] * actliq[1];
				y2[i] = (x[0] * Psat[0] * actliq[0]) / Pbolha2[i];
				double i4 = y2[i];
				double Pbol2 = Pbolha2[i];

				series0.add(i2, Pbol1); // (x1,Pbolha) - Raoult
				series1.add(i2, Pbol2); // (x1,Pbolha2) - Raoult-Mod
				series2.add(i3, Pbol1); // (y1,Pbolha) - Raoult
				series3.add(i4, Pbol2); // (y1,Pbolha2) - Raoult-Mod
				series7.add(i2,i3); // (x1,y1) - Raoult
				series8.add(i2,i4); // (x1,y2) - Raoult-Mod

				// Print the results of each variable in the console
				System.out.println(x1[i] + "\t" + y1[i] + "\t" + Pbolha[i] + "\t" + x1[i] + "\t" + y2[i] + "\t" + Pbolha2[i]);

			}

			// data of the experimental points
			double[] Pexp = { 227, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 722 };
			double[] x1exp = { 0, 0.048, 0.157, 0.26, 0.361, 0.461, 0.554, 0.643, 0.727, 0.809, 0.894, 0.93 };
			double[] y1exp = { 0, 0.118, 0.317, 0.447, 0.543, 0.626, 0.697, 0.759, 0.813, 0.863, 0.912, 0.93 };
			
			// data of the diagonal
			double[] x1teor = { 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
			double[] y1teor = { 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
			
			// Loop to calculate get the experimental points
			for (int j = 0; j < Pexp.length; j++) {
				series4.add(x1exp[j], Pexp[j]); // (x1exp,Pexp) - Experimental
				series5.add(y1exp[j], Pexp[j]); // (y1exp,y1exp) - Experimental
				series6.add(x1exp[j], y1exp[j]); // (x1exp,y1exp) - Experimental
			}
			
			// Loop to calculate get the diagonal points			
			for (int jj = 0; jj < y1teor.length; jj++) {
			    series9.add(x1teor[jj], y1teor[jj]);  // (x1teor,y1teor) - Diagonal
			}

/*			First plot - LEFT SIDE*/
			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setBackgroundPaint(Color.WHITE);
			plot.setDomainGridlinesVisible(false);
			plot.setRangeGridlinesVisible(false);
			XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();

			// Changing the line width of series
			r.setSeriesStroke(0, new BasicStroke(2.5f));
			r.setSeriesStroke(1, new BasicStroke(2.5f));
			r.setSeriesStroke(2, new BasicStroke(2.5f));
			r.setSeriesStroke(3, new BasicStroke(2.5f));
			r.setSeriesStroke(4, new BasicStroke(2.5f));
			r.setSeriesStroke(5, new BasicStroke(2.5f));

			// Set "dashed" line
			Stroke dashed = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 },
					0);
			r.setDrawSeriesLineAsPath(true);
			r.setSeriesStroke(1, dashed); // Raoult-Mod (x1,Pbolha2)
			r.setSeriesStroke(3, dashed); // Raoult-Mod (y1,Pbolha2)
			
			// Legends
			r.setSeriesVisibleInLegend(0, true);
			r.setSeriesVisibleInLegend(1, false);
			r.setSeriesVisibleInLegend(2, false);
			r.setSeriesVisibleInLegend(3, true);
			r.setSeriesVisibleInLegend(4, true);
			r.setSeriesVisibleInLegend(5, false);

			// Only markers for series 4 and 5 - Experimental points
			r.setSeriesLinesVisible(4, false);
			r.setSeriesShapesVisible(4, true);
			r.setSeriesShapesFilled(4, true);
			r.setSeriesLinesVisible(5, false);
			r.setSeriesShapesVisible(5, true);
			r.setSeriesShapesFilled(5, true);

		
			// Set color lines to all series
			r.setSeriesPaint(0, Color.black);
			r.setSeriesPaint(1, Color.black);
			r.setSeriesPaint(2, Color.black);
			r.setSeriesPaint(3, Color.black);
			r.setSeriesPaint(4, Color.black);
			r.setSeriesPaint(5, Color.black);

			// For circle dot shape to the Experimental points
			Shape shape1 = new Ellipse2D.Double(-2, -2, 6, 6);
			r.setSeriesShape(4, shape1);
			r.setSeriesShape(5, shape1);

			// Adjusting the plot range for the "x" axis
			plot.getDomainAxis().setAutoRange(false);
			plot.getDomainAxis().setRange(new Range(0.0, 1.0));

			// Adjusting the plot range for the "y" axis
			ValueAxis yAxis = plot.getRangeAxis();
			yAxis.setRange(200, 800);			
			
/*			Second plot - RIGHT SIDE*/
			XYPlot plot2 = (XYPlot) chart1.getPlot();
			plot2.setBackgroundPaint(Color.WHITE);
			plot2.setDomainGridlinesVisible(false);
			plot2.setRangeGridlinesVisible(false);
			XYLineAndShapeRenderer r1 = (XYLineAndShapeRenderer) plot2.getRenderer();

			// Changing the line width of the series
			r1.setSeriesStroke(0, new BasicStroke(2.5f));
			r1.setSeriesStroke(1, new BasicStroke(2.5f));
			r1.setSeriesStroke(2, new BasicStroke(2.5f));
			r1.setSeriesStroke(3, new BasicStroke(2.5f));

			// Set "dashed" and "dashed1" lines
			Stroke dashed1 = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 10 },
					0);
			r1.setSeriesStroke(3, dashed1);
			r1.setSeriesStroke(2, dashed);
			
			// Set the same space for each dash
			r1.setDrawSeriesLineAsPath(true);
			
			// Legends
			r1.setSeriesVisibleInLegend(0, true);
			r1.setSeriesVisibleInLegend(1, true);
			r1.setSeriesVisibleInLegend(2, true);
			r1.setSeriesVisibleInLegend(3, false);

			// Only markers for series 6 - Experimental points
			r1.setSeriesLinesVisible(0, false);
			r1.setSeriesShapesVisible(0, true);

			// Set color lines (all series)
			r1.setSeriesPaint(0, Color.black);
			r1.setSeriesPaint(1, Color.black);
			r1.setSeriesPaint(2, Color.black);

			// For circle dot shape
			r1.setSeriesShape(0, shape1);

			// Adjusting the plot range for the "x" axis
			plot2.getDomainAxis().setAutoRange(false);
			plot2.getDomainAxis().setRange(new Range(0.0, 1.0));

			// Adjusting the plot range for the "y" axis
			ValueAxis yAxis1 = plot2.getRangeAxis();
			yAxis1.setRange(0, 1);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Trabalho01_Ribas blabla = new Trabalho01_Ribas();
		blabla.setVisible(true);
		blabla.setSize(new Dimension(1400, 800));

		// Set the Frame location in the middle of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
		Point newLocation = new Point(middle.x - (blabla.getWidth() / 2), 
		                              middle.y - (blabla.getHeight() / 2));
		blabla.setLocation(newLocation);
	}
}