package com.eziosoft.patchyvm.guac.objects;

import com.eziosoft.patchyvm.guac.Auth;
import com.google.gson.Gson;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public class Database {

    private String ip;
    private int port;
    private String username;
    private String password;
    private boolean h;

    private String vms = "vms";
    private String user = "users";

    private static Connection thonk;
    private static final RethinkDB r = RethinkDB.r;
    private Gson g = new Gson();

    public Database(String ip, int port, String username, String password, boolean h){
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.h = h;
    }

    public void initDB(){
        try {
            // just run thru the stuff
            Connection.Builder builder = null;
            if (h) {
                builder = r.connection().hostname(ip).port(port).user(username, password);
            } else {
                builder = r.connection().hostname(ip).port(port);
            }
            thonk = builder.connect();
            thonk.use("rentavm");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkForUser(String username){
        // for some fucking reason, guacamole tries to call this function when there has been no login attempts
        // so i guess we have to deal with that
        // thanks, guac
        boolean h = false;
        try {
            h = r.table(user).getAll(username).count().eq(1).run(thonk, boolean.class).first();
        } catch (Exception e){
            e.printStackTrace();
        }
       return h;
    }

    public User getUser(String username){
        return g.fromJson(r.table(user).get(username).toJson().run(thonk, String.class).first(), User.class);
    }

    public VirtualMachine getVM(String vmid){
        return g.fromJson(r.table(vms).get(vmid).toJson().run(thonk, String.class).first(), VirtualMachine.class);
    }
}
