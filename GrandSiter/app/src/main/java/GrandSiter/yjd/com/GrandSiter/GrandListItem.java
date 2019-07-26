package GrandSiter.yjd.com.GrandSiter;

public class GrandListItem {
    private String name;
    private String age;
    private String gender;
    private String characteristic;
    private String id;

    public GrandListItem(String id, String name, String age, String gender, String characteristic){
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.characteristic = characteristic;
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setName(String text){
        name = text;
    }
    public String getName(){
        return this.name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public void setId(String id){this.id = id;}

    public String getId(){return id;}

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }
}
