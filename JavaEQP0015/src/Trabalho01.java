
import br.com.vrtech.iise.IISEClient;
import br.com.vrtech.iise.ThermoServer;

public class Trabalho01 {
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
			String comps[] = {"n-butane", "isobutane", "n-pentane", "n-hexane"};
			thermo.configureMixture(mix, comps);

			// Once the mixture is configured we can configure phases.
			// In the phase configuration we need to specify:
			//  - the mixture to be used
			//  - the phase type (Liquid or Vapour)
			//  - the equation of state for calculations ("PR", "SRK", ...)
			thermo.configurePhase(liq, ThermoServer.Liquid, mix, "PR", "SCMR", "UNIFAC(Do)");
			thermo.configurePhase(vap, ThermoServer.Vapour, mix, "PR", "SCMR", "UNIFAC(Do)");
			
			// Set the state of the phases
			double T = 25.0;   String Tunit = "C";   // Temperature in deg C
			double P = 1.0;    String Punit = "bar"; // Pressure in bar
			double []z = {0.2, 0.3, 0.1, 0.4};       // composition
			thermo.setPhaseState(liq, T, P, z, Tunit, Punit);
			thermo.setPhaseState(vap, T, P, z, Tunit, Punit);
			
			double Psat[] = thermo.getPureProperty(mix, ThermoServer.SaturationPressure, "psi", T, "K");
						
			
			double x1[]= {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
			double x[] = new double[comps.length];
			for (int i = 0; i < x1.length; i++) {
				
			}
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}