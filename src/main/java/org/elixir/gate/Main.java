package org.elixir.gate;

import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.gui.MainFrame;
import gate.util.GateException;
import org.elixir.Constants;

import java.io.File;

public class Main {

	public static void main(String[] args) throws GateException {
		Gate.setGateHome(new File(Constants.GATE_HOME));
		Gate.init();    // prepare the library
		MainFrame.getInstance().setVisible(true);
		Document document = Factory.newDocument("This is a new document");

	}
}
