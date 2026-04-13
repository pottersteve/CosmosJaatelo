// Scientist.java
package com.example.cosmosjaatelo;

public class Scientist extends CrewMember {

    public Scientist(String name) {
        super(name);
        this.baseSkill = 6;
        this.resilience = 3;
        this.maxEnergy = 90;
        initEnergy();
    }

    @Override
    public int act() {
        return (int)(getEffectiveSkill() + Math.random() * 3);
    }

    @Override
    public String getType() { return "Scientist"; }

    public String getSpecialAbility() { return "Threat Analysis"; }
}