package org.example;

public class Main {
    public static void main(String[] args) {
        Main app = new Main();

        app.run();
    }

    void run() {
        for (int i = 0; i < 3; i++) {
            processItem(i);
        }

        finish();
    }

    void processItem(int itemNumber) {
        System.out.println("Processing item " + itemNumber);
    }

    void finish() {
        System.out.println("Done");
    }
}
