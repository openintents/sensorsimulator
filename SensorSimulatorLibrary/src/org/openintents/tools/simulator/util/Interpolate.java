/*
 * Copyright (C) 2011 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.tools.simulator.util;

import java.util.ArrayList;

import org.openintents.tools.simulator.model.StateModel;

/**
 * Generates intermediary states between 2 main states 
 * and interpolates 3D vectors.
 * @author ilarele
 *
 */
public class Interpolate {

	public static ArrayList<StateModel> getIntermediateStates(StateModel s1,
			StateModel s2, int intermediateNo) {
		ArrayList<StateModel> result = new ArrayList<StateModel>();

		for (int i = 1; i < intermediateNo + 1; i++) {
			StateModel newState = new StateModel();
			newState.fillLinearValues(s1, s2, i, intermediateNo + 1);
			newState.fillNonLinearValues(s1, s2, i, intermediateNo + 1);
			result.add(newState);
		}
		return result;
	}

	public static StateModel getIntermediateState(StateModel firstState,
			StateModel secondState) {
		StateModel newState = new StateModel();
		// linear interpolation
		newState.fillLinearValues(firstState, secondState, 1, 2);

		// nonlinear interpolation
		newState.fillNonLinearValues(firstState, secondState, 1, 2);
		return newState;
	}

	public static float[] interpolate(float[] v1, float[] v2, int k, int n) {
		if (v1[0] == v2[0] && v1[1] == v2[1] && v1[2] == v2[2])
			return v1.clone();
		float[] result = new float[3];
		float t = (float) k / n;

		float aux = (v1[0] * v2[0]) + (v1[1] * v2[1]) + (v1[2] * v2[2]);

		if (aux < 0.0f) {
			// Negate the second quaternion and the result of the dot product
			v2[0] *= -1;
			v2[1] *= -1;
			v2[2] *= -1;
			aux = -aux;
		}

		// Set the first and second scale for the interpolation
		float scale0 = 1 - t;
		float scale1 = t;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if ((1 - aux) > 0.1f) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			float theta = (float) Math.acos(aux);
			float invSinTheta = (float) (1f / Math.sin(theta));

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scale0 = (float) (Math.sin((1 - t) * theta) * invSinTheta);
			scale1 = (float) (Math.sin((t * theta)) * invSinTheta);
		}

		// Calculate the x, y, z and w values for the quaternion by using a
		// special
		// form of linear interpolation for quaternions.
		result[0] = (scale0 * v1[0]) + (scale1 * v2[0]);
		result[1] = (scale0 * v1[1]) + (scale1 * v2[1]);
		result[2] = (scale0 * v1[2]) + (scale1 * v2[2]);
		return result;
	}

	public static void print3f(float[] v) {
		System.out.println("\t" + v[0] + ", " + v[1] + ", " + v[2]);
	}

}
