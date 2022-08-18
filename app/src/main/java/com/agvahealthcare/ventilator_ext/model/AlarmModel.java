package com.agvahealthcare.ventilator_ext.model;

import static com.agvahealthcare.ventilator_ext.utility.utils.Configs.*;

import com.agvahealthcare.ventilator_ext.database.entities.AlarmDBModel;
import com.agvahealthcare.ventilator_ext.utility.utils.AlarmConfiguration;

public class AlarmModel {

    private String message;
    private String code;
    private String createdAt;


    public AlarmModel(String message, String code, String createdAt) {
        this.message = message;
        this.code = code;
        this.createdAt = createdAt;
    }

    public AlarmModel(AlarmDBModel model){
        this.code = model.getKey();
        this.message = model.getMessage();
        this.createdAt = model.getCreatedAt();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public AlarmType getPriority() { return AlarmConfiguration.getPriority(this.code); }

    public int getColor() { return AlarmConfiguration.getColor(this.code); }

    public AlarmDBModel toDBModel(){
        return new AlarmDBModel(
                getCode(),
                getMessage(),
                getCreatedAt()
        );
    }



}
