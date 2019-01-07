/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Result {
    public ArrayList<ResultInterval> intervals;
    public String eic;
    
    public Result(Node node){
        init(node);
    }
    
    public Result(){
        intervals = new ArrayList<>();
    }

    public void init(Node node){
        intervals = new ArrayList<>();
        
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);

            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if(tempNode.getNodeName().equals("Interval")){
                    intervals.add(new ResultInterval(tempNode));
                }
            }
        }
    }

    
}
