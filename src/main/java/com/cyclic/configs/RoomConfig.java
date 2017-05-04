package com.cyclic.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Created by serych on 27.04.17.
 */
@ConfigurationProperties(prefix = "game.room")
public class RoomConfig {
    private Integer playersCount;
    private Integer startBonusCount;
    private Integer startTowerUnits;
    private Integer bonusMinValue;
    private Integer bonusMaxValue;
    private Integer fieldWidth;

    private Integer fieldHeight;

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

    public boolean isCorrect() {
        return
                playersCount != null &&
                        startBonusCount != null &&
                        startTowerUnits != null &&
                        bonusMaxValue != null &&
                        bonusMinValue != null &&
                        fieldHeight != null &&
                        fieldWidth != null;
    }
}
