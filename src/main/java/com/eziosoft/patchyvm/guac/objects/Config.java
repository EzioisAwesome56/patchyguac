package com.eziosoft.patchyvm.guac.objects;

public class Config {

    private String dbip;
    private int dbport;
    private String dbuser;
    private String dbpass;
    private String salt;
    private boolean needsinfo;

    public Config(){};

    public void createDefaults(){
        this.dbip = "0.0.0.0";
        this.dbport = 69;
        this.dbpass = "mycoolpassword";
        this.dbuser = "gamer moments";
        this.salt = "haha salt";
        this.needsinfo = false;
    }

    public int getDbport() {
        return dbport;
    }

    public String getDbip() {
        return dbip;
    }

    public String getDbpass() {
        return dbpass;
    }

    public String getDbuser() {
        return dbuser;
    }

    public String getSalt() {
        return salt;
    }

    public boolean isNeedsinfo(){return this.needsinfo; }
}
