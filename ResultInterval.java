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
public class ResultInterval {
    public String hour;
    public String netPositionRounded;
    public double netPos;
    
    public ResultInterval(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String tempName = tempNode.getNodeName();
                if (tempName.equals("Pos")) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    hour = nodeMap.item(0).getNodeValue();
                } else if (tempName.equals("NetPositionRounded")) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    netPositionRounded = nodeMap.item(0).getNodeValue();
                }
            }
        }
     //   System.out.println("hr: " + hour + ", netPos: " + netPositionRounded);
    }
    
     public ResultInterval(String hour) {
        this.hour = hour;
    }
     
    public ResultInterval(String hour, String netPositionRounded) {
        this.hour = hour;
        this.netPositionRounded = netPositionRounded;
    }
}
