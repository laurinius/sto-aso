package com.kor.admiralty.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Maintenance {;
    @XmlValue
    private String name;

    @XmlAttribute
    private Long readyTime;

    public Maintenance() {
    }

    public Maintenance(String name, Long readyTime) {
        this.name = name;
        this.readyTime = readyTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getReadyTime() {
        return readyTime;
    }
}
