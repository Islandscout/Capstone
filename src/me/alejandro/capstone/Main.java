package me.alejandro.capstone;


import me.alejandro.capstone.window.WindowDashboard;


public class Main {

    //The purpose of this program is to display engine RPM, torque, and power as analog gauges, in real time.
    //It should be able to graph RPM-torque and RPM-hp curves
    //It should be able to record and export data as CSV files.

    //Originally I wanted to write this in C++, but since I'm much more fluent in Java and since I just
    // want to get this over with, I decided this should be done in Java. Doing this in C/C++ would have
    // been a great learning experience for writing drivers and getting graphics to work (perhaps by using
    // OpenGL), but I think that would take too much effort and time. However, interfacing with the Arduino
    // might require utilizing the JNI if no Java libraries exist for it. This will be interesting.

    public static void main(String[] args) {
        System.out.println("Capstone Engine Dynamometer Stand II (2023)");
        System.out.println("Drew Craney - ME: Mechanical design and fabrication");
        System.out.println("Grant Hoos - ME: Mechanical design and fabrication");
        System.out.println("Alexander Walker - ME: Mechatronics and integration");
        System.out.println("Alejandro Miller - EE: Electrical design and programming");

        //So here's the idea. I want GUI that "feels" responsive. To do this, I want to
        // write a loop that executes around 60 Hz. That should be fast enough. I don't care about
        // v-sync, and we won't be doing heavy rendering, so Java's Graphics2D should be sufficient
        // for this program.



        WindowDashboard gui = new WindowDashboard();

        System.out.println();

        try {
            gui.initArduino();
        } catch (Exception e) {
            System.out.println("A critical exception has occurred while initializing. Program will now exit.");
            e.printStackTrace();
            quit();
        }

        gui.startLoop();

    }

    public static void quit() {
        System.exit(0);
    }
}
