package org.openintents.tools.simulator.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openintents.tools.simulator.Global;
import org.openintents.tools.simulator.model.SensorsScenarioModel;
import org.openintents.tools.simulator.model.StateModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLUtil {
	/**
	 * Fill scenario model Refresh scenario view
	 * 
	 * @param file
	 */
	public static void loadScenarioFromXml(File file,
			SensorsScenarioModel mModel) {
		NodeList statesList;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			float betweenTime = Float.parseFloat(root
					.getAttribute("between_time"));
			statesList = root.getElementsByTagName("state");

			for (int i = 0; i < statesList.getLength(); i++) {
				// get current state
				Element stateElement = (Element) statesList.item(i);
				StateModel stateModel = new StateModel();
				stateModel.setTime(betweenTime);
				float[] sensorValues = new float[4];

				// get all sensors in the state
				NodeList sensorsList = stateElement
						.getElementsByTagName("sensor");
				for (int j = 0; j < sensorsList.getLength(); j++) {
					Element sensorElement = (Element) sensorsList.item(j);
					// get sensor name
					String sensorName = sensorElement.getAttribute("type");
					// get sensor values
					NodeList valuesElements = sensorElement
							.getElementsByTagName("value");
					for (int k = 0; k < valuesElements.getLength(); k++) {
						Element crtValueElement = (Element) valuesElements
								.item(k);
						String crtValue = crtValueElement.getChildNodes()
								.item(0).getNodeValue();
						sensorValues[k] = Float.parseFloat(crtValue);
					}
					stateModel.fillSensor(sensorName, sensorValues);
				}
				mModel.add(stateModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveScenarioToXml(File file, SensorsScenarioModel mModel) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.newDocument();

			// set between time
			Element globalElement = document.createElement("global");
			globalElement.setAttribute("between_time", ""
					+ Global.INTERPOLATION_DISTANCE); // TODO
			document.appendChild(globalElement);

			// for each state in the model
			ArrayList<StateModel> states = mModel.getStates();

			// first state:
			if (states.size() > 0) {
				StateModel lastState = states.get(0);
				float lastTime = lastState.getTime();
				Element stateElement = buildStateElement(lastState, document);
				globalElement.appendChild(stateElement);

				// next states
				for (int i = 1; i < states.size(); i++) {
					StateModel crtState = states.get(i);
					final int intermediateNo = (int) (lastTime
							/ Global.INTERPOLATION_DISTANCE - 1);
					if (intermediateNo == 0) {
						// no interpolation
					} else if (intermediateNo == 1) {
						// generate only one intermediate state
						StateModel intermediateState = Interpolate
								.getIntermediateState(lastState, crtState);
						stateElement = buildStateElement(intermediateState,
								document);
						globalElement.appendChild(stateElement);
					} else {
						// generate an array with intermediate states
						ArrayList<StateModel> interpStates = Interpolate
								.getIntermediateStates(lastState, crtState,
										intermediateNo);
						// save all resulted states in xml
						for (StateModel stateModel : interpStates) {
							stateElement = buildStateElement(stateModel,
									document);
							globalElement.appendChild(stateElement);
						}
					}

					// save the current state in the xml
					stateElement = buildStateElement(crtState, document);
					globalElement.appendChild(stateElement);

					lastTime = crtState.getTime();
					lastState = crtState;
				}
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				transformer.transform(source, new StreamResult(file));
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private static Element buildStateElement(StateModel state, Document document) {
		Element stateElement = document.createElement("state");
		HashMap<String, float[]> stateSensors = state.getStateSensors();
		for (Entry<String, float[]> sensor : stateSensors.entrySet()) {
			String sensorName = sensor.getKey();
			float[] sensorValues = sensor.getValue();

			Element sensorElement = document.createElement("sensor");
			sensorElement.setAttribute("type", sensorName);

			Element valueElement = document.createElement("value");
			for (int i = 0; i < sensorValues.length; i++)
				valueElement.setTextContent("" + sensorValues[i]);
			sensorElement.appendChild(valueElement);

			stateElement.appendChild(sensorElement);
		}
		return stateElement;
	}
}
