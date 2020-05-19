
public class ALU{
	
	private Processor processor;
	
	
	public ALU(Processor processor){
		this.processor = processor;
	}
	
	
	public void run(String aluControl, String operand1, String operand2) {
		
		String aluResult;
		switch(aluControl){
			//subtract
			case "000":
				aluResult = Processor.convertDecToBinSigned(Processor.convertBinToDecSigned(operand1) - Processor.convertBinToDecSigned(operand2));
				break;
			//add
			case "001":
				aluResult = Processor.convertDecToBinSigned(Processor.convertBinToDecSigned(operand1) + Processor.convertBinToDecSigned(operand2));
				break;
			//multiply
			case "010":
				aluResult = Processor.convertDecToBinSigned(Processor.convertBinToDecSigned(operand1) * Processor.convertBinToDecSigned(operand2));
				break;
			//or
			case "011":
				aluResult = Processor.convertDecToBinSigned(Processor.convertBinToDecSigned(operand1) | Processor.convertBinToDecSigned(operand2));
				break;
			//and
			case "100":
				aluResult = Processor.convertDecToBinSigned(Processor.convertBinToDecSigned(operand1) & Processor.convertBinToDecSigned(operand2));
				break;
			//shift right
			case "101":
				aluResult = Processor.convertDecToBinSigned(Processor.convertBinToDecSigned(operand1) >> Processor.convertBinToDecSigned(operand2));
				break;
			//shift left
			case "110":
				aluResult = Processor.convertDecToBinSigned(Processor.convertBinToDecSigned(operand1) << Processor.convertBinToDecSigned(operand2));
				break;
			//set on less than
			case "111":
				aluResult = Processor.convertDecToBinSigned(Processor.convertBinToDecSigned(operand1)<Processor.convertBinToDecSigned(operand2)? 1 : 0);
				break;
			default:
				aluResult = "00000000000000000000000000000000";
		}
		String zeroFlag = aluResult.equals("00000000000000000000000000000000")? "1" : "0";
		String negativeFlag = Processor.convertBinToDecSigned(aluResult)<0? "1" : "0";
		
		processor.setAluResult(aluResult);
		processor.setZeroFlag(zeroFlag);
		processor.setNegativeFlag(negativeFlag);
	}
	
	
}