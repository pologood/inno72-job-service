package com.inno72.job.executer.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "inno72_game_user_life")
public class Inno72GameUserLife {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * 游戏用户id inno72_game_user.id
     */
    @Column(name = "game_user_id")
    private String gameUserId;

    /**
     * inno72_game_user_channel.id
     */
    @Column(name = "user_channel_id")
    private String userChannelId;

    @Column(name = "third_ref_id")
    private String thirdRefId;

    /**
     * 机器编号
     */
    @Column(name = "machine_code")
    private String machineCode;

    /**
     * 用户昵称
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 登录时间
     */
    @Column(name = "login_time")
    private Date loginTime;

    @Column(name = "activity_id")
    private String activityId;

    /**
     * 活动名称
     */
    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "activity_plan_id")
    private String activityPlanId;

    /**
     * 游戏id
     */
    @Column(name = "game_id")
    private String gameId;

    /**
     * 游戏名称
     */
    @Column(name = "game_name")
    private String gameName;

    /**
     * 机器id
     */
    @Column(name = "mer_point_id")
    private String merPointId;

    /**
     * 商圈
     */
    @Column(name = "mer_point_address")
    private String merPointAddress;

    /**
     * 游戏结果 成功:1; 失败:0;
     */
    @Column(name = "game_result")
    private String gameResult;

    /**
     * 成功后下单的id
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 0 - 女； 1 - 男；
     */
    private Byte sex;

    /**
     * 年龄
     */
    private Byte age;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取游戏用户id inno72_game_user.id
     *
     * @return game_user_id - 游戏用户id inno72_game_user.id
     */
    public String getGameUserId() {
        return gameUserId;
    }

    /**
     * 设置游戏用户id inno72_game_user.id
     *
     * @param gameUserId 游戏用户id inno72_game_user.id
     */
    public void setGameUserId(String gameUserId) {
        this.gameUserId = gameUserId;
    }

    /**
     * 获取inno72_game_user_channel.id
     *
     * @return user_channel_id - inno72_game_user_channel.id
     */
    public String getUserChannelId() {
        return userChannelId;
    }

    /**
     * 设置inno72_game_user_channel.id
     *
     * @param userChannelId inno72_game_user_channel.id
     */
    public void setUserChannelId(String userChannelId) {
        this.userChannelId = userChannelId;
    }

    /**
     * @return third_ref_id
     */
    public String getThirdRefId() {
        return thirdRefId;
    }

    /**
     * @param thirdRefId
     */
    public void setThirdRefId(String thirdRefId) {
        this.thirdRefId = thirdRefId;
    }

    /**
     * 获取机器编号
     *
     * @return machine_code - 机器编号
     */
    public String getMachineCode() {
        return machineCode;
    }

    /**
     * 设置机器编号
     *
     * @param machineCode 机器编号
     */
    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    /**
     * 获取用户昵称
     *
     * @return nick_name - 用户昵称
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 设置用户昵称
     *
     * @param nickName 用户昵称
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 获取登录时间
     *
     * @return login_time - 登录时间
     */
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * 设置登录时间
     *
     * @param loginTime 登录时间
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * @return activity_id
     */
    public String getActivityId() {
        return activityId;
    }

    /**
     * @param activityId
     */
    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    /**
     * 获取活动名称
     *
     * @return activity_name - 活动名称
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * 设置活动名称
     *
     * @param activityName 活动名称
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * @return activity_plan_id
     */
    public String getActivityPlanId() {
        return activityPlanId;
    }

    /**
     * @param activityPlanId
     */
    public void setActivityPlanId(String activityPlanId) {
        this.activityPlanId = activityPlanId;
    }

    /**
     * 获取游戏id
     *
     * @return game_id - 游戏id
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * 设置游戏id
     *
     * @param gameId 游戏id
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * 获取游戏名称
     *
     * @return game_name - 游戏名称
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * 设置游戏名称
     *
     * @param gameName 游戏名称
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * 获取机器id
     *
     * @return mer_point_id - 机器id
     */
    public String getMerPointId() {
        return merPointId;
    }

    /**
     * 设置机器id
     *
     * @param merPointId 机器id
     */
    public void setMerPointId(String merPointId) {
        this.merPointId = merPointId;
    }

    /**
     * 获取商圈
     *
     * @return mer_point_address - 商圈
     */
    public String getMerPointAddress() {
        return merPointAddress;
    }

    /**
     * 设置商圈
     *
     * @param merPointAddress 商圈
     */
    public void setMerPointAddress(String merPointAddress) {
        this.merPointAddress = merPointAddress;
    }

    /**
     * 获取游戏结果 成功:1; 失败:0;
     *
     * @return game_result - 游戏结果 成功:1; 失败:0;
     */
    public String getGameResult() {
        return gameResult;
    }

    /**
     * 设置游戏结果 成功:1; 失败:0;
     *
     * @param gameResult 游戏结果 成功:1; 失败:0;
     */
    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }

    /**
     * 获取成功后下单的id
     *
     * @return order_id - 成功后下单的id
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置成功后下单的id
     *
     * @param orderId 成功后下单的id
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取0 - 女； 1 - 男；
     *
     * @return sex - 0 - 女； 1 - 男；
     */
    public Byte getSex() {
        return sex;
    }

    /**
     * 设置0 - 女； 1 - 男；
     *
     * @param sex 0 - 女； 1 - 男；
     */
    public void setSex(Byte sex) {
        this.sex = sex;
    }

    /**
     * 获取年龄
     *
     * @return age - 年龄
     */
    public Byte getAge() {
        return age;
    }

    /**
     * 设置年龄
     *
     * @param age 年龄
     */
    public void setAge(Byte age) {
        this.age = age;
    }
}