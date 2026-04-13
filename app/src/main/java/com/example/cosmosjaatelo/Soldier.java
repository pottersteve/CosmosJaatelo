// Soldier.java
package com.example.cosmosjaatelo;

public class Soldier extends CrewMember {

    public Soldier(String name) {
        super(name);
        this.baseSkill = 7;
        this.resilience = 5;
        this.maxEnergy = 130;
        initEnergy();
    }

    @Override
    public int act() {
        return (int)(getEffectiveSkill() + Math.random() * 3);
    }

    @Override
    public String getType() { return "Soldier"; }

    public String getSpecialAbility() { return "Combat Surge"; }
}