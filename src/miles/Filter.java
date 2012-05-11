package miles;
import java.io.File;
import java.io.FilenameFilter;

public class Filter {

    public static File[] finder(String dirName, final String fileNameStart){
        File dir = new File(dirName);

        return dir.listFiles(new FilenameFilter() { 
                 public boolean accept(File dir, String filename)
                      { return filename.startsWith(fileNameStart) && filename.endsWith(".MTD"); }
        } );

    }

}
