package zzl.beans.json;

/**
 * 返回司机的基本信息和路线单价运费
 */
public class PathInfoBean {
	private int id;
	private String nickName;
	private String phone;
	private float price;// 运费单价

	private String location;

	private String destination;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price >= 0 ? price : 0.0f;
	}
}
