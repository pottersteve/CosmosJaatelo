package com.example.cosmosjaatelo;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ColonyManager implements Iterable<CrewMember> {

    // singleton creation
    private static ColonyManager instance;

    public static ColonyManager getInstance() {
        if (instance == null) {
            instance = new ColonyManager();
        }
        return instance;
    }


    private HashMap<Integer, CrewMember> crewMap;
    private int nextCrewId;
    //list of missions/stat creation possibly goes here


    private ColonyManager() {
        crewMap = new HashMap<>();
        //mission & stat
        nextCrewId = 1;
    }

    //crew management
    public CrewMember getCrewById(int id) {
        return crewMap.get(id);
    }

    public CrewMember recruitCrew(String name, String type) {
        CrewMember c = createByType(name, type);
        c.setId(nextCrewId);
        nextCrewId++;
        crewMap.put(c.getId(), c);
        //colonyStats.totalRecruited++;
        return c;
    }


    private CrewMember createByType(String name, String type) {
        switch (type) {
            case "Pilot":     return new Pilot(name);
            case "Engineer":  return new Engineer(name);
            case "Medic":     return new Medic(name);
            case "Scientist": return new Scientist(name);
            case "Soldier":   return new Soldier(name);
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public void dismissCrew(int id) {
        crewMap.remove(id);
    }

    public void moveCrew(int id, Location loc) {
        CrewMember c = crewMap.get(id);
        if (c == null) return;

        // cannot move crew who are recovering in medbay
        if (c.isInMedbay()) return;

        // energy restored when returning to Quarters
        if (loc == Location.QUARTERS) {
            c.resetEnergy();
        }

        c.setLocation(loc);
    }

    public ArrayList<CrewMember> getCrewAt(Location loc) {
        ArrayList<CrewMember> result = new ArrayList<>();
        for (CrewMember c : crewMap.values()) {
            if (c.getLocation() == loc) {
                result.add(c);
            }
        }
        return result;
    }

    public void trainCrew(int id) {
        CrewMember c = crewMap.get(id);
        // can only train crew who are in the simulator
        if (c != null && c.getLocation() == Location.SIMULATOR) {
            c.train();
        }
    }




    public void tickMedbay() {
        //  looping only over MEDBAY crew
        Iterator<CrewMember> it = iteratorAt(Location.MEDBAY);
        while (it.hasNext()) {
            CrewMember c = it.next();
            c.setRecoveryTurnsLeft(c.getRecoveryTurnsLeft() - 1);
            if (c.getRecoveryTurnsLeft() <= 0) {
                c.returnFromMedbay();
            }
        }
    }



    @Override
    public Iterator<CrewMember> iterator() {
        // lets ColonyManager itself be used in a for-each loop
        return crewMap.values().iterator();
    }

    public Iterator<CrewMember> iteratorAt(Location loc) {
        return new LocationIterator(crewMap.values(), loc);
    }

    //data storage in file (bonus feature)
    public void saveToFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(
                    "crew.txt", Context.MODE_PRIVATE);

            for (CrewMember c : crewMap.values()) {
                String line = c.getId()                     + ","
                        + c.getName()                   + ","
                        + c.getType()                   + ","
                        + c.getExperience()             + ","
                        + c.getLocation().name()        + ","
                        + c.isInMedbay()                + ","
                        + c.getRecoveryTurnsLeft()      + ","
                        + c.getMissionsPlayed()         + ","
                        + c.getMissionsWon()            + ","
                        + c.getMissionsLost()
                        //mission methods etc.
                        + "\n";
                fos.write(line.getBytes());
            }

            // save nextCrewId on its own line to re-load
            fos.write(("nextId:" + nextCrewId + "\n").getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(Context context) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            context.openFileInput("crew.txt")));

            crewMap.clear();
            String line;

            while ((line = reader.readLine()) != null) {

                // restore nextCrewId from its = line
                if (line.startsWith("nextId:")) {
                    nextCrewId = Integer.parseInt(line.split(":")[1]);
                    continue;
                }

                String[] p = line.split(",");

                // p[0]=id, p[1]=name, p[2]=type, p[3]=xp,
                // p[4]=location, p[5]=inMedbay, p[6]=recoveryTurns
                // p[7]=missionsCompleted, p[8]=victories, p[9]=training

                CrewMember c = createByType(p[1], p[2]);
                c.setId(Integer.parseInt(p[0]));
                c.setExperience(Integer.parseInt(p[3]));
                c.setLocation(Location.valueOf(p[4]));
                c.setInMedbay(Boolean.parseBoolean(p[5]));
                c.setRecoveryTurnsLeft(Integer.parseInt(p[6]));
                //mission

                if (p.length > 7) {
                    c.setMissionsPlayed(Integer.parseInt(p[7]));
                    c.setMissionsWon(Integer.parseInt(p[8]));
                    c.setMissionsLost(Integer.parseInt(p[9]));
                }

                crewMap.put(c.getId(), c);
            }

            reader.close();

        } catch (FileNotFoundException e) {
            // first launch — no file yet, start fresh
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}
