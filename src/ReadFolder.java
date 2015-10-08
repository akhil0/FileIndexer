import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ReadFolder {

	public static void main(String[] args) throws IOException {

		String target_dir = "C:\\Users\\AKI\\Downloads\\Test";
        File dir = new File(target_dir);
        File[] files = dir.listFiles();

        for (File f : files) {
            if(f.isFile()) {
                BufferedReader inputStream = null;

                try {
                    inputStream = new BufferedReader(
                                    new FileReader(f));
                    String line;

                    while ((line = inputStream.readLine()) != null) {
                        System.out.println(line);
                        System.out.println(line.length());
                    }
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }
	}

}
