/**
 * File: Main.java
 * Date: January 21, 2020
 * @Author: Brian Rease
 * Purpose: This program is meant to create 3 simple images and use Java 2D graphics methods to rotate, scale,
 * and translate each of the images in a sequence.
 *
 * This specific class implements the GUI for the program and the main() method. The main method creates the window (GUI)
 * and starts a Timer 'animationTimer'. The "action" of the code happens inside of the paintComponent() method which is
 * and overridden method from JComponent.
 */

package com.company;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends JPanel {

    // A counter that increases by one in each frame.
    private int frameNumber;
    // The time, in milliseconds, since the animation started.
    private long elapsedTimeMillis; //used during animationTimer

    // This is the measure of a pixel in the coordinate system
    // It can be used for setting line widths, for example.
    private float pixelSize; //used in the applyWindowToViewportTransformation() method

    static int translateX = 0;
    static int translateY = 0;
    static double rotation = 0.0;
    static double scaleX = 1.0;
    static double scaleY = 1.0;

    ImageTemplate myImages = new ImageTemplate();
    BufferedImage mImage = myImages.getImage(ImageTemplate.letterM);
    BufferedImage fImage = myImages.getImage(ImageTemplate.letterF);
    BufferedImage numberImage = myImages.getImage(ImageTemplate.numberFour);

    public static void main(String[] args) {
        JFrame window;
        window = new JFrame("Java Animation");  // The parameter shows in the window title bar.
        final Main panel = new Main(); // The drawing area.
        window.setContentPane(panel); // Show the panel in the window.
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // End program when window closes.
        window.setPreferredSize(new Dimension(800, 600));
        window.pack();  // Set window size based on the preferred sizes of its contents.
        window.setResizable(false); // Don't let user resize window.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation( // Center window on screen.
                (screen.width - window.getWidth()) / 2,
                (screen.height - window.getHeight()) / 2);
        Timer animationTimer;  // A Timer that will emit events to drive the animation.
        final long startTime = System.currentTimeMillis();
        // Taken from AnimationStarter
        // Modified to change timing and allow for recycling
        animationTimer = new Timer(1600, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (panel.frameNumber > 5) {
                    panel.frameNumber = 0;
                } else {
                    panel.frameNumber++;
                }
                panel.elapsedTimeMillis = System.currentTimeMillis() - startTime;
                panel.repaint();
            }
        });
        window.setVisible(true); // Open the window, making it visible on the screen.
        animationTimer.start();  // Start the animation running.
    } //end of main()

    // This is where all of the action takes place
    // Code taken from AnimationStarter.java but modified to add the specific Images
    // Also added looping structure for Different transformations
    @Override
    protected void paintComponent(Graphics g) {

        /* First, create a Graphics2D drawing context for drawing on the panel.
         * (g.create() makes a copy of g, which will draw to the same place as g,
         * but changes to the returned copy will not affect the original.)
         */
        Graphics2D g2 = (Graphics2D) g.create();

        /* Turn on antialiasing in this graphics context, for better drawing.
         */
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /* Fill in the entire drawing area with white.
         */
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight()); // From the old graphics API!

        // Controls your zoom and area you are looking at
        applyWindowToViewportTransformation(g2, -75, 75, -75, 75, true);

        AffineTransform savedTransform = g2.getTransform();
        System.out.println("Frame is " + frameNumber);
        switch (frameNumber) {
            case 1: // First frame is unmodified.
                translateX = 0;
                translateY = 0;
                scaleX = 1.0;
                scaleY = 1.0;
                rotation = 0;
                break;
            case 2: // Second frame translates each image by (-5, 7).
                translateX = -5;
                translateY = 7;
                break;
            case 3: // Third frame rotates each image by 45 degrees counter clockwise
                rotation = 45 * Math.PI / 180.0;
                break;
            case 4: //Fourth frame rotates each image by 90 degrees clock wise
                rotation = -90;
                break;
            case 5: //Fifth frame scaled 2 times for the x component and 0.5 for the y component
                scaleX = 2.0;
                scaleY = 0.5;
                break;
            default:
                break;
        } // End switch

        //call the performOperations() method to translate, rotate, scale, and draw image
        performOperations(g2, mImage, savedTransform,-10, 10);
        performOperations(g2, fImage, savedTransform, -35, 40);
        performOperations(g2, numberImage, savedTransform, -50, -40);
    } //end of paintComponent()

    //method to translate, rotate, scale, and draw images
    protected void performOperations(Graphics2D g2, BufferedImage image, AffineTransform savedTransform, int x, int y) {
        g2.translate(translateX, translateY);
        g2.translate(x, y);
        g2.rotate(rotation);
        g2.scale(scaleX, scaleY);
        g2.drawImage(image, 0, 0, this);
        g2.setTransform(savedTransform);
    } //end of performOperations()

    // Method taken directly from AnimationStarter.java Code
    private void applyWindowToViewportTransformation(Graphics2D g2, double left, double right, double bottom, double top, boolean preserveAspect) {
        int width = getWidth();   // The width of this drawing area, in pixels.
        int height = getHeight(); // The height of this drawing area, in pixels.
        if (preserveAspect) {
            // Adjust the limits to match the aspect ratio of the drawing area.
            double displayAspect = Math.abs((double) height / width);
            double requestedAspect = Math.abs((bottom - top) / (right - left));
            if (displayAspect > requestedAspect) {
                // Expand the viewport vertically.
                double excess = (bottom - top) * (displayAspect / requestedAspect - 1);
                bottom += excess / 2;
                top -= excess / 2;
            } else if (displayAspect < requestedAspect) {
                // Expand the viewport vertically.
                double excess = (right - left) * (requestedAspect / displayAspect - 1);
                right += excess / 2;
                left -= excess / 2;
            }
        }
        g2.scale(width / (right - left), height / (bottom - top));
        g2.translate(-left, -top);
        double pixelWidth = Math.abs((right - left) / width);
        double pixelHeight = Math.abs((bottom - top) / height);
        pixelSize = (float) Math.max(pixelWidth, pixelHeight);
    } //end of applyWindowToViewportTransformation()

} //end of Main
