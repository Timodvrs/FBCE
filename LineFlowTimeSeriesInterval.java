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
public class LineFlowTimeSeriesInterval {

    public String hour;
    public String roundedInQty;
    public String roundedOutQty;
    public double inQty;
    public double outQty;

    public LineFlowTimeSeriesInterval(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);

            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String tempName = tempNode.getNodeName();
                NamedNodeMap nodeMapTSI = tempNode.getAttributes();
                String attValue = nodeMapTSI.item(0).getNodeValue();
                switch (tempName) {
                    case "Pos":
                        this.hour = attValue;
                        break;
                    case "RoundedInQty":
                        this.roundedInQty = attValue;
                        break;
                    case "RoundedOutQty":
                        this.roundedOutQty = attValue;
                        break;
                    default:
                        break;
                }
            } 
        }
       // System.out.println("hr: " + hour + ", RoundedInQty: " + roundedInQty + ", RoundedOutQty: " + roundedOutQty);
    }
    
    public LineFlowTimeSeriesInterval(String hour, String roundedInQty, String roundedOutQty) {
        this.hour = hour;
        this.roundedInQty = roundedInQty;
        this.roundedOutQty = roundedOutQty;
       // System.out.println("hr: " + hour + ", RoundedInQty: " + roundedInQty + ", RoundedOutQty: " + roundedOutQty);
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getRoundedInQty() {
        return roundedInQty;
    }

    public void setRoundedInQty(String roundedInQty) {
        this.roundedInQty = roundedInQty;
    }

    public String getRoundedOutQty() {
        return roundedOutQty;
    }

    public void setRoundedOutQty(String roundedOutQty) {
        this.roundedOutQty = roundedOutQty;
    }
}
