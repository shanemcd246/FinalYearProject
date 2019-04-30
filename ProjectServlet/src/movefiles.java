import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


public class movefiles {
    public static void main(String[] args) {

        File file = new File("C:/Users/shane/Desktop");
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        System.out.println(Arrays.toString(directories));
        File source = new File("C:\\Users\\shane\\Desktop\\testfrom");
        File dest = new File("C:\\Users\\shane\\Desktop\\testto");
        try {
            FileUtils.copyDirectory(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

