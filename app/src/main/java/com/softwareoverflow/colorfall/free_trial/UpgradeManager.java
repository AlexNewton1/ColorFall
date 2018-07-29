package com.softwareoverflow.colorfall.free_trial;

public class UpgradeManager {

    private static boolean hasUserUpgraded = false;

    //TODO - this will be the class where all upgrades and interaction with google services are handled

    public static boolean isFreeUser(){
        return !hasUserUpgraded;
    }

}
