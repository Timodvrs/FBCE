package flowcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author tdevries
 */
public class FlowCreator {
    public static String path = "C:\\Users\\tdevries\\Documents\\Experimenten\\";
    public static String businessDayString = "26-09-2018";
    public static int version = 1;
    public static String bdType;
    public static String eicSenderPMB = "17X100A100M003CI";
       
    public static DateFormat dateInputFormat = new SimpleDateFormat("ddMMyyyy");
    public static DateFormat timeIntervalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat timeIntervalTimeFormat = new SimpleDateFormat("HH:mm:ss");
    public static DateFormat dateOutputFormat = new SimpleDateFormat("yyyyMMdd");
    
    public static String signatureStr = "Created by Timo de Vries. For questions: tdevries@sysqa.nl";
    
    public static String pdg = "01 - Preliminary Data Gathering\\";
    public static String idg = "02 - Initial Data Gathering\\";
    public static String fdg = "03 - Final Data Gathering\\";
    public static String ndg = "04 - Non CWE Data Gathering\\";
    public static String npv = "05 - Net Positions Validation\\";
    public static String gfc = "06 - Global Final Conformation\\";
    
    public static ArrayList<String> peakHours = new ArrayList<>();
    public static ArrayList<String> offPeakHours = new ArrayList<>();
    
    public static Map<String, Organization> orgs = new HashMap<>();
    public static Map<String, Country> countries = new HashMap<>();
    public static Map<String, Border> borders = new HashMap<>();
    public static Map<Integer, String[]> nonCWE = new HashMap<>(); 
    
    public final static String[] senders103 = {"ELI", "TTN", "APG", "RTE", "TTG", "TNG", "AMP", "50H"};
    public final static String[] senders104 = {"ELI", "TTN", "TTG", "APG", "RTE", "TBW", "AMP"};
    public final static String[] senders107 = {"ELI", "TTN", "APG", "RTE", "AMP"};
    public final static String[] senders129 = {"TTN", "APG", "RTE", "AMP"};
    public final static String[] senders272 = {"ELI", "TTN", "APG", "RTE", "TTG"};
    
    final int N103 = senders103.length;
    final int N104 = senders104.length;
    final int N107 = senders107.length;
    final int N129 = senders129.length;
    final int N272 = senders272.length;
    final int NnonCWE = nonCWE.size();
        
    public F103[] flows103 = new F103[N103];
    public F104[] flows104 = new F104[N104];
    public F107[] flows107 = new F107[N107];
    public F129[] flows129 = new F129[N129];
    public F272[] flows272 = new F272[N272];
    public List<NonCWE> nonCWEs = new ArrayList<>();
    
    public static String[] info183 = {"TTG", "10YDOM-1010A0213", "DE", "DK", "1500", "1300"};
    public static String[] info184 = {"TTN", "10YDOM-1001A041Z", "NL", "NO", "433", "433"};
    public static String[] info250 = {"RTE", "10YDOM--ES-FR--D", "ES", "FR", "2500", "300"};
    public static String[] info255 = {"RTE", "10YDOM--FR-IT--Q", "FR", "IT", "2300", "1000"};
    public static String[] info290 = {"APG", "10YDOM-1010A0116", "AT", "IT", "240", "100"};
    public static String[] info313 = {"APG", "10YDOM-AT-SI---V", "AT", "SI", "880", "1020"};
    public static String[] info389 = {"TTN", "10YDK-1--------W", "NL", "DK", "900", "1020"};
    
    public static String today;
    public static String creationDateTime;
    public static ArrayList<Integer> hours;
    //Time intervals in this format: "2018-04-18T22:00Z/2018-04-19T22:00Z"
    public static String timeInt;
    public static Date businessDay;
    public static Date nextBusinessDay;
    public static Date previousBusinessDay;
    
    public FlowCreator(){
        DateHandler.handleDate(businessDayString);
        loadSettings(); 
    }
    
    public static void main(String[] args) {
        FlowCreator newInputFlows = new FlowCreator();
        
        newInputFlows.preliminaryDataGathering();
        newInputFlows.initialDataGathering();
      //  newInputFlows.finalDataGathering();
        newInputFlows.nonCWEDataGathering();
      //  newInputFlows.npvgfc();
    }
    
    private void preliminaryDataGathering(){
        for (int i = 0; i < N103; i++) {
            flows103[i] = new F103(senders103[i]);
            flows103[i].saveFile();
        }
        
        for (int i = 0; i < N107; i++) {
            flows107[i] = new F107(senders107[i]);
            flows107[i].saveFile();
        }
        
        for (int i = 0; i < N272; i++) {
            flows272[i] = new F272(senders272[i]);
            flows272[i].saveFile();
        }
        
        zipFiles(pdg);
    }
    
    private void initialDataGathering(){
        F100 flow100 = new F100();
        flow100.saveFile();
            
        F105 flow105 = new F105();
        flow105.saveFile();
        
        F370 flow370 = new F370();
        flow370.saveFile();
            
    //    for (int i = 0; i < N104; i++) {
    //        flows104[i] = new F104(senders104[i]);
    //        flows104[i].saveFile();
    //    }
        
        zipFiles(idg);
    }
    
    private void finalDataGathering(){
        for (int i = 0; i < N129; i++) {
            flows129[i] = new F129(senders129[i]);
            flows129[i].saveFile();
        }
        zipFiles(fdg);
    }
    
    private void nonCWEDataGathering(){
        nonCWE.forEach( (k,v) -> {
            nonCWEs.add(new NonCWE(k)); 
            nonCWEs.get(nonCWEs.size() - 1).saveFile();
        });
        zipFiles(ndg);
    }
    
    private void npvgfc(){
        F152 flow152 = new F152();
        flow152.saveFile();
        F192 flow192 = new F192(flow152);
        flow192.saveFile();
    }
    
    public void zipFiles(String subject){
        File dir = new File(path + dateOutputFormat.format(businessDay) + "\\" + subject);
        String zipDirName = path + dateOutputFormat.format(businessDay) + "\\" + subject + "bulk.zip";
        
        try {
            List<String> filesListInDir = populateFilesList(dir);
            
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {
                System.out.println("Zipping " + filePath);
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();

        } catch (IOException e) {
        }
        
    }
    
    private List<String> populateFilesList(File dir) throws IOException {
        List<String> filesListInDir = new ArrayList<>();
        File[] files = dir.listFiles();
        for(File file : files){
            if (!"bulk.zip".equals(file.getName())) filesListInDir.add(file.getAbsolutePath());
        }
        return filesListInDir;
    }
        
    @SuppressWarnings("empty-statement")
    public static void loadSettings(){
        countries.put("NL", new Country("The Netherlands", "NL", "10YNL----------L", true));
        countries.put("FR", new Country("France", "FR", "10YFR-RTE------C", true));
        countries.put("DE", new Country("Germany", "DE", "10YDE-EON------1", true));
        countries.put("AT", new Country("Austria", "AT", "10YAT-APG------L", true));
        countries.put("BE", new Country("Belgium", "BE", "10YBE----------2", true));
        countries.put("IT", new Country("Italy", "IT", "10YIT-GRTN-----B", false));
        countries.put("GB", new Country("Great Brittain", "GB", "10YGB----------A", false));
        countries.put("ES", new Country("Spain", "ES", "10YES-REE------0", false));
        countries.put("DK", new Country("Denmark", "DK", "10YDK-1--------W", false));
        countries.put("NO", new Country("Norway", "NO", "10YNO-2--------T", false));
        countries.put("SI", new Country("Slovenia", "SI", "10YSI-ELES-----O", false));
        
        orgs.put("ELI", new Organization("ELIA", "ELI", "10X1001A1001A094", countries.get("BE")));
        orgs.put("TTN", new Organization("Tennet NL", "TTN", "10X1001A1001A361", countries.get("NL")));
        orgs.put("APG", new Organization("APG", "APG", "10XAT-APG------Z", countries.get("AT")));
        orgs.put("RTE", new Organization("RTE", "RTE", "10XFR-RTE------Q", countries.get("FR")));
        orgs.put("TTG", new Organization("Tennet GE", "TTG", "10XDE-EON-NETZ-C", countries.get("DE")));
        orgs.put("TNG", new Organization("Transnet", "TNG", "10XDE-ENBW--TNGX", countries.get("DE")));
        orgs.put("AMP", new Organization("Amprion", "AMP", "10XDE-RWENET---W", countries.get("DE")));
        orgs.put("50H", new Organization("50 Hertz", "50H", "10XDE-VE-TRANSMK", countries.get("DE")));
        orgs.put("COR", new Organization("Coreso", "COR", "22XCORESO------S"));
        orgs.put("JAO", new Organization("JAO", "JAO", "10X1001A1001A57U"));
        orgs.put("CS",  new Organization("Common System", "CS",  "17XTSO-CS------W"));
        orgs.put("EPX", new Organization("EPEX", "EPX", "17X100A100M007GZ"));
        orgs.put("NPS", new Organization("Nord Pool Spot", "NPS", "11XNORDPOOLSPOT2"));
        orgs.put("MNA", new Organization("NEMO Coordinator", "MNA", "17X100A100M003CI"));
        
        borders.put("NLBE", new Border(countries.get("NL"), countries.get("BE"), true, 692));
        borders.put("NLDE", new Border(countries.get("NL"), countries.get("DE"), true, 888));
        borders.put("BEFR", new Border(countries.get("BE"), countries.get("FR"), true, 375));
        borders.put("DEAT", new Border(countries.get("DE"), countries.get("AT"), true, 4900));
        borders.put("DEFR", new Border(countries.get("DE"), countries.get("FR"), true, 1000));
        borders.put("BENL", new Border(countries.get("BE"), countries.get("NL"), true, 780));
        borders.put("DENL", new Border(countries.get("DE"), countries.get("NL"), true, 890));
        borders.put("FRBE", new Border(countries.get("FR"), countries.get("BE"), true, 1700));
        borders.put("ATDE", new Border(countries.get("AT"), countries.get("DE"), true, 4900));
        borders.put("FRDE", new Border(countries.get("FR"), countries.get("DE"), true, 1350));
        borders.put("NLDK", new Border(countries.get("NL"), countries.get("DK"), false));
        borders.put("NLNO", new Border(countries.get("NL"), countries.get("NO"), false));
        borders.put("NLDK", new Border(countries.get("NL"), countries.get("DK"), false));
        borders.put("NLGB", new Border(countries.get("NL"), countries.get("GB"), false));
        borders.put("FRGB", new Border(countries.get("FR"), countries.get("GB"), false));
        borders.put("FRES", new Border(countries.get("FR"), countries.get("ES"), false));
        borders.put("ATSI", new Border(countries.get("AT"), countries.get("SI"), false));
        borders.put("ATIT", new Border(countries.get("AT"), countries.get("IT"), false));
        borders.put("DEDK", new Border(countries.get("DE"), countries.get("DK"), false));
        borders.put("FRIT", new Border(countries.get("FR"), countries.get("IT"), false));
        borders.put("NONL", new Border(countries.get("NO"), countries.get("NL"), false));
        borders.put("DKNL", new Border(countries.get("DK"), countries.get("NL"), false));
        borders.put("GBNL", new Border(countries.get("GB"), countries.get("NL"), false));
        borders.put("GBFR", new Border(countries.get("GB"), countries.get("FR"), false));
        borders.put("ESFR", new Border(countries.get("ES"), countries.get("FR"), false));
        borders.put("SIAT", new Border(countries.get("SI"), countries.get("AT"), false));
        borders.put("ITAT", new Border(countries.get("IT"), countries.get("AT"), false));
        borders.put("DKDE", new Border(countries.get("DK"), countries.get("DE"), false));
        borders.put("ITFR", new Border(countries.get("IT"), countries.get("FR"), false));
   
        nonCWE.put(183, info183);
        nonCWE.put(184, info184);
        nonCWE.put(250, info250);
        nonCWE.put(255, info255);
        nonCWE.put(290, info290);
        nonCWE.put(313, info313);
        nonCWE.put(313, info313);
        nonCWE.put(389, info389);
    }
    
   
}
