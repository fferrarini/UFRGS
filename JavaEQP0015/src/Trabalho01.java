
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import br.com.vrtech.iise.IISEClient;
import br.com.vrtech.iise.ThermoServer;

public class Trabalho01 extends JFrame{
	public static void main(String[] args) {
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

				thermo.setPhaseState(liq, T, P, x, Tunit, Punit);


				actliq = thermo.getPhaseProperty(liq, ThermoServer.ActivityCoefficient, "");
				Pbolha2[i]=x1[i]*Psat[0]*actliq[0] + (1-x1[i])*Psat[1]*actliq[1];

				System.out.println(x1[i]+"\t"+y1[i]+"\t"+Pbolha[i]+"\t"+Pbolha2[i]);

			}



		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		JFrame f = new JFrame("my first gui program");
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container content = f.getContentPane();
		content.setLayout(new BorderLayout());
		content.add(new JButton("Click on me"));
		
		f.setSize(250, 100);
		f.setLocationRelativeTo(null);
		
		f.setVisible(true);
		
		
	}
}