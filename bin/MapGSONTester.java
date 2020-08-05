import com.google.gson.Gson;


    public class MapGSONTester {


        private MapJson MapObj;

        public MapGSONTester(){
            MapObj = new MapJson();

            MapObj.setBuildingCount(77);
            MapObj.setVersion(3);

            MapJson.Building school = MapObj.new Building();
            MapJson.Building club = MapObj.new Building();
            MapJson.Building home = MapObj.new Building();

            school.posX = 200;
            school.posY = 300;
            MapObj.setSchool(school);

            club.posX = 700;
            club.posY = 800;
            MapObj.setSchool(club);

            home.posX = 200;
            home.posY = 800;
            MapObj.setSchool(home);


            Gson gson = new Gson();

            String jsonString = gson.toJson(MapObj);
            System.out.println(jsonString);
            MapObj = gson.fromJson(jsonString, MapJson.class);

            System.out.println("Version: "+ MapObj.getVersion());
            System.out.println("Schule: "+ MapObj.getSchool());


        }
    }

    class MapJson {

        private int BuildingCount;
        private int Version;

        private Building school;
        private Building home;
        private Building club;


        public void setBuildingCount(int buildingCount) {
            BuildingCount = buildingCount;
        }
        public void setVersion(int version) {
            Version = version;
        }

        public int getBuildingCount() {
            return BuildingCount;
        }
        public int getVersion() {
            return Version;
        }


        public void setSchool(Building school) {
            this.school = school;
        }
        public void setHome(Building home) {
            this.home = home;
        }
        public void setClub(Building club) {
            this.club = club;
        }

        public Building getSchool(){
            return school;
        }
        public Building getHome(){
            return home;
        }
        public Building getClub(){
            return club;
        }


        class Building {
            public int posX;
            public int posY;

        }

    }

