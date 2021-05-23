package tracker;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Coordinate {
    @Id
    @TableGenerator(name = "CoordinateIdGenerator", table = "coo_id_gen",
            pkColumnName = "coo_id_gen", valueColumnName = "coo_id_val")
    @GeneratedValue(generator = "CoordinateIdGenerator")
    private long coo_id;

    @Column(name = "latitude")
    private double lat;

    @Column(name = "longitude")
    private double lon;

    @ManyToOne
    @JoinColumn(name = "activityId")
    private ActivityWithTrack cooActivity;


    public Coordinate() {
    }

    public Coordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.lat, lat) == 0 && Double.compare(that.lon, lon) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }

    public long getCoo_id() {
        return coo_id;
    }

    public void setCoo_id(long coo_id) {
        this.coo_id = coo_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public ActivityWithTrack getCooActivity() {
        return cooActivity;
    }

    public void setCooActivity(ActivityWithTrack cooActivity) {
        this.cooActivity = cooActivity;
    }

}
