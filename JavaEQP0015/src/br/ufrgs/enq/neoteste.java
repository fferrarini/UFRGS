package br.ufrgs.enq;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

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
import org.jfree.ui.RectangleInsets;

import br.com.vrtech.iise.IISEClient;
import br.com.vrtech.iise.ThermoServer;

@SuppressWarnings("serial")
public class neoteste extends JFrame {

	public neoteste() {
		super("ELV - Etano(1)/Propeno(2)");

		JTabbedPane T1 = new JTabbedPane();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = getContentPane();

		// A dataset will contain all data for the chart
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeriesCollection dataset1 = new XYSeriesCollection();

		// With the ChartFactory we create an already configured chart.
		JFreeChart chart = ChartFactory.createXYLineChart("", "Fração molar de etano (x1,y1)",
				"Pressão do sistema (psi)", dataset, PlotOrientation.VERTICAL, true, true, false);
		JFreeChart chart1 = ChartFactory.createXYLineChart("", "Fração molar de etano - líquido (x1)",
				"Fração molar de etano - vapor (y1)", dataset1, PlotOrientation.VERTICAL, true, true, false);

		// Add the chart inside of a ChartPanel and pack()
		content.add(T1, BorderLayout.CENTER);
		T1.add("x1y1 vs Pressure", new ChartPanel(chart));
		T1.add("x1 vs y1", new ChartPanel(chart1));

		XYSeries series0 = new XYSeries("Dados Experimentais", false);
		XYSeries series1 = new XYSeries("Dados Experimentais", false);
		XYSeries series2 = new XYSeries("Peng-Robinson", false);	
		XYSeries series3 = new XYSeries("Peng-Robinson", false);
		XYSeries series4 = new XYSeries("Dados Experimentais", false);
		XYSeries series5 = new XYSeries("Peng-Robinson", false);
		XYSeries series6 = new XYSeries("Diagonal", false);

		// XYSeries series7 = new XYSeries("SRK", false);
		// XYSeries series8 = new XYSeries("SRK", false);
		// XYSeries series9 = new XYSeries("SRK", false);

		dataset.addSeries(series0);
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		// dataset.addSeries(series7);
		// dataset.addSeries(series8);
		dataset1.addSeries(series4);
		dataset1.addSeries(series5);
		dataset1.addSeries(series6);
		// dataset1.addSeries(series9);

		try {
			// Connect to a remote host (not needed when using the desktop
			// version)
			String host = "lvpp-srv02.nuvem.ufrgs.br";
			String user = "fabricioferrarini@gmail.com";
			String key = "e245df41-15f0-4547-8b4e-804456e0d0b4";
			// IISEClient.connect(host, user, key);

			// Get a reference for the iiSE thermodynamics server
			ThermoServer thermo = null;

			try {
				thermo = IISEClient.getThermo();
			} catch (Exception r) {
				r.printStackTrace();
			}
			// ThermoServer thermo1 = IISEClient.getThermo();

			// Lets use the first mixture "slot" and two phase "slots".
			// There is no limit (other than the machine memory) for the number
			// of "slots" for mixtures and phases.
			// Most applications will use only 1 or 2 slots.
			int mix = 1;
			int liq = 1;
			int vap = 2;

			// Configure the mixture with the desired components.
			String comps[] = { "ethane", "propylene" };
			thermo.configureMixture(mix, comps);
			thermo.configurePhase(liq, ThermoServer.Liquid, mix, "PR");
			thermo.configurePhase(vap, ThermoServer.Vapour, mix, "PR");
			// thermo1.configureMixture(mix, comps);
			// thermo1.configurePhase(liq, ThermoServer.Liquid, mix, "SRK");
			// thermo1.configurePhase(vap, ThermoServer.Vapour, mix, "SRK");

			// Set the state of the phases
			double T = 100;
			String Tunit = "F"; // Temperature in F
			String Punit = "psi"; // Pressure in psi

			// data of the diagonal
			double[] x1teor = { 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1 };
			double[] y1teor = { 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1 };
			try {
				thermo = IISEClient.getThermo();
			} catch (Exception r) {
				r.printStackTrace();
			}
			// Loop to calculate get the diagonal points
			for (int jj = 0; jj < y1teor.length; jj++) {
				series6.add(x1teor[jj], y1teor[jj]); // (x1teor,y1teor) -
														// Diagonal -
														// panel1/chart2
			}

			// Calculo de Psat
			// double Psat[] = thermo.getPureProperty(mix,
			// ThermoServer.SaturationPressure, Punit, T, Tunit);

			double sumxK = 1;
			double sumxK1 = 1;

			double toler = 1e-5;
			boolean tol1 = false;
			boolean tol2 = false;
			double x1[] = new double[1001];
			double[] Pexp = { 227, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 722 };
			double[] x1exp = { 0, 0.048, 0.157, 0.26, 0.361, 0.461, 0.554, 0.643, 0.727, 0.809, 0.894, 0.93 };
			double[] y1exp = { 0, 0.118, 0.317, 0.447, 0.543, 0.626, 0.697, 0.759, 0.813, 0.863, 0.912, 0.93 };
			int maxint = 100;
			int cont1 = 0;
			int cont2 = 0;
			double P = Pexp[0];

			// Set the decimal format that will show in console
			DecimalFormat df = new DecimalFormat("0.000");
			df.setRoundingMode(RoundingMode.CEILING);

			for (int jj = 0; jj < x1exp.length; jj++) {
				series0.add(x1exp[jj], Pexp[jj]);
				series1.add(y1exp[jj], Pexp[jj]);
				series4.add(x1exp[jj], y1exp[jj]);
			}

			double y1[] = new double[comps.length];

			for (int j = 0; j < x1.length; j++) {
				double i2 = j / (x1.length - 1.);
				cont2 = 0;
				x1[j] = i2;
				double x[] = { x1[j], 1 - x1[j] };
				// Inicial Kick for yi
				if (j == 0) {
					y1[0] = x1[j];
					y1[1] = 1 - y1[0];
				}

				double newP = P;
				thermo.setPhaseState(liq, T, P, x, Tunit, Punit);
				thermo.setPhaseState(vap, T, P, y1, Tunit, Punit);
				double fugliq[] = new double[comps.length];
				double fugvap[] = new double[comps.length];

				try {
					fugliq = thermo.getPhaseProperty(liq, thermo.FugacityCoefficient, null);
					fugvap = thermo.getPhaseProperty(vap, thermo.FugacityCoefficient, null);
				} catch (Exception s) {
					s.printStackTrace();
				}

				double xK[] = new double[comps.length];

				// Contadores das iterações
				cont1 = 0;
				cont2 = 0;

				while (!tol2) {
					cont1++;
					cont2 = 0;
					P = newP;
					while (!tol1) {
						cont2++;
						sumxK = sumxK1;
						sumxK1 = 0;
						double varK[] = new double[comps.length];
						for (int i = 0; i < fugliq.length; i++) {
							varK[i] = fugliq[i] / fugvap[i];
							sumxK1 += x[i] * varK[i];
						}
						for (int cont = 0; cont < comps.length; cont++) {
							y1[cont] = varK[cont] * x[cont] / sumxK1;
						}
						thermo.setPhaseState(liq, T, P, x, Tunit, Punit);
						thermo.setPhaseState(vap, T, P, y1, Tunit, Punit);
						try {
							fugliq = thermo.getPhaseProperty(liq, thermo.FugacityCoefficient, null);
							fugvap = thermo.getPhaseProperty(vap, thermo.FugacityCoefficient, null);
						} catch (Exception t) {
							t.printStackTrace();
							continue;
							// throw new NullPointerException();
						}
						tol1 = Math.abs(sumxK - sumxK1) < toler || cont2 > maxint;
					}
					tol1 = false;
					newP = (P / (2 - sumxK1));
					tol2 = Math.abs(P - newP) < (toler) || cont1 > maxint;
				}

				tol2 = false;

				if (Math.abs(series2.getMaxY() - P) > 1e-1 || j == 0) {
					series2.add(x[0], P);
					series3.add(y1[0], P);
					series5.add(x[0], y1[0]);
				}

				System.out.println("x1" + " " + df.format(x[0]) + "\t" + "x2" + " " + df.format(x[1]) + "\t" + "y1"
						+ " " + df.format(y1[0]) + "\t" + "y2" + " " + df.format(y1[1]) + "\t" + "P" + " "
						+ df.format(P) + "\t" + "P" + " " + df.format(Math.abs(series2.getMaxY() - P)));

			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}

		// ----------------------Plots configs----------------------//
		// ------------------Pane1------------------//
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesStroke(2, new BasicStroke(1.5f));
		r.setSeriesStroke(3, new BasicStroke(1.5f));
		r.setSeriesVisibleInLegend(3, false);
		r.setSeriesLinesVisible(0, false);
		r.setSeriesShapesVisible(0, true);
		r.setSeriesShapesFilled(0, true);
		r.setSeriesLinesVisible(1, false);
		r.setSeriesShapesVisible(1, true);
		r.setSeriesShapesFilled(1, true);
		r.setSeriesVisibleInLegend(1, false);
		r.setSeriesPaint(0, Color.black);
		r.setSeriesPaint(1, Color.black);
		r.setSeriesPaint(2, Color.black);
		r.setSeriesPaint(3, Color.black);
		Shape shape2 = new Ellipse2D.Double(-2, -2, 6, 6);
		r.setSeriesShape(0, shape2);
		r.setSeriesShape(1, shape2);
		plot.getDomainAxis().setAutoRange(false);
		plot.getDomainAxis().setRange(new Range(0.0, 1.0));
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(200, 800);
		Font labelFont = new Font("Curinga", Font.PLAIN, 14);
		plot.getDomainAxis().setLabelFont(labelFont);
		plot.getRangeAxis().setLabelFont(labelFont);
		plot.setAxisOffset(new RectangleInsets(8, 0, 0, 16));
		PDFChart.createPDFChart(chart, "VLE-Ethane(1)Propylene(2)-x1y1&Pressure-Método\u03D5-\u03D5");

		// ------------------Pane2------------------//
		XYPlot plot1 = (XYPlot) chart1.getPlot();
		plot1.setBackgroundPaint(Color.WHITE);
		plot1.setDomainGridlinesVisible(true);
		plot1.setDomainGridlinePaint(Color.BLACK);
		plot1.setRangeGridlinesVisible(true);
		plot1.setRangeGridlinePaint(Color.BLACK);
		XYLineAndShapeRenderer r1 = (XYLineAndShapeRenderer) plot1.getRenderer();
		r1.setSeriesStroke(0, new BasicStroke(2.5f));
		r1.setSeriesStroke(1, new BasicStroke(2.5f));
		r1.setSeriesStroke(2, new BasicStroke(2.5f));
		Stroke d1 = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 10 }, 0);
		r1.setSeriesStroke(2, d1);
		r1.setDrawSeriesLineAsPath(true);
		r1.setSeriesVisibleInLegend(0, true);
		r1.setSeriesVisibleInLegend(1, true);
		r1.setSeriesVisibleInLegend(2, false);
		r1.setSeriesLinesVisible(0, false);
		r1.setSeriesShape(0, shape2);
		r1.setSeriesShapesVisible(0, true);
		r1.setSeriesShapesFilled(0, true);
		r1.setSeriesLinesVisible(1, true);
		r1.setSeriesShapesVisible(1, false);
		r1.setSeriesShapesFilled(1, false);
		r1.setSeriesPaint(0, Color.black);
		r1.setSeriesPaint(1, Color.black);
		plot1.getDomainAxis().setAutoRange(false);
		plot1.getDomainAxis().setRange(new Range(0.0, 1.0));
		ValueAxis yAxis1 = plot1.getRangeAxis();
		yAxis1.setRange(0, 1);
		plot1.getDomainAxis().setLabelFont(labelFont);
		plot1.getRangeAxis().setLabelFont(labelFont);
		plot1.setAxisOffset(new RectangleInsets(8, 0, 0, 16));
		PDFChart.createPDFChart(chart1, "VLE-Ethane(1)Propylene(2)-x1&y1-Método\u03D5-\u03D5");

	}

	public static void main(String[] args) {
		neoteste graph = new neoteste();
		graph.setVisible(true);
		graph.setSize(new Dimension(1000, 800));
		graph.setLocationRelativeTo(null);
	}
}