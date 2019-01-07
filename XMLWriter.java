/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import flowcreator.Flow;
import static flowcreator.FlowCreator.timeInt;
import java.io.File;
import org.w3c.dom.Element;

/**
 *
 * @author tdevries
 */
public class XMLWriter extends Flow {

    Element statusElt;
    Element flowBasedTimeSeriesElt;
    F398 flow;
    String n = "0.0";
    String[] lineflows = {"LineFlowTimeSeries", "NEMOHubLineFlowTimeSeries", "SchedulingAreaLineFlowTimeSeries"};
    boolean includeF144 = false;

    public XMLWriter(F398 flow, String path, boolean includeF144) {
        this.includeF144 = includeF144;
        this.F152 = F398Manipulator.F152;
        this.flow = flow;
        this.directory = new File(path + "\\");
        this.flowNumber = 398;
        this.senderShort = "MNA";
        this.documentType = "A52";
        this.documentName = "ns2:ResultsDocument";
        this.timeIntName = "DocumentTimeInterval";

        this.createHeader();

        rootElement.removeChild(processTypeElt);
        rootElement.removeChild(domainElt);

        receiverRoleElt.setAttribute("v", "A36");
        statusElt = doc.createElement("Status");
        rootElement.appendChild(statusElt);
        statusElt.setAttribute("v", "A02");

        flow.lfts.forEach((temp) -> {

            writeLFTS(temp, 0);
        });

        flow.bats.forEach((temp) -> {
            writeBATS(temp);
        });

        flowBasedTimeSeriesElt = doc.createElement("FlowBasedTimeSeries");
        rootElement.appendChild(flowBasedTimeSeriesElt);

        addElement(flowBasedTimeSeriesElt, "TimeSeriesIdentification", flow.FlowBasedTimeSeriesTimeSeriesIdentification);
        addProduct(flowBasedTimeSeriesElt);
        addElement(flowBasedTimeSeriesElt, "BalancingArea", domain, true);
        addCurrencyAndMeasureUnitPrice(flowBasedTimeSeriesElt);

        if (includeF144) {
            flow.constraints.forEach((temp) -> {
                writeFBTS(temp);
            });
        } else {
            flow.fbts.forEach((temp) -> {
                writeFBTS(temp);
            });
        }

        flow.nhlfts.forEach((temp) -> {
            writeLFTS(temp, 1);
        });

        flow.salfts.forEach((temp) -> {
            writeLFTS(temp, 2);
        });
    }

    public void writeLFTS(LineFlowTimeSeries temp, int type) {
        Element lineFlowTimeSeriesElt = doc.createElement(lineflows[type]);
        rootElement.appendChild(lineFlowTimeSeriesElt);

        addElement(lineFlowTimeSeriesElt, "TimeSeriesIdentification", temp.border.timeSeriesIdentification);
        addProduct(lineFlowTimeSeriesElt);
        addElement(lineFlowTimeSeriesElt, "InArea", temp.border.inArea, true);
        addElement(lineFlowTimeSeriesElt, "OutArea", temp.border.outArea, true);
        addMeasureUnit(lineFlowTimeSeriesElt);
        addCurrencyAndMeasureUnitPrice(lineFlowTimeSeriesElt);

        Element periodElt = doc.createElement("Period");
        lineFlowTimeSeriesElt.appendChild(periodElt);

        addTimeIntervalAndResolution(periodElt);

        for (LineFlowTimeSeriesInterval interval : temp.border.intervals) {
            Element intervalElt = doc.createElement("Interval");
            periodElt.appendChild(intervalElt);
            addElement(intervalElt, "Pos", interval.hour);
            addElement(intervalElt, "InQty", n);
            addElement(intervalElt, "OutQty", n);
            addElement(intervalElt, "RoundedInQty", interval.roundedInQty);
            addElement(intervalElt, "RoundedOutQty", interval.roundedOutQty);
            if (type == 0) {
                addElement(intervalElt, "TotalQty", n);
                addElement(intervalElt, "ShadowPriceCapacityAmount", n);
                addElement(intervalElt, "ShadowPriceRampingAmount", n);
                addElement(intervalElt, "ShadowPriceMSFAmount", n);
            }
        }

    }

    public void writeBATS(BiddingAreaTimeSeries temp) {
        Element biddingAreaTimeSeriesElt = doc.createElement("BiddingAreaTimeSeries");
        rootElement.appendChild(biddingAreaTimeSeriesElt);

        addElement(biddingAreaTimeSeriesElt, "TimeSeriesIdentification", temp.TimeSeriesID);
        addProduct(biddingAreaTimeSeriesElt);
        addElement(biddingAreaTimeSeriesElt, "BiddingArea", temp.ba.eic, true);
        addMeasureUnitQuantity(biddingAreaTimeSeriesElt);
        addCurrencyAndMeasureUnitPrice(biddingAreaTimeSeriesElt);

        Element areaResultElt = doc.createElement("AreaResults");
        biddingAreaTimeSeriesElt.appendChild(areaResultElt);

        addResult(areaResultElt, temp.ba, 0);

        for (NemoHub res : temp.ba.nhs) {
            Element nemoHubResultsElt = doc.createElement("NEMOHubResults");
            biddingAreaTimeSeriesElt.appendChild(nemoHubResultsElt);
            addResult(nemoHubResultsElt, res, 1);
        }

        for (SchedulingArea res : temp.ba.sas) {
            Element schedulingAreaResultElt = doc.createElement("SchedulingAreaResults");
            biddingAreaTimeSeriesElt.appendChild(schedulingAreaResultElt);
            addResult(schedulingAreaResultElt, res, 2);
        }

    }

    public void writeFBTS(Constraint temp) {
        Element constraintElt = doc.createElement("Constraint");
        flowBasedTimeSeriesElt.appendChild(constraintElt);

        addElement(constraintElt, "ConstraintIdentification", temp.constraintIdentification);
        Element periodElt = doc.createElement("Period");
        constraintElt.appendChild(periodElt);

        addTimeIntervalAndResolution(periodElt);

        Element intervalElt = doc.createElement("Interval");
        periodElt.appendChild(intervalElt);

        addElement(intervalElt, "Pos", temp.hour);
        addElement(intervalElt, "ShadowPriceAmount", temp.shadowPriceAmount);
    }

    public void addResult(Element parent, Area res, int resultType) {
        //resultType = 0 ==> AreaResults, resultType = 1 ==> NEMOHubResults, resultType = 2 ==> SchedulingAreaResults
        switch (resultType) {
            case 1:
                addElement(parent, "NEMOHub", res.eic, true);
                break;
            case 2:
                addElement(parent, "SchedulingArea", res.eic, true);
                break;
            default:
                break;
        }

        Element periodElt = doc.createElement("Period");
        parent.appendChild(periodElt);
        addTimeIntervalAndResolution(periodElt);

        for (ResultInterval temp : res.netPositions) {
            Element intervalElt = doc.createElement("Interval");
            periodElt.appendChild(intervalElt);

            addElement(intervalElt, "Pos", temp.hour);
            addElement(intervalElt, "NetPositionRounded", temp.netPositionRounded);
            addElement(intervalElt, "NetPositionUnrounded", n);
            if (resultType != 2) {
                addInterval(intervalElt, (resultType == 0));
            }
        }
    }

    public void addInterval(Element parent, boolean isAreaResult) {
        addElement(parent, "MatchedDemandQuantityCurve", n);
        addElement(parent, "MatchedDemandQuantityNonCurve", n);
        addElement(parent, "MatchedDemandQuantityComplex", n);
        addElement(parent, "MatchedSupplyQuantityCurve", n);
        addElement(parent, "MatchedSupplyQuantityNonCurve", n);
        addElement(parent, "MatchedSupplyQuantityComplex", n);
        addElement(parent, "ConsumerSurplus", n);
        addElement(parent, "ProducerSurplus", n);
        if (isAreaResult) {
            addElement(parent, "MarketPriceUnroundedAmount", n);
            addElement(parent, "MarketPriceRoundedAmount", n);
        }
    }

    public void addProduct(Element parent) {
        addElement(parent, "Product", "8716867000016");
    }

    public void addCurrencyAndMeasureUnitPrice(Element parent) {
        addElement(parent, "Currency", "EUR");
        addElement(parent, "MeasureUnitPrice", "MWH");
    }

    public void addMeasureUnitQuantity(Element parent) {
        addElement(parent, "MeasureUnitQuantity", "MAW");
    }

    public void addMeasureUnit(Element parent) {
        addElement(parent, "MeasureUnit", "MAW");
    }

    public void addTimeIntervalAndResolution(Element parent) {
        addElement(parent, "TimeInterval", timeInt);
        addElement(parent, "Resolution", "PT60M");
    }
}
