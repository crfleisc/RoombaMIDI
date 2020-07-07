package conversion;

import java.io.File;

public class MIDI_iROBOT extends AbstractConverter{

	public MIDI_iROBOT() {

		do{
			listFiles(0);
			inputPath = getMidi();
			inputPath.concat(".mid");
		}while(!testInput(inputPath, "mid"));
		
		outputPath = getOutput();
		outputPath = outputPath.concat(".lisp");

		// We create a temporary CSV file along the way to converting the MIDI to an iRobot script, then delete it when we're done.
		run(MIDI2CSV, inputPath, "tempCSV.txt");
		if(new File("tempCSV.txt").exists() && new File("tempCSV.txt")!= null){
			@SuppressWarnings("unused")
			CSV_iROBOT c2r = new CSV_iROBOT("tempCSV.txt", outputPath);
			new File("tempCSV.txt").delete();
		}
		
		
		


	}
	
}
