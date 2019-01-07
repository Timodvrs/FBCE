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
public class BiddingArea extends Area {

    public ArrayList<SchedulingArea> sas;
    public ArrayList<NemoHub> nhs;

    public BiddingArea() {
        sas = new ArrayList<>();
        nhs = new ArrayList<>();
    }

    
}
