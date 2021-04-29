package com.eziosoft.patchyvm.guac;

import com.eziosoft.patchyvm.guac.objects.Config;
import com.eziosoft.patchyvm.guac.objects.Database;
import com.eziosoft.patchyvm.guac.objects.User;
import com.eziosoft.patchyvm.guac.objects.VirtualMachine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.simple.SimpleAuthenticationProvider;
import org.apache.guacamole.net.auth.simple.SimpleSystemPermissionSet;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Auth extends SimpleAuthenticationProvider{

    private static Config c = null;
    private static Database db = null;

    @Override
    public Map<String, GuacamoleConfiguration> getAuthorizedConfigurations(Credentials credentials) throws GuacamoleException {
        // do we already have config loaded?
        if (c == null) {
            // check to see if the config file exists (where ever it goes idfk)
            File config = new File("/etc/patchy/config.json");
            Gson g = new GsonBuilder().setPrettyPrinting().create();
            try {
                if (!config.exists()) {
                    System.err.println("No config file found! Generating defaults...");
                    Config conf = new Config();
                    conf.createDefaults();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(config));
                    writer.write(g.toJson(conf));
                    writer.close();
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            // if we're here, the config file _probably_ exists
            // load it!
            try {
                c = g.fromJson(new BufferedReader(new FileReader(config)), Config.class);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        System.err.println("Config file loaded, proof: salt = " + c.getSalt());
        // do we have the db loaded and configured?
        if (db == null){
            db = new Database(c.getDbip(), c.getDbport(), c.getDbuser(), c.getDbpass(), false);
            db.initDB();
        }
        // now we can actually do auth things lol
        // check to see if the username exists
        if (!db.checkForUser(credentials.getUsername())){
            // fail
            return null;
        }
        System.err.println("We have checked the db for user!");
        // load user
        User u = db.getUser(credentials.getUsername());
        // do the pash hashes matches
        if (!BCrypt.hashpw(credentials.getPassword(), c.getSalt()).equals(u.getPasshash())){
            // fail
            return null;
        }
        // do they even have a vm?
        if (u.getVmid() == 0){
            // fail
            return null;
        }
        VirtualMachine vm = db.getVM(Integer.toString(u.getVmid()));
        // we have logged in, create configurations
        Map<String, GuacamoleConfiguration> guacmap = new HashMap<String, GuacamoleConfiguration>();
        GuacamoleConfiguration guac = new GuacamoleConfiguration();
        guac.setProtocol("rdp");
        guac.setParameter("hostname", vm.getIpaddr());
        guac.setParameter("port", "3389");
        guacmap.put("PatchyVM Connection", guac);
        return guacmap;
    }

    @Override
    public String getIdentifier() {
        return "patchyVM";
    }
}
