import com.example.appforproject.WeedData;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "getIDs",urlPatterns = {"/getIDs"})
public class GetIDs extends HttpServlet {
    private Connection connection;
    private PreparedStatement getIDsQuery;

    public void init(ServletConfig config) throws ServletException {
        System.out.println("Here in getIDs init");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/finalyearproject", "root", "root");

        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new UnavailableException(exception.getMessage());
        }
    }  // end of init method


    protected void processRequest(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException, ClassNotFoundException, SQLException {
        response.setContentType("application/octet-stream");
        InputStream in = request.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);
        OutputStream outstr = response.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outstr);
        List message = (List) ois.readObject();
        System.out.println(message.get(0));
        ArrayList<Integer> idList = new ArrayList<>();
        getIDsQuery = connection.prepareStatement("SELECT * from "+message.get(0));
        ResultSet rs = getIDsQuery.executeQuery();
        WeedData dataToSend = new WeedData();
        if ( !rs.next() ) {
            System.out.println("nothing to find");
        }
        else{
            do{
                idList.add(rs.getInt(1));
            }while (rs.next());
            dataToSend.setIdNumbers(idList);
        }
        System.out.println(idList);
        System.out.println("FOUND "+idList.size());
        oos.writeObject(dataToSend);
        ois.close();
        oos.flush();
        oos.close();
    }

    public void destroy() {
        try {
            connection.close();
            getIDsQuery.close();
        }
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        }
    }

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
