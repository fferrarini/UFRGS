package br.ufrgs.enq;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import br.com.vrtech.iise.IISEClient;
import br.com.vrtech.iise.ThermoServer;

public class Trab2 {
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	public static void main(String[] args) {
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
			int vap = 2;

			// Configure the mixture with the desired components.
			String comps[] = { "ethane", "propylene" };
			thermo.configureMixture(mix, comps);
			thermo.configurePhase(liq, ThermoServer.Liquid, mix, "PR");
			thermo.configurePhase(vap, ThermoServer.Vapour, mix, "PR");

			// Set the state of the phases
			double T = 100;
			String Tunit = "F"; // Temperature in F
//			double P = 300;
			String Punit = "psi"; // Pressure in psi

			// Calculo de Psat
			double Psat[] = thermo.getPureProperty(mix, ThermoServer.SaturationPressure, Punit, T, Tunit);

			double sumxK = 1;
			double sumxK1 = 1;

			double toler = 1e-10;
			boolean tol1 = false;
			boolean tol2 = false;
//			double x1[] = new double[21];
			double[] Pexp = { 227, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 722 };
			double[] x1exp = { 0, 0.048, 0.157, 0.26, 0.361, 0.461, 0.554, 0.643, 0.727, 0.809, 0.894, 0.93 };
			double[] y1exp = { 0, 0.118, 0.317, 0.447, 0.543, 0.626, 0.697, 0.759, 0.813, 0.863, 0.912, 0.93 };
//			double x1[] = new double[x1exp.length];2
			int maxint = 100;
			int cont1 = 0;
			int cont2 = 0;
			
			DecimalFormat df = new DecimalFormat("0.000");
			df.setRoundingMode(RoundingMode.CEILING);

			for (int j = 0; j < x1exp.length; j++) {
//				double i2 = j / (x1exp.length - 1.);
				cont2=0;
//				x1[j] = i2;
//				double x[] = { x1exp[j], 1-x1exp[j]};
				double x[] = { x1exp[j], 1 - x1exp[j] };
				// Inicial Kick for yi
				double y1[] = { y1exp[j], 1 - y1exp[j] };
//				double newP = (Psat[1] + (Psat[0]-Psat[1])*(j/(x1exp.length)));
				double newP = Pexp[j];
				thermo.setPhaseState(liq, T, newP, x, Tunit, Punit);
				thermo.setPhaseState(vap, T, newP, y1, Tunit, Punit);
				double fugliq[] = new double[comps.length];
				double fugvap[] = new double[comps.length];
				fugliq = thermo.getPhaseProperty(liq, thermo.FugacityCoefficient, null);
				fugvap = thermo.getPhaseProperty(vap, thermo.FugacityCoefficient, null);				
				double xK[] = new double[comps.length];
				
				cont1=0;
				cont2=0;

				while (!tol2) {
					cont1++;
					cont2=0;	
					double P = newP;
					while (!tol1) {
						cont2++;
						sumxK = sumxK1;
						sumxK1 = 0;
						double varK[] = new double [comps.length];
						for (int i = 0; i < fugliq.length; i++) {
							varK[i] = fugliq[i] / fugvap[i];
							sumxK1 += x[i] * varK[i];
						}
						for (int cont = 0; cont < comps.length; cont++) {
							y1[cont] = varK[cont] * x[cont] / sumxK1;
						}
						thermo.setPhaseState(liq, T, P, x, Tunit, Punit);
						thermo.setPhaseState(vap, T, P, y1, Tunit, Punit);
						fugliq = thermo.getPhaseProperty(liq, thermo.FugacityCoefficient, null);
						fugvap = thermo.getPhaseProperty(vap, thermo.FugacityCoefficient, null);
//						System.out.println(fugliq[0]+"\t"+fugvap[0]);
						tol1 = Math.abs(sumxK - sumxK1) < toler && cont1 < maxint;
					}
					tol1 = false;
					newP = P / (2 - sumxK1);
					tol2 = Math.abs(P - newP) < (toler) && cont2 < maxint;

				}
				
				tol2 = false;

				System.out.println("x1" + " " + df.format(x[0]) + "\t" + "x2" + " " + df.format(x[1]) + "\t" + "y1" + " " + df.format(y1[0])
						+ "\t" + "y2" + " " + df.format(y1[1]) + "\t" + "P" + " " + df.format(newP)+ "\t" + "P" + " " + df.format(Pexp[j]));
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}