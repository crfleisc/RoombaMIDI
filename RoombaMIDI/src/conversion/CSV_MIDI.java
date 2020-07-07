package conversion;

public class CSV_MIDI extends AbstractConverter{
	public CSV_MIDI(){
		do{
			listFiles(1);
			inputPath = getCSV();
			inputPath.concat(".txt");
		}while(!testInput(inputPath, "txt"));
		
		outputPath = getOutput();
		outputPath = outputPath.concat(".mid");
		
		run(CSV2MIDI, inputPath, outputPath);
		
		System.out.println("DONE! " + outputPath + " created");
		return;
	}
}
