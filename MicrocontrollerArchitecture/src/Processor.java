
public class Processor {
	
	// modules
	private ProgramCounter programCounter;
	private InstructionMemory instructionMemory;
	private RegisterFile registerFile;
	private Control control;
	private ALU alu;
	private DataMemory dataMemory;
	private Cache cache;
	
	// pipeline registers
	// ifId =  {currentInstruction}
	// idEx =  {writeBackControlSignals, memoryControlSignals, executeControlSignals, readData1, readData2, immediate, writeRegister}
	// exMem = {writeBackControlSignals, memoryControlSignals, ALUresult, readData2, writeRegister}
	// memWb=  {writeBackControlSignals, memoryReadData, ALUresult, writeRegister}
	private String[] ifIdRegisters;
	private Object[] idExRegisters;
	private Object[] exMemRegisters;
	private Object[] memWbRegisters;
	
	// outputs
	private String fetchedInstruction;
	private String []executeControlSignals;
	private String []memoryControlSignals;
	private String []writeBackControlSignals;
	private String readData1;
	private String readData2;
	private String immediate;
	private String writeRegister;
	private String aluResult;
	private String zeroFlag;
	private String negativeFlag;
	private String memoryReadData;
	
	
	// constructor
	public Processor() {
		
		// modules
		programCounter = new ProgramCounter();
		instructionMemory = new InstructionMemory(this);
		registerFile = new RegisterFile(this);
		control = new Control(this);
		alu = new ALU(this);
		dataMemory = new DataMemory();
		cache = new Cache(this, dataMemory);
		
		// pipeline registers
		ifIdRegisters = new String[1];
		idExRegisters = new Object[7];
		exMemRegisters = new Object[5];
		memWbRegisters = new Object[4];
		
		// initialization of pipeline registers
		String[] writeBackControlSignals = {"0","0"};
		String[] memoryControlSignals = {"0","0"};
		String[] executeControlSignals = {"000","0","0","00"};
		ifIdRegisters[0] = "00000000000000000000000000000000";
		idExRegisters[0] = writeBackControlSignals;
		idExRegisters[1] = memoryControlSignals;
		idExRegisters[2] = executeControlSignals;
		idExRegisters[3] = "00000000000000000000000000000000";
		idExRegisters[4] = "00000000000000000000000000000000";
		idExRegisters[5] = "00000000000000000000000000000000";
		idExRegisters[6] = "00000";
		exMemRegisters[0] = writeBackControlSignals;
		exMemRegisters[1] = memoryControlSignals;
		exMemRegisters[2] = "00000000000000000000000000000000";
		exMemRegisters[3] = "00000000000000000000000000000000";
		exMemRegisters[4] = "00000";
		memWbRegisters[0] = writeBackControlSignals;
		memWbRegisters[1] = "00000000000000000000000000000000";
		memWbRegisters[2] = "00000000000000000000000000000000";
		memWbRegisters[3] = "00000";
		
		// initialization of outputs
		fetchedInstruction = "00000000000000000000000000000000";
		this.executeControlSignals = executeControlSignals;
		this.memoryControlSignals = memoryControlSignals;
		this.writeBackControlSignals = writeBackControlSignals;
		readData1 = "00000000000000000000000000000000";
		readData2 = "00000000000000000000000000000000";
		immediate = "00000000000000000000000000000000";
		writeRegister = "00000";
		aluResult = "00000000000000000000000000000000";
		zeroFlag = "0";
		negativeFlag = "0";
		memoryReadData = "00000000000000000000000000000000";
  	}
	
	
	// setters
	public void setFetchedInstruction(String currentInstruction) {
		this.fetchedInstruction = currentInstruction;
	}
	public void setExecuteControlSignals(String[] executeControlSignals) {
		this.executeControlSignals = executeControlSignals;
	}
	public void setMemoryControlSignals(String[] memoryControlSignals) {
		this.memoryControlSignals = memoryControlSignals;
	}
	public void setWriteBackControlSignals(String[] writeBackControlSignals) {
		this.writeBackControlSignals = writeBackControlSignals;
	}	
	public void setReadData1(String readData1) {
		this.readData1 = readData1;
	}
	public void setReadData2(String readData2) {
		this.readData2 = readData2;
	}
	public void setAluResult(String aluResult) {
		this.aluResult = aluResult;
	}
	public void setZeroFlag(String zeroFlag) {
		this.zeroFlag = zeroFlag;
	}
	public void setNegativeFlag(String negativeFlag) {
		this.negativeFlag = negativeFlag;
	}
	public void setMemoryReadData(String memoryReadData) {
		this.memoryReadData = memoryReadData;
	}
	
	
	// helper methods
	public static String convertDecToBinUnsigned(int decimalNumber) {
		String result = "";
		for (int i = 0; i < 32; i++) {
			int remainder = decimalNumber % 2;
			decimalNumber /= 2;
			result = remainder + result;
		}
		return result;
	}
	
	public static int convertBinToDecUnsigned(String binaryNumber) {
		int result = 0;
		int power = 0;
		for (int i = binaryNumber.length() - 1; i >= 0; i--) {
			int x = 0;
			if (binaryNumber.charAt(i) == '1')
				x = 1;
			result += (int) (Math.pow(2, power)) * x;
			power++;
		}

		return result;
	}
	
	public static String convertDecToBinSigned(int decimalNumber) {
		StringBuilder result = new StringBuilder();
		for (int i = 31; i >= 0; i--) {
			int mask = 1 << i;
			result.append((decimalNumber & mask) != 0 ? 1 : 0);
		}
		return result.toString();
	}
	
	public static int convertBinToDecSigned(String binaryNumber) {
		return (int) Long.parseLong(binaryNumber, 2);
	}
	
	public void resetPc() {
		programCounter.setPc("00000000000000000000000000000000");
	}
	
	public String signExtend(String immediate) {
		if (immediate.charAt(0) == '1')
			for (int i = 0; i < 20; i++)
				immediate = "1" + immediate;
		else
			for (int i = 0; i < 20; i++)
				immediate = "0" + immediate;

		return immediate;
	}
	
	public void shiftPipelineRegisters() {
		// memWb registers
		memWbRegisters[0] = exMemRegisters[0];
		memWbRegisters[1] = memoryReadData;
		memWbRegisters[2] = exMemRegisters[2];
		memWbRegisters[3] = exMemRegisters[4];
		// exMem registers
		exMemRegisters[0] = idExRegisters[0];
		exMemRegisters[1] = idExRegisters[1];
		exMemRegisters[2] = aluResult;
		exMemRegisters[3] = idExRegisters[4];
		exMemRegisters[4] = idExRegisters[6];
		// idEx registers
		idExRegisters[0] = writeBackControlSignals;
		idExRegisters[1] = memoryControlSignals;
		idExRegisters[2] = executeControlSignals;
		idExRegisters[3] = readData1;
		idExRegisters[4] = readData2;
		idExRegisters[5] = immediate;
		idExRegisters[6] = writeRegister;
		// ifId registers
		ifIdRegisters[0] = fetchedInstruction;
	}
	
	
	// stages
	public void instructionFetch() {
		
		// get the pc value
		String pc = programCounter.getPc();
		
		// read the instruction from the instruction memory
		instructionMemory.read(pc);
		
		// update the pc
		pc = convertDecToBinUnsigned(convertBinToDecUnsigned(pc)+1);
		programCounter.setPc(pc);
		
		// printing
		System.out.println("Next PC: " + pc);
		System.out.println("Instruction: " + fetchedInstruction);
	}
	
	public void instructionDecode() {

		// get the values from the previous stage
		String instruction = ifIdRegisters[0];

		// getting fields from the instruction
		String opcode = "";
		String rd = "";
		String r1 = "";
		String r2 = "";
		String immediate = "";
		
		for (int i = 0; i < 5; i++)
			opcode += instruction.charAt(i);
		for (int i = 5; i < 10; i++)
			rd += instruction.charAt(i);
		for (int i = 10; i < 15; i++)
			r1 += instruction.charAt(i);
		for (int i = 15; i < 20; i++)
			r2 += instruction.charAt(i);
		for (int i = 20; i < 32; i++)
			immediate += instruction.charAt(i);

		// control unit
		control.decode(opcode);

		// register file
		registerFile.read(r1, r2);

		// sign extend
		immediate = signExtend(immediate);
		
		// set processor variables
		this.immediate = immediate;
		this.writeRegister = rd;
		
		// printing
		System.out.println("Read Register 1: " + r1);
		System.out.println("Read Register 2: " + r2);
		System.out.println("Write Register: " + rd);
		System.out.println("Read data 1: " + readData1);
		System.out.println("Read data 2: " + readData2);
		System.out.println("Immediate: " + immediate);
		System.out.println("EX controls: ALUControl: " + executeControlSignals[0] + ", ALUSrc: " + executeControlSignals[1] + ", Jump: " + executeControlSignals[2] + ", Branch: " + executeControlSignals[3]);
		System.out.println("MEM controls: MemRead: " + memoryControlSignals[0] + ", MemWrite: " + memoryControlSignals[1]);
		System.out.println("WB controls: RegWrite: " + writeBackControlSignals[0] + ", MemToReg: " + writeBackControlSignals[1]);	
	}
	
	public void instructionExecute() {
		
		// get the values from the previous stage
		String [] writeBackControlSignals= (String[]) idExRegisters[0];
		String [] memoryControlSignals= (String[]) idExRegisters[1];
		String [] executeControlSignals= (String[]) idExRegisters[2];
		String readData1 = (String)idExRegisters[3];
		String readData2 = (String)idExRegisters[4];
		String immediate = (String)idExRegisters[5];
		String writeRegister = (String) idExRegisters[6];
		// control signals
		String aluControl = executeControlSignals[0];
		String aluSrc = executeControlSignals[1];
		String jump = executeControlSignals[2];
		String branch = executeControlSignals[3];
		
		// ALU
		String operand2 = aluSrc=="0"? readData2 : immediate; 
		alu.run(aluControl, readData1, operand2);
		
		// branch and jump
		// branch on equal
		if (branch.equals("01") && zeroFlag.equals("1"))
			programCounter.setPc(immediate);
		// branch on less than
		if (branch.equals("10") && negativeFlag.equals("1"))
			programCounter.setPc(immediate);
		// jump
		if (jump.equals("1"))
			programCounter.setPc(readData1);
		
		// printing
		System.out.println("ALU result: " + aluResult);
		System.out.println("Zero flag: " + zeroFlag);
		System.out.println("Negative flag: " + negativeFlag);
		System.out.println("Branch Address: " + immediate);
		System.out.println("Jump Address: " + readData1);
		System.out.println("Memory write data: " + readData2);
		System.out.println("Write Register: " + writeRegister);
		System.out.println("EX controls: ALUControl: " + executeControlSignals[0] + ", ALUSrc: " + executeControlSignals[1] + ", Jump: " + executeControlSignals[2] + ", Branch: " + executeControlSignals[3]);
		System.out.println("MEM controls: MemRead: " + memoryControlSignals[0] + ", MemWrite: " + memoryControlSignals[1]);
		System.out.println("WB controls: RegWrite: " + writeBackControlSignals[0] + ", MemToReg: " + writeBackControlSignals[1]);	
	}
	
	public void memoryAccess() {
		
		// get the values from the previous stage
		String[] writeBackControlSignals = (String[]) exMemRegisters[0];
		String[] memoryControlSignals = (String[]) exMemRegisters[1];
		String address = (String) exMemRegisters[2];
		String writeData = (String) exMemRegisters[3];
		String writeRegister = (String) exMemRegisters[4];
		// control signals
		String memRead = memoryControlSignals[0];
		String memWrite = memoryControlSignals[1];
		
		// data memory
		if(memRead.equals("1")) {
			cache.read(address);
		}
		else if(memWrite.equals("1")) {
			cache.write(address, writeData);
			memoryReadData="00000000000000000000000000000000";
		}
		else {
			memoryReadData="00000000000000000000000000000000";
		}
		
		// printing
		System.out.println("Address: " + address);
		System.out.println("Write data: " + writeData);
		System.out.println("Read data: " + memoryReadData);
		System.out.println("Write Register: " + writeRegister);
		System.out.println("Mem controls: MemRead: " + memoryControlSignals[0] + ", MemWrite: " + memoryControlSignals[1]);
		System.out.println("WB controls: RegWrite: " + writeBackControlSignals[0] + ", MemToReg: " + writeBackControlSignals[1]);
	}
	
	public void writeBack() {
		
		// get the values from the previous stage
		String[] writeBackControlSignals = (String[]) memWbRegisters[0];
		String memoryReadData = (String) memWbRegisters[1];
		String aluResult = (String) memWbRegisters[2];
		String writeRegister = (String) memWbRegisters[3];
		// control signals
		String regWrite = writeBackControlSignals[0];
		String memToReg = writeBackControlSignals[1];
		
		// choose which data to write
		String writeData = memToReg.equals("0")? aluResult : memoryReadData;
		
		// register file
		registerFile.write(writeRegister, writeData, regWrite);

		// printing
		System.out.println("ALU result: " + aluResult);
		System.out.println("Memory read data: " + memoryReadData);
		System.out.println("Write Register: " + writeRegister);
		System.out.println("Write data: " + writeData);
		System.out.println("WB controls: RegWrite: " + writeBackControlSignals[0] + ", MemToReg: " + writeBackControlSignals[1]);
	}
	
	
	// main methods
	public void load(String[] program, boolean overwrite) {
		if (program.length!=0)
			instructionMemory.write(program[0], overwrite);
		for (int i=1 ; i<program.length ; i++) {
			instructionMemory.write(program[i], false);
		}
	}
	
	public void simulate() {
		
		// print the contents of the register file and data memory initially
		registerFile.view();
		System.out.println();
		System.out.println("---------------------------------------------------------------------------------------------");
		System.out.println();
		dataMemory.viewHead();
		System.out.println();
		System.out.println("---------------------------------------------------------------------------------------------");
		System.out.println();
		
		// control variables
		String instructionInFetch = instructionMemory.getFirstInstruction(programCounter.getPc());
		String instructionInDecode = "";
		String instructionInExecute = "";
		String instructionInMemory = "";
		String instructionInWriteBack = "";
		boolean fetchInstruction = convertBinToDecUnsigned(programCounter.getPc())<instructionMemory.getLastInstructionIndex();
		boolean decodeInstruction = false;
		boolean executeInstruction = false;
		boolean memoryAccess = false;
		boolean writeBack = false;
		int clockCycle = 0;
		
		while (fetchInstruction || decodeInstruction || executeInstruction || memoryAccess || writeBack) {
			
			System.out.println("Clock cycle " + clockCycle++);
			System.out.println();
			
			// execute stages
			if (fetchInstruction) {
				System.out.println("Instruction: " + instructionInFetch + " is in Fetch stage");
				instructionFetch();
				System.out.println();
			}
			if (decodeInstruction) {
				System.out.println("Instruction: " + instructionInDecode + " is in Decode stage");
				instructionDecode();
				System.out.println();
			}
			if (executeInstruction) {
				System.out.println("Instruction: " + instructionInExecute + " is in Execute stage");
				instructionExecute();
				System.out.println();
			}
			if (memoryAccess) {
				System.out.println("Instruction: " + instructionInMemory + " is in Memory Access stage");
				memoryAccess();
				System.out.println();
			}
			if (writeBack) {
				System.out.println("Instruction: "+instructionInWriteBack+" is in Write Back stage");
				writeBack();
				System.out.println();
			}
			
			System.out.println("---------------------------------------------------------------------------------------------");
			System.out.println();
			
			// shifting pipeline registers
			shiftPipelineRegisters();

			// shifting instructions
			instructionInWriteBack=instructionInMemory;
			instructionInMemory=instructionInExecute;
			instructionInExecute=instructionInDecode;
			instructionInDecode=instructionInFetch;
			instructionInFetch = instructionMemory.getFirstInstruction(programCounter.getPc());
			
			// configuring flags for next cycle
			if (memoryAccess)
				writeBack = true;
			else
				writeBack = false;
			if (executeInstruction)
				memoryAccess = true;
			else
				memoryAccess = false;
			if (decodeInstruction)
				executeInstruction = true;
			else
				executeInstruction = false;
			if (fetchInstruction)
				decodeInstruction = true;
			else
				decodeInstruction = false;
			fetchInstruction = convertBinToDecUnsigned(programCounter.getPc())<instructionMemory.getLastInstructionIndex();
		}
		
		// print the contents of the register file and data memory after the program finishes
		registerFile.view();
		System.out.println();
		System.out.println("---------------------------------------------------------------------------------------------");
		System.out.println();
		dataMemory.viewHead();
		System.out.println();
		System.out.println("---------------------------------------------------------------------------------------------");
		System.out.println();
		
	}
	
	
}