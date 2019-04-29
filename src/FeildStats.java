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
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "feildStats",urlPatterns = {"/feildStats"})
public class FeildStats extends HttpServlet {
    private Connection connection;
    private PreparedStatement getData;

    public void init(ServletConfig config) throws ServletException {
        System.out.println("IM HERE IN feildStats INIT\n");
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


    protected void processRequest(HttpServletRequest request, HttpServletResponse response ) throws IOException, SQLException, ClassNotFoundException {
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
        List message = (List) ois.readObject();
        System.out.println(message);
        //Integer val = (Integer)ois.readObject();
        //System.out.println("Value"+val);
        getData = connection.prepareStatement("SELECT * from "+ message.get(0) +"");
        ResultSet rs = getData.executeQuery();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Float> counter = new ArrayList<>();
        if ( !rs.next() ) {
            System.out.println("nothing to find");
        }
        else{
            do{
                if(names.contains(rs.getString(2))){
                    int num = names.indexOf(rs.getString(2));
                    float newCount = counter.get(num) +1;
                    System.out.println(newCount);
                    counter.set(num,newCount);
                }
                else{
                    names.add(rs.getString(2));
                    int num = names.indexOf(rs.getString(2));
                    counter.add(num, (float) 1);
                }
                System.out.println(rs.getString(2));
            }while (rs.next());
            System.out.println(names);
            System.out.println(counter);
        }
        if(names.size() > 0){
            WeedData d = new WeedData();
            d.setNames(names);
            d.setCount(counter);
            float max =0;
            for(int x= 0;x<counter.size();x++){
                max+=counter.get(x);
            }
            for(int x= 0;x<counter.size();x++){
                counter.set(x,(counter.get(x)/max)*100);
            }
            d.setMax((int) max);
            oos.writeObject(d);
        }
        ois.close();
        oos.flush();
        oos.close();
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doGet( request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
