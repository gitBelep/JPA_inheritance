package tracker;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //IDENTITY nem lehet öröklődésnél
    private long actId;

    private LocalDateTime startTime;

    private LocalDateTime writingTime;

    @Column(name="description", nullable = false, length = 200)
    private String description;


    public Activity() {
    }

    public Activity(LocalDateTime startTime, String description) {
        this.startTime = startTime;
        this.description = description;
    }


    public long getActId() {
        return actId;
    }

    public void setActId(long actId) {
        this.actId = actId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getWritingTime() {
        return writingTime;
    }

    public void setWritingTime(LocalDateTime writingTime) {
        this.writingTime = writingTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
