package tracker;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class ActivityTrackerDaoTest {
    private ActivityTrackerDao dao;

    @Before
    public void setUp() throws Exception{
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("puB");
        dao = new ActivityTrackerDao( emf );

        Activity a1 = new Activity(LocalDateTime.of(2020,11,11,11,11,0), "Túráztam a Raxon");
        dao.saveActivity( a1 );

        Activity abc = new Activity(LocalDateTime.of(2020,12,12,12,12,0), "A közeli ABC-be mentem");
        Activity obi = new Activity(LocalDateTime.of(2020,10,10,10,10,0), "OBI-ban voltam");
        Activity aWithTrack = new ActivityWithTrack(LocalDateTime.of(2018,7,7,7,7,0),"Trükkös2", 55.8, 8000);
        dao.saveActivities( List.of(abc, obi, aWithTrack) );
    }

    @Test
    public void findActivityByDescLike() {
        Activity aR = dao.findActivityByDesc("axon");
//        System.out.println(">>> "+ aR.getDescription());
        Activity aBC = dao.findActivityByDesc("ABC");
        Activity aObi = dao.findActivityByDesc("OBI");

        assertEquals(11, aR.getStartTime().getDayOfMonth());
        assertEquals(10, aObi.getStartTime().getDayOfMonth());
        assertEquals(12, aBC.getStartTime().getDayOfMonth());
    }

    @Test
    public void saveAndFindInheritance(){
        dao.saveActivities(List.of(
                new SimpleActivity(LocalDateTime.of(2019,9,9,9,9,0),"Simple vagyok", "Bécs"),
                new ActivityWithTrack(LocalDateTime.of(2017,7,7,7,7,0),"Trükős", 55.7, 7007),
                new SimpleActivitySpecial(LocalDateTime.of(2016,6,6,6,6,0),"Speckó", "Ács", "Feriékkel"),
                new SimpleActivity(LocalDateTime.of(2018,8,8,8,8,0),"Még egy Simple", "Pécs")
        ));

        Activity a2 = dao.findActivityByDesc("egy ");
        Activity a4 = dao.findActivityByDesc("ABC");
        Activity a3 = dao.findActivityByDesc("Trükős");
        Activity a1 = dao.findActivityByDesc("vagyok");
        Activity a5 = dao.findActivityByDesc("Speck");

        assertEquals("Trükős", a3.getDescription());
        assertEquals(7, ( (ActivityWithTrack)a3 ).getStartTime().getMinute());
        assertEquals(2018, ((SimpleActivity)a2).getStartTime().getYear());
        assertEquals("Bécs", ((SimpleActivity)a1).getPlace());
        assertEquals("A közeli ABC-be mentem", a4.getDescription());
        assertEquals("Feriékkel", ((SimpleActivitySpecial)a5).getSpecial() );
    }

    @Test
    public void testSelectCoordinatesAfterADate(){
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2017,7,7,7,7,0),"Trükős", 55.7, 7007));
        Coordinate c1 = new Coordinate(1.1, 1.1);
        Coordinate c2 = new Coordinate(1.2, 1.2);
        Coordinate c3 = new Coordinate(1.3, 1.3);

        Activity aa1 = dao.findActivityByDesc("Trükős");
        Activity aa2 = dao.findActivityByDesc("Trükkös2");
        dao.addCoordinate(c1, aa1.getActId());
        dao.addCoordinate(c2, aa2.getActId());
        dao.addCoordinate(c3, aa2.getActId());

        ActivityWithTrack reloadA1 = dao.findActivityWithCoordinateByDesc("Trükős");
        ActivityWithTrack reloadA2 = dao.findActivityWithCoordinateByDesc("Trükkös2");

        assertEquals(1.1, reloadA1.getCoordinates().get(0).getLat(), 0.01);
        //@Override: .equals()
        assertTrue(reloadA2.getCoordinates().contains( new Coordinate(1.3, 1.3) ));


    }

}
