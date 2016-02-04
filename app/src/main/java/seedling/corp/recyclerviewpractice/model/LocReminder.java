package seedling.corp.recyclerviewpractice.model;

/**
 * Created by Ankur Nigam on 22-12-2015.
 */
public class LocReminder {

    private String type;
    private String title;
    private double latitude;
    private double longitude;
    private int radius;
    private String repeat;
    private String imgPath;
    private boolean enabled;

    public LocReminder(String type, String title, double latitude, double longitude,
                       int radius, String repeat, String imgPath, boolean enabled) {
        this.type = type;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.repeat = repeat;
        this.imgPath = imgPath;
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
