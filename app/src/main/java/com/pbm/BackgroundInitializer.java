package com.pbm;

public class BackgroundInitializer extends Thread {
    private Command cmd;

    void runMethod(com.pbm.Command cmd) {
        cmd.execute();
    }

    public BackgroundInitializer(com.pbm.Command cmd) { this.cmd = cmd; }

    public void run() { runMethod(this.cmd); }
}

