
public class Block {

	private int validBit;
	private int tag;
	private String data;
	
	
	public Block() {
		validBit = 0;
		tag = 0;
		data = "00000000000000000000000000000000";
	}
	
	
	public int getValidBit() {
		return validBit;
	}
	public void setValidBit(int validBit) {
		this.validBit = validBit;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
}
