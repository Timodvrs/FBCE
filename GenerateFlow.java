/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import flowcreator.CSVReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author tdevries
 */
public class GenerateFlow {

   // public int timeStamps = FlowCreator.hours.size() - 1;
    public int timeStamps = 1;
    public Map<String, BiddingAreaTimeSeries> biddingAreasByNH = new HashMap<>();
    public Map<String, BiddingAreaTimeSeries> biddingAreasBySA = new HashMap<>();
    public Map<String, BiddingAreaTimeSeries> biddingAreasByBA = new HashMap<>();
    public Map<String, SchedulingArea> schedulingAreasByNH = new HashMap<>();
    public Map<String, SchedulingArea> schedulingAreasBySA = new HashMap<>();
    public Map<String, NemoHub> nemoHubsByNH = new HashMap<>();
    public Map<String, Border> allBordersByEIC = new HashMap<>();

    F398 flow;
    ArrayList<Border> allBorders;
    CSVReader borders;
    CSVReader areas;

    public GenerateFlow() {

    }

    public void init() {
        flow = new F398();
        flow.FlowBasedTimeSeriesTimeSeriesIdentification = "21";
        borders = new CSVReader("./src/Data/borders.csv");
        areas = new CSVReader("./src/Data/NHSABZ.csv");
        allBorders = new ArrayList<>();
        GenerateAreas();
        GenerateBorders();
        GenerateLFTS();
        flow.lfts = sortLFTS(flow.lfts);
        flow.nhlfts = sortLFTS(flow.nhlfts);
        flow.salfts = sortLFTS(flow.salfts);
        CleanEmptySAs();
        GenerateBATS();
        GenerateFBTS();
    }

    public void GenerateFBTS() {
        for (int i = 0; i < 5262; i++) {
            Constraint temp = new Constraint();
            temp.constraintIdentification = Integer.toString(i + 1);
            Random rand = new Random();
            int n = rand.nextInt(timeStamps) + 1;
            temp.hour = Integer.toString(n);
            temp.shadowPriceAmount = "0.0";
            flow.fbts.add(temp);
        }
    }

    public void CleanEmptySAs() {
        for (BiddingAreaTimeSeries temp : flow.bats) {
            if (temp.ba.sas.get(0).eic.length() < 2) {
                temp.ba.sas.remove(0);
            }
        }
    }

    public void GenerateBATS() {
        //rounded in bij outArea - rounded out bij inArea
        for (BiddingAreaTimeSeries temp : flow.bats) {
            temp.ba.netPositions = calcNetPos(temp.ba);

            for (SchedulingArea tempSA : temp.ba.sas) {
                tempSA.netPositions = calcNetPos(tempSA);
               // System.out.println(tempSA.eic);
            }

            for (NemoHub tempNH : temp.ba.nhs) {
                System.out.println(tempNH.eic + " in " + temp.ba.eic);
                tempNH.netPositions = calcNetPos(tempNH);
            }
        }
    }

    public ArrayList<ResultInterval> calcNetPos(Area area) {
        System.out.print("NP for: " + area.eic + ": \t");
        ArrayList<ResultInterval> res = new ArrayList<>();
        for (int i = 0; i < timeStamps; i++) {
            double sum = 0;
            //System.out.println(area.outBorders.size());
            for (Border tempBorder : area.outBorders) {
                sum += tempBorder.intervals.get(i).inQty;
                   System.out.print("out: " + tempBorder.inArea + " ");
            }
            for (Border tempBorder : area.inBorders) {
                sum -= tempBorder.intervals.get(i).outQty;
                  System.out.print("in: " + tempBorder.outArea + " ");
            }
            System.out.print("\n");
            ResultInterval temp = new ResultInterval(Integer.toString(i + 1));
            temp.netPos = sum;
            temp.netPositionRounded = formatNumber(sum);
            res.add(temp);
        }
        return res;
    }

    public void GenerateLFTS() {
        initAreas();
        for (String keyNH : nemoHubsByNH.keySet()) {
            for (Border temp : nemoHubsByNH.get(keyNH).inBorders) {
                //System.out.println(keyNH + " " + temp.inArea + " " + temp.outArea);
                for (int i = 0; i < timeStamps; i++) {
                    LineFlowTimeSeriesInterval tempInt = new LineFlowTimeSeriesInterval(Integer.toString(i + 1), "0.0", "0.0");
                    if (temp.exports) {
                        tempInt.outQty = Math.floor(-Math.log(Math.random()) * 7500) / 10;
                        tempInt.inQty = tempInt.outQty;
                        tempInt.roundedInQty = formatNumber(tempInt.inQty);
                        tempInt.roundedOutQty = formatNumber(tempInt.outQty);
                        //System.out.println(tempInt.outQty);
                        System.out.println(temp.inArea + " <-> " + temp.outArea);
                        fillParent(tempInt, temp, i);
                    } else {
                        fillParent(tempInt, temp, i);
                    }

                    temp.intervals.add(tempInt);
                    System.out.println(temp.inArea + " " + temp.outArea);
                }
                LineFlowTimeSeries lfts = new LineFlowTimeSeries();
                lfts.border = temp;
                flow.nhlfts.add(lfts);
            }
        }
    }

    public void fillParent(LineFlowTimeSeriesInterval tempInt, Border temp, int i) {
        if (!temp.in.parentBA.eic.equals(temp.out.parentBA.eic)) {
            for (Border tempParent : temp.in.parentBA.inBorders) {
                if (tempParent.outArea.equals(temp.out.parentBA.eic)) {
                    LineFlowTimeSeriesInterval parentInt = tempParent.intervals.get(i);
                    parentInt.inQty += tempInt.inQty;
                    parentInt.outQty += tempInt.outQty;
                    parentInt.roundedInQty = formatNumber(parentInt.inQty);
                    parentInt.roundedOutQty = formatNumber(parentInt.outQty);
                }
            }
        }
        if (!temp.in.parentSA.eic.equals(temp.out.parentSA.eic)) {
            for (Border tempParent : temp.in.parentSA.inBorders) {
                if (tempParent.outArea.equals(temp.out.parentSA.eic)) {
                    LineFlowTimeSeriesInterval parentInt = tempParent.intervals.get(i);
                    parentInt.inQty += tempInt.inQty;
                    parentInt.outQty += tempInt.outQty;
                    parentInt.roundedInQty = formatNumber(parentInt.inQty);
                    parentInt.roundedOutQty = formatNumber(parentInt.outQty);
                }
            }
        }
    }

    public void initAreas() {
        schedulingAreasBySA.keySet().stream().filter((keySA) -> (keySA.length() > 0)).forEachOrdered((keySA) -> {
            schedulingAreasBySA.get(keySA).inBorders.forEach((temp) -> {
                initIntervals(temp, false);
            });
        });
        biddingAreasByBA.keySet().stream().filter((keyBA) -> (keyBA.length() > 0)).forEachOrdered((keyBA) -> {
            biddingAreasByBA.get(keyBA).ba.inBorders.forEach((temp) -> {
                initIntervals(temp, true);
            });
        });
    }

    public void initIntervals(Border temp, boolean isBA) {
        for (int i = 0; i < timeStamps; i++) {
            LineFlowTimeSeriesInterval tempInt = new LineFlowTimeSeriesInterval(Integer.toString(i + 1), "0.0", "0.0");
            tempInt.outQty = 0;
            tempInt.inQty = 0;
            temp.intervals.add(tempInt);
        }
        LineFlowTimeSeries lfts = new LineFlowTimeSeries();
        lfts.border = temp;
        if (isBA) {
            flow.lfts.add(lfts);
        } else {
            flow.salfts.add(lfts);
        }
    }

    public void GenerateBorders() {
        for (String[] line : borders.generators) {
            boolean exports = ThreadLocalRandom.current().nextBoolean();
            Border borderOne = new Border(line[0], line[2], line[3], exports);
            Border borderTwo = new Border(line[1], line[3], line[2], !exports);
            allBorders.add(borderOne);
            allBorders.add(borderTwo);
            allBordersByEIC.put(line[2] + line[3], borderOne);
            allBordersByEIC.put(line[3] + line[2], borderTwo);
            String set = line[0].substring(0, 1);
            switch (set) {
                case "3":
                    borderOne.in = biddingAreasByBA.get(line[2]).ba;
                    borderOne.out = biddingAreasByBA.get(line[3]).ba;
                    borderTwo.in = biddingAreasByBA.get(line[3]).ba;
                    borderTwo.out = biddingAreasByBA.get(line[2]).ba;
                    biddingAreasByBA.get(line[2]).ba.inBorders.add(borderOne);
                    biddingAreasByBA.get(line[3]).ba.inBorders.add(borderTwo);
                    biddingAreasByBA.get(line[3]).ba.outBorders.add(borderOne);
                    biddingAreasByBA.get(line[2]).ba.outBorders.add(borderTwo);
                    borderOne.level = 2;
                    borderTwo.level = 2;
                    break;
                case "8":
                    System.out.println(line[3] + " gets " + borderOne.inArea + " as in, and " + borderOne.outArea + " as out");
                    System.out.println(line[2] + " gets " + borderTwo.inArea + " as in, and " + borderTwo.outArea + " as out");
                    borderOne.in = nemoHubsByNH.get(line[2]);
                    borderOne.out = nemoHubsByNH.get(line[3]);
                    borderTwo.in = nemoHubsByNH.get(line[3]);
                    borderTwo.out = nemoHubsByNH.get(line[2]);
                    nemoHubsByNH.get(line[2]).inBorders.add(borderOne);
                    nemoHubsByNH.get(line[3]).inBorders.add(borderTwo);
                    nemoHubsByNH.get(line[3]).outBorders.add(borderOne);
                    nemoHubsByNH.get(line[2]).outBorders.add(borderTwo);
                    System.out.println(nemoHubsByNH.get(line[2]).eic + " " + nemoHubsByNH.get(line[3]).eic);
                    borderOne.level = 0;
                    borderTwo.level = 0;
                    FindParent(borderOne);
                    FindParent(borderTwo);
                    break;
                case "9":
                    borderOne.in = schedulingAreasBySA.get(line[2]);
                    borderOne.out = schedulingAreasBySA.get(line[3]);
                    borderTwo.in = schedulingAreasBySA.get(line[3]);
                    borderTwo.out = schedulingAreasBySA.get(line[2]);
                    schedulingAreasBySA.get(line[2]).inBorders.add(borderOne);
                    schedulingAreasBySA.get(line[3]).inBorders.add(borderTwo);
                    schedulingAreasBySA.get(line[3]).outBorders.add(borderOne);
                    schedulingAreasBySA.get(line[2]).outBorders.add(borderTwo);
                    borderOne.level = 1;
                    borderTwo.level = 1;
                    break;
            }
        }
    }

    public void FindParent(Border border) {
        border.in.parentBA.inBorders.stream().filter((temp) -> (temp.outArea.equals(border.out.parentBA.eic))).forEachOrdered((temp) -> {
            border.parentBA = temp;
        });
        border.in.parentSA.inBorders.stream().filter((temp) -> (temp.outArea.equals(border.out.parentSA.eic))).forEachOrdered((temp) -> {
            border.parentSA = temp;
        });
    }

    public void GenerateAreas() {
        areas.generators.forEach((line) -> {
            NemoHub newNH = new NemoHub();
            newNH.eic = line[1];
            nemoHubsByNH.put(newNH.eic, newNH);
            if (biddingAreasByBA.containsKey(line[3])) {
                biddingAreasByNH.put(newNH.eic, biddingAreasByBA.get(line[3]));
                newNH.parentBA = biddingAreasByBA.get(line[3]).ba;
                if (schedulingAreasBySA.containsKey(line[2])) {
                    schedulingAreasBySA.get(line[2]).nhs.add(newNH);
                    schedulingAreasByNH.put(newNH.eic, schedulingAreasBySA.get(line[2]));
                    newNH.parentSA = schedulingAreasBySA.get(line[2]);
                } else {
                    SchedulingArea newSA = new SchedulingArea();
                    newSA.eic = line[2];
                    newSA.nhs.add(newNH);
                    biddingAreasBySA.put(newSA.eic, biddingAreasByBA.get(line[3]));
                    schedulingAreasBySA.put(newSA.eic, newSA);
                    schedulingAreasByNH.put(newNH.eic, newSA);
                    newNH.parentSA = newSA;
                    biddingAreasByNH.get(newNH.eic).ba.sas.add(newSA);
                }
                biddingAreasByNH.get(newNH.eic).ba.nhs.add(newNH);
            } else {
                BiddingAreaTimeSeries newBA = newBA(line);
                SchedulingArea newSA = new SchedulingArea();
                newSA.eic = line[2];
                newSA.nhs.add(newNH);
                biddingAreasByBA.put(newBA.ba.eic, newBA);
                biddingAreasBySA.put(newSA.eic, biddingAreasByBA.get(line[3]));
                biddingAreasByNH.put(newNH.eic, biddingAreasByBA.get(line[3]));
                schedulingAreasBySA.put(newSA.eic, newSA);
                schedulingAreasByNH.put(newNH.eic, newSA);
                newNH.parentSA = newSA;
                newNH.parentBA = newBA.ba;
            }
        });
    }

    public BiddingAreaTimeSeries newBA(String[] line) {
        NemoHub newNH = new NemoHub();
        newNH.eic = line[1];
        SchedulingArea newSA = new SchedulingArea();
        newSA.eic = line[2];
        newSA.nhs.add(newNH);
        BiddingAreaTimeSeries newBA = new BiddingAreaTimeSeries();
        newBA.TimeSeriesID = line[0];
        newBA.ba.eic = line[3];
        newBA.ba.nhs.add(newNH);
        newBA.ba.sas.add(newSA);
        if (newSA.eic.length() > 0) {
            schedulingAreasBySA.put(newSA.eic, newSA);
            biddingAreasBySA.put(newSA.eic, newBA);
        }
        biddingAreasByBA.put(newBA.ba.eic, newBA);
        flow.bats.add(newBA);
        return newBA;
    }

    public String formatNumber(double number) {
        String res = String.format(Locale.ROOT, "%.1f", number);
        return res;
    }

    public void mergeSA(SchedulingArea one, SchedulingArea two) {
        for (int i = 0; i < two.nhs.size() - 1; i++) {
            one.nhs.add(two.nhs.get(i));
        }
        two.netPositions.forEach((temp) -> {
            one.netPositions.add(temp);
        });
    }

    public void mergeBA(BiddingArea one, BiddingArea two) {
        two.nhs.forEach((temp) -> {
            nemoHubsByNH.put(temp.eic, temp);
            one.nhs.add(temp);
        });
        boolean sameSAfound = false;
        for (int j = 0; j < two.sas.size(); j++) {
            SchedulingArea temp = two.sas.get(j);
            for (int i = 0; i < one.sas.size(); i++) {

                SchedulingArea tempi = one.sas.get(i);
                if (tempi.eic.equals(temp.eic)) {
                    mergeSA(tempi, temp);
                    sameSAfound = true;
                }
            }
            if (!sameSAfound) {
                schedulingAreasBySA.put(temp.eic, temp);
                schedulingAreasByNH.put(one.nhs.get(one.nhs.size() - 1).eic, temp);
                one.sas.add(temp);
            }
        }
        two.netPositions.forEach((temp) -> {
            one.netPositions.add(temp);
        });
    }

    public ArrayList<LineFlowTimeSeries> sortLFTS(ArrayList<LineFlowTimeSeries> temp) {
        Collections.sort(temp, Comparator.comparing(LineFlowTimeSeries::getID));
        return temp;
    }

    public static void main(String[] args) {
        GenerateFlow it = new GenerateFlow();

    }
}
