/*
import com.google.gson.Gson;

public class MapGSON2 {

    private MapJsonFinal MapObj;

    public void serialize() {
        MapObj = new MapJsonFinal();

        MapObj.setVersion(5);
        MapObj.setBuildingCount(77);

        MapObj.Building school = Building.new Building();
    }

        
        


            Student.Name name = student.new Name();

            name.firstName = "Mahesh";
            name.lastName = "Kumar";
            student.setName(name);
            Gson gson = new Gson();

            String jsonString = gson.toJson(student);
            System.out.println(jsonString);
            student = gson.fromJson(jsonString, Student.class);

            System.out.println("Roll No: "+ student.getRollNo());
            System.out.println("First Name: "+ student.getName().firstName);
            System.out.println("Last Name: "+ student.getName().lastName);

            String nameString = gson.toJson(name);
            System.out.println(nameString);

            name = gson.fromJson(nameString,Student.Name.class);
            System.out.println(name.getClass());
            System.out.println("First Name: "+ name.firstName);
            System.out.println("Last Name: "+ name.lastName);
        }
    }

class MapJsonFinal {

    private int BuildingCount;
    private int Version;

    private Building[] BuildingList;


    public void setBouildingList(Building[] BL) {
        this.BuildingList = BL;
    }

    public Building[] getBuildingList() {
        return BuildingList;
    }

    public int getBuildingCount() {
        return BuildingCount;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public void setBuildingCount(int buildingCount) {
        BuildingCount = buildingCount;
    }

    class Building {
        public int posX;
        public int posY;

        public int width;
        public int height;
    }

}
}

 */
