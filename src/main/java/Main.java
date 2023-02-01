import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
      //  SplashScreen sc = new SplashScreen();//Creating object of SplashScreenDemo class
        DBViewForm app = new DBViewForm();
        app.setVisible(true);


            }
}
