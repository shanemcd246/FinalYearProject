import com.example.appforproject.WeedData;

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
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;

@WebServlet(name = "submitAnswer",urlPatterns = {"/submitAnswer"})
public class AddName extends HttpServlet {
    private Connection connection;
    private PreparedStatement setName;
    private PreparedStatement getlocation;

    public void init(ServletConfig config) throws ServletException {
        System.out.println("IM HERE IN submitAnswer INIT\n");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/finalyearproject", "root", "root");
            setName = connection.prepareStatement("UPDATE feildrun_notdetected set PLANTNAME=? where ID =?");
            getlocation = connection.prepareStatement("SELECT * FROM feildrun_notdetected where ID =?");
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
        WeedData d = (WeedData) ois.readObject();
        System.out.println(d.getName());
        Integer resultCode =0;
        try{
            System.out.println(d.getId());
            setName.setString(1,d.getName());
            setName.setInt(2,d.getId());
            System.out.println("SGDGDGSGSG");
            setName.executeUpdate();
            getlocation.setInt(1,d.getId());
            ResultSet resultSet = getlocation.executeQuery();
            System.out.println("SGDGDGSGSG");
            if(resultSet.next()){
                String imageLocation = resultSet.getString("IMAGE");
                System.out.println(imageLocation);
                String timeDate = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date());
                System.out.println(timeDate);
                Files.copy(Paths.get(imageLocation),
                        Paths.get("C:/Users/shane/Documents/Tensorflow/trainDataSet/retainImages/" +
                                d.getName()+"/"+d.getName()+"_"+timeDate+".jpg"));
            }
            else{
                System.out.println("no DATA");
            }

            resultCode=1;
        }
        catch (Exception e){
            System.out.println(e);
            System.out.println("ERROR updatting database");
        }
        oos.writeObject(resultCode);

        ois.close();
        oos.flush();
        oos.close();

    }
    public void destroy()
    {
        // attempt to close statements and database connection
        try {
            setName.close();
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
