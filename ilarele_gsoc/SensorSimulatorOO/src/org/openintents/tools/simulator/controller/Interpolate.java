package org.openintents.tools.simulator.controller;

import java.util.ArrayList;

import org.openintents.tools.simulator.model.StateModel;

public class Interpolate {

	public static ArrayList<StateModel> getIntermediateStates(StateModel s1,
			StateModel s2, int intermediateNo) {
		ArrayList<StateModel> result = new ArrayList<StateModel>();

		for (int i = 1; i < intermediateNo + 1; i++) {
			StateModel newState = new StateModel();
			newState.fillLinearValues(s1, s2, i, intermediateNo + 1);
			result.add(newState);
		}
		return result;
	}

	public static StateModel getIntermediateState(StateModel firstState,
			StateModel secondState) {
		StateModel newState = new StateModel();
		// linear interpolation
		newState.fillLinearValues(firstState, secondState, 1, 2);

		// TODO: nonlinear interpolation
		return newState;
	}

}
