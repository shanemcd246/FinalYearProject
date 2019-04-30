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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "deleteItem",urlPatterns = {"/deleteItem"})
public class RemoveItem extends HttpServlet {
    private Connection connection;
    private PreparedStatement deleteItem;
    private PreparedStatement getLocation;

    public void init(ServletConfig config) throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/finalyearproject", "root", "root");
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new UnavailableException(exception.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException, ClassNotFoundException, SQLException {
        // set up response to client
        System.out.println("HERE in DELETE");
        response.setContentType("application/octet-stream");
        InputStream in = request.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);
        OutputStream outstr = response.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outstr);
        List message = (List) ois.readObject();
        System.out.println(message);
        Integer resultCode =0;
        try{
            System.out.println("mmmmmmmmmmm");
            getLocation = connection.prepareStatement("SELECT * FROM "+message.get(0)+" where ID =?");
            getLocation.setString(1, String.valueOf(message.get(1)));
            System.out.println("asfdgfhgjh");
            deleteItem = connection.prepareStatement("DELETE FROM "+message.get(0)+" where ID ="+message.get(1)+"");
            String imageLocation ="NOTSET";
            System.out.println("DONE ITserdhgfhgjh");
            ResultSet rs = getLocation.executeQuery();
            System.out.println("DONE IT");
            if ( !rs.next() ) {
                System.out.println("nothing to find");
            }
            else {
                do {
                    System.out.println("init");
                    imageLocation = rs.getString("IMAGE");;
                    System.out.println("GOT " + imageLocation);
                } while (rs.next());
            }
            System.out.println(imageLocation);
            File file = new File(imageLocation);
            if(file.delete()){
                System.out.println(imageLocation + " Removed");
            }else System.out.println(imageLocation +" doesn't exist");
            resultCode =deleteItem.executeUpdate();
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
            deleteItem.close();
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
