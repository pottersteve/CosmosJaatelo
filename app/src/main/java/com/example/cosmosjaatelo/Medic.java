// Medic.java
package com.example.cosmosjaatelo;

public class Medic extends CrewMember {

    public Medic(String name) {
        super(name);
        this.baseSkill = 3;
        this.resilience = 6;
        this.maxEnergy = 120;
        initEnergy();
    }

    @Override
    public int act() {
        return (int)(getEffectiveSkill() + Math.random() * 3);
    }

    @Override
    public String getType() { return "Medic"; }

    public String getSpecialAbility() { return "Field Treatment"; }
}