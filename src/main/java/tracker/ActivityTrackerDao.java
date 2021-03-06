package tracker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Activity> listActivities(){
        EntityManager em = emf.createEntityManager();
        List<Activity> az = em.createQuery(
                "select a from Activity a", Activity.class)
                .getResultList();
        em.close();
        return az;
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
        ActivityWithTrack a = em.createQuery(
                "select a from ActivityWithTrack a join fetch a.coordinates where a.description like :text", ActivityWithTrack.class)
                .setParameter("text", likeText)
                .getSingleResult();
        em.close();
        return a;
    }

    public List<Coordinate> findCoordinatesByActivityDate(LocalDateTime afterThis, int start, int max){
        EntityManager em = emf.createEntityManager();
        List<Coordinate> result = em.createQuery(
// it is working, but..:
// "select c from ActivityWithTrack t join t.coordinates c where t.startTime > :date", Coordinate.class)
        "select c from Coordinate c where c.cooActivity.startTime > :date", Coordinate.class)
                .setParameter("date", afterThis)
                .setFirstResult(start)
                .setMaxResults(max)
                .getResultList();
        em.close();
        return result;
    }

    public List<Coordinate> findCoordinatesByNamedQueryAfterDate(LocalDateTime afterThis, int start, int max){
        EntityManager em = emf.createEntityManager();
        List<Coordinate> result = em.createNamedQuery("coordinatesAfterGivenActivityDate", Coordinate.class)
                .setParameter("date", afterThis)
                .setFirstResult(start)
                .setMaxResults(max)
                .getResultList();
        em.close();
        return result;
    }

    public List<Coordinate> findCoordinatesByStreamAfterDate(LocalDateTime afterThis, int start, int max){
        EntityManager em = emf.createEntityManager();
        List<Coordinate> result = em.createQuery(
                "select t from ActivityWithTrack t join t.coordinates", ActivityWithTrack.class)
                .getResultStream()
                .filter(t -> t.getStartTime().isAfter(afterThis))
                .map(ActivityWithTrack::getCoordinates)
                .flatMap(c -> c.stream())
                .skip(start)
                .limit(max)
                .distinct()   //WHY is it necessary?? (Both: Activity & Coordi will be selected?)
                .collect(Collectors.toList());
        em.close();
        return result;
    }

    public List<CoordinateDTO> listCoordinateDTO(){
         EntityManager em = emf.createEntityManager();
        List<CoordinateDTO> result = em.createQuery(
                "select new tracker.CoordinateDTO(c.lat, c.lon) from Coordinate c order by c.id")
                .getResultList();
        em.close();
        return result;
    }

    public List<CoordinateDTO> listCoordinateDTOAfterDate(LocalDateTime afterThis){
        EntityManager em = emf.createEntityManager();
        List<CoordinateDTO> result = em.createQuery(
                "select new tracker.CoordinateDTO(c.lat, c.lon) from Coordinate c where c.cooActivity.startTime > :date order by c.id")
                .setParameter("date", afterThis)
                .getResultList();
        em.close();
        return result;
    }

    public List<Object[]> findCoordinateCountByActivityDescription(){
        EntityManager em = emf.createEntityManager();
        List<Object[]> result = em.createQuery(
        "select t.description, count(c) from ActivityWithTrack t join t.coordinates c group by t.description order by t.description desc")
        .getResultList();
        em.close();
        return result;
    }

    public void removeActivitiesByDateAndType(LocalDateTime afterThis, ActivityType type){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery(
            "delete Activity a where a.type = :type and a.startTime >= :afterThis")
            .setParameter("type", type)
            .setParameter("afterThis", afterThis)
            .executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    public List<Activity> listActivitiesByType(ActivityType type){
        EntityManager em = emf.createEntityManager();
        List<Activity> result = em.createQuery(
                "select a from Activity a where type = :type", Activity.class)
                .setParameter("type", type)
                .getResultList();
        em.close();
        return result;
    }
}
