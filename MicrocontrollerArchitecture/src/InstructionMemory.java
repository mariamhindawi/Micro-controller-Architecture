
public class InstructionMemory {
	
	private Processor processor;
	private String[] memory;
	private int lastInstructionIndex;
	
	
	public InstructionMemory(Processor processor) {
		this.processor = processor;
		memory = new String[1024];
		lastInstructionIndex = 0;
    }
	
	
	public int getLastInstructionIndex() {
		return lastInstructionIndex;
	}
	

	public void read(String address) {
		String instruction = memory[Processor.convertBinToDecUnsigned(address)];
		processor.setFetchedInstruction(instruction);
	}
	
	public void write(String instruction, boolean overwrite) {
		if (overwrite) {
			lastInstructionIndex = 0;
			processor.resetPc();
		}
		if (lastInstructionIndex<memory.length)
			memory[lastInstructionIndex++] = instruction;
	}
	
	
	public String getFirstInstruction(String address) {
		return memory[Processor.convertBinToDecUnsigned(address)]; 
	}
	
	
}
