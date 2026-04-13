// Engineer.java
package com.example.cosmosjaatelo;

public class Engineer extends CrewMember {

    public Engineer(String name) {
        super(name);
        this.baseSkill = 4;
        this.resilience = 5;
        this.maxEnergy = 110;
        initEnergy();
    }

    @Override
    public int act() {
        return (int)(getEffectiveSkill() + Math.random() * 3);
    }

    @Override
    public String getType() { return "Engineer"; }

    public String getSpecialAbility() { return "System Repair"; }
}