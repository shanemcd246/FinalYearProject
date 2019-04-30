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

@WebServlet(name = "getLonLat",urlPatterns = {"/getLonLat"})
public class GetLonLat extends HttpServlet {
    private Connection connection;
    private PreparedStatement lonLat;

    public void init(ServletConfig config) throws ServletException {
        System.out.println("IM HERE IN lonLat INIT\n");
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
        WeedData d = new WeedData();
        ArrayList<Double> Qlon = new ArrayList<>();
        ArrayList<Double> Qlat = new ArrayList<>();
        ArrayList<String> QgeoName = new ArrayList<>();
        try{
            lonLat = connection.prepareStatement("SELECT * FROM "+message.get(0)+"");
            ResultSet rs =lonLat.executeQuery();
            if ( !rs.next() ) {
                System.out.println("nothing to find");
            }
            else{
                do{
                    if(!rs.getString(5).contains("NOT SET")){
                        Qlon.add(Double.valueOf(rs.getString(5)));
                        Qlat.add(Double.valueOf(rs.getString(6)));
                        QgeoName.add(rs.getString(2));
                    }
                }while (rs.next());
                System.out.println(Qlon);
                System.out.println(Qlat);
                System.out.println(QgeoName);
            }
        }
        catch (Exception e){
            System.out.println(e);
            System.out.println("ERROR updatting database");
        }
        d.setLon(Qlon);
        d.setLat(Qlat);
        d.setGeoName(QgeoName);
        oos.writeObject(d);
        ois.close();
        oos.flush();
        oos.close();

    }
    public void destroy()
    {
        // attempt to close statements and database connection
        try {
            lonLat.close();
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
