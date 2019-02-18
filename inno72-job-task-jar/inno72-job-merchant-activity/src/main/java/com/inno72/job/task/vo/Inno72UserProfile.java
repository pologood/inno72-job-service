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

    public String gender;

	public String age;

	public String shopping;

	public String sample;

	public String interaction;

	public String attempt;

	public String city;

	public String pos;

	public String buypower;

	public String gameTalent;

	public String gameNovice;

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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
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

	public String getBuypower() {
		return buypower;
	}

	public void setBuypower(String buypower) {
		this.buypower = buypower;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getShopping() {
		return shopping;
	}

	public void setShopping(String shopping) {
		this.shopping = shopping;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	public String getGameTalent() {
		return gameTalent;
	}

	public void setGameTalent(String gameTalent) {
		this.gameTalent = gameTalent;
	}

	public String getGameNovice() {
		return gameNovice;
	}

	public void setGameNovice(String gameNovice) {
		this.gameNovice = gameNovice;
	}
}
