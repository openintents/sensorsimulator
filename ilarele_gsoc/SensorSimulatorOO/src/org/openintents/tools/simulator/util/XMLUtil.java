package org.openintents.tools.simulator.util;

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
			statesList = root.getElementsByTagName("state");

			for (int i = 0; i < statesList.getLength(); i++) {
				// get current state
				Element stateElement = (Element) statesList.item(i);
				StateModel stateModel = new StateModel();

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
					float[] sensorValues = new float[3];

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
			document.appendChild(globalElement);

			// for each state in the model
			ArrayList<StateModel> states = mModel.getStates();

			// next states
			for (int i = 0; i < states.size(); i++) {
				StateModel crtState = states.get(i);
				// save the current state in the xml
				Element stateElement = buildStateElement(crtState, document);
				globalElement.appendChild(stateElement);
			}
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.transform(source, new StreamResult(file));
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

			for (int i = 0; i < sensorValues.length; i++) {
				Element valueElement = document.createElement("value");
				valueElement.setTextContent("" + sensorValues[i]);
				sensorElement.appendChild(valueElement);
			}

			stateElement.appendChild(sensorElement);
		}
		return stateElement;
	}
}
