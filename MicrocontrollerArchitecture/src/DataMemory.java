
public class DataMemory {
	
	private String[] memory;
	
	
	public DataMemory() {
		memory = new String[1024];
		
		//for testing purposes each address is initialized to contain its value
		for (int i=0 ; i<memory.length ; i++)
			memory[i] = Processor.convertDecToBinUnsigned(i);
    }
	
	
	public String read(String address) {
		return memory[Processor.convertBinToDecUnsigned(address)];
	}
	
	public void write(String address, String data) {
		memory[Processor.convertBinToDecUnsigned(address)] = data;
	}
	
	public void viewHead() {
		System.out.println("Data Memory Contents:");
		System.out.println("(first 50 addresses only)");
		for (int i=0 ; i<50 ; i++)
			System.out.println("Address " + i + ": " + memory[i]);
	}
	
	
}
