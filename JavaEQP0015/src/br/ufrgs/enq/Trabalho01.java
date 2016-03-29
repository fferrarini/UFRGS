package br.ufrgs.enq;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
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

@SuppressWarnings("serial")
public class Trabalho01 extends JFrame {

	public Trabalho01() {
		super("Trabalho01");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container content = getContentPane();

		// A dataset will contain all data for the chart
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeriesCollection dataset2 = new XYSeriesCollection();

		// With the ChartFactory we create an already configured chart.
		JFreeChart chart = ChartFactory.createXYLineChart("", "Fração molar de etano (x1/y1)",
				"Pressão do sistema (psi)", dataset, PlotOrientation.VERTICAL, true, true, false);
		JFreeChart chart2 = ChartFactory.createXYLineChart("", "Fração no molar de etano no liquido (x1)",
				"Fração no molar de etano no vapor (y1)", dataset2, PlotOrientation.VERTICAL, true, true, false);
		// Add the chart inside of a ChartPanel and pack()
		content.add(new ChartPanel(chart), BorderLayout.WEST);
		content.add(new ChartPanel(chart2), BorderLayout.EAST);
		// pack();

		// Create the series (the lines) and add to the dataset
		XYSeries series0 = new XYSeries("Lei de Raoult", false);
		XYSeries series1 = new XYSeries("", false);
		XYSeries series2 = new XYSeries("Lei de Raoult Modificada", false);
		XYSeries series3 = new XYSeries("", false);
		XYSeries series4 = new XYSeries("Dados Experimentais", false);
		XYSeries series5 = new XYSeries("", false);
		XYSeries series6 = new XYSeries("Dados Experimentais", false);
		XYSeries series7 = new XYSeries("Lei de Raoult", false);
		XYSeries series8 = new XYSeries("Lei de Raoult Modificada", false);
		XYSeries series9 = new XYSeries("", false);
		dataset.addSeries(series0);
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		dataset.addSeries(series4);
		dataset.addSeries(series5);
		dataset2.addSeries(series6);
		dataset2.addSeries(series7);
		dataset2.addSeries(series8);
		dataset2.addSeries(series9);

		// Experimental Data
		double[] Pexp = { 227, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 722 };
		double[] x1exp = { 0.0, 0.048, 0.157, 0.26, 0.361, 0.461, 0.554, 0.643, 0.727, 0.809, 0.894, 0.93 };
		double[] y1exp = { 0.0, 0.118, 0.317, 0.447, 0.543, 0.626, 0.697, 0.759, 0.813, 0.863, 0.912, 0.93 };
		for (int i = 0; i < x1exp.length; i++) {
			series4.add(x1exp[i], Pexp[i]);
			series5.add(y1exp[i], Pexp[i]);
			series6.add(x1exp[i], y1exp[i]);

		}

		try {
			// Connect to a remote host (not needed when using the desktop
			// version)
			String host = "lvpp-srv02.nuvem.ufrgs.br";
			// String user = "fabricioferrarini@gmail.com";
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
			// int vap = 2;

			// Configure the mixture with the desired components.
			String comps[] = { "ethane", "propylene" };
			thermo.configureMixture(mix, comps);
			thermo.configurePhase(liq, ThermoServer.Liquid | ThermoServer.ActPureFugPvapIdeal, mix, "PR", null,
					"UNIFAC(Do)");

			// Set the state of the phases
			double T = 100.0;
			String Tunit = "F"; // Temperature in deg C
			double P = 1.0;
			String Punit = "psi"; // Pressure in bar

			// Calculo de Psat
			double Psat[] = thermo.getPureProperty(mix, ThermoServer.SaturationPressure, Punit, T, Tunit);

			System.out
					.println("x1" + "\t" + "y1_Raoult" + "\t" + "Pbolha_Raoult" + "\t" + "y1_RM" + "\t" + "Pbolha_RM");
			// Lei de Raoult
			double x1[] = new double[21];
			double Pbolha[] = new double[x1.length];
			double y1[] = new double[x1.length];
			double y2[] = new double[x1.length];
			double actliq[] = new double[x1.length];
			double Pbolha2[] = new double[x1.length];
			for (int i = 0; i < x1.length; i++) {
				double i2 = i / (x1.length - 1.);
				x1[i] = i2;
				double x[] = { x1[i], 1 - x1[i] };
				Pbolha[i] = x[0] * Psat[0] + x[1] * Psat[1];
				y1[i] = (x[0] * Psat[0]) / Pbolha[i];
				thermo.setPhaseState(liq, T, P, x, Tunit, Punit);

				actliq = thermo.getPhaseProperty(liq, ThermoServer.ActivityCoefficient, "");
				Pbolha2[i] = x[0] * Psat[0] * actliq[0] + x[1] * Psat[1] * actliq[1];
				y2[i] = (x[0] * Psat[0] * actliq[0]) / Pbolha2[i];

				System.out.println(x1[i] + "\t" + y1[i] + "\t" + Pbolha[i] + "\t" + y2[i] + "\t" + Pbolha2[i]);

				series0.add(x1[i], Pbolha[i]);
				series1.add(y1[i], Pbolha[i]);
				series2.add(x1[i], Pbolha2[i]);
				series3.add(y2[i], Pbolha2[i]);
				series7.add(x1[i], y1[i]);
				series8.add(x1[i], y2[i]);
				series9.add(x1[i], x1[i]);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}

		// ---------Plot1 config------------------//

		// Some additional configurations (comment the following lines to stay
		// with the default
		// configuration)
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();

		// Changing the line width of series0
		r.setSeriesStroke(0, new BasicStroke(1.5f));
		r.setSeriesStroke(1, new BasicStroke(1.5f));
		r.setSeriesVisibleInLegend(1, false);

		// set dashed line
		Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5 }, 0);
		r.setSeriesStroke(2, dashed);
		r.setSeriesStroke(3, dashed);
		r.setSeriesVisibleInLegend(3, false);

		// Only markers for series 4 and 5
		r.setSeriesLinesVisible(4, false);
		r.setSeriesShapesVisible(4, true);
		r.setSeriesShapesFilled(4, true);
		r.setSeriesLinesVisible(5, false);
		r.setSeriesShapesVisible(5, true);
		r.setSeriesShapesFilled(5, true);
		r.setSeriesVisibleInLegend(5, false);

		// Set color lines (all series)
		r.setSeriesPaint(0, Color.black);
		r.setSeriesPaint(1, Color.black);
		r.setSeriesPaint(2, Color.black);
		r.setSeriesPaint(3, Color.black);
		r.setSeriesPaint(4, Color.black);
		r.setSeriesPaint(5, Color.black);

		// For rectangular dot shape
		// Shape shape1 = new Rectangle2D.Double(-2, -2, 2, 2);
		// For circle dot shape
		Shape shape2 = new Ellipse2D.Double(-2, -2, 6, 6);
		r.setSeriesShape(4, shape2);
		r.setSeriesShape(5, shape2);

		// adjusting the plot range for the "x" axis
		plot.getDomainAxis().setAutoRange(false);
		plot.getDomainAxis().setRange(new Range(0.0, 1.0));

		// adjusting the plot range for the "y" axis
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(200, 800);

		// ---------Plot2 config------------------//

		XYPlot plot2 = (XYPlot) chart2.getPlot();
		plot2.setBackgroundPaint(Color.WHITE);
		plot2.setDomainGridlinesVisible(false);
		plot2.setRangeGridlinesVisible(false);
		XYLineAndShapeRenderer r2 = (XYLineAndShapeRenderer) plot2.getRenderer();

		r2.setSeriesStroke(1, new BasicStroke(1.5f));

		Stroke dashed2 = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 10 }, 0);
		r2.setSeriesStroke(1, dashed);
		r2.setSeriesStroke(3, dashed2);

		r2.setSeriesLinesVisible(0, false);
		r2.setSeriesShapesVisible(0, true);
		r2.setSeriesShapesFilled(0, true);
		r2.setSeriesVisibleInLegend(3, false);

		r2.setSeriesPaint(0, Color.black);
		r2.setSeriesPaint(1, Color.black);
		r2.setSeriesPaint(2, Color.black);
		r2.setSeriesPaint(3, Color.black);

		r2.setSeriesShape(0, shape2);

		// For rectangular dot shape
		// Shape shape1 = new Rectangle2D.Double(-2, -2, 2, 2);
		// For circle dot shape
		Shape shape3 = new Ellipse2D.Double(-2, -2, 6, 6);
		r2.setSeriesShape(4, shape3);
		r2.setSeriesShape(5, shape3);

		// adjusting the plot range for the "x" axis
		plot2.getDomainAxis().setAutoRange(false);
		plot2.getDomainAxis().setRange(new Range(0.0, 1.0));

		// adjusting the plot range for the "y" axis
		ValueAxis yAxis2 = plot2.getRangeAxis();
		yAxis2.setRange(0.0, 1.0);
	}

	public static void main(String[] args) {
		Trabalho01 graph = new Trabalho01();

		graph.setLocationRelativeTo(null);
		graph.setVisible(true);
		graph.setSize(new Dimension(1360, 800));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
		Point newLocation = new Point(middle.x - (graph.getWidth() / 2), middle.y - (graph.getHeight() / 2));
		graph.setLocation(newLocation);
	}
}