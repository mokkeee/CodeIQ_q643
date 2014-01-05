import java.util.*;

public class Main {
    /**
     * ジャイ夫の家
     */
    private static final JaiosHome jaiosHome;

    /**
     * 心の友の家
     */
    private static final List<Home> friendsHomes;

    /**
     * ある家から他の家までの移動時間のリスト
     */
    public static final Map<Home, List<HomeAndTravelTime>> travelTimeMap;

    static {
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
        jaiosHome = new JaiosHome(0, 'A');
        int id;
        char name;
        List<Home> tmpList = new ArrayList<>();
        for (id = 1, name = 'B'; id < 8; id++, name++) {
            tmpList.add(new FriendsHome(id, name));
        }
        friendsHomes = Collections.unmodifiableList(tmpList);

        // ある家から他の家への移動時間のセットアップ
        Map<Home, List<HomeAndTravelTime>> tmpMap = new HashMap<>();
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
            tmpMap.put(from, travelTimeList);
        }
        travelTimeMap = Collections.unmodifiableMap(tmpMap);
    }

    public static void main(String args[]) {
        Jaio jaio = new Jaio(jaiosHome, friendsHomes);

        while(!jaio.isGetsComplete()) {
            HomeAndTravelTime visitHomeAndTime = jaio.next();
            jaio.gets(visitHomeAndTime);
        }
        jaio.goHome();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.MINUTE, jaio.getTotalTime().getTime());
        System.out.println("帰宅時間：" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
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

        return id == home.id;
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
}

/**
 * 移動時間
 */
class TravelTime implements Comparable<TravelTime> {
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

    @Override
    public int compareTo(TravelTime o) {
        return this.time - o.time;
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
    public int compareTo(HomeAndTravelTime t) {
        return this.getTime().compareTo(t.getTime());
    }
}

/**
 * ジャイ夫君
 */
class Jaio {
    Jaio(Home jaiosHome, List<Home> friendsHomes) {
        this.myHome = jaiosHome;
        this.targetHomes = new ArrayList<>(friendsHomes);
        this.currentPoint = this.myHome;
    }

    /** ジャイ夫君の家 */
    private final Home myHome;

    /** ジャイ夫君がおせちをGet'sしに廻る家 */
    private List<Home> targetHomes;

    /** ジャイ夫君の現在地 */
    private Home currentPoint;

    /** ジャイ夫君の移動時間 */
    private TravelTime totalTime = new TravelTime(0);
    TravelTime getTotalTime() {
        return totalTime;
    }

    /** 全ての家からGet'sしたかを返す */
    public boolean isGetsComplete() {
        return targetHomes.isEmpty();
    }

    /** おせちをGet'sする*/
    public void gets(HomeAndTravelTime homeAndTime) {
        this.totalTime = this.totalTime.add(homeAndTime.getTime());
        this.currentPoint = homeAndTime.getHome();
        System.out.println(this.currentPoint.getName() + "でおせちをGet's.");
        boolean b = this.targetHomes.remove(this.currentPoint);
        assert b;
    }

    /** 次にGet'sする家を決める */
    public HomeAndTravelTime next() {
        List<HomeAndTravelTime> targets = getCandidateList();
        if(targets.isEmpty()) return null;
        return Collections.min(targets);
    }

    /** 家に戻る */
    public void goHome() {
        HomeAndTravelTime homeAndTime = getHome();
        System.out.println(this.myHome.getName() + "に帰宅");
        this.totalTime = this.totalTime.add(homeAndTime.getTime());
    }

    private HomeAndTravelTime getHome() {
        for(HomeAndTravelTime homeAndTime : Main.travelTimeMap.get(this.currentPoint)) {
            if(homeAndTime.getHome().equals(this.myHome)) return homeAndTime;
        }
        assert false;
        return null;
    }

    /**
     * 次のGet's先候補のリストを取得する
     * @return 次のGet's先候補のリスト
     */
    private List<HomeAndTravelTime> getCandidateList() {
        List<HomeAndTravelTime> list = new ArrayList<>();
        for(HomeAndTravelTime homeAndTime : Main.travelTimeMap.get(this.currentPoint)) {
            if(this.targetHomes.contains(homeAndTime.getHome())) {
                list.add(homeAndTime);
            }
        }
        return list;
    }
}
