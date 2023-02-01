import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class DB {
    public static Connection TSListConnection, AltiumConnection;
    public static Statement TSListSelectStatement, AltiumSelectStatement;
    public static ResultSet TSListSelectResultSet, AltiumSelectResultSet;
    public static ArrayList<String> partNumberList = new ArrayList<>();
    public static ArrayList<String> manufacturerList = new ArrayList<>();
    public static ArrayList<String> chipList = new ArrayList<>();
    public static ArrayList<String> kuList = new ArrayList<>();
    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void ConnectToTSList() throws ClassNotFoundException, SQLException
    {
        String fileName = "Разработанные тестовые решения (DB Core).accdb";
        String DB_URL = "jdbc:ucanaccess://D:/" + fileName;
        TSListConnection = null;
        TSListConnection = DriverManager.getConnection(DB_URL);
        if (TSListConnection != null) {
            DatabaseMetaData meta = TSListConnection.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("База Подключена!");
        }
        TSListSelectStatement = TSListConnection.createStatement();

    }

    public static void ConnectToAltium() throws ClassNotFoundException, SQLException
    {
        String fileName = "Altium Database (DB Core).accdb";
        String DB_URL = "jdbc:ucanaccess://D:/" + fileName;  //  //y:/DB/DB Core/
        AltiumConnection = null;
        AltiumConnection = DriverManager.getConnection(DB_URL);
        if (AltiumConnection != null) {
            DatabaseMetaData meta = AltiumConnection.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("База Подключена!");
        }
        AltiumSelectStatement = AltiumConnection.createStatement();

    }

    public static void GetDataFromAltiumDB() throws SQLException {
        String sql ="SELECT * FROM [Объекты контроля];";
        AltiumSelectResultSet = AltiumSelectStatement.executeQuery(sql);
        while(AltiumSelectResultSet.next()) {
            String partNumber = AltiumSelectResultSet.getString("PartNumber");
            String manufacturer = AltiumSelectResultSet.getString("Manufacturer");
            String chip = AltiumSelectResultSet.getString("Chip");
            String ku = AltiumSelectResultSet.getString("Drawning");
            partNumberList.add(partNumber);
            manufacturerList.add(manufacturer);
            chipList.add(chip);
            kuList.add(ku);
        }

    }
    public static DefaultTableModel get_all_test_solutions(DefaultTableModel model) throws SQLException, IOException {
        String sql ="SELECT * FROM Глобальный_перечень_тестовых_решений ORDER BY Изделие,CHAR_LENGTH(Изделие);";
        TSListSelectResultSet = TSListSelectStatement.executeQuery(sql);
        while(TSListSelectResultSet.next()) {
            String testSolution = TSListSelectResultSet.getString("Изделие");
            String type = TSListSelectResultSet.getString("ИСП");
            String objectsOfControl = TSListSelectResultSet.getString("Микросхема");
            String description = TSListSelectResultSet.getString("Описание");
            String chipPackage = TSListSelectResultSet.getString("КУ");
            String kuPackage = TSListSelectResultSet.getString("Номер_чертежа");
           // System.out.println(testSolution + " " + objectsOfControl + " " + description);
            if (testSolution != null) {
                char [] letters = testSolution.toCharArray();
                String solution_number = "";
                String solution_letters = "";
                String solution_version = "";
                int index = testSolution.indexOf('-');
                if (index > 0) {
                    solution_version = testSolution.substring(index, testSolution.length());
                }
                for (int i = 0; i < index ; i++) {
                    if (Character.isDigit(letters[i])) {

                        solution_number = solution_number + letters[i];
                    } else  solution_letters = solution_letters + letters[i];
                }
                if (solution_number.length() > 0 && solution_number.charAt(0) == '0' && solution_number.charAt(1) == '0'){
                    solution_number = solution_number.replaceFirst("0","");
                    solution_number = solution_number.replaceFirst("0","");
                }
                if (solution_number.length() > 0 && solution_number.charAt(0) == '0'){
                    solution_number = solution_number.replaceFirst("0","");

                }
                String new_solution_name = solution_letters + solution_number + solution_version;
              // System.out.println(new_solution_name);
                testSolution = new_solution_name;
            }

            int pnIndex = -1;
            String chip = "";
            chip = chipPackage;
           // System.out.println(chip);
            if (chip == null) {
                pnIndex = partNumberList.indexOf(objectsOfControl);
                if (pnIndex > 0) {
                    chip = chipList.get(pnIndex);
                    //System.out.println(chip + " ****");
                }
            }


            String ku = "";
            ku = kuPackage;
            // System.out.println(chip);
            if (ku == null) {

                if (pnIndex > 0) {
                    ku = kuList.get(pnIndex);
                    //System.out.println(chip + " ****");
                }
            }

           /* Vector<String> row = new Vector<>();
            row.add(testSolution);
            row.add(type);
            row.add(objectsOfControl);
            row.add(description);
            row.add(chip);
            row.add(ku);*/
            Object [] row = new Object[7];
            row[0] = testSolution;
            row[1] = type;
            row[2] = objectsOfControl;
            row[3] = description;
            row[4] = chip;
            row[5] = ku;
          //  row[6] = "<HTML><a href=" +  objectsOfControl + ">Загрузить Datasheet</a></HTML>";

  /*          String dir = "y:/DB/Datasheets/Components/";
            File f = new File(dir + objectsOfControl + ".pdf");
             /* Filename must be constructed with a canonical path in
            order to successfully use Desktop.browse(URI)! */
  /*        if (f.exists()) {
               f = new File(f.getCanonicalPath());
               URI uriFile = f.toURI();
             //  LinkLabel linkLabelFile = new LinkLabel(uriFile,objectsOfControl + ".pdf");
             //  linkLabelFile.init();
               row[6] = "<HTML><a href=" + uriFile + objectsOfControl + ".pdf>" + objectsOfControl +"</a></HTML>";
           }*/

            model.addRow(row);

        }
        return model;

    }
    public static void Close() throws ClassNotFoundException, SQLException
    {
        if(TSListSelectResultSet != null)
            TSListSelectResultSet.close();
        if(TSListSelectStatement != null)
            TSListSelectStatement.close();
         if(TSListConnection != null)
            TSListConnection.close();

        if(AltiumSelectResultSet != null)
            AltiumSelectResultSet.close();
        if(AltiumSelectStatement != null)
            AltiumSelectStatement.close();
        if(AltiumConnection != null)
            AltiumConnection.close();
        System.out.println("Соединения закрыты");
    }
}
