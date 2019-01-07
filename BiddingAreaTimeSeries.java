/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author tdevries
 */
public class BiddingAreaTimeSeries {

    public String TimeSeriesID;
    public BiddingArea ba;

    public BiddingAreaTimeSeries(Node node) {
        ba = new BiddingArea();
        init(node);
    }
    
    public BiddingAreaTimeSeries(){
        ba = new BiddingArea();
    }
    
    public void init(Node node){
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String tempName = tempNode.getNodeName();
                switch (tempName) {
                    case "TimeSeriesIdentification":
                        NamedNodeMap nodeMapTSI = tempNode.getAttributes();
                        TimeSeriesID = nodeMapTSI.item(0).getNodeValue();
                        break;
                    case "BiddingArea":
                        NamedNodeMap nodeMapBA = tempNode.getAttributes();
                        for (int j = 0; j < nodeMapBA.getLength(); j++) {
                            Node attribute = nodeMapBA.item(j);
                            if (attribute.getNodeName().equals("v")) {
                                ba.eic = attribute.getNodeValue();
                            }
                        }
                        break;
                    case "AreaResults":
                        NodeList nodeListAR = tempNode.getChildNodes();
                        ba.init(nodeListAR.item(1));
                        break;
                    case "NEMOHubResults":
                        addNH(tempNode);
                        break;
                    case "SchedulingAreaResults":
                        addSA(tempNode);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public Area addResult(Node node) {
        NodeList nodeList = node.getChildNodes();
        Area temp = new NemoHub();
        //(nodeList.item(3));

        NamedNodeMap nodeMap = nodeList.item(1).getAttributes();
        for (int j = 0; j < nodeMap.getLength(); j++) {
            Node attribute = nodeMap.item(j);
            if (attribute.getNodeName().equals("v")) {
                temp.eic = attribute.getNodeValue();
            }
        }
        return temp;
    }

    public void addNH(Node node) {
        NodeList nodeList = node.getChildNodes();
        NemoHub temp = new NemoHub();
        temp.init(nodeList.item(3));

        NamedNodeMap nodeMap = nodeList.item(1).getAttributes();
        for (int j = 0; j < nodeMap.getLength(); j++) {
            Node attribute = nodeMap.item(j);
            if (attribute.getNodeName().equals("v")) {
                temp.eic = attribute.getNodeValue();
            }
        }
        ba.nhs.add(temp);
    }

    public void addSA(Node node) {
        NodeList nodeList = node.getChildNodes();
        SchedulingArea temp = new SchedulingArea();
        temp.init(nodeList.item(3));

        NamedNodeMap nodeMap = nodeList.item(1).getAttributes();
        for (int j = 0; j < nodeMap.getLength(); j++) {
            Node attribute = nodeMap.item(j);
            if (attribute.getNodeName().equals("v")) {
                temp.eic = attribute.getNodeValue();
            }
        }
        ba.sas.add(temp);
    }

}
