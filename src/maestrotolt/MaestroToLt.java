package maestrotolt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MaestroToLt {

	public static final double CHANNEL_WIDTH = 0.006163; // ns
	public static final int START_LINE_NUMBER = 800; // ns
	public static final int LAST_LINE_NUMBER = 7500; // ns
	public static final String SUBDIRECTORY_NAME = "lt";
	
	private static void convertFile(File f, String path, String spectraName, Double channelWidth, Integer firstLine, Integer lastLine){
		try {
			String[] tmp = (f.getName()).split("\\.");
			String outputFilename = tmp[0] + ".dat";
			String outputFile = path + File.separator + SUBDIRECTORY_NAME + File.separator + outputFilename;
			FileWriter out = new FileWriter(outputFile);

			// Write LT header
			out.write(spectraName);
			out.write(System.lineSeparator());
			out.write(String.valueOf(channelWidth)); // Channel 
			out.write(System.lineSeparator());
			out.write("200"); // Temperature
			out.write(System.lineSeparator());
			out.write("0.260"); // ?
			out.write(System.lineSeparator());

			// Write lines 800 to 7500 from Maestro file
			BufferedReader br = new BufferedReader(new FileReader(f));
			Integer lineNumber = 0;
			String line;
			while ((line = br.readLine()) != null){
				lineNumber++;
				if (lineNumber >= firstLine && lineNumber <= lastLine){
					out.write(line);
					out.write(System.lineSeparator());					
				}
			}
			br.close();
			out.close();
			System.out.println("\"" + outputFile + "\" saved");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main (String[] args) {
		//args[0] = "/Users/petrstepanov/Dropbox/SelimLab Public Folder/LifetimeSpectroscopy/Data/2016-08-15-Cu-Poly-Irrad";
		System.out.println(args[0]);
		if (args.length > 0) {
			File folder = new File(args[0]);
			System.out.println("Opening directory: " + folder);

			// Create ./lt/ sub directory
			File file = new File(folder + File.separator + SUBDIRECTORY_NAME);
			file.mkdir();

			// Spectra name is the directory name
			File[] allFiles = folder.listFiles();
			
			// Get Channel Width
			Double channelWidth = (args.length >= 2) ? Double.parseDouble(args[2]): CHANNEL_WIDTH;
			Integer firstLine = (args.length >= 3) ? Integer.parseInt(args[3]): START_LINE_NUMBER;
			Integer lastLine = (args.length >= 4) ? Integer.parseInt(args[4]): LAST_LINE_NUMBER;

			for (File f : allFiles){
				if (f.getName().contains(".Spe")){
					convertFile(f, folder.getAbsolutePath(), folder.getName(), channelWidth, firstLine, lastLine);
				}
			}
		}
		else {
			System.out.println("Maestro .Spe to LT spectra converter help:");
			System.out.println("    1st argument - path to directory with Maestro .Spe files");
			System.out.println("    2st argument - channel width (optional)");
			System.out.println("    3rd argument - first line number (optional)");
			System.out.println("    4th argument - last line number (optional)");
		}
    }
}
