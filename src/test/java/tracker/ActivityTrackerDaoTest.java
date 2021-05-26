package tracker;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ActivityTrackerDaoTest {
    private ActivityTrackerDao dao;

    @Before
    public void setUp() throws Exception{
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("puB");
        dao = new ActivityTrackerDao( emf );

        Activity a1 = new Activity(LocalDateTime.of(2020,11,11,11,11,0), "Túráztam a Raxon", ActivityType.HIKING);
        dao.saveActivity( a1 );

        Activity abc = new Activity(LocalDateTime.of(2020,12,12,12,12,0), "A közeli ABC-be mentem", ActivityType.RUNNING);
        Activity obi = new Activity(LocalDateTime.of(2020,10,10,10,10,0), "OBI-ban voltam", ActivityType.RUNNING);
        Activity aWithTrack = new ActivityWithTrack(LocalDateTime.of(2018,7,7,7,7,0),"Trükkös2", ActivityType.HIKING, 55.71, 7017);
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
                new SimpleActivity(LocalDateTime.of(2019,9,9,9,9,0),"Simple vagyok", ActivityType.BASKETBALL, "Bécs"),
                new ActivityWithTrack(LocalDateTime.of(2017,1,7,7,7,0),"Trükős", ActivityType.BASKETBALL, 55.71, 7017),
                new SimpleActivitySpecial(LocalDateTime.of(2016,6,6,6,6,0),"Speckó", ActivityType.BASKETBALL, "Ács", "Feriékkel"),
                new SimpleActivity(LocalDateTime.of(2018,8,8,8,8,0),"Még egy Simple", ActivityType.BASKETBALL, "Pécs")
        ));

        Activity a2 = dao.findActivityByDesc("egy ");
        Activity a4 = dao.findActivityByDesc("ABC");
        Activity a3 = dao.findActivityByDesc("Trükős");
        Activity a1 = dao.findActivityByDesc("vagyok");
        Activity a5 = dao.findActivityByDesc("Speck");

        assertEquals("Trükős", a3.getDescription());
        assertEquals(7, a3.getStartTime().getMinute());    //(ActivityWithTrack)
        assertEquals(2018, a2.getStartTime().getYear());   //(SimpleActivity)
        assertEquals("Bécs", ((SimpleActivity)a1).getPlace());
        assertEquals("A közeli ABC-be mentem", a4.getDescription());
        assertEquals("Feriékkel", ((SimpleActivitySpecial)a5).getSpecial() );
    }

    @Test
    public void testSaveCoordinates(){
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2020,2,7,7,7,0),"Trükős", ActivityType.RUNNING, 55.72, 7027));
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

    @Test
    public void testSelectCoordinatesAfterADate() {
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2020, 2, 7, 7, 7, 0), "Trükős", ActivityType.RUNNING, 55.72, 7027));
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2021, 3, 7, 7, 7, 0), "Türkök", ActivityType.RUNNING, 55.73, 7037));
        Coordinate c1 = new Coordinate(1.1, 1.1);
        Coordinate c2 = new Coordinate(1.2, 1.2);
        Coordinate c3 = new Coordinate(1.3, 1.3);
        Coordinate c4 = new Coordinate(1.4, 1.4);

        Activity aa1 = dao.findActivityByDesc("Trükős");
        Activity aa2 = dao.findActivityByDesc("Trükkös2");
        Activity aa4 = dao.findActivityByDesc("Türkök");

        dao.addCoordinate(c1, aa1.getActId());
        dao.addCoordinate(c2, aa1.getActId());
        dao.addCoordinate(c3, aa2.getActId());
        dao.addCoordinate(c4, aa4.getActId());

        //3 Coordinates: (c1, c2), (c4)
        List<Coordinate> coos1 = dao.findCoordinatesByActivityDate(LocalDateTime.of(2020,1,1,1,1,0), 0,40);
        //1 Coordinate: (c4)
        List<Coordinate> coos2 = dao.findCoordinatesByActivityDate(LocalDateTime.of(2021,1,1,1,1,0), 0,40);

        assertEquals(3, coos1.size());
        assertEquals(1, coos2.size());
    }

    @Test
    public void testNamedQuerySelectCoordinatesAfterADate() {
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2020, 2, 7, 7, 7, 0), "Trükős", ActivityType.RUNNING, 55.72, 7027));
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2021, 3, 7, 7, 7, 0), "Türkök", ActivityType.RUNNING, 55.73, 7037));
        Coordinate c1 = new Coordinate(1.1, 1.1);
        Coordinate c2 = new Coordinate(1.2, 1.2);
        Coordinate c3 = new Coordinate(1.3, 1.3);
        Coordinate c4 = new Coordinate(1.4, 1.4);

        Activity aa1 = dao.findActivityByDesc("Trükős");
        Activity aa2 = dao.findActivityByDesc("Trükkös2");
        Activity aa4 = dao.findActivityByDesc("Türkök");

        dao.addCoordinate(c1, aa1.getActId());
        dao.addCoordinate(c2, aa1.getActId());
        dao.addCoordinate(c3, aa2.getActId());
        dao.addCoordinate(c4, aa4.getActId());

        //3 Coordinates: (c1, c2), (c4)
        List<Coordinate> coos1 = dao.findCoordinatesByNamedQueryAfterDate(LocalDateTime.of(2020,1,1,1,1,0), 0,40);
        //1 Coordinate: (c4)
        List<Coordinate> coos2 = dao.findCoordinatesByNamedQueryAfterDate(LocalDateTime.of(2021,1,1,1,1,0), 0,40);

        assertEquals(3, coos1.size());
        assertEquals(1, coos2.size());
    }

    @Test
    public void testStreamSelectCoordinatesAfterADate() {
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2020, 2, 7, 7, 7, 0), "Trükős", ActivityType.RUNNING, 55.72, 7027));
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2021, 3, 7, 7, 7, 0), "Türkök", ActivityType.RUNNING, 55.73, 7037));
        Coordinate c1 = new Coordinate(1.1, 1.1);
        Coordinate c2 = new Coordinate(1.2, 1.2);
        Coordinate c3 = new Coordinate(1.3, 1.3);
        Coordinate c4 = new Coordinate(1.4, 1.4);

        Activity aa1 = dao.findActivityByDesc("Trükős");
        Activity aa2 = dao.findActivityByDesc("Trükkös2");
        Activity aa4 = dao.findActivityByDesc("Türkök");

        dao.addCoordinate(c1, aa1.getActId());
        dao.addCoordinate(c2, aa1.getActId());
        dao.addCoordinate(c3, aa2.getActId());
        dao.addCoordinate(c4, aa4.getActId());

        //3 Coordinates: (c1, c2), (c4)
        List<Coordinate> coos1 = dao.findCoordinatesByStreamAfterDate(LocalDateTime.of(2020,1,1,1,1,0), 0,40);
         //1 Coordinate: (c4)
        List<Coordinate> coos2 = dao.findCoordinatesByStreamAfterDate(LocalDateTime.of(2021,1,1,1,1,0), 0,40);

        assertEquals(1, coos2.size());
        assertEquals(3, coos1.size());
    }

    @Test
    public void testListCoordinateDTO(){
        Coordinate c1 = new Coordinate(1.1, 1.1);
        Coordinate c2 = new Coordinate(1.2, 1.2);
        Coordinate c3 = new Coordinate(1.3, 1.3);
        Coordinate c4 = new Coordinate(1.4, 1.4);

        Activity aa = dao.findActivityByDesc("Trükkös2");
        dao.addCoordinate(c1, aa.getActId());
        dao.addCoordinate(c2, aa.getActId());
        dao.addCoordinate(c3, aa.getActId());
        dao.addCoordinate(c4, aa.getActId());

        List<CoordinateDTO> c = dao.listCoordinateDTO();
        assertEquals(4, c.size());
        assertEquals(1.3, c.get(2).getLatitude(),0.01);
    }

    @Test
    public void testListCoordinateDTOAfterDate(){
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2021, 2, 7, 7, 7, 0), "Tr2021", ActivityType.RUNNING, 55.72, 7027));
        Coordinate c1 = new Coordinate(1.1, 1.1);
        Coordinate c2 = new Coordinate(1.2, 1.2);
        Coordinate c3 = new Coordinate(1.3, 1.3);
        Coordinate c4 = new Coordinate(1.4, 1.4);

        Activity aa2018 = dao.findActivityByDesc("Trükkös2");
        Activity aa2021 = dao.findActivityByDesc("Tr2021");

        dao.addCoordinate(c1, aa2018.getActId());
        dao.addCoordinate(c2, aa2021.getActId());
        dao.addCoordinate(c3, aa2018.getActId());
        dao.addCoordinate(c4, aa2021.getActId());

        List<CoordinateDTO> c = dao.listCoordinateDTOAfterDate(LocalDateTime.of(2019,2,2,2,2,0));
        //System.out.println(c.get(0).getLatitude() +" "+c.get(1).getLongitude());  //1.2, 1.4
        assertEquals(2, c.size());
        assertEquals(1.2, c.get(0).getLatitude(),0.01);
    }

    @Test
    public void testFindCoordinateCountByActivityDescription(){
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2021, 2, 7, 7, 7, 0), "Tr2021", ActivityType.RUNNING, 55.72, 7027));
        dao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2020, 1, 7, 7, 7, 0), "Tr2020", ActivityType.RUNNING, 40.72, 6027));
        Coordinate c1 = new Coordinate(1.1, 1.1);
        Coordinate c2 = new Coordinate(1.2, 1.2);
        Coordinate c3 = new Coordinate(1.3, 1.3);
        Coordinate c4 = new Coordinate(1.4, 1.4);
        Coordinate c5 = new Coordinate(1.5, 1.5);

        Activity aa2018 = dao.findActivityByDesc("Trükkös2");
        Activity aa2021 = dao.findActivityByDesc("Tr2021");
        Activity aa2020 = dao.findActivityByDesc("Tr2020");

        dao.addCoordinate(c1, aa2018.getActId());
        dao.addCoordinate(c2, aa2021.getActId());
        dao.addCoordinate(c3, aa2018.getActId());
        dao.addCoordinate(c4, aa2021.getActId());
        dao.addCoordinate(c5, aa2020.getActId());

        List<Object[]> result = dao.findCoordinateCountByActivityDescription();
        assertEquals(3, result.size());
        assertEquals(2L, result.get(1)[1]);
        assertEquals("Tr2020", result.get(2)[0].toString());

        for(Object[] o : result){
            System.out.print( Arrays.toString(o) +" ");          //it also works with String & Long
            System.out.println( o[0].toString() +" ~ "+ o[1] );  //it is redundant to cast to Long
        }
    }

    @Test
    public void testRemoveActivitiesByDateAndType(){
        dao.saveActivities(List.of(
                new Activity(LocalDateTime.of(1999,1,1,1,1,0),"a", ActivityType.BIKING),
                new Activity(LocalDateTime.of(1999,2,1,1,1,0),"a", ActivityType.HIKING),
                new Activity(LocalDateTime.of(2020,3,1,1,1,0),"a", ActivityType.BIKING),
                new Activity(LocalDateTime.of(2020,4,1,1,1,0),"a", ActivityType.BASKETBALL),
                new Activity(LocalDateTime.of(2018,5,1,1,1,0),"a", ActivityType.BIKING),
                new Activity(LocalDateTime.of(2018,6,1,1,1,0),"a", ActivityType.RUNNING),
                new Activity(LocalDateTime.of(2009,7,1,1,1,0),"a", ActivityType.BIKING)
        ));

        List<Activity> wholeList = dao.listActivities();
        List<Activity> beforeList = dao.listActivitiesByType(ActivityType.BIKING);
        dao.removeActivitiesByDateAndType(LocalDateTime.of(2011,4,4,4,4,4), ActivityType.BIKING);
        List<Activity> afterList = dao.listActivitiesByType(ActivityType.BIKING);

        assertEquals(11, wholeList.size());        //4 above + 7 here
        assertEquals(4, beforeList.size());
        assertEquals(2, afterList.size());
    }

}
