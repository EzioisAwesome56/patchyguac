package com.eziosoft.patchyvm.guac;

import com.eziosoft.patchyvm.guac.objects.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.simple.SimpleAuthenticationProvider;
import org.apache.guacamole.protocol.GuacamoleConfiguration;

import java.io.*;
import java.util.Map;

public class Auth extends SimpleAuthenticationProvider{
    @Override
    public Map<String, GuacamoleConfiguration> getAuthorizedConfigurations(Credentials credentials) throws GuacamoleException {
        // check to see if the config file exists (where ever it goes idfk)
        File config = new File("config.json");
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        Config c = null;
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
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        // if we're here, the config file _probably_ exists
        // load it!
        try {
            c = g.fromJson(new BufferedReader(new FileReader(config)), Config.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ok, that should be everything we need i think i
        return null;
    }

    @Override
    public String getIdentifier() {
        return null;
    }
}
