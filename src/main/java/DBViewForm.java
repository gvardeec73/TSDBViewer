import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class DBViewForm extends JFrame{

    private JPanel rootPanel;
    final String dir = "y:/DB/Datasheets/Components/";
    public static ArrayList<String> filesList= new ArrayList<>();
    public static ArrayList<String> filesPathList = new ArrayList<>();
    public static ArrayList<String> kuList = new ArrayList<>();
    public static ArrayList<String> kuPathList = new ArrayList<>();
    JLabel searchMessageLabel = new JLabel("Идет поиск Datasheet:");
    public JProgressBar searchProgressBar = new JProgressBar();
    int progress = 0;

    public void setTableAlignment(JTable table){
        // table header alignment
        JTableHeader header = table.getTableHeader();
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
        header.setDefaultRenderer(renderer);
        renderer.setHorizontalAlignment(JLabel.CENTER);

        // table content alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        int colNumber = table.getColumnCount();
        int rowNumber = table.getRowCount();
        for(int i = 0; i < 2; i++){
            table.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }


    }

    public static void searchDataSheets(File root, ArrayList<String> filesList, ArrayList<String> filesPathList) {



        try {
            boolean recursive = true;

            Collection files = FileUtils.listFiles(root, null, recursive);

            for (Iterator iterator = files.iterator(); iterator.hasNext(); ) {
                File file = (File) iterator.next();
                filesList.add(file.getName());
                filesPathList.add(file.getAbsolutePath());
                // System.out.println(file.getAbsolutePath());

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void addDataSheets(JXTable table) {
       System.out.println("Начат поиск..");
       File root = new File("y:\\DB\\Datasheets\\Components\\");
        searchDataSheets(root,filesList,filesPathList);

        for (int i = 0; i < table.getRowCount(); i++) {
            String partNumber = (String) table.getModel().getValueAt(i, 2);
            String fileName = partNumber + ".pdf";
            String ref = "<HTML><a href=" + partNumber + ".pdf>" + partNumber + ".pdf</a></HTML>";
            //System.out.println(fileName);

            if (filesList.contains(fileName)) {
                table.getModel().setValueAt(ref, i, 6);
            } else {
                table.getModel().setValueAt("", i, 6);
            }
        }
            File root2 = new File("y:\\DB\\!!TEST\\");
            searchDataSheets(root2,kuList,kuPathList);
            for (int j = 0; j < table.getRowCount(); j++) {
                String partNumber2 = (String) table.getModel().getValueAt(j, 5);
                String fileName2 = partNumber2 + ".pdf";
                String ref2 = "<HTML><a href=" + partNumber2 + ".pdf>" + partNumber2 + ".pdf</a></HTML>";
                //System.out.println(fileName);

                if (kuList.contains(fileName2)) {
                    table.getModel().setValueAt(ref2, j, 7);
                } else {
                    table.getModel().setValueAt("", j, 7);
                }
            }
        }

    public DBViewForm() throws SQLException, ClassNotFoundException, IOException {
        super();

        this.setTitle(Constants.PROGRAM_NAME + "v." + Constants.PROGRAM_VERSION);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        Container container = this.getContentPane(); //получаем слой JFrame для расположения элементов
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        // container.setLayout(new GridLayout(4, 2, 5, 5));


        //==============================================================================================================
        DefaultTableModel model = new DefaultTableModel();
// Create a couple of columns

        model.addColumn("Изделие");
        model.addColumn("ИСП");
        model.addColumn("Микросхема");
        model.addColumn("Описание");
        model.addColumn("Корпус");
        model.addColumn("КУ");
        model.addColumn("Datasheet микросхемы");
        model.addColumn("Datasheet КУ");

        DB.ConnectToTSList();
        DB.ConnectToAltium();
        DB.GetDataFromAltiumDB();
        DB.get_all_test_solutions(model);
        DB.Close();



        // Создание таблицы

        final JXTable table = new JXTable(model);
        setTableAlignment(table);
        TableColumn testSolutionCol = table.getColumn("Изделие");
        testSolutionCol.setMaxWidth(80);
        testSolutionCol.setMinWidth(80);
        TableColumn purposeCol = table.getColumn("ИСП");
        purposeCol.setMaxWidth(50);
        purposeCol.setMinWidth(50);
        table.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 16));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
                int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
            //    System.out.println(row + " " + col);

           //     String url = (String) table.getModel().getValueAt(row, col);
          //      System.out.println(url + " was clicked");
                // DO here what you want to do with your url
                if (col == 6){
                    String partNumber = (String) table.getModel().getValueAt(row, 2);
                    System.out.println(partNumber);
                    String fileName = partNumber + ".pdf";
                    if (filesList.contains(fileName)) {
                        String filePath = filesPathList.get(filesList.indexOf(fileName));
                        File f = new File(filePath);
                        if (f.exists()) {
                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().open(f);
                                    System.out.println(filePath);
                                } catch (IOException ex) {
                                    // no application registered for PDFs
                                }
                            }
                        }
                    } else {
                        table.getModel().setValueAt("",row,col);
                    }
                }
                if (col == 7){
                    String partNumber = (String) table.getModel().getValueAt(row, 5);
                    System.out.println(partNumber);
                    String fileName = partNumber + ".pdf";
                    if (kuList.contains(fileName)) {
                        String filePath = kuPathList.get(kuList.indexOf(fileName));
                        File f = new File(filePath);
                        if (f.exists()) {
                            if (Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().open(f);
                                    System.out.println(filePath);
                                } catch (IOException ex) {
                                    // no application registered for PDFs
                                }
                            }
                        }
                    } else {
                        table.getModel().setValueAt("",row,col);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
                if (col == 0) {
                    table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
                if (col != 0) {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        final JPanel btnPanel = new JPanel();
        final JButton loadDSBtn = new JButton("Искать Datasheet");

        btnPanel.setMaximumSize(new Dimension(Constants.WINDOW_WIDTH,20));
        btnPanel.add(loadDSBtn);

        searchProgressBar.setStringPainted(true);
        searchProgressBar.setMinimum(0);
        searchProgressBar.setMaximum(100);
        searchProgressBar.setValue(0);
        btnPanel.add(searchMessageLabel);
        btnPanel.add(searchProgressBar);
        searchMessageLabel.setVisible(false);
        searchProgressBar.setVisible(false);
        container.add(btnPanel);
        JScrollPane tablePanel = new JScrollPane();
        tablePanel.setViewportView(table);

        tablePanel.getViewport().setViewSize(new Dimension(600,400));
                container.add(tablePanel);

        loadDSBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDSBtn.setEnabled(false);
                searchMessageLabel.setVisible(true);

                JOptionPane jop = new JOptionPane();
                jop.setMessageType(JOptionPane.PLAIN_MESSAGE);
                jop.setMessage("Пожалуйста, подождите идет поиск Datasheet на сервере");
                final JDialog dialog = jop.createDialog(null, "Идет поиск Datasheet");

                // Set a 2 second timer
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                        }
                        dialog.dispose();
                    }

                }).start();
                dialog.setVisible(true);
                table.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                addDataSheets(table);
                table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                searchMessageLabel.setVisible(false);
                loadDSBtn.setVisible(false);
                btnPanel.setVisible(false);


              //

            }
        });

    }



}
