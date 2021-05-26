package tracker;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class SimpleActivity extends Activity{
    @Column(name="la_place")
    private String place;


    public SimpleActivity() {
    }

    public SimpleActivity(LocalDateTime startTime, String description, ActivityType type, String place) {
        super(startTime, description, type);
        this.place = place;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

}
