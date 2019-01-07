/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LineFlowTimeSeries {
    public Border border;
    
    public LineFlowTimeSeries() {
        border = new Border();
    }
    
    public void addPeriod(Node node){
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.getNodeName().equals("Interval")){
                LineFlowTimeSeriesInterval tempInt = new LineFlowTimeSeriesInterval(tempNode);
                this.border.intervals.add(tempInt);
            }
        }
    }
    
    public void addArea(Node node, boolean inArea) {
        NamedNodeMap nodeMap = node.getAttributes();
        for (int j = 0; j < nodeMap.getLength(); j++) {
            Node attribute = nodeMap.item(j);
            if (attribute.getNodeName().equals("v")) {
                if (inArea) {
                    border.inArea = attribute.getNodeValue();
                } else {
                    border.outArea = attribute.getNodeValue();
                }
            }
        }
    }
    
    public void addLineFlowTimeSeriesInterval(LineFlowTimeSeriesInterval interval) {
        this.border.intervals.add(interval);
    }
    
    public int getID(){
        String tempEndStr = this.border.timeSeriesIdentification.substring(1);
        String formatted = String.format("%03d", Integer.parseInt(tempEndStr));
        int tempEnd = Integer.parseInt(this.border.timeSeriesIdentification.substring(0, 1) + formatted);
        return tempEnd;
    }
    
}
