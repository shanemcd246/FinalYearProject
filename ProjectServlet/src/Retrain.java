import com.example.appforproject.WeedData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.HTTP;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;

@WebServlet(name = "retrain",urlPatterns = {"/retrain"})
public class Retrain extends HttpServlet {

    private Runtime commandPrompt;
    private Connection connection;
    private PreparedStatement getEnterys;
    private PreparedStatement deleteEntery;
    String retrainImageLocation = "C:\\weedImages";

    public void init(ServletConfig config) throws ServletException {
        System.out.println("IM HERE IN retrain INIT\n");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/finalyearproject", "root", "root");
            deleteEntery = connection.prepareStatement("DELETE FROM feildrun_notdetected WHERE ID =?");
            getEnterys = connection.prepareStatement("SELECT * FROM feildrun_notdetected");
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new UnavailableException(exception.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException, ClassNotFoundException, SQLException {
        // set up response to client
        response.setContentType("application/octet-stream");
        InputStream in = request.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);
        OutputStream outstr = response.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outstr);
        Integer temp = (Integer) ois.readObject();
        ResultSet rs = getEnterys.executeQuery();
        if (!rs.next()) {
            System.out.println("nothing to find");
        }
        else {
            do {
                if (rs.getString(2).length()>1) {
                    deleteEntery = connection.prepareStatement("DELETE FROM feildrun_notdetected WHERE ID ="+rs.getString(1)+"");
                    deleteEntery.executeUpdate();
                    System.out.println(rs.getString(4));
                    String [] arrOfStr = rs.getString(4).split("/");
                    File file = new File("C:\\Users\\shane\\PycharmProjects\\untitled\\NotDetected\\"+arrOfStr[arrOfStr.length-1]);
                    if(file.renameTo
                            (new File("C:\\weedImages\\"+rs.getString(2)+"\\"+arrOfStr[arrOfStr.length-1])))
                    {
                        // if file copied successfully then delete the original file
                        file.delete();
                        System.out.println("File moved successfully");
                    }
                    else
                    {
                        System.out.println("Failed to move the file");
                    }
                    System.out.println(arrOfStr[arrOfStr.length-1]);
                    System.out.println("Done");
                }
            } while (rs.next());
            Integer resultCode = 0;
            Process p = Runtime.getRuntime().exec("cmd.exe /c start python C:/Users/shane/Documents/Tensorflow/trainDataSet/retrain.py --image_dir C:/Users/shane/Documents/Tensorflow/trainDataSet/weedImages --random_crop 7 --random_scale 5 --random_brightness 5 --flip_left_right");

            oos.writeObject(resultCode);

            ois.close();
            oos.flush();
            oos.close();

        }
    }
        public void destroy()
        {
            // attempt to close statements and database connection
            try {
                getEnterys.close();
                connection.close();

            }
            // handle database exceptions by returning error to client
            catch( SQLException sqlException ) {
                sqlException.printStackTrace();
            }
        }  // end of destroy method

        protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
            doGet( request,response);
        }


        protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            try {
                processRequest(request, response);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}