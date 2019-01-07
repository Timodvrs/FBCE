/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import flowcreator.FlowCreator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author tdevries
 */
public class XLSWriter {
    
    public String filename;
    HSSFWorkbook workbook = new HSSFWorkbook();
    CreationHelper createHelper = workbook.getCreationHelper();
    HSSFSheet lfts = workbook.createSheet("LineFlowTimeSeries");
    HSSFSheet bats = workbook.createSheet("BiddingAreaTimeSeries");
    HSSFSheet fbts = workbook.createSheet("FlowBasedTimeSeries");
    HSSFSheet data = workbook.createSheet("data");
    boolean includeF144 = false;

    public XLSWriter(Reader flow, String path, boolean includeF144) {
        this.includeF144 = includeF144;
        filename = path + "\\" + "PMB results.xls";
        init();
        fillLfts(flow.lfts, "LineFlowTimeSeries");
        fillLfts(flow.salfts, "SchedulingAreaLineFlowTimeSeries");
        fillLfts(flow.nhlfts, "NEMOHubLineFlowTimeSeries");
        fillBats(flow.bats);
        if (includeF144){
            fillFbts(flow.constraints, flow.FlowBasedTimeSeriesTimeSeriesIdentification);
        } else {
            fillFbts(flow.fbts, flow.FlowBasedTimeSeriesTimeSeriesIdentification);
        }
        SaveXLSWriter();

    }
    // TimeSeriesIdentification	ConstraintIdentification	Hour	ShadowPriceAmount

    public void fillFbts(ArrayList<Constraint> temp, String id) {
        for (Constraint item : temp) {
            HSSFRow row = fbts.createRow(fbts.getLastRowNum() + 1);
            row.createCell(0).setCellValue(id);
            row.createCell(1).setCellValue(item.constraintIdentification);
            row.createCell(2).setCellValue(item.hour);
            row.createCell(3).setCellValue(item.shadowPriceAmount);
        }
    }

    public void fillBats(ArrayList<BiddingAreaTimeSeries> temp) {
        temp.stream().map((item) -> {
            item.ba.netPositions.forEach((resInt) -> {
                fillBatsRow(item.TimeSeriesID, "AreaResults", item.ba.eic, resInt.hour, resInt.netPositionRounded);
            });
            return item;
        }).map((item) -> {
            item.ba.sas.forEach((sar) -> {
                sar.netPositions.forEach((resInt) -> {
                    fillBatsRow(item.TimeSeriesID, "SchedulingAreaResults", sar.eic, resInt.hour, resInt.netPositionRounded);
                });
            });
            return item;
        }).forEachOrdered((item) -> {
            item.ba.nhs.forEach((nhr) -> {
                nhr.netPositions.forEach((resInt) -> {
                    fillBatsRow(item.TimeSeriesID, "NEMOHubResults", nhr.eic, resInt.hour, resInt.netPositionRounded);
                });
            });
        });
    }

    public void fillBatsRow(String timeSeriesIdentification, String type, String eic, String hour, String netPositionRounded) {
        HSSFRow row = bats.createRow(bats.getLastRowNum() + 1);
        row.createCell(0).setCellValue(timeSeriesIdentification);
        row.createCell(1).setCellValue(type);
        row.createCell(2).setCellValue(eic);
        row.createCell(3).setCellValue(hour);
        row.createCell(4).setCellValue(netPositionRounded);

    }

    public void fillLfts(ArrayList<LineFlowTimeSeries> temp, String type) {
        temp.forEach((item) -> {
            item.border.intervals.forEach((interval) -> {
                HSSFRow row = lfts.createRow(lfts.getLastRowNum() + 1);
                row.createCell(0).setCellValue(type);
                row.createCell(1).setCellValue(item.border.timeSeriesIdentification);
                row.createCell(2).setCellValue(item.border.inArea);
                row.createCell(3).setCellValue(item.border.outArea);
                row.createCell(4).setCellValue(interval.hour);
                row.createCell(5).setCellValue(interval.roundedInQty);
                row.createCell(6).setCellValue(interval.roundedOutQty);
            });
        });
    }

    public void init() {
        HSSFRow dataHeader = data.createRow(0);
        
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(
        createHelper.createDataFormat().getFormat("dd-mm-yyyy"));
        
        HSSFCell dateCell = dataHeader.createCell(0);
        dateCell.setCellValue(FlowCreator.businessDayString);
        dateCell.setCellStyle(cellStyle);
        
        dataHeader.createCell(1).setCellValue(FlowCreator.eicSenderPMB);
        System.out.println(FlowCreator.eicSenderPMB);
        dataHeader.createCell(2).setCellValue((FlowCreator.eicSenderPMB.equals("17X100A100M006F3") ? "152" : "398"));
        dataHeader.createCell(3).setCellValue(FlowCreator.version);
        
        HSSFRow lftsHeader = lfts.createRow(0);
        lftsHeader.createCell(0).setCellValue("Type");
        lftsHeader.createCell(1).setCellValue("TimeSeriesIdentification");
        lftsHeader.createCell(2).setCellValue("InArea");
        lftsHeader.createCell(3).setCellValue("OutArea");
        lftsHeader.createCell(4).setCellValue("Hour");
        lftsHeader.createCell(5).setCellValue("RoundedInQty");
        lftsHeader.createCell(6).setCellValue("RoundedOutQty");

        HSSFRow batsHeader = bats.createRow(0);
        batsHeader.createCell(0).setCellValue("TimeSeriesIdentification");
        batsHeader.createCell(1).setCellValue("Type");
        batsHeader.createCell(2).setCellValue("Area");
        batsHeader.createCell(3).setCellValue("Hour");
        batsHeader.createCell(4).setCellValue("NetPositionRounded");

        HSSFRow fbtsHeader = fbts.createRow(0);
        fbtsHeader.createCell(0).setCellValue("TimeSeriesIdentification");
        fbtsHeader.createCell(1).setCellValue("ConstraintIdentification");
        fbtsHeader.createCell(2).setCellValue("Hour");
        fbtsHeader.createCell(3).setCellValue("ShadowPriceAmount");
    }

    public void SaveXLSWriter() {
        for (int i = 0; i < 7; i++) {
            lfts.autoSizeColumn(i);
            bats.autoSizeColumn(i);
            fbts.autoSizeColumn(i);
        }

        lfts.createFreezePane(0, 1);
        bats.createFreezePane(0, 1);
        fbts.createFreezePane(0, 1);

        lfts.setAutoFilter(CellRangeAddress.valueOf("A1:G1"));
        bats.setAutoFilter(CellRangeAddress.valueOf("A1:E1"));
        fbts.setAutoFilter(CellRangeAddress.valueOf("A1:D1"));

        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            System.out.println("Your excel file has been generated!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Close the PMB results file first!");
            //Logger.getLogger(XLSWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
