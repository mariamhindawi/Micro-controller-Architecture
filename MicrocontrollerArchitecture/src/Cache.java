
public class Cache {
	
	private Processor processor;
	private DataMemory dataMemory;
	private Block[] cache;
	
	
	public Cache(Processor processor, DataMemory dataMemory) {
		this.processor = processor;
		this.dataMemory = dataMemory;
		cache = new Block[128];
		
		for (int i=0 ; i<cache.length ; i++)
			cache[i] = new Block();
	}
	
	
	public void read(String address) {
		int addressInt = Processor.convertBinToDecUnsigned(address);
		if (cache[addressInt%128].getValidBit()==0 || cache[addressInt%128].getTag()!=addressInt/128) {
			cache[addressInt%128].setData(dataMemory.read(address));
			cache[addressInt%128].setTag(addressInt/128);
			cache[addressInt%128].setValidBit(1);
		}
		processor.setMemoryReadData(cache[addressInt%128].getData());
	}
	
	public void write(String address, String writeData) {
		int addressInt = Processor.convertBinToDecUnsigned(address);
		if (cache[addressInt%128].getValidBit()==0 || cache[addressInt%128].getTag()!=addressInt/128) {
			cache[addressInt%128].setData(dataMemory.read(address));
			cache[addressInt%128].setTag(addressInt/128);
			cache[addressInt%128].setValidBit(1);
		}
		cache[addressInt%128].setData(writeData);
		dataMemory.write(address, writeData);
	}
	
	
}
