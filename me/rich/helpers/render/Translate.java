package me.rich.helpers.render;

public class Translate {

    private float x;
    private float y;
    private long lastMS;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
        this.lastMS = System.currentTimeMillis();
    }

    public void interpolate(float targetX, float targetY, int xSpeed, int ySpeed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - lastMS;//16.66666
        lastMS = currentMS;
        int deltaX = (int) (Math.abs(targetX - x) * 0.51f);
        int deltaY = (int) (Math.abs(targetY - y) * 0.51f);
        x = AnimationHelper.calculateCompensation(targetX, x, delta, deltaX);
        y = AnimationHelper.calculateCompensation(targetY, y, delta, deltaY);
    }

    public void interpolate(float targetX, float targetY, double speed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - lastMS;//16.66666
        lastMS = currentMS;
        double deltaX = 0;
        double deltaY = 0;
        if(speed != 0){
            deltaX = (Math.abs(targetX - x) * 0.35f)/(10/speed);
            deltaY = (Math.abs(targetY - y) * 0.35f)/(10/speed);
        }
        x = AnimationHelper.calculateCompensation(targetX, x, delta, deltaX);
        y = AnimationHelper.calculateCompensation(targetY, y, delta, deltaY);
    }

    public float getX() {
        return x;
    } 

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}