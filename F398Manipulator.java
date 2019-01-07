/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package F398Manipulator;

import flowcreator.DateHandler;
import flowcreator.FlowCreator;
import static flowcreator.FlowCreator.businessDayString;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class F398Manipulator extends javax.swing.JFrame {

    public F398Manipulator() {

        FlowCreator fc = new FlowCreator();
        initComponents();

        // quickStart();   
        // System.exit(0);
    }

    public void quickStart() {
        //File file = new File("C:\\Users\\tdevries\\Documents\\Flow398.xls");
        GenerateFlow flowObject = new GenerateFlow();
        flowObject.init();
        XMLWriter resultXML = new XMLWriter(flowObject.flow, directoryName, useF144);
        resultXML.saveFile(directoryName + "\\");
    }

    private void initComponents() {
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jButton1.setText("Select file");
        jButton1.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton1ActionPerformed(evt);
        });

        jTextField1.setText("");
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jButton2.setText("Select output location");
        jButton2.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton2ActionPerformed(evt);
        });

        jTextField2.setText(path);
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jButton4.setText("Transfer!");
        jButton4.addActionListener((java.awt.event.ActionEvent evt) -> {
            try {
                jButton4ActionPerformed(evt);
            } catch (IOException | InvalidFormatException ex) {
                Logger.getLogger(F398Manipulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        jButton3.setText("Select flow 144");
        jButton3.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton3ActionPerformed(evt);
        });

        jButton3.setToolTipText("");
        jButton3.setEnabled(false);

        jTextField3.setEnabled(false);

        jButton4.setText("Transfer!");

        jCheckBox1.setText("Include F144");
        jCheckBox1.addActionListener((java.awt.event.ActionEvent evt) -> {
            jCheckBox1ActionPerformed(evt);
        });

        jCheckBox2.setText("XML -> XML");
        jCheckBox2.addActionListener((java.awt.event.ActionEvent evt) -> {
            jCheckBox2ActionPerformed(evt);
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton4))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                                                        .addComponent(jTextField2)
                                                        .addComponent(jTextField1)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jCheckBox1)
                                                        .addComponent(jCheckBox2))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton2)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton3)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton4)
                                        .addComponent(jLabel1))
                                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {
        useF144 = !useF144;
        jButton3.setEnabled(useF144);
        jTextField3.setEnabled(useF144);
    }

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {
        xmlToXml = !xmlToXml;
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(fileName));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML OR XLS FILES", "xml", "xls", "xlsx");
        chooser.setFileFilter(filter);

        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getPath();
            jTextField1.setText(fileName);
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(directoryName));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            directoryName = chooser.getSelectedFile().getPath();
            jTextField2.setText(directoryName);
        }
    }

    private void jButton3ActionPerformed(ActionEvent evt) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(flow144Name));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML FILES ONLY", "xml");
        chooser.setFileFilter(filter);

        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            flow144Name = chooser.getSelectedFile().getPath();
            jTextField3.setText(flow144Name);
        }
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) throws IOException, InvalidFormatException {
        int checkFields = check();

        jLabel1.setText(errors[checkFields]);
        jLabel1.setForeground(Color.RED);
        if (checkFields == 0) {
            File file = new File(fileName);
            File file144 = null;
            if (useF144) {
                file144 = new File(flow144Name);
            } 
            if (xmlToXml) {
                Reader flowObject = new Reader(true, file, file144);
                DateHandler.handleDate(FlowCreator.businessDayString);
                XMLWriter resultXML = new XMLWriter(flowObject, directoryName, useF144);
                resultXML.saveFile(directoryName + "\\");
            } else {
                if (FilenameUtils.getExtension(fileName).equals("xml")) {
                    Reader flowObject = new Reader(true, file, file144);
                    XLSWriter resultXLS = new XLSWriter(flowObject, directoryName, useF144);
                } else {
                    Reader flowObject = new Reader(false, file, file144);
                    XMLWriter resultXML = new XMLWriter(flowObject, directoryName, useF144);
                    resultXML.saveFile(directoryName + "\\");
                }
            }
            jLabel1.setText("File has been saved. ");
            jLabel1.setForeground(Color.getHSBColor(0.33f, 0.68f, 0.29f));
        } else if (checkFields == 5) {
            quickStart();
            System.exit(0);
        }

    }

    private int check() {
        int res = 0;
        File f = new File(directoryName);
        File g = new File(fileName);
        boolean directoryExists = false;
        boolean fileCorrectExtension = false;
        boolean fileExists = false;
        if (f.exists() && f.isDirectory()) {
            directoryExists = true;
        }
        if (g.exists() && !g.isDirectory()) {
            fileExists = true;
        }
        if (fileExists && (FilenameUtils.getExtension(fileName).equals("xml") || FilenameUtils.getExtension(fileName).equals("xls"))) {
            fileCorrectExtension = true;
        }

        if (directoryExists) {
            if (fileExists) {
                if (!fileCorrectExtension) {
                    res = 2;
                }
            } else {
                res = 3;
            }
        } else {
            if (fileCorrectExtension) {
                res = 1;
            } else {
                res = 4;
            }
        }
        if (!fileExists) {
            res = 5;
        }
        return res;
    }

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {
        fileName = jTextField1.getText();
    }

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {
        directoryName = jTextField2.getText();
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(F398Manipulator.class.getName()).log(Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new F398Manipulator().setVisible(true);
            }
        });
    }

    public String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
    public String fileName = path;
    public String flow144Name = path;
    public String directoryName = path;
    public boolean useF144 = false;
    public boolean xmlToXml = false;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private JFileChooser chooser;
    private javax.swing.JLabel jLabel1;
    public static boolean F152 = false;
    private final String[] errors = {" ", "This is not a valid folder.", "This is not an XML or XLS file.", "This is not a file.", "These are not valid paths.", ""};
}
