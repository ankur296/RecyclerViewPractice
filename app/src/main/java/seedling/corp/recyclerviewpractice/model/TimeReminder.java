package seedling.corp.recyclerviewpractice.model;

/**
 * Created by Ankur Nigam on 22-12-2015.
 */
public class TimeReminder {

    private String type;
    private String title;
    private String date;
    private String time;
    private String repeat;
    private String imgPath;
    private boolean enabled;
    private int systemTime;

    public TimeReminder(String type, String title, String date, String time, String repeat, String imgPath, boolean enabled, int systemTime) {
        this.type = type;
        this.title = title;
        this.date = date;
        this.time = time;
        this.repeat = repeat;
        this.imgPath = imgPath;
        this.enabled = enabled;
        this.systemTime = systemTime;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public int getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(int systemTime) {
        this.systemTime = systemTime;
    }
}
