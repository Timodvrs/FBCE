/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import java.util.ArrayList;

/**
 *
 * @author tdevries
 */
public class Border {
    public Area in;
    public Area out;
    public String inArea;
    public String outArea;
    public String timeSeriesIdentification;
    public boolean exports;
    public int level;
    public ArrayList<LineFlowTimeSeriesInterval> intervals;
    public Border parentSA;
    public Border parentBA;
    //level 0: NH, 1: SA, 2: BZ
    
    public Border(){
        intervals = new ArrayList<>();
    }
    
    public Border (String id, String inArea, String outArea, boolean exports){
        this.timeSeriesIdentification = id;
        this.inArea = inArea;
        this.outArea = outArea;
        this.exports = exports;
        intervals = new ArrayList<>();
    }
    
    public String getTimeSeriesIdentification(){
        return this.timeSeriesIdentification;
    }
    
    
}
