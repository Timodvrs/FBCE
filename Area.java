/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author tdevries
 */
public class Area {
    public ArrayList<ResultInterval> netPositions = new ArrayList<>();
    public String eic;
    public Boolean cwe;
    public ArrayList<Border> inBorders = new ArrayList<>();
    public ArrayList<Border> outBorders = new ArrayList<>();
    public Area parentSA;
    public Area parentBA;
    
    
    public void init(Node node){
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);

            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if(tempNode.getNodeName().equals("Interval")){
                    netPositions.add(new ResultInterval(tempNode));
                }
            }
        }
    }
}
