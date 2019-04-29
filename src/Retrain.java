import org.json.HTTP;

import java.io.*;

public class Retrain {
    public static void main(String argv[]) {
        Runtime commandPrompt = Runtime.getRuntime();
        try {
            Process powershell = commandPrompt.exec("powershell -Command \"python C:/Users/shane/Documents/Tensorflow/trainDataSet/retrain.py --image_dir C:/Users/shane/Documents/Tensorflow/trainDataSet/weedImages\"");
            powershell.waitFor();
            //BufferedReader stdInput = new BufferedReader(new
            //        InputStreamReader(powershell.getInputStream()));
            //BufferedReader stdError = new BufferedReader(new
            //        InputStreamReader(powershell.getErrorStream()));
            //System.out.println("Here is the standard output of the command:\n");
            //String s = null;
            //while( == 0) {
             //   System.out.println("in it");
            //while ((s = stdInput.readLine()) != null) {
            //        System.out.println(s);
            //    }
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}