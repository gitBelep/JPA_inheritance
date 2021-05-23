package tracker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;

public class ActivityTrackerDao {
    private final EntityManagerFactory emf;

    public ActivityTrackerDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void saveActivity(Activity activity){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        activity.setWritingTime( LocalDateTime.now() );
        em.persist( activity );
        em.getTransaction().commit();
        em.close();
    }

    public void saveActivities(List<Activity> activities){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        for(Activity a : activities){
            a.setWritingTime( LocalDateTime.now() );
            em.persist( a );
        }
        em.getTransaction().commit();
        em.close();
    }

    public Activity findActivityByDesc(String text){
        EntityManager em = emf.createEntityManager();
        String likeText = "%"+ text + "%";
        Activity a = em.createQuery(
                "select a from Activity a where description like :text", Activity.class)
                .setParameter("text", likeText)
                .getSingleResult();
        em.close();
        return a;
    }

    public void addCoordinate(Coordinate coordinate, long id){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        ActivityWithTrack activity = em.getReference(ActivityWithTrack.class, id);
        coordinate.setCooActivity( activity );
        em.persist( coordinate );
        em.getTransaction().commit();
        em.close();
    }

    public ActivityWithTrack findActivityWithCoordinateByDesc(String text){
        EntityManager em = emf.createEntityManager();
        String likeText = "%"+ text + "%";
        Activity a = em.createQuery(
                "select a from Activity a join fetch a.coordinates where a.description like :text", Activity.class)
                .setParameter("text", likeText)
                .getSingleResult();
        em.close();
        return (ActivityWithTrack) a;
    }


}
