import java.util.*;

public class Main {
    public static void main(String args[]) {
        HomesMap map = new HomesMap();
        Jaio jaio = new Jaio(map.getJaiosHome(), map);
        TravelTime totalTime = new TravelTime(0);

        while(!jaio.isGetsComplete()) {
            HomeAndTravelTime visitHomeAndTime = jaio.next();
            jaio.gets((FriendsHome)visitHomeAndTime.getHome());
            totalTime = totalTime.add(visitHomeAndTime.getTime());
            System.out.println("経過時間：" + totalTime.getTime());
        }

        HomeAndTravelTime myhomeAndTime = jaio.goHome();
        totalTime = totalTime.add(myhomeAndTime.getTime());

        System.out.println("合計時間：" + totalTime.getTime());

    }
}

/**
 * 地図上の家
 */
class Home {
    /**
     * 移動時間表のid
     */
    private final int id;

    /**
     * 家の名前
     */
    private final char name;

    Home(int id, char name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    char getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Home home = (Home) o;

        if (id != home.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

/**
 * ジャイ夫君の家
 */
class JaiosHome extends Home {

    JaiosHome(int id, char name) {
        super(id, name);
    }
}

/**
 * 心の友の家
 */
class FriendsHome extends Home {
    FriendsHome(int id, char name) {
        super(id, name);
    }

    public void visit() {

    }
}

/**
 * 移動時間
 */
class TravelTime {
    private final int time;

    public TravelTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public TravelTime add(TravelTime travelTime) {
        return new TravelTime(this.time + travelTime.time);
    }
}

/**
 * 家と距離の地図
 */
class HomesMap {
    /**
     * ジャイ夫の家
     */
    private JaiosHome jaiosHome;

    /**
     * 心の友の家
     */
    private List<FriendsHome> friendsHomes = new LinkedList<>();

    public Home getJaiosHome() {
        return jaiosHome;
    }

    List<FriendsHome> getFriendsHomes() {
        return friendsHomes;
    }

    /**
     * ある家から他の家までの移動時間のリスト
     */
    private Map<Home, List<HomeAndTravelTime>> travelTimeMap = new HashMap<>();


    /** 指定された家から、他の家への距離のリストを取得する */
    public List<HomeAndTravelTime> getTravelTimeList(Home from) {
        return travelTimeMap.get(from);
    }

    public HomesMap() {
        final int[][] TravelTimes = {
                {0, 10, 20, 7, 15, 24, 18, 22},
                {10, 0, 11, 5, 8, 20, 21, 15},
                {20, 11, 0, 10, 12, 6, 25, 20},
                {7, 5, 10, 0, 9, 17, 15, 13},
                {15, 8, 12, 9, 0, 7, 10, 6},
                {24, 20, 6, 17, 7, 0, 14, 10},
                {18, 21, 25, 15, 10, 14, 0, 5},
                {22, 15, 20, 13, 6, 10, 5, 0},
        };

        // 地図上の家のセットアップ
        this.jaiosHome = new JaiosHome(0, 'A');
        int id;
        char name;
        for (id = 1, name = 'B'; id < 8; id++, name++) {
            this.friendsHomes.add(new FriendsHome(id, name));
        }

        // ある家から他の家への移動時間のセットアップ
        List<Home> homesOnMap = new LinkedList<>();
        homesOnMap.add(jaiosHome);
        homesOnMap.addAll(friendsHomes);
        for (id = 0; id < homesOnMap.size(); id++) {
            Home from = homesOnMap.get(id);
            int[] travelTime = TravelTimes[from.getId()];
            List<HomeAndTravelTime> travelTimeList = new ArrayList<>();
            for (int i = 0; i < travelTime.length; i++) {
                if (travelTime[i] == 0) continue;

                travelTimeList.add(new HomeAndTravelTime( homesOnMap.get(i), travelTime[i]));
            }
            this.travelTimeMap.put(from, travelTimeList);
        }
    }
}

/**
 * 他の家までの移動時間
 */
class HomeAndTravelTime implements Comparable<HomeAndTravelTime>{
    private final Home home;
    private final TravelTime travelTime;

    HomeAndTravelTime( Home home, int travelTime) {
        this.travelTime = new TravelTime(travelTime);
        this.home = home;
    }

    public TravelTime getTime() {
        return travelTime;
    }

    public Home getHome() {
        return home;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ) return false;
        if (o instanceof Home) return this.getHome().equals(o);
        if (getClass() != o.getClass()) return false;

        HomeAndTravelTime that = (HomeAndTravelTime) o;

        if (!home.equals(that.home)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return home.hashCode();
    }

    @Override
    public int compareTo(HomeAndTravelTime t) {
        return this.getTime().getTime() - t.getTime().getTime();
    }
}

/**
 * ジャイ夫君
 */
class Jaio {
    Jaio(Home currentPoint, HomesMap map ) {
        this.currentPoint = currentPoint;
        this.targetHomes = map.getFriendsHomes();
        this.myHome = map.getJaiosHome();
        this.map = map;
    }

    /** ジャイ夫君の家 */
    private Home myHome;

    /** 地図 */
    private HomesMap map;

    /** ジャイ夫君の現在地 */
    private Home currentPoint;

    /** ジャイ夫君がおせちをGet'sしに廻る家 */
    private List<FriendsHome> targetHomes;

    /** 全ての家からGet'sしたかを返す */
    public boolean isGetsComplete() {
        return targetHomes.isEmpty();
    }

    /** おせちをGet'sする*/
    public void gets(FriendsHome getsHome) {
        System.out.println(getsHome.getName() + "でおせちをGet's.");
        this.targetHomes.remove(getsHome);
        this.currentPoint = getsHome;
    }

    /** 次にGet'sする家を決める */
    public HomeAndTravelTime next() {
        List<HomeAndTravelTime> targets = new ArrayList<>(map.getTravelTimeList(this.currentPoint));
        targets.retainAll(this.targetHomes);
        if(targets.isEmpty()) return null;
        return Collections.min(targets);
    }

    public HomeAndTravelTime goHome() {
        List<HomeAndTravelTime> travelTimeList = map.getTravelTimeList(this.currentPoint);
        return travelTimeList.get(travelTimeList.indexOf(this.myHome));
    }

}
