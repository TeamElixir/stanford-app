package org.elixir.gate;

import gate.Factory;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import org.elixir.Constants;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		try {
			Gate.setGateHome(new File(Constants.GATE_HOME));
			Gate.init();
			SerialAnalyserController controller = (SerialAnalyserController)
					PersistenceManager
							.loadObjectFromFile(new File(new File(Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
									ANNIEConstants.DEFAULT_FILE));

			Gate.getCreoleRegister().registerDirectories(
					new File(Gate.getPluginsHome(), "Tools").toURL()
			);

			ProcessingResource morpher = (ProcessingResource) Factory.createResource("gate.creole.morph.Morph");

		}
		catch (GateException | IOException e) {
			e.printStackTrace();
		}
	}
}
