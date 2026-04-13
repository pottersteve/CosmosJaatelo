
package com.example.cosmosjaatelo;

public class Pilot extends CrewMember {

    public Pilot(String name) {
        super(name);
        this.baseSkill = 5;
        this.resilience = 4;
        this.maxEnergy = 100;
        initEnergy();
    }

    @Override
    public int act() {
        return (int)(getEffectiveSkill() + Math.random() * 3);
    }

    @Override
    public String getType() { return "Pilot"; }

    public String getSpecialAbility() { return "Evasive Maneuver"; }
}