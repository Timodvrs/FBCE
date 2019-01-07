/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowcreator;

import static flowcreator.FlowCreator.businessDay;
import static flowcreator.FlowCreator.bdType;
import static flowcreator.FlowCreator.creationDateTime;
import static flowcreator.FlowCreator.dateInputFormat;
import static flowcreator.FlowCreator.dateOutputFormat;
import static flowcreator.FlowCreator.hours;
import static flowcreator.FlowCreator.nextBusinessDay;
import static flowcreator.FlowCreator.offPeakHours;
import static flowcreator.FlowCreator.peakHours;
import static flowcreator.FlowCreator.previousBusinessDay;
import static flowcreator.FlowCreator.timeInt;
import static flowcreator.FlowCreator.timeIntervalDateFormat;
import static flowcreator.FlowCreator.timeIntervalTimeFormat;
import static flowcreator.FlowCreator.today;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author tdevries
 */
public class DateHandler {
     public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    
    public static String createTimeInterval(int start, int end) {
        String startDate;
        String endDate;
        end = Math.min(end, hours.size()-1);
        int startHour = hours.get(start);
        int endHour = hours.get(end);
        
        if (start < 2 && startHour > 21) {
            startDate = timeIntervalDateFormat.format(previousBusinessDay);
        } else {
            startDate = timeIntervalDateFormat.format(businessDay);
        }
        
        if (end < 2 && endHour > 21) {
            endDate = timeIntervalDateFormat.format(previousBusinessDay);
        }else {
            endDate = timeIntervalDateFormat.format(businessDay);
        }
        
        return startDate + "T"+ startHour + ":00Z/" + endDate + "T" + endHour + ":00Z";
    }
    
    public static String createTimeInterval() {
        return createTimeInterval(0,25);
    }
    
    public static void handleDate(String dateInput){
        Date todaysDate = new Date();
        
        hours = new ArrayList<>(
        Arrays.asList(23, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 , 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22));
        
        today = dateOutputFormat.format(todaysDate);     
        creationDateTime = timeIntervalDateFormat.format(todaysDate) + "T" + timeIntervalTimeFormat.format(todaysDate) + "Z";
	String dateBusinessDay = dateInput.split("-")[0] + dateInput.split("-")[1] + dateInput.split("-")[2];

        try {
            businessDay = dateInputFormat.parse(dateBusinessDay);
            nextBusinessDay = addDays(businessDay, 1);
            previousBusinessDay = addDays(businessDay, -1);
              
            if(TimeZone.getDefault().inDaylightTime(businessDay)){
                hours.add(0, 22);
                if(TimeZone.getDefault().inDaylightTime(nextBusinessDay)){
                    bdType = "Summer";
                } else {
                    bdType = "LCC";
                }
            } else {
                if(TimeZone.getDefault().inDaylightTime(nextBusinessDay)){
                    bdType = "SCC";
                } else {
                    bdType = "Winter";
                }
            }
            if(!TimeZone.getDefault().inDaylightTime(nextBusinessDay)){
                hours.add(23);
            }

        } catch (ParseException ex) {

        }
        
        timeInt = createTimeInterval();
        for(int i = 1; i < hours.size(); i++){
            if (i < 7 || i > hours.size() - 3){
                offPeakHours.add(Integer.toString(i));
            } else {
                peakHours.add(Integer.toString(i));
            }
        }

    }  
}
