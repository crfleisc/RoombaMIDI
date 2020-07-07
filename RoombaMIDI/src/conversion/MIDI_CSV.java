package conversion;

public class MIDI_CSV extends AbstractConverter{
	public MIDI_CSV() {

		do{
			listFiles(0);
			inputPath = getMidi();
			inputPath.concat(".mid");
		}while(!testInput(inputPath, "mid"));
			
		outputPath = getOutput();
		outputPath = outputPath.concat(".txt");
			
		run(MIDI2CSV, inputPath, outputPath);
			
		System.out.println("\nDONE! " + outputPath + " created");
		return;
	}
}
