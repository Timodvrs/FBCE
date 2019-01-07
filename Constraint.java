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
public class Constraint {

    public String constraintIdentification;
    public String hour;
    public String shadowPriceAmount;

    public Constraint(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String tempName = tempNode.getNodeName();
                if (tempName.equals("ConstraintIdentification")) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    constraintIdentification = nodeMap.item(0).getNodeValue();
                } else if (tempName.equals("Period")) {
                    addPeriod(tempNode);
                }
            }
        }
    }
    
    public Constraint(){
        
    }
    
    public void addPeriod(Node node){
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {                 
                if (tempNode.getNodeName().equals("Interval")) {
                    addInterval(tempNode);
                } 
            }
        }
    }
    
    public void addInterval(Node node){
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {                 
                if (tempNode.getNodeName().equals("Pos")) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    hour = nodeMap.item(0).getNodeValue();
                } else if (tempNode.getNodeName().equals("ShadowPriceAmount")) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    shadowPriceAmount = nodeMap.item(0).getNodeValue();
                }
            }
        }
    }
}
