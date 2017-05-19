package com.cyclic.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by serych on 27.04.17.
 */
@ConfigurationProperties(prefix = "game.room")
public class RoomConfig implements Cloneable{
    private Integer playersCount;
    private Integer startBonusCount;
    private Integer startTowerUnits;
    private Integer bonusMinValue;
    private Integer bonusMaxValue;
    private Integer fieldWidth;
    private Integer fieldHeight;
    private Double moveRadius;
    private Double moveTimeSeconds;

    /**
     * Copy constructor
     */
    public RoomConfig(RoomConfig config) {
        playersCount = new Integer(config.getPlayersCount());
        startBonusCount = new Integer(config.getStartBonusCount());
        startTowerUnits = new Integer(config.getStartTowerUnits());
        bonusMinValue = new Integer(config.getBonusMinValue());
        bonusMaxValue = new Integer(config.getBonusMaxValue());
        fieldWidth = new Integer(config.getFieldWidth());
        fieldHeight = new Integer(config.getFieldHeight());
        moveRadius = new Double(config.getMoveRadius());
        moveTimeSeconds = new Double(config.getMoveTimeSeconds());
    }

    public RoomConfig() {
    }

    public Integer getPlayersCount() {
        return playersCount;
    }

    public void setPlayersCount(Integer playersCount) {
        this.playersCount = playersCount;
    }

    public Integer getStartBonusCount() {
        return startBonusCount;
    }

    public void setStartBonusCount(Integer startBonusCount) {
        this.startBonusCount = startBonusCount;
    }

    public Integer getStartTowerUnits() {
        return startTowerUnits;
    }

    public void setStartTowerUnits(Integer startTowerUnits) {
        this.startTowerUnits = startTowerUnits;
    }

    public Integer getBonusMinValue() {
        return bonusMinValue;
    }

    public void setBonusMinValue(Integer bonusMinValue) {
        this.bonusMinValue = bonusMinValue;
    }

    public Integer getBonusMaxValue() {
        return bonusMaxValue;
    }

    public void setBonusMaxValue(Integer bonusMaxValue) {
        this.bonusMaxValue = bonusMaxValue;
    }

    public Integer getFieldWidth() {
        return fieldWidth;
    }

    public void setFieldWidth(Integer fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public Integer getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldHeight(Integer fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    public Double getMoveRadius() {
        return moveRadius;
    }

    public void setMoveRadius(Double moveRadius) {
        this.moveRadius = moveRadius;
    }

    public Double getMoveTimeSeconds() {
        return moveTimeSeconds;
    }

    public void setMoveTimeSeconds(Double moveTimeSeconds) {
        this.moveTimeSeconds = moveTimeSeconds;
    }

    public boolean isCorrect() {
        return
                playersCount != null &&
                        startBonusCount != null &&
                        startTowerUnits != null &&
                        bonusMaxValue != null &&
                        bonusMinValue != null &&
                        fieldHeight != null &&
                        moveRadius != null &&
                        moveTimeSeconds != null &&
                        fieldWidth != null;
    }
}
