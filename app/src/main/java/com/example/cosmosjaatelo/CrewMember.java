package com.example.cosmosjaatelo;

public abstract class CrewMember {

    // private — only this class touches these directly
    private int id;
    private String name;
    private int energy;
    private float experience;

    private Location location;
    private boolean inMedbay;
    private int recoveryTurnsLeft;

    private int missionsPlayed = 0;
    private int missionsWon = 0;
    private int missionsLost = 0;

    // protected — subclasses set these in their own constructors
    protected int maxEnergy;
    protected int baseSkill;
    protected int resilience;
    //stat


    public CrewMember(String name) {
        this.name = name;
        this.experience = 0;
        this.location = Location.QUARTERS;
        this.inMedbay = false;
        this.recoveryTurnsLeft = 0;
        //stat
        // subclass constructor must set
        // maxEnergy first, then call initEnergy() to set energy
    }

    public void initEnergy() {
        this.energy = this.maxEnergy;
    }


    public abstract int act();


    public void defend(int incomingDamage) {
        int actualDamage = Math.max(0, incomingDamage - resilience);
        this.energy = Math.max(0, this.energy - actualDamage);
    }

    public float getEffectiveSkill() {
        // XP adds directly to skill — per spec
        return baseSkill + experience;
    }

    public void train() {

        if (this.energy > 0) {
            this.experience += 0.5;
            this.energy--;
        }
    }

    public void resetEnergy() {
        this.energy = this.maxEnergy;
    }

    public boolean isDead() {
        return energy <= 0;
    }

    public void sendToMedbay(int recoveryMissions) {
        this.inMedbay = true;
        this.recoveryTurnsLeft = recoveryMissions;
        this.location = Location.MEDBAY;
    }

    public void returnFromMedbay() {
        this.energy = this.maxEnergy;
        this.experience = this.experience / 2; // XP penalty
        this.inMedbay = false;
        this.recoveryTurnsLeft = 0;
        this.location = Location.QUARTERS;
    }

    public void recordWin() {
        this.missionsPlayed++;
        this.missionsWon++;
    }

    public void recordLoss() {
        this.missionsPlayed++;
        this.missionsLost++;
    }

    // typical getters

    public int getId()                  { return id; }
    public String getName()             { return name; }
    public int getEnergy()              { return energy; }
    public float getExperience()          { return experience; }
    public Location getLocation()       { return location; }
    public boolean isInMedbay()         { return inMedbay; }
    public int getRecoveryTurnsLeft()   { return recoveryTurnsLeft; }
    public int getMissionsPlayed() { return missionsPlayed; }
    public int getMissionsWon() { return missionsWon; }
    public int getMissionsLost() { return missionsLost; }


    //stat

    public abstract String getType();


    public void setId(int id)                           { this.id = id; }
    public void setExperience(float experience)           { this.experience = experience; }
    public void setLocation(Location location)          { this.location = location; }
    public void setInMedbay(boolean inMedbay)           { this.inMedbay = inMedbay; }
    public void setRecoveryTurnsLeft(int turns)         { this.recoveryTurnsLeft = turns; }

    public void setMissionsPlayed(int p) { this.missionsPlayed = p; }
    public void setMissionsWon(int w) { this.missionsWon = w; }
    public void setMissionsLost(int l) { this.missionsLost = l; }
}
