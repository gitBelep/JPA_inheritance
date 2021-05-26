package tracker;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ActivityWithTrack extends Activity{
    private double distance;

    private int duration;

    @OneToMany(mappedBy = "cooActivity", cascade = {CascadeType.ALL})
    private List<Coordinate> coordinates = new ArrayList<>();


    public ActivityWithTrack() {
    }

    public ActivityWithTrack(LocalDateTime startTime, String description, ActivityType type, double distance, int duration) {
        super(startTime, description, type);
        this.distance = distance;
        this.duration = duration;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

}
