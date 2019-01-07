/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import flowcreator.DateHandler;
import flowcreator.FlowCreator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Reader extends F398 {

    File flow144; //= new File("C:\\Users\\tdevries\\Documents\\Experimenten\\constraintID\\EXT7_17XTSO-CS------W_17X100A100M006F3_CWE-FB-A26A01-144_20181009-F144-01.xml");    
    Workbook workbook;

    public Reader(boolean isXML, File file, File flow144) throws IOException, InvalidFormatException {
        this.flow144 = flow144;
        this.fbts = new ArrayList<>();
        this.bats = new ArrayList<>();
        this.lfts = new ArrayList<>();
        this.nhlfts = new ArrayList<>();
        this.salfts = new ArrayList<>();

        if (isXML) {
            try {
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                DocumentBuilder dBuilder2 = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                if (flow144 != (null)) {
                    Document doc144 = dBuilder2.parse(flow144);

                    this.constraints = new ArrayList<>();
                    if (doc144.hasChildNodes()) {
                        NodeList tempNodeList = read144(doc144.getChildNodes(), "FlowBasedParameterDocument");
                        tempNodeList = read144(tempNodeList, "FlowBasedParameterTimeSeries");
                        tempNodeList = read144(tempNodeList, "Period");;
                        for (int i = 0; i < tempNodeList.getLength(); i++) {
                            Node tempNode = tempNodeList.item(i);
                            if (tempNode.getNodeName().equals("Interval") && tempNode.getNodeType() == Node.ELEMENT_NODE) {
                                fillConstraints(tempNode.getChildNodes());
                            }
                        }
                    }
                }

                if (doc.hasChildNodes()) {
                    for (int i = 0; i < 4; i++) {
                        if (doc.getChildNodes().item(i).getNodeName().equals("ns2:ResultsDocument")) {
                            addNode(doc.getChildNodes().item(i).getChildNodes());
                            break;
                        }
                    }
                }

            } catch (IOException | ParserConfigurationException | SAXException e) {
                System.out.println(e.getMessage());
            }
        } else {
            workbook = WorkbookFactory.create(file);
            for (Sheet sheet : workbook) {
                switch (sheet.getSheetName()) {
                    case "LineFlowTimeSeries":
                        addLineFlowTimeSeries(sheet);
                        break;
                    case "BiddingAreaTimeSeries":
                        addBiddingAreaTimeSeries(sheet);
                        break;
                    case "FlowBasedTimeSeries":
                        addFlowBasedTimeSeries(sheet);
                        break;
                    case "data":
                        readData(sheet);
                        break;
                    default:
                        break;
                }
            }
            workbook.close();
        }
    }

    public void fillConstraints(NodeList nodeList) {
        String pos = "0";
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String tempName = tempNode.getNodeName();
                switch (tempName) {
                    case "Pos":
                        pos = tempNode.getAttributes().item(0).getNodeValue();
                        break;
                    case "Constraint":
                        Constraint tempCons = new Constraint();
                        tempCons.constraintIdentification = (tempNode.getChildNodes().item(1).getAttributes().item(0).getNodeValue());
                        tempCons.hour = pos;
                        tempCons.shadowPriceAmount = "0.0";
                        this.constraints.add(tempCons);
                        break;
                }
            }
        }
    }

    public NodeList read144(NodeList nodeList, String nodeName) {
        NodeList res = nodeList;
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String tempName = tempNode.getNodeName();
                if (tempName.equals(nodeName)) {
                    res = tempNode.getChildNodes();
                }
            }
        }
        return res;
    }

    public void addBiddingAreaTimeSeries(Node node) {
        BiddingAreaTimeSeries temp = new BiddingAreaTimeSeries(node);
        bats.add(temp);
    }

    private void addBiddingAreaTimeSeries(Sheet sheet) {
        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            String[] cells = readRow(sheet.getRow(i));
            if (!bats.isEmpty() && bats.get(bats.size() - 1).TimeSeriesID.equals(cells[0])) {
                updateBATS(bats.get(bats.size() - 1), cells);
            } else {
                BiddingAreaTimeSeries temp = new BiddingAreaTimeSeries();
                temp.TimeSeriesID = cells[0];
                temp.ba.eic = cells[2];
                updateBATS(temp, cells);
                bats.add(temp);
            }
        }
    }

    public void addFlowBasedTimeSeries(Node node) {
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);

            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.getNodeName().equals("TimeSeriesIdentification")) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    FlowBasedTimeSeriesTimeSeriesIdentification = nodeMap.item(0).getNodeValue();
                } else if (tempNode.getNodeName().equals("Constraint")) {
                    fbts.add(new Constraint(tempNode));
                }
            }
        }
    }

    private void addFlowBasedTimeSeries(Sheet sheet) {
        FlowBasedTimeSeriesTimeSeriesIdentification = "";

        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            String[] cells = readRow(sheet.getRow(i));
            if (i == 1) {
                FlowBasedTimeSeriesTimeSeriesIdentification = cells[0];
            }
            Constraint cons = new Constraint();
            cons.constraintIdentification = cells[1];
            cons.hour = cells[2];
            cons.shadowPriceAmount = cells[3];
            fbts.add(cons);
        }
    }

    public void addLineFlowTimeSeries(ArrayList<LineFlowTimeSeries> list, Node node) {
        LineFlowTimeSeries temp = new LineFlowTimeSeries();
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node tempNode = nodeList.item(i);

            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String tempName = tempNode.getNodeName();
                switch (tempName) {
                    case "TimeSeriesIdentification":
                        NamedNodeMap nodeMapTSI = tempNode.getAttributes();
                        temp.border.timeSeriesIdentification = nodeMapTSI.item(0).getNodeValue();
                        break;
                    case "InArea":
                        temp.addArea(tempNode, true);
                        break;
                    case "OutArea":
                        temp.addArea(tempNode, false);
                        break;
                    case "Period":
                        temp.addPeriod(tempNode);
                        break;
                    default:
                        break;
                }
            }
        }
        list.add(temp);
    }

    private void addLineFlowTimeSeries(Sheet sheet) {
        for (Row row : sheet) {
            String[] cells = readRow(row);
            switch (cells[0]) {
                case "LineFlowTimeSeries":
                    addRowToLineFlowTimeSeries(lfts, cells);
                    break;
                case "SchedulingAreaLineFlowTimeSeries":
                    addRowToLineFlowTimeSeries(salfts, cells);
                    break;
                case "NEMOHubLineFlowTimeSeries":
                    addRowToLineFlowTimeSeries(nhlfts, cells);
                    break;
                default:
                    break;
            }
        }
    }

    private void addNode(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);

            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                String tempName = tempNode.getNodeName();
                switch (tempName) {
                    case "FlowBasedTimeSeries":
                        addFlowBasedTimeSeries(tempNode);
                        break;
                    case "BiddingAreaTimeSeries":
                        addBiddingAreaTimeSeries(tempNode);
                        break;
                    case "LineFlowTimeSeries":
                        addLineFlowTimeSeries(lfts, tempNode);
                        break;
                    case "NEMOHubLineFlowTimeSeries":
                        addLineFlowTimeSeries(nhlfts, tempNode);
                        break;
                    case "SchedulingAreaLineFlowTimeSeries":
                        addLineFlowTimeSeries(salfts, tempNode);
                        break;
                    case "SenderIdentification":
                        NamedNodeMap nodeMap = tempNode.getAttributes();
                        FlowCreator.eicSenderPMB = nodeMap.item(1).getNodeValue();
                        break;
                    case "DocumentTimeInterval":
                        NamedNodeMap nodeMapTimeInt = tempNode.getAttributes();
                        String timeInt = nodeMapTimeInt.item(0).getNodeValue();
                        String[] tempDate = timeInt.split("/")[1].split("-");
                        FlowCreator.businessDayString = tempDate[2].substring(0, 2) + "-" + tempDate[1] + "-" + tempDate[0];
                        break;
                    case "DocumentVersion":
                        NamedNodeMap nodeMapVersion = tempNode.getAttributes();
                        FlowCreator.version = Integer.parseInt(nodeMapVersion.item(0).getNodeValue()) + 1;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void addSA(ArrayList<SchedulingArea> res, String[] cells, ResultInterval tempResInt) {
        if (!res.isEmpty() && res.get(res.size() - 1).eic.equals(cells[2])) {
            res.get(res.size() - 1).netPositions.add(tempResInt);
        } else {
            SchedulingArea tempRes = new SchedulingArea();
            tempRes.eic = cells[2];
            tempRes.netPositions.add(tempResInt);
            res.add(tempRes);
        }
    }

    private void addNH(ArrayList<NemoHub> res, String[] cells, ResultInterval tempResInt) {
        if (!res.isEmpty() && res.get(res.size() - 1).eic.equals(cells[2])) {
            res.get(res.size() - 1).netPositions.add(tempResInt);
        } else {
            NemoHub tempRes = new NemoHub();
            tempRes.eic = cells[2];
            tempRes.netPositions.add(tempResInt);
            res.add(tempRes);
        }
    }

    private void addRowToLineFlowTimeSeries(ArrayList<LineFlowTimeSeries> list, String[] cells) {
        if (!list.isEmpty() && list.get(list.size() - 1).border.timeSeriesIdentification.equals(cells[1])) {
            list.get(list.size() - 1).border.intervals.add(new LineFlowTimeSeriesInterval(cells[4], cells[5], cells[6]));
        } else {
            LineFlowTimeSeries temp = new LineFlowTimeSeries();
            temp.border.timeSeriesIdentification = cells[1];
            temp.border.inArea = cells[2];
            temp.border.outArea = cells[3];
            temp.border.intervals.add(new LineFlowTimeSeriesInterval(cells[4], cells[5], cells[6]));
            list.add(temp);
        }
    }

    private void readData(Sheet sheet) {
        for (Row row : sheet) {
            String[] cells = readRow(row);
            FlowCreator.businessDayString = cells[0];
            if (cells[2].equals("152")) {
                F398Manipulator.F152 = true;
            }
            FlowCreator.eicSenderPMB = cells[1];
            DateHandler.handleDate(FlowCreator.businessDayString);
            FlowCreator.version = Integer.parseInt(cells[3]);
        }
    }

    public String[] readRow(Row row) {
        DataFormatter dataFormatter = new DataFormatter();
        String[] cells = new String[row.getLastCellNum()];
        for (int i = 0; i < row.getLastCellNum(); i++) {
            cells[i] = dataFormatter.formatCellValue(row.getCell(i));
        }
        return cells;
    }

    private void updateBATS(BiddingAreaTimeSeries temp, String[] cells) {
        ResultInterval tempResInt = new ResultInterval(cells[3], cells[4]);
        switch (cells[1]) {
            case "AreaResults":
                if (temp.ba.netPositions.isEmpty()) {
                    temp.ba.eic = cells[2];
                }
                temp.ba.netPositions.add(tempResInt);
                break;
            case "SchedulingAreaResults":
                addSA(temp.ba.sas, cells, tempResInt);
                break;
            case "NEMOHubResults":
                addNH(temp.ba.nhs, cells, tempResInt);
                break;
            default:
                break;
        }
    }

}
