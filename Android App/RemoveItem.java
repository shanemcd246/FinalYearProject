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

    public void init(ServletConfig config) throws ServletException {
        System.out.println("IM HERE IN submitAnswer INIT\n");

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
        response.setContentType("application/octet-stream");
        InputStream in = request.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);
        OutputStream outstr = response.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outstr);
        List message = (List) ois.readObject();
        Integer resultCode =0;
        try{
            deleteItem = connection.prepareStatement("DELETE FROM "+message.get(0)+" where ID = "+message.get(1)+"");
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
