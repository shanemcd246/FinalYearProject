import com.example.apptotomcat.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;

@WebServlet(name = "pleaseWork",urlPatterns = {"/pleaseWork"})
public class ProjectServlet extends HttpServlet{
    private Connection connection;
    private PreparedStatement getData;
    public void init(ServletConfig config) throws ServletException {
        System.out.println("IM HERE IN INIT\n");
        // attempt database connection and create PreparedStatements
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/servlettest", "root", "root");
            getData = connection.prepareStatement("SELECT * from users WHERE id = 1");
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new UnavailableException(exception.getMessage());
        }
    }  // end of init method
    protected void processRequest(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException, ClassNotFoundException, SQLException {
        System.out.println("IM HERE IN DOGET\n");
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
        ResultSet rs = getData.executeQuery();
        WeedData dataToSend = new WeedData();
        if ( !rs.next() ) {
            System.out.println("nothing to find");
                //oos.writeObject(dataToSend);
                //oos.writeObject();
        }
        else{
            System.out.println(rs.getString(2));
            System.out.println(rs.getString(3));
            dataToSend.setName(rs.getString(2));
            dataToSend.setPassword(rs.getString(3));
        }
        System.out.println("Sending Uname : "+dataToSend.getName());
        System.out.println("Sending password : "+dataToSend.getPassword());
        oos.writeObject(dataToSend);
        //oos.writeObject(val);
        ois.close();
        oos.flush();
        oos.close();





//        System.out.println("9");
//        try {
//            System.out.println("10");
//            Integer val = (Integer)ois.readObject();
//            System.out.println("11");
//            getData.setInt(1, (int)val);
//            System.out.println(getData.toString());
//            ResultSet rs = getData.executeQuery();
//            if ( !rs.next() ) {
//                System.out.println("nothing to find");
//                //oos.writeObject(d);
//                oos.writeObject();
//            }
//            else{
//                System.out.println(rs.getString(2));
//                System.out.println(rs.getString(3));
//                d.setName(rs.getString(2));
//                d.setPassword(rs.getString(3));
//                oos.writeObject(d);
//            }
//            ois.close();
//            rs.close();
//            getData.close();
//            connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            System.out.println("Interger fdsf");
//        } finally{
//
//            oos.writeObject(last);
//            oos.flush();
//            oos.close();
//        }
    }
    public void destroy()
    {
        // attempt to close statements and database connection
        try {
            getData.close();
            connection.close();
        }
        // handle database exceptions by returning error to client
        catch( SQLException sqlException ) {
            sqlException.printStackTrace();
        }
    }  // end of destroy method
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
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

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}