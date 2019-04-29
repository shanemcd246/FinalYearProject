import com.example.appforproject.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.util.List;

@WebServlet(name = "pleaseWork",urlPatterns = {"/pleaseWork"})
public class ProjectServlet extends HttpServlet{
    private Connection connection;
    private PreparedStatement getData;
    private PreparedStatement getCount;

    public void init(ServletConfig config) throws ServletException {
        System.out.println("IM HERE IN pleaseWork INIT\n");
        // attempt database connection and create PreparedStatements
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response ) throws ServletException,
            IOException, ClassNotFoundException, SQLException {
        System.out.println("IM HERE IN DOGET Servlet\n");
        response.setContentType("application/octet-stream");
        InputStream in = request.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(in);
        OutputStream outstr = response.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outstr);

        List message = (List) ois.readObject();
        System.out.println(message);
        //Integer val = (Integer)ois.readObject();
        //System.out.println("Value"+val);
        getData = connection.prepareStatement("SELECT * from "+ message.get(0) +" WHERE id = "+message.get(1)+"");
        ResultSet rs = getData.executeQuery();
        getCount = connection.prepareStatement("SELECT COUNT(*) from "+message.get(0)+"");
        ResultSet amount = getCount.executeQuery();
        WeedData dataToSend = new WeedData();

        if ( !amount.next() ) {
            System.out.println("nothing to find");
            //oos.writeObject(dataToSend);
            //oos.writeObject();
        }
        else{
            dataToSend.setMaxEntery(amount.getInt(1));
            System.out.println(amount.getInt(1));
        }


        //System.out.println(amount);

        if ( !rs.next() ) {
            System.out.println("nothing to find");
                //oos.writeObject(dataToSend);
                //oos.writeObject();
        }
        else{
            System.out.println(rs.getInt(1));
            System.out.println(rs.getString(2));
            System.out.println(rs.getString(3));
            System.out.println(rs.getBlob(4));
            dataToSend.setId(rs.getInt(1));
            dataToSend.setName(rs.getString(2));
            dataToSend.setScore(rs.getString(3));
            String imageLoc = rs.getString(4);
            FileInputStream fis = new FileInputStream(imageLoc);
            byte [] buffer = new byte[fis.available()];
            fis.read(buffer);
            System.out.println(buffer);
            dataToSend.setPhoto(buffer);
        }
        System.out.println("Sending Uname : "+dataToSend.getName());
        System.out.println("Sending score : "+dataToSend.getScore());
        oos.writeObject(dataToSend);
        //oos.writeObject(val);
        ois.close();
        oos.flush();
        oos.close();

    }
    public void destroy()
    {
        // attempt to close statements and database connection
        try {
            getData.close();
            getCount.close();
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