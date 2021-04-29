package com.eziosoft.patchyvm.guac.objects;

import com.google.gson.Gson;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public class Database {

    private String ip;
    private int port;
    private String username;
    private String password;

    private String vms = "vms";
    private String user = "users";

    private Connection thonk;
    private final RethinkDB r = RethinkDB.r;
    private Gson g = new Gson();

    public Database(String ip, int port, String username, String password){
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void initDB(){
        // just run thru the stuff
        Connection.Builder builder = r.connection().hostname(ip).port(port).user(username, password);
        thonk = builder.connect();
        thonk.use("rentavm");
    }

    public boolean checkForUser(String username){
        return r.table(user).getAll(username).count().eq(1).run(thonk, boolean.class).first();
    }

    public User getUser(String username){
        return g.fromJson(r.table(user).get(username).toJson().run(thonk, String.class).first(), User.class);
    }

    public VirtualMachine getVM(String vmid){
        return g.fromJson(r.table(vms).get(vmid).toJson().run(thonk, String.class).first(), VirtualMachine.class);
    }
}
