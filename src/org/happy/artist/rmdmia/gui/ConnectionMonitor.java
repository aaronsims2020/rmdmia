package org.happy.artist.rmdmia.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;

public class ConnectionMonitor extends JFrame{
    private JTable table;
//    private JButton btnAdd;
    private DefaultTableModel tableModel;
    private JTextField txtField1;
    private JTextField txtField2;
    private final static String EMPTY_STRING="";
    private final static Boolean TRUE = new Boolean(true);
    private final static Boolean FALSE = new Boolean(false);  
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSteps; 
    private javax.swing.JButton btnAddTopic;
    private javax.swing.JButton btnAddService;    
  //  private javax.swing.JButton btnMakeMD5Script; 
  //  private javax.swing.JButton btnMakeServiceTypesScript;
  //  private javax.swing.JButton btnImportServiceTypes;
  //  private javax.swing.JButton btnImportMD5;    
    private javax.swing.JLabel lblMasterURI;
    private javax.swing.JTextField txtMasterURI;
    // Application Menu
    private javax.swing.JMenuBar menu;
//    private javax.swing.JMenu mnuEdit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuItem mnuItemSave;      
    private javax.swing.JMenuItem mnuItemClose;                    
    private javax.swing.JMenuItem mnuItemExit;
    private javax.swing.JMenuItem mnuItemHelpAbout;
    private javax.swing.JMenu mnuTools;    
    
    // getMD5Script() method variables.
    private StringBuffer document;
    
    // btnMakeMD5ScriptActionPerformed method variables
    private String md5Script;    
    
    private String serviceTypesScript;
    
    // saveStringToFile method variables
    private FileWriter fw;
    private JFileChooser chooser;
    private File file;
    
    // openTextFile method variables
    private File fileOpen;
    private int returnVal;
    private BufferedReader br;
    private StringBuffer sb;
    private String line;  
    
    // parseServiceTypeImportPropertiesToMap method variables
    private String[] lines;
    private Map<String,String> serviceTypesMap; 
    
    // parseMD5ImportPropertiesToMap method variables.
    private Map<String,String> md5Map;  
    
    // btnImportMD5ActionPerformed method variables.
    private Map MD5s;
    private String currentMD5;   
    
    // menu items
    private javax.swing.JMenuItem mnuItemMakeMD5Script;     
    private javax.swing.JMenuItem mnuItemMakeServiceTypesScript;  
    private javax.swing.JMenuItem mnuItemImportServiceTypes; 
    private javax.swing.JMenuItem mnuItemImportMD5;      
    private javax.swing.JMenuItem mnuItemHowtoConfigure;  
    
    // Column sizing column reference.
    private TableColumn column = null;
    
    // mnuItemHelpAboutActionPerformed method variables.
    private AboutBoxDialog about;    

    
    public ConnectionMonitor(Object[] columnNames) 
    {
        initialize(new Object[0][],columnNames);
        SwingUtilities.invokeLater(new Runnable() 
        {

            @Override
            public void run() 
            {
                ConnectionMonitor.this.setLocationByPlatform(true);
                ConnectionMonitor.this.pack();
                ConnectionMonitor.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                ConnectionMonitor.this.setVisible(true);

            }

        });        
    }
    
    private String caller_id;
    private ROSSubscriberMessageManager smm;
    // Used by the SubscriberMessageManager to generate TopicRegistryDefinitions to add.
    private Object[][] data;
    private ROSNode rosNode;
    private Properties properties;
    private String propertiesFilePath;
    private String master_uri;
    /** if smm is null SubscriberMessageManager is instantiated here. */
    public ConnectionMonitor(Object[][] data, Object[] columnNames, String caller_id, ROSSubscriberMessageManager smm, ROSNode rosNode, java.util.Properties properties, String propertiesFilePath) 
    {
        //TODO: 09/15/2013 - Implement Message Manager add & remove to GUI, and tie to save. Need to iterate through each topic/service and create TopicRegistryDefinition, them call add to each one via SubscriberMessageManager.
        this.caller_id=caller_id;        
        // insert column 0 for status
        data=getInsertColumnInDataArray(data);
      
        columnNames=getInsertColumnInHeaderDataArray(columnNames);
        this.rosNode=rosNode; 
        this.properties=properties;
        this.propertiesFilePath=propertiesFilePath;
        if(properties!=null&&properties.getProperty("master_url")!=null)
        {
            this.master_uri=properties.getProperty("master_url");
        }
        try 
        {
            if(smm!=null)
            {
                this.smm = smm;
            }
            else
            {
                this.smm = ROSSubscriberMessageManager.getInstance(caller_id, null);                
            }
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(ROSSubscriberMessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
        initialize(data,columnNames);
        SwingUtilities.invokeLater(new Runnable() 
        {

            @Override
            public void run() 
            {
                ConnectionMonitor.this.setLocationByPlatform(true);
                ConnectionMonitor.this.pack();
                ConnectionMonitor.this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                ConnectionMonitor.this.setVisible(true);

            }

        });        
    }
    
    private int insertCount;
    private int insertCount2;
    private Object[][] insertArray;
    private Object[][] getInsertColumnInDataArray(Object[][] dataArr)
    {
        this.insertArray = new Object[dataArr.length][31];
        this.insertCount=0;
        while(insertCount<dataArr.length)
        {
            this.insertCount2 = 1;
            while(insertCount2<31)
            {
                insertArray[insertCount][insertCount2]=dataArr[insertCount][insertCount2-1];
                this.insertCount2=insertCount2 + 1;        
            }
            this.insertCount=insertCount + 1;
        }
        return insertArray;
    }
    
    private int insertCount3;
    private String[] insertColArray;
    private String[] getInsertColumnInHeaderDataArray(Object[] dataArr)
    {
        this.insertColArray = new String[31];
        insertColArray[0]=" ";
        this.insertCount3=1;
        while(insertCount3<dataArr.length)
        {
            insertColArray[insertCount3]=(String)dataArr[insertCount3-1];
            this.insertCount3=insertCount3 + 1;
        }
        return insertColArray;
    }    

    private Object[] tableHeader = new Object[]{"column1","column2"};
    public void setTableHeaders(String[] tableHeaders)
    {
        this.tableHeader=tableHeaders;
    }
    
    public Object[] getTableHeaders()
    {
        return tableHeader;
    }
    
    private int rowCount;
    public void addRow(Object[] tableRow)
    {
        this.rowCount = tableModel.getRowCount()+1;
        tableModel.addRow(tableRow);
    }
    
    public void addData(Object[][] data, Object[] headers)
    {
        tableModel.setDataVector(data, headers);
    }
    
    /** Returns the active Table Model. */
    public TableModel getTableModel()
    {
        return tableModel;
    }
    
    // Save configuration variables.
    private ROSTopicRegistryMessageDefinition trmd;
    /** initialize the GUI after the Table Headers are set. */
    public void initialize(Object[][] data, Object[] columnNames)
    {  
        this.data=data;
        setTitle("RMDMIA - ROS Configuration Manager Preview Release 1");
           setLayout(new BorderLayout());
        JScrollPane pane = new JScrollPane();
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        table = new JTable()
        {
            //Implement table cell tool tips.           
            public String getToolTipText(MouseEvent e) 
            {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try 
                {
                    //comment row, exclude heading
                    if(rowIndex != 0)
                    {
                      tip = getValueAt(rowIndex, colIndex).toString();
                    }
                } 
                catch (RuntimeException e1) 
                {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
        };     
        pane.setViewportView(table);
        JPanel eastPanel = new JPanel();
//        btnAdd = new JButton("Add");
  //      eastPanel.add(btnAdd);
        JPanel northPanel = new JPanel();
        // Menu instantiations.
        menu = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuItemSave = new javax.swing.JMenuItem();           
        mnuItemClose = new javax.swing.JMenuItem();                
        mnuItemExit = new javax.swing.JMenuItem();
        mnuItemHowtoConfigure = new javax.swing.JMenuItem();        
 //       mnuEdit = new javax.swing.JMenu();
        mnuTools = new javax.swing.JMenu();
        mnuHelp = new javax.swing.JMenu();
        mnuItemHelpAbout = new javax.swing.JMenuItem();  
        mnuItemMakeMD5Script = new javax.swing.JMenuItem();     
        mnuItemMakeServiceTypesScript = new javax.swing.JMenuItem();  
        mnuItemImportServiceTypes = new javax.swing.JMenuItem(); 
        mnuItemImportMD5 = new javax.swing.JMenuItem();  
        
        // Frame instantiations
        txtMasterURI = new javax.swing.JTextField();
        lblMasterURI = new javax.swing.JLabel();
        btnConnect = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnSteps = new javax.swing.JButton();
        btnAddTopic = new javax.swing.JButton();
        btnAddService = new javax.swing.JButton();         
//        btnMakeMD5Script = new javax.swing.JButton();     
//        btnMakeServiceTypesScript = new javax.swing.JButton(); 
//        this.btnAdd = new JButton("Save Configuration");        
 //       btnImportServiceTypes = new javax.swing.JButton(); 
 //       btnImportMD5 = new javax.swing.JButton();         

        mnuFile.setMnemonic('F');
        mnuFile.setText("File");
          
        mnuItemSave.setMnemonic('S');
        mnuItemSave.setText("Save");
        mnuItemSave.setToolTipText("Save configuration");
        mnuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemSaveActionPerformed(evt);
            }
        });
              

        mnuFile.add(mnuItemSave);        
        
        mnuItemClose.setMnemonic('c');
        mnuItemClose.setText("Close");
        mnuItemClose.setToolTipText("Close Window, continue running RMDMIA.");
        mnuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemCloseActionPerformed(evt);
            }
        });
        mnuFile.add(mnuItemClose);
        
        mnuItemExit.setMnemonic('x');
        mnuItemExit.setText("Exit");
        mnuItemExit.setToolTipText("Exit Application");
        mnuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuItemExit);

        menu.add(mnuFile);

//        mnuEdit.setText("Edit");
//        menu.add(mnuEdit);

// Tools Menu Items
        mnuItemMakeMD5Script = new javax.swing.JMenuItem();     
        mnuItemMakeServiceTypesScript = new javax.swing.JMenuItem();  
        mnuItemImportServiceTypes = new javax.swing.JMenuItem(); 
        mnuItemImportMD5 = new javax.swing.JMenuItem();               
        
        mnuTools.setText("Tools");
        mnuTools.setToolTipText("");
        menu.add(mnuTools);

        mnuHelp.setMnemonic('H');
        mnuHelp.setText("Help");

        mnuItemHowtoConfigure.setMnemonic('O');
        mnuItemHowtoConfigure.setText("Configuration Howto");
        mnuItemHowtoConfigure.setToolTipText("");
        mnuItemHowtoConfigure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemHowtoConfigureActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuItemHowtoConfigure);                
                
        mnuItemHelpAbout.setMnemonic('A');
        mnuItemHelpAbout.setText("About");
        mnuItemHelpAbout.setToolTipText("");
        mnuItemHelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemHelpAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuItemHelpAbout);

        menu.add(mnuHelp);
        // Set the menubar in the frame.
        setJMenuBar(menu);        
        
        txtMasterURI.setColumns(20);
        txtMasterURI.setText(master_uri);
        txtMasterURI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMasterURIActionPerformed(evt);
            }
        });

        lblMasterURI.setText("ROS Master URI:");

        btnConnect.setMnemonic('C');
        btnConnect.setText("Connect");
        btnConnect.setToolTipText("Connection Toggle Connect/Disconnect");
        btnConnect.setActionCommand("");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        btnRefresh.setMnemonic('R');
        btnRefresh.setText("Refresh");
        btnRefresh.setToolTipText("Refresh the Topic/Service Data");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        
            btnSteps.setMnemonic('o');
        btnSteps.setText("Configuration Howto");
        btnSteps.setToolTipText("Instructions for ROS Topic/Service configuration");
        btnSteps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepsActionPerformed(evt);
            }
        });       
        
        btnAddTopic.setMnemonic('T');
        btnAddTopic.setText("Add Topic");
        btnAddTopic.setToolTipText("Add an unlisted Pub/Sub Topic");
        btnAddTopic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTopic(evt);
            }

            private void btnAddTopic(ActionEvent evt) {
                Object[] newRow = new Object[31];
                newRow[1]="Topic";
                addRow(newRow);
            }
        });   
        
        btnAddService.setMnemonic('S');
        btnAddService.setText("Add Service");
        btnAddService.setToolTipText("Add an unlisted Service");
        btnAddService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddService(evt);
            }

            private void btnAddService(ActionEvent evt) {
                Object[] newRow = new Object[31];
                newRow[1]="Service";
                addRow(newRow);
            }
        });           

        mnuItemMakeMD5Script.setMnemonic('G');
        mnuItemMakeMD5Script.setText("Generate MD5SUM Script");
        mnuItemMakeMD5Script.setToolTipText("Automatically generate a script Topic/Service MD5Sums for import configuration. File extension of .sh, or .bat added automatically.");
        mnuItemMakeMD5Script.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMakeMD5ScriptActionPerformed(evt);
            }
        });
        mnuTools.add(mnuItemMakeMD5Script);
        
        mnuItemMakeServiceTypesScript.setMnemonic('e');
        mnuItemMakeServiceTypesScript.setText("Generate Service Types Script");
        mnuItemMakeServiceTypesScript.setToolTipText("Automatically generate a script for Service Types import configuration. File extension of .sh, or .bat added automatically.");
        mnuItemMakeServiceTypesScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMakeServiceTypesScriptActionPerformed(evt);
            }
        });        
        mnuTools.add(mnuItemMakeServiceTypesScript);
  
        mnuItemImportServiceTypes.setMnemonic('I');
        mnuItemImportServiceTypes.setText("Import Service Types Data File");
        mnuItemImportServiceTypes.setToolTipText("Import the Service Type \"rossvctypes.properties\" data file generated by the Service Types import script.");
        mnuItemImportServiceTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportServiceTypesActionPerformed(evt);
            }
        }); 
        mnuTools.add(mnuItemImportServiceTypes);
        
        mnuItemImportMD5.setMnemonic('m');
        mnuItemImportMD5.setText("Import MD5 Data File");
        mnuItemImportMD5.setToolTipText("Import the Topic/Service \"rosmd5.properties\" data file generated by the MD5 import script.");
        mnuItemImportMD5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportMD5ActionPerformed(evt);
            }
        });     
        mnuTools.add(mnuItemImportMD5);        
        
//        btnMakeMD5Script.setMnemonic('G');
//        btnMakeMD5Script.setText("Generate MD5 Script");
//        btnMakeMD5Script.setToolTipText("Automatically generate a script Topic/Service MD5Sums for import configuration.");
//        btnMakeMD5Script.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
 //               btnMakeMD5ScriptActionPerformed(evt);
 //           }
 //       });
        
//        btnMakeServiceTypesScript.setMnemonic('E');
//        btnMakeServiceTypesScript.setText("Generate SVC Type Script");
 //       btnMakeServiceTypesScript.setToolTipText("Automatically generate a script for Service Types import configuration.");
//        btnMakeServiceTypesScript.addActionListener(new java.awt.event.ActionListener() {
 //           public void actionPerformed(java.awt.event.ActionEvent evt) {
 //               btnMakeServiceTypesScriptActionPerformed(evt);
 //           }
 //       });        

  
//        btnImportServiceTypes.setMnemonic('M');
//        btnImportServiceTypes.setText("Import SVC Type Data");
//        btnImportServiceTypes.setToolTipText("Import the Service Type \"rossvctypes.properties\" data file generated by the Service Types import script.");
 //       btnImportServiceTypes.addActionListener(new java.awt.event.ActionListener() {
 //           public void actionPerformed(java.awt.event.ActionEvent evt) {
  //              btnImportServiceTypesActionPerformed(evt);
  //          }
  //      }); 
    
  //      btnImportMD5.setMnemonic('I');
  //      btnImportMD5.setText("Import MD5 Data");
  //      btnImportMD5.setToolTipText("Import the Topic/Service \"rosmd5.properties\" data file generated by the MD5 import script.");
  //      btnImportMD5.addActionListener(new java.awt.event.ActionListener() {
  //          public void actionPerformed(java.awt.event.ActionEvent evt) {
   //             btnImportMD5ActionPerformed(evt);
  //          }
   //     });         
        
        northPanel.add(lblMasterURI);
        northPanel.add(txtMasterURI);
        northPanel.add(btnConnect);
        northPanel.add(btnRefresh);
        northPanel.add(btnSteps); 
       // northPanel.add(btnAddTopic); 
       // northPanel.add(btnAddService);         
//        northPanel.add(btnAdd);     
//        northPanel.add(btnMakeServiceTypesScript);            
//        northPanel.add(btnMakeMD5Script);
 ////       northPanel.add(btnImportServiceTypes);
//        northPanel.add(btnImportMD5);        
        
        
        //     northPanel.add(lblField1);
   //     northPanel.add(txtField1);
  //      northPanel.add(lblField2);
//        northPanel.add(txtField2);
  //      txtField1.setPreferredSize(lblField1.getPreferredSize());
  //      txtField2.setPreferredSize(lblField2.getPreferredSize());

        add(northPanel, BorderLayout.NORTH);
        add(eastPanel, BorderLayout.EAST);
        add(pane, BorderLayout.CENTER);
        for(int i=0;i<data.length;i++)
        {
            if(data[i][12].equals("1"))
            {    
                data[i][12]=TRUE;
            }
            else
            {
                data[i][12]=FALSE;
            }
            if(data[i][13].equals("1"))
            {    
                data[i][13]=TRUE;
            }
            else
            {
                data[i][13]=FALSE;
            }     
            if(data[i][14].equals("1"))
            {    
                data[i][14]=TRUE;
            }
            else
            {
                data[i][14]=FALSE;
            }
            if(data[i][15].equals("1"))
            {    
                data[i][15]=TRUE;
            }
            else
            {
                data[i][15]=FALSE;
            }            
            if(data[i][16].equals(""))
            {    
                data[i][16]="TCPROS";
            }
            if(data[i][17].equals(""))
            {    
                data[i][17]="1024";
            }   
            if(data[i][18].equals(""))
            {    
                data[i][18]="1024";
            } 
            // Topic Message Handlers
            if(data[i][19].equals("")&&!data[i][1].equals("Service"))
            {
                data[i][19]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultMessageHandler";
            }
            if(data[i][20].equals("")&&!data[i][1].equals("Service"))
            {
                data[i][20]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultInitializerMessageHandler";
            }
            if(data[i][21].equals("")&&!data[i][1].equals("Service"))
            {
                data[i][21]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultTunerMessageHandler";
            }   
            // Service Message Handlers            
            if(data[i][19].equals("")&&data[i][1].equals("Service"))
            {
                data[i][19]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultServiceMessageHandler";
            }
            if(data[i][20].equals("")&&data[i][1].equals("Service"))
            {
                data[i][20]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultServiceInitializerMessageHandler";
            }
            if(data[i][21].equals("")&&data[i][1].equals("Service"))
            {
                data[i][21]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultServiceTunerMessageHandler";
            }               
            if(data[i][26].equals("1"))
            {    
                data[i][26]=TRUE;
            }
            else
            {
                data[i][26]=FALSE;
            }       
            if(data[i][27].equals(""))
            {
                data[i][27]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherMessageHandler";
            }
            if(data[i][28].equals(""))
            {
                data[i][28]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherInitializerMessageHandler";
            }
            if(data[i][29].equals(""))
            {
                data[i][29]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherTunerMessageHandler";
            }    
            if(data[i][30]!=null&&data[i][30].equals("1"))
            {
                data[i][30]=TRUE;
            } 
            else
            {
                data[i][30]=FALSE;
            }                 
        if (txtMasterURI.getText().equals(""))
        {  
            //Master URI required, but is empty String. 
            txtMasterURI.setBackground(Color.red);
          //  JOptionPane.showMessageDialog(null,"Please enter the ROS Master URI to connect.]nLocated in the System Environment Variables on your computer.");
            txtMasterURI.requestFocusInWindow();
        }
    }

        // Table Model Data Types.
    this.tableModel = new DefaultTableModel(data,columnNames) 
    {
        Class[] types = {
            ImageIcon.class, String.class, String.class, String.class, String.class, String.class, 
            String.class, String.class, String.class, String.class, 
            String.class, String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class, String.class, String.class, String.class, Boolean.class
        };
        // making sure that it returns boolean.class.   
        @Override
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }
    };        
        table.setModel(tableModel);
        //6474

        table.setPreferredScrollableViewportSize(new Dimension(6454,(int)table.getPreferredSize().getHeight()));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

for (int i = 0; i < 31; i++) 
{
    this.column = table.getColumnModel().getColumn(i);
    if (i == 0) 
    {
        column.setPreferredWidth(36); //third column is bigger
    }     
    else if (i == 1) 
    {
        column.setPreferredWidth(110); //third column is bigger
    } 
    else if (i == 2) 
    {
        column.setPreferredWidth(200);
    }
    else if (i == 3) 
    {
        column.setPreferredWidth(150);
    }    
    else if (i == 4) 
    {
        column.setPreferredWidth(150);
    }
    else if (i == 5) 
    {
        column.setPreferredWidth(150);
    }
    else if (i == 6) 
    {
        column.setPreferredWidth(150);
    }    
    else if (i == 7) 
    {
        column.setPreferredWidth(130);
    }
    else if (i == 8) 
    {
        column.setPreferredWidth(80);
    }
    else if (i == 9) 
    {
        column.setPreferredWidth(70);
    } 
    else if (i == 10) 
    {
        column.setPreferredWidth(150);
    }
    else if (i == 11) 
    {
        column.setPreferredWidth(200);
    }
    else if (i == 12) 
    {
        column.setPreferredWidth(140);
    } 
    else if (i == 13) 
    {
        column.setPreferredWidth(140);
    }
    else if (i == 14) 
    {
        column.setPreferredWidth(100);
    }
    else if (i == 15) 
    {
        column.setPreferredWidth(100);
    } 
    else if (i == 16) 
    {
        column.setPreferredWidth(130);
    }
    else if (i == 17) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 18) 
    {
        column.setPreferredWidth(120);
    } 
    else if (i == 19) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 20) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 21) 
    {
        column.setPreferredWidth(120);
    } 
    else if (i == 22) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 23) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 27) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 28) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 29) 
    {
        column.setPreferredWidth(120);
    } 
    else if (i == 30) 
    {
        column.setPreferredWidth(100);
    }      
    else
    {
        column.setPreferredWidth(100);        
    } 
    // TODO image
    this.todoIcon = getImageIcon("/images/todo.png", "Additional Connection parameters required. Likely causes can be a missing type, or md5sum parameter.");
    // Service Connected Image   
    this.connectedIcon = getImageIcon("/images/connect.png", "Service Connected");
    // Service Not Connected Image
    this.notConnectedIcon = getImageIcon("/images/disconnect.png","Service Disconnected"); 
    // Subscriber only Connected Image   
    this.subConnectedIcon = getImageIcon("/images/subconnect.png", "Service Connected");
    // Publisher only Connected Image
    this.pubConnectedIcon = getImageIcon("/images/pubconnect.png","Service Disconnected");    
    // Subscriber only Disconnected Image   
    this.subDisconnectedIcon = getImageIcon("/images/subdisconnect.png", "Service Connected");
    // Publisher only Disconnected Image
    this.pubDisconnectedIcon = getImageIcon("/images/pubdisconnect.png","Service Disconnected");      
    // Pub/Sub Connected Image   
    this.pubSubConnectedIcon = getImageIcon("/images/pubsubconnect.png", "Publisher/Subscriber Connected");
    // Pub/Sub Not Connected Image
    this.pubSubNotConnectedIcon = getImageIcon("/images/pubsubdisconnect.png","Publisher/Subscriber Disconnected");     // Pub Connected/Sub Not Connected Image   
    this.pubConnectedSubNotConnectedIcon = getImageIcon("/images/pubconsubdis.png", "Publisher Connected/Subscriber Disconnected");
    // Pub/Sub Not Connected Image
    this.pubNotConnectedSubConnectedIcon = getImageIcon("/images/pubdissubcon.png","Publisher Disconnected/Subscriber Connected");    
    // update column 0
    this.data=getUpdateStatusColumn(data, getConnectionStatusImages());
    updateStatusIcons();
    setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
    validate();
    pack();
}   


//////////////////////

        //TableColumn column = table.getColumnModel().getColumn(9).;
/*       TableColumn column = table.getColumnModel().getColumn(9);
        column.setCellRenderer(new MyComboBoxRenderer(new String[] { "1", "2", "3" }));
        column.setCellEditor(new MyComboBoxEditor(new String[] { "1", "2", "3" }));

        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
        editor.setClickCountToStart(1);
        column = table.getColumnModel().getColumn(0);
        column.setCellEditor(editor);           
  */      
/////////////////////////        
        // btnAdd call save to the configuration (save to topic_registry.eax).
//        btnAdd.addActionListener(new ActionListener()
//        {
//            @Override
 //           public void actionPerformed(ActionEvent e) 
 //           {
 //               save();
  //  }        });     

    }

private URL imageURL;
/** Returns image icon from classpath. */
private ImageIcon getImageIcon(String path, String description) 
{
    this.imageURL = getClass().getResource(path);
    if (imageURL != null) 
    {
        return new ImageIcon(imageURL, description);
    } 
    else 
    {
        return null;
    }
}    
   private Object[] saveData;
   private void save()
    {
                System.out.println("Action Performed Save Configuration");
                try
                {
                    if(!txtMasterURI.getText().equals(master_uri))
                    {
                        properties.setProperty("master_url", txtMasterURI.getText());
                        //save properties to project root folder
                        properties.store(new FileOutputStream(propertiesFilePath), null);
                    } 
                }
                catch (IOException ex) 
                {
                     Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.WARNING, "Unable to write the Properties File update to property key master_url in rcsm.properties", ex);
                }                    
            
                // update the table data with additional fields like md5.
                ConnectionMonitor.this.data=ConnectionMonitor.this.getTableData(table);
               // TODO: Implement call to load the UI configuration to topic_registry.
                //System.out.println("Data length: " + ConnectionMonitor.this.data.length);
             for(int i =0;i<ConnectionMonitor.this.data.length;i++)
             {
                ConnectionMonitor.this.trmd=new ROSTopicRegistryMessageDefinition();

                // Set topic or service, and the name.
                if(((String)ConnectionMonitor.this.data[i][1]).equals("Topic"))
                {
                    trmd.topic=(String)ConnectionMonitor.this.data[i][2];
                    trmd.service="";
                }
                else if(((String)ConnectionMonitor.this.data[i][1]).equals("Service"))
                {
                    trmd.topic="";
                    trmd.service=(String)ConnectionMonitor.this.data[i][2];
                }   
                else
                {
                    trmd.topic="";
                    trmd.service="";
                }
                // Set the topic type
                trmd.type=(String)ConnectionMonitor.this.data[i][3];
                if(trmd.type==null)
                {
                    trmd.type=EMPTY_STRING;
                }
                // Set the caller id
                trmd.callerid=ConnectionMonitor.this.caller_id;
                
                // Set the md5sum.
                trmd.md5sum=(String)ConnectionMonitor.this.data[i][10];

                ///trmd.definition
                trmd.definition=(String)ConnectionMonitor.this.data[i][11];
                
                // ALWAYS_CONNECTED
                if(((Boolean)ConnectionMonitor.this.data[i][13]).booleanValue())
                {
                    trmd.always_connected="1";
                }
                else
                {
                    trmd.always_connected="0";                    
                }

                // CONNECT_ON_START
                if(((Boolean)ConnectionMonitor.this.data[i][12]).booleanValue())
                {
                    trmd.connect_on_start="1";
                }
                else
                {
                    trmd.connect_on_start="0";                    
                } 
                
                // TCP_NODELAY
                if(((Boolean)ConnectionMonitor.this.data[i][14]).booleanValue())
                {
                    trmd.tcp_nodelay="1";
                }
                else
                {
                    trmd.tcp_nodelay="0";                    
                }     
                // Service PERSISTANT
                if(((Boolean)ConnectionMonitor.this.data[i][15]).booleanValue())
                {
                    trmd.persistant="1";
                }
                else
                {
                    trmd.persistant="0";                    
                }                  
                // PREFERRED Protocol
                trmd.preferred_protocol=(String)ConnectionMonitor.this.data[i][16];      
                // TCP Block size
                trmd.tcp_block_size=(String)ConnectionMonitor.this.data[i][17];      
                // UDP Packet size
                trmd.udp_packet_size=(String)ConnectionMonitor.this.data[i][18];      
                // Message Handler
                trmd.redirect_class=(String)ConnectionMonitor.this.data[i][19];      
                // Message Handler Initializer
                trmd.redirect_class_initializer=(String)ConnectionMonitor.this.data[i][20];      
                // Message Handler Tuner
                trmd.redirect_class_tuner=(String)ConnectionMonitor.this.data[i][21];                    // Service Request Type
                trmd.request_type=(String)ConnectionMonitor.this.data[i][22]; 
                // Service Response Type
                trmd.response_type=(String)ConnectionMonitor.this.data[i][23]; 
                // Service Request Definition               
                trmd.request_type_definition=(String)ConnectionMonitor.this.data[i][24];
                // Service Response Definition               
                trmd.response_type_definition=(String)ConnectionMonitor.this.data[i][25];
                // Publisher Connect on Start
                if(((Boolean)ConnectionMonitor.this.data[i][26]).booleanValue())
                {
                    trmd.publisher_connect_on_start="1";
                }
                else
                {
                    trmd.publisher_connect_on_start="0";                    
                }                   
                // Message Handler
                trmd.publisher_redirect_class=(String)ConnectionMonitor.this.data[i][27];      
                // Message Handler Initializer
                trmd.publisher_redirect_class_initializer=(String)ConnectionMonitor.this.data[i][28];      
                // Publisher Message Handler Tuner
                trmd.publisher_redirect_class_tuner=(String)ConnectionMonitor.this.data[i][29];     
                // Publisher Latching
                if(((Boolean)ConnectionMonitor.this.data[i][30]).booleanValue())
                {
                    trmd.latching="1";
                }
                else
                {
                    trmd.latching="0";                    
                }   
                try {
              
                     ConnectionMonitor.this.smm.add(ConnectionMonitor.this.trmd);
                 } catch (UnsupportedEncodingException ex) {
                     Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
                 }
             } 
              System.out.println("Action Completed.");
            }    
    
    public static void main(String[] args)
    {
 //        Object[][] data = {{"Test","Data"},{"Test 1", "Data 2"}};
//         Object[] columnNames={"Column 1","Column 2"};
 //        ConnectionMonitor monitor = new ConnectionMonitor(data, columnNames,"/rmdmia",null);
    }
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {

            
//            public void run() {
//                ConnectionMonitor frm = new ConnectionMonitor();
//                frm.setLocationByPlatform(true);
//                frm.pack();
//                frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
//                frm.setVisible(true);

//            }

//        });
//    }
    
// getTableData method variables.    
private DefaultTableModel dtm; 
private Object[][] tableData;
private int nRow;
private int nCol;
/** Return the table data as a 2D array. Copied method idea from http://stackoverflow.com/questions/4765469/how-to-retrieve-jtable-data-as-an-array */    
public Object[][] getTableData (JTable table) 
{
    this.dtm = (DefaultTableModel) table.getModel();
    this.nRow = dtm.getRowCount();
    this.nCol = dtm.getColumnCount();
    this.tableData = new Object[nRow][nCol];
    for (int i = 0 ; i < nRow ; i++)
        for (int j = 0 ; j < nCol ; j++)
            tableData[i][j] = dtm.getValueAt(i,j);
    return tableData;
}


    private void txtMasterURIActionPerformed(java.awt.event.ActionEvent evt) {                                             
        if (txtMasterURI.getText().equals(""))
        {  
            //Master URI required, but is empty String. 
            txtMasterURI.setBackground(Color.red);
        }
        else 
        { 
            // Text exists change background to normal.
            txtMasterURI.setBackground(UIManager.getColor("TextField.background"));
        }
    }                                            

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {                                           
        if (txtMasterURI.getText().equals(""))
        {  
            //Master URI required, but is empty String. 
            txtMasterURI.setBackground(Color.red);
            JOptionPane.showMessageDialog(null,"Please enter the ROS Master URI to connect.]nLocated in the System Environment Variables on your computer.");
            txtMasterURI.requestFocusInWindow();
        }
        else 
        { 
            // Text exists change background to normal.
            txtMasterURI.setBackground(UIManager.getColor("TextField.background"));
        }
    }    
    
    private InformationDialog information;
    private void btnStepsActionPerformed(java.awt.event.ActionEvent evt) 
    {                                           
         information = new InformationDialog(this, true) ;
         information.setVisible(true);
    } 
    
    private void btnMakeMD5ScriptActionPerformed(java.awt.event.ActionEvent evt) 
    {                                           
        this.md5Script = getMD5Script("rosmd5.properties");
        // Popup FileChooser dialg andSave file.
        saveStringToFile(md5Script);
    }    
 
    private void btnMakeServiceTypesScriptActionPerformed(java.awt.event.ActionEvent evt) 
    {                                           
        this.serviceTypesScript = getServiceTypesScript("rossvctypes.properties");
        // Popup FileChooser dialg andSave file.
        saveStringToFile(serviceTypesScript);
    }  
    
    // btnImportServiceTypesActionPerformed method variables.
    private String documentText;
    private String currentService;
    private String currentServiceType;  
    private Map serviceTypes;
    
    private void btnImportServiceTypesActionPerformed(java.awt.event.ActionEvent evt) 
    {                                           
        try 
        {
            // Popup FileChooser dialg andSave file.
            this.documentText = openTextFile();
            // If documentText is not null parse String.
            if(documentText!=null)
            {
                this.serviceTypes = parseServiceTypeImportPropertiesToMap(documentText);
                // update types in table data
                for(int i =0;i<ConnectionMonitor.this.data.length;i++)
                {
                    this.currentServiceType=(String) serviceTypes.get((String)ConnectionMonitor.this.data[i][2]);

                    // Set topic or service, and the name.
                   if(((String)ConnectionMonitor.this.data[i][1]).equals("Service")&& serviceTypes.containsKey(((String)ConnectionMonitor.this.data[i][2])) &&currentServiceType!=null)
                   {
                       ConnectionMonitor.this.data[i][3]=currentServiceType;
                   }
                }
            }
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        // update the table data.
        addData(data, this.tableColumnNames = getInsertColumnInHeaderDataArray(rosNode.getMonitoringTableColumnNames()));
    }       
    
    private void btnImportMD5ActionPerformed(java.awt.event.ActionEvent evt) 
    {                                           
        try 
        {
            // Popup FileChooser dialg andSave file.
            this.documentText = openTextFile();
            // If documentText is not null parse String.
            if(documentText!=null)
            {
                this.MD5s = parseMD5ImportPropertiesToMap(documentText);

                // update MD5Sums in table data
                for(int i =0;i<ConnectionMonitor.this.data.length;i++)
                {
                    this.currentMD5=null;
                    if(((String)ConnectionMonitor.this.data[i][1]).equals("Service"))
                    {
                        // Service
                        this.currentMD5=(String) MD5s.get("md5:service:" + (String)ConnectionMonitor.this.data[i][2]);
                    }
                    else if(((String)ConnectionMonitor.this.data[i][1]).equals("Topic"))
                    {
                        // Topic
                         this.currentMD5=(String) MD5s.get("md5:topic:" + (String)ConnectionMonitor.this.data[i][2]);                       
                    }       
                    if(currentMD5!=null&&!currentMD5.trim().equals(""))
                    {
                       ConnectionMonitor.this.data[i][10]=currentMD5;               
                    }
                }
            }
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        // update the table data.
        addData(data, this.tableColumnNames = getInsertColumnInHeaderDataArray(rosNode.getMonitoringTableColumnNames()));
    }           
    
    /** Parse service types String document to Map of Service name key, 
     * Service Type value. 
     */
    private Map parseServiceTypeImportPropertiesToMap(String importDocument)
    {
        this.lines = importDocument.split(System.getProperty("line.separator"));
        this.serviceTypesMap = new HashMap<String,String>();
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].startsWith("type:service:")&&(i+1)<lines.length&&!lines[i+1].startsWith("type:service:"))
            {
                serviceTypesMap.put(lines[i].substring(13),lines[i+1]);
                // iterate past following line.
                i = i + 1;
            }
        }
        return serviceTypesMap;
    }
    

    /** Parse the topic/service md5 String document to Map of topic/service 
     * name key, md5sum value. 
     */
    private Map parseMD5ImportPropertiesToMap(String importDocument)
    {
        this.lines = importDocument.split(System.getProperty("line.separator"));
        this.md5Map = new HashMap<String,String>();
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].startsWith("md5:service:")&&(i+1)<lines.length&&!lines[i+1].startsWith("md5:service:"))
            {
                md5Map.put(lines[i],lines[i+1]);
                // iterate past following line.
                i = i + 1;
            }
            if(lines[i].startsWith("md5:topic:")&&(i+1)<lines.length&&!lines[i+1].startsWith("md5:topic:"))
            {
                md5Map.put(lines[i],lines[i+1]);
                // iterate past following line.
                i = i + 1;
            }          
        }
        return md5Map;
    }    
    
    /** Open File, read, and return String document. 
     * 
     * @return String text document.
     */
    private String openTextFile() throws FileNotFoundException, IOException
    {
        this.chooser = new JFileChooser();
        this.returnVal = chooser.showOpenDialog(this);
        // Select file to import via file chooser.
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            this.fileOpen = chooser.getSelectedFile();
            //This is where a real application would open the file.
            this.br = new BufferedReader(new FileReader( fileOpen));
            try 
            {
                this.sb = new StringBuffer();
                this.line = br.readLine();

                while (line != null) 
                {
                    sb.append(line);
                    sb.append("\n");
                    this.line = br.readLine();
                }
                return sb.toString();
            } 
            finally 
            {
                br.close();
            }
        }
        return null;
    }
    
    /** Save document String to File as executable script. */
    private void saveStringToFile(String document)
    {
        this.chooser = new JFileChooser();
        chooser.setCurrentDirectory(this.file=new File("."));
        file.setExecutable(true);
        file.setReadable(true);
        file.setWritable(true);
        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) 
        {
            try 
            {
                if(System.getProperty("os.name").startsWith("Windows"))
                {
                    this.fw = new FileWriter(chooser.getSelectedFile()+".bat");                            }
                else
                {
                    this.fw = new FileWriter(chooser.getSelectedFile()+".sh");
                }
                       
                fw.write(document);
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
            finally
            {
                try 
                {
                    fw.flush();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try 
                {
                    fw.close();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }    
    
    /** Return the ROS services types collector script. 
     * 
     * @return String ROS services types Collector script.
     */
    private String getServiceTypesScript(String outputFileName)
    {
        this.document = new StringBuffer();
        // loop through topic/service data.
        for(int i=0;i<data.length;i++)
        {
            if(data[i][1].equals("Service"))
            {
                document.append("echo \"type:service:" + data[i][2] + "\" >> " + outputFileName + "\n");
                document.append("rosservice type " + data[i][2] + " >> " + outputFileName + "\n");
            }
        }         
        return document.toString();
    }      
    
    /** Return the ROS topic/service md5 collector script. 
     * 
     * @return String ROS topic/service MD5 Collector script.
     */
    private String getMD5Script(String outputFileName)
    {
        this.document = new StringBuffer();
        // loop through topic/service data.
        for(int i=0;i<data.length;i++)
        {
            if(data[i][1].equals("Service"))
            {
                document.append("echo \"md5:service:" + data[i][2] + "\" >> " + outputFileName + "\n");
                document.append("rossrv md5 " + data[i][3] + " >> " + outputFileName + "\n");
            }
            else
            {
                document.append("echo \"md5:topic:" + data[i][2] + "\" >> " + outputFileName + "\n");
                document.append("rosmsg md5 " + data[i][3] + " >> " + outputFileName + "\n");
            }
        }         
        return document.toString();
    }
    
    private Object[] tableColumnNames;
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        this.data = getInsertColumnInDataArray(rosNode.getMonitoringInformation());
        for(int i=0;i<data.length;i++)
        {
            if(data[i][12].equals("1"))
            {    
                data[i][12]=TRUE;
            }
            else
            {
                data[i][12]=FALSE;
            }
            if(data[i][13].equals("1"))
            {    
                data[i][13]=TRUE;
            }
            else
            {
                data[i][13]=FALSE;
            }     
            if(data[i][14].equals("1"))
            {    
                data[i][14]=TRUE;
            }
            else
            {
                data[i][14]=FALSE;
            }
            if(data[i][15].equals("1"))
            {    
                data[i][15]=TRUE;
            }
            else
            {
                data[i][15]=FALSE;
            }            
            if(data[i][16].equals(""))
            {    
                data[i][16]="1024";
            }
            if(data[i][17].equals(""))
            {    
                data[i][17]="1024";
            }   
            if(data[i][19].equals(""))
            {
                data[i][19]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultMessageHandler";
            }
            if(data[i][20].equals(""))
            {
                data[i][20]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultInitializerMessageHandler";
            }
            if(data[i][21].equals(""))
            {
                data[i][21]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaulTunerMessageHandler";
            }       
            if(data[i][26].equals("1"))
            {    
                data[i][26]=TRUE;
            }
            else
            {
                data[i][26]=FALSE;
            }   
            if(data[i][27].equals(""))
            {
                data[i][27]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherMessageHandler";
            }
            if(data[i][28].equals(""))
            {
                data[i][28]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherMessageHandler";
            }
            if(data[i][29].equals(""))
            {
                data[i][29]="org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaulPublisherTunerMessageHandler";
            }               
        // update the table data.
            addData(data, this.tableColumnNames = getInsertColumnInHeaderDataArray(rosNode.getMonitoringTableColumnNames()));
        }   
      table.setPreferredScrollableViewportSize(new Dimension(5534,600));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

for (int i = 0; i < 30; i++) 
{
    column = table.getColumnModel().getColumn(i);
    if (i == 0) 
    {
        column.setPreferredWidth(36); //third column is bigger
    }     
    else if (i == 1) 
    {
        column.setPreferredWidth(110); //third column is bigger
    } 
    else if (i == 2) 
    {
        column.setPreferredWidth(200);
    }
    else if (i == 3) 
    {
        column.setPreferredWidth(150);
    }    
    else if (i == 4) 
    {
        column.setPreferredWidth(150);
    }
    else if (i == 5) 
    {
        column.setPreferredWidth(150);
    }
    else if (i == 6) 
    {
        column.setPreferredWidth(150);
    }    
    else if (i == 7) 
    {
        column.setPreferredWidth(130);
    }
    else if (i == 8) 
    {
        column.setPreferredWidth(80);
    }
    else if (i == 9) 
    {
        column.setPreferredWidth(70);
    } 
    else if (i == 10) 
    {
        column.setPreferredWidth(150);
    }
    else if (i == 11) 
    {
        column.setPreferredWidth(200);
    }
    else if (i == 12) 
    {
        column.setPreferredWidth(140);
    } 
    else if (i == 13) 
    {
        column.setPreferredWidth(140);
    }
    else if (i == 14) 
    {
        column.setPreferredWidth(100);
    }
    else if (i == 15) 
    {
        column.setPreferredWidth(100);
    } 
    else if (i == 16) 
    {
        column.setPreferredWidth(130);
    }
    else if (i == 17) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 18) 
    {
        column.setPreferredWidth(120);
    } 
    else if (i == 19) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 20) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 21) 
    {
        column.setPreferredWidth(120);
    } 
    else if (i == 22) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 23) 
    {
        column.setPreferredWidth(120);
    }
    
    else if (i == 27) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 28) 
    {
        column.setPreferredWidth(120);
    }
    else if (i == 29) 
    {
        column.setPreferredWidth(120);
    }     
    else
    {
        column.setPreferredWidth(100);        
    }        
    }
        // update column 0
        this.data=getUpdateStatusColumn(data, getConnectionStatusImages());
        updateStatusIcons();
    }   
    private void mnuItemSaveActionPerformed(java.awt.event.ActionEvent evt) 
    {             
        save();
    } 
      
      
    private void mnuItemCloseActionPerformed(java.awt.event.ActionEvent evt) 
    {             
        setVisible(false);
        dispose();
    } 
        
    private void mnuItemExitActionPerformed(java.awt.event.ActionEvent evt) 
    {             
        System.exit(0);
    }                                           

    private void mnuItemHowtoConfigureActionPerformed(java.awt.event.ActionEvent evt) {                                                 
         information = new InformationDialog(this, true) ;
         information.setVisible(true);
    }       
            
    private void mnuItemHelpAboutActionPerformed(java.awt.event.ActionEvent evt) {                                                 
         about = new AboutBoxDialog(this, true) ;
         about.setVisible(true);
    }     
    
    // Method code from http://stackoverflow.com/questions/4165562/set-order-of-columns-in-jtable
 /*   public static void setColumnOrder(int[] indices, TableColumnModel columnModel) 
    {
        TableColumn column[] = new TableColumn[indices.length];

        for (int i = 0; i < column.length; i++) {
            column[i] = columnModel.getColumn(indices[i]);
        }

        while (columnModel.getColumnCount() > 0) {
            columnModel.removeColumn(columnModel.getColumn(0));
        }

        for (int i = 0; i < column.length; i++) {
            columnModel.addColumn(column[i]);
        }
    }    
   */ 
    // setConnectionStatus method variables;
    // TODO image
    private ImageIcon todoIcon;
    // Service Connected Image   
    private ImageIcon connectedIcon;
    // Service Not Connected Image
    private ImageIcon notConnectedIcon; 
    // Subscriber only Connected Image   
    private ImageIcon subConnectedIcon;
    // Publisher only Connected Image
    private ImageIcon pubConnectedIcon;    
    // Subscriber only Disconnected Image   
    private ImageIcon subDisconnectedIcon;
    // Publisher only Disconnected Image
    private ImageIcon pubDisconnectedIcon;         
    // Pub/Sub Connected Image   
    private ImageIcon pubSubConnectedIcon;
    // Pub/Sub Not Connected Image
    private ImageIcon pubSubNotConnectedIcon;
    // Pub Connected/Sub Not Connected Image   
    private ImageIcon pubConnectedSubNotConnectedIcon;
    // Pub/Sub Not Connected Image
    private ImageIcon pubNotConnectedSubConnectedIcon;       
    private ImageIcon[] statusIcons;
    private String[] topicSubscriberAPIs;
    private String[] topicPublisherURLs;    
    private CommunicationSenderInterface[] subscriberProviders;
    private CommunicationSenderInterface[] publisherProviders;
    private CommunicationSenderInterface[] serviceProviders; 
    private CommunicationSenderInterface subscriberProvider;
    private CommunicationSenderInterface publisherProvider;    
    private String md5Ref;
    private int imageCount;
    private int subscriberProvidersLength;
    private boolean subscriber_is_todo;
    private boolean publisher_is_todo;      
    private boolean subscriber_is_connected;
    private boolean publisher_is_connected; 
    private boolean subscriber_is_registered;
    private boolean publisher_is_registered;     
    /** Set Topic/Service Connection Status images in the statusIcons array. Image descriptions are tooltips. */
    private ImageIcon[] getConnectionStatusImages()
    {  
        this.subscriberProviders=rosNode.getSubscriberSenders();
        this.publisherProviders=rosNode.getPublisherSenders();
        this.serviceProviders=rosNode.getServiceSenders();
        this.topicSubscriberAPIs=rosNode.getTopicSubscriberAPIs();
        this.topicPublisherURLs=rosNode.getTopicPublisherURLs();        
        // Image Status Array
        if(statusIcons==null||(subscriberProviders.length + serviceProviders.length)!=statusIcons.length)
        {
            this.statusIcons = new ImageIcon[(subscriberProviders.length + serviceProviders.length)];
        }
        // Set imageCount loop count to 0;
        this.imageCount=0;
        // Update Connection Status (ImageIcon/String) for each topic/service
        while(imageCount<subscriberProviders.length)
        {
            this.subscriber_is_todo=false;
            this.publisher_is_todo=false;  
            this.subscriberProvider = subscriberProviders[imageCount];
            this.publisherProvider = publisherProviders[imageCount];            
            if(subscriberProvider!=null)
            {
                if(subscriberProvider.isConnected())
                {
                    // set isConnected icon
                    this.subscriber_is_connected=true;
                }
                else
                {
                    // Either incomplete, warning or disconnected icon
                    // if topic MD5Sum is empty its a todo icon, otherwise its not connected icon.
                    if((this.md5Ref=((String)data[imageCount][10]))!=null&&!md5Ref.isEmpty())
                    {
                        // not connected icon.
                         this.subscriber_is_connected=false;
                    }
                    else
                    {
                        // TODO icon
                        this.subscriber_is_todo=true;
                    }
                }
                if(topicPublisherURLs[imageCount]!=null&&!topicPublisherURLs[imageCount].isEmpty())
                {
                    // topic publisher url is registered
                    this.subscriber_is_registered=true;
                }
                else
                {
                    // topic publisher url is not registered.
                    this.subscriber_is_registered=false;                    
                }
            }
            else
            {
               
                // Either incomplete, warning or disconnected icon
                // if topic MD5Sum is empty its a todo icon, otherwise its not connected icon.
                if((this.md5Ref=((String)data[imageCount][10]))!=null&&!md5Ref.isEmpty())
                {
                    // not connected icon.
                     this.subscriber_is_connected=false;
                }
                else
                {
                    // TODO icon
                    this.subscriber_is_todo=true;
                }
                if(topicPublisherURLs[imageCount]!=null&&!topicPublisherURLs[imageCount].isEmpty())
                {
                    // topic publisher url is registered
                    this.subscriber_is_registered=true;
                }
                else
                {
                    // topic publisher url is not registered.
                    this.subscriber_is_registered=false;                    
                }
                // if topic connect_on_start is 0, display  not connected icon.
                if(!(((Boolean)data[imageCount][12])))
                {
                    // not connected icon.
                     this.subscriber_is_connected=false;
                }
                else
                {
                    if(rosNode.getIsSubscribable(rosNode.getTopicByIndice(imageCount)))
                    {
                        // TODO icon
                        this.subscriber_is_todo=true;
                    }
                    else
                    {
                        // No todo icon since the topic is not subscribable
                        this.subscriber_is_todo=false;                        
                    }
                }
            }        
            if (publisherProvider != null)           
            {
                if(publisherProvider.isConnected())
                {
                    // set isConnected icon
                    this.publisher_is_connected=true;
                }
                else
                {                 
                    // Either incomplete, warning or disconnected icon
                    // if topic MD5Sum is empty its a todo icon, otherwise its not connected icon.
                    if((this.md5Ref=((String)data[imageCount][10]))!=null&&!md5Ref.isEmpty())
                    {
                        // not connected icon.
                         this.publisher_is_connected=false;
                    }
                    else
                    {
                        // TODO icon
                        this.publisher_is_todo=true;
                    }
                    if(topicSubscriberAPIs[imageCount]!=null&&!topicSubscriberAPIs[imageCount].isEmpty())
                    {
                        // topic publisher url is registered
                        this.publisher_is_registered=true;
                    }
                    else
                    {
                        // topic publisher url is not registered.
                        this.publisher_is_registered=false;                    
                    }                    
                }
            }
            else
            {
                // Either incomplete, warning or disconnected icon
                // if topic MD5Sum is empty its a todo icon, otherwise its not connected icon.
                if((this.md5Ref=((String)data[imageCount][10]))!=null&&!md5Ref.isEmpty())
                {
                    // not connected icon.
                     this.publisher_is_connected=false;
                }
                else
                {
                    // TODO icon
                    this.publisher_is_todo=true;
                }
                if(topicSubscriberAPIs[imageCount]!=null&&!topicSubscriberAPIs[imageCount].isEmpty())
                {
                    // topic publisher url is registered
                    this.publisher_is_registered=true;
                }
                else
                {
                    // topic publisher url is not registered.
                    this.publisher_is_registered=false;                    
                }  
                // if topic connect_publisher_on_start is 0, display  not connected icon.
                if(!(((Boolean)data[imageCount][26])))
                {
                     // not connected icon.
                     this.publisher_is_connected=false;
                }
    //            else if(!publisher_is_connected)
     //           {
     //                           System.out.println("!!!!!!!!!!!!!!!!!!!!!!: " + (((Boolean)data[imageCount][26])));
                    // TODO icon
     //               this.publisher_is_todo=true;                
     //           }                
            }            
//            System.out.println("publisher_is_todo: " + publisher_is_todo);
//            System.out.println("publisher_is_registered: " + publisher_is_registered);
//            System.out.println("publisher_is_connected: " + publisher_is_connected);                            
            if(publisher_is_todo||subscriber_is_todo)
            {
                // Is a todo...
                statusIcons[imageCount]=todoIcon;
            }
            else if(publisher_is_connected&&subscriber_is_connected)
            {
                // publisher/subscriber connected
                statusIcons[imageCount]=pubSubConnectedIcon;
            }
            else if(!publisher_is_connected&&!subscriber_is_connected)
            {
                if(publisher_is_registered&&!subscriber_is_registered)
                {
                    // publisher is connected
                    statusIcons[imageCount]=pubDisconnectedIcon;
                }   
                else if(!publisher_is_registered&&subscriber_is_registered)
                {
                    // subscriber is connected
                    statusIcons[imageCount]=subDisconnectedIcon;
                }                 
                else
                {
                    // publisher/subscriber disconnected
                    statusIcons[imageCount]=pubSubNotConnectedIcon;
                }                
            }            
            else if(publisher_is_connected&&!subscriber_is_connected)
            {
                if(publisher_is_registered&&!subscriber_is_registered)
                {
                    // publisher is connected
                    statusIcons[imageCount]=pubConnectedIcon;
                }               
                else
                {
                    // publisher connected/subsciber disconnected
                    statusIcons[imageCount]=pubConnectedSubNotConnectedIcon;
                }                 
            }                   
            else if(!publisher_is_connected&&subscriber_is_connected)
            {
                if(!publisher_is_registered&&subscriber_is_registered)
                {
                    // subscriber is connected
                    statusIcons[imageCount]=subConnectedIcon;
                }               
                else
                {
                    // publisher disconnected/subsciber connected
                    statusIcons[imageCount]=pubNotConnectedSubConnectedIcon;
                }                                     
            }   
            this.imageCount = imageCount + 1;
        }
        this.subscriberProvidersLength=subscriberProviders.length;
        this.imageCount=subscriberProvidersLength;
        // count service connections
        while(imageCount<(subscriberProvidersLength + serviceProviders.length))
        {
            this.subscriberProvider = serviceProviders[imageCount-subscriberProvidersLength];
            // set subscriber_is_todo to false. At end if true then set todo icon
            this.subscriber_is_todo=false;
            if(subscriberProvider!=null)
            {
                if(subscriberProvider.isConnected())
                {
                    // set isConnected icon
                    statusIcons[imageCount]=connectedIcon;
                }
                else
                {
                    // Either incomplete, warning or disconnected icon
                    // if topic MD5Sum is empty its a todo icon, otherwise its not connected icon.
                    if((this.md5Ref=((String)data[imageCount][10]))!=null&&!md5Ref.isEmpty())
                    {
                        // not connected icon.
                         statusIcons[imageCount]=notConnectedIcon;
                    }
                    else
                    {
                        // TODO icon
                        statusIcons[imageCount]=todoIcon;
                    }
                }
            }
            else
            {
                // Either incomplete, warning or disconnected icon
                // if topic MD5Sum is empty its a todo icon, otherwise its not connected icon.
                if((this.md5Ref=((String)data[imageCount][10]))!=null&&!md5Ref.isEmpty())
                {
                    // not connected icon.
                     statusIcons[imageCount]=notConnectedIcon;
                }
                else
                {
                    this.subscriber_is_todo=true;
                }
                
                // if topic connect_on_start is 0, display  not connected icon.
                if(!(((Boolean)data[imageCount][12])))
                {
                    // not connected icon.
                     statusIcons[imageCount]=notConnectedIcon;
                }
                else
                {
                    // TODO icon
                    this.subscriber_is_todo=true;
                }   
                if(subscriber_is_todo)
                {
                    // TODO icon
                    statusIcons[imageCount]=todoIcon;
                }
            }            
            this.imageCount = imageCount + 1;            
        }
        this.subscriberProviders=null;
        this.serviceProviders=null;
        return statusIcons;
    }
    
    private void updateStatusIcons()
    {
        for(int i=0;i<statusIcons.length;i++)
        {
            table.setValueAt(statusIcons[i], i, 0);
        }
    }
    
    private int columns;
    private int rows;
    private Object[] columnData;
    private Object[][] getUpdateStatusColumn(Object[][] dataArray, ImageIcon[] images)
    {
        this.columns = tableModel.getColumnCount();
        this.rows = tableModel.getRowCount();
       table.getColumnModel().getColumn(0).setCellRenderer(new ConnectionMonitor.ImageTooltipCellRenderer());
        for (int i = 0; i < dataArray.length; i++)
        {
            dataArray[i][0] = images[i];
        }

       return dataArray;
    }   
    
    class ImageTooltipCellRenderer extends DefaultTableCellRenderer 
    {
        public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) 
        {
            String imageDescriptionValue;
            JLabel c = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // This...
            c.setIcon((Icon)value);
            c.setText("");
            c.setHorizontalAlignment(JLabel.CENTER);
            if(value!=null&&value.getClass().getName().indexOf("Icon")!=-1)
            {
                imageDescriptionValue = ((ImageIcon)value).getDescription();
            }
            else
            {
                imageDescriptionValue="";
            }
            c.setToolTipText(imageDescriptionValue);
            return c;
        }
    }    
}
