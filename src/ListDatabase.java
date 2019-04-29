import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "listDatabase",urlPatterns = {"/listDatabase"})
public class ListDatabase extends HttpServlet {
    private Connection connection;
    private PreparedStatement sqlListDatabase;

    public void init(ServletConfig config) throws ServletException {
        System.out.println("IM HERE IN listDatabase INIT\n");
        // attempt database connection and create PreparedStatements
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/finalyearproject", "root", "root");
            //sqlListDatabase = connection.prepareStatement("SHOW DATABABES");
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new UnavailableException(exception.getMessage());
        }
    }  // end of init method


    protected void processRequest(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException, ClassNotFoundException, SQLException {
        // set up response to client

        System.out.println("9");
        response.setContentType("application/octet-stream");
        //DecimalFormat twoDigits = new DecimalFormat( "0.00" );
        System.out.println("1");
        InputStream in = request.getInputStream();
        System.out.println("2");
        ObjectInputStream ois = new ObjectInputStream(in);
        System.out.println("3");
        OutputStream outstr = response.getOutputStream();
        System.out.println("4");
        ObjectOutputStream oos = new ObjectOutputStream(outstr);
        System.out.println("5");
        Integer val = (Integer)ois.readObject();
        System.out.println("Value"+val);
        ArrayList<String> dbList = new ArrayList<String>();
        DatabaseMetaData md = connection.getMetaData();
        String[] types = {"TABLE"};
        ResultSet rs = md.getTables(null, null, "%", types);
        while (rs.next()) {
            System.out.println(rs.getString(3));
            String s =rs.getString("TABLE_NAME");
            if(s.contains("feildrun")){
                dbList.add(rs.getString("TABLE_NAME"));
            }
        }
        System.out.println(dbList);
        System.out.println(dbList.size());
        oos.writeObject(dbList);

        ois.close();
        oos.flush();
        oos.close();

    }
    public void destroy()
    {
        // attempt to close statements and database connection
        try {
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
