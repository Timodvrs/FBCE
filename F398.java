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
public class F398 {
    public String FlowBasedTimeSeriesTimeSeriesIdentification;
    public ArrayList<BiddingAreaTimeSeries> bats;
    public ArrayList<Constraint> fbts;
    public ArrayList<LineFlowTimeSeries> lfts;
    public ArrayList<LineFlowTimeSeries> nhlfts;
    public ArrayList<LineFlowTimeSeries> salfts;
    public ArrayList<Constraint> constraints;
    
    public F398(){
        fbts = new ArrayList<>();
        bats = new ArrayList<>();
        lfts = new ArrayList<>();
        nhlfts = new ArrayList<>();
        salfts = new ArrayList<>();
    }
}
