package tracker;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class SimpleActivitySpecial extends SimpleActivity{
    private String special;


    public SimpleActivitySpecial() {
        }

    public SimpleActivitySpecial(LocalDateTime startTime, String description, ActivityType type, String place, String special) {
        super(startTime, description, type, place);
        this.special = special;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

}
