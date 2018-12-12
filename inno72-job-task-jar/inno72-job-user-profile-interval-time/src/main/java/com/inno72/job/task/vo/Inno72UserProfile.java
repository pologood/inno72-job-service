package com.inno72.job.task.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Document
public class Inno72UserProfile implements Serializable {
    private static final long serialVersionUID = 6901042169292733902L;
    @Id
    public String id;

    public String userId;

    public String sex;

	public String age;

	public String payment;

	public String interaction;

	public String attempt;

	public String city;

	public String pos;

	public String gametime;

	public String buypower;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getInteraction() {
		return interaction;
	}

	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}

	public String getAttempt() {
		return attempt;
	}

	public void setAttempt(String attempt) {
		this.attempt = attempt;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getGametime() {
		return gametime;
	}

	public void setGametime(String gametime) {
		this.gametime = gametime;
	}

	public String getBuypower() {
		return buypower;
	}

	public void setBuypower(String buypower) {
		this.buypower = buypower;
	}
}