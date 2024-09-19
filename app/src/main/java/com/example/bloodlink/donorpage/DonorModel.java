//package com.example.bloodlink.donorpage;
//
//import android.widget.ImageButton;
//
//public class DonorModel {
//    String acceptButtonText;
//
//    int img;
//    boolean acceptButtonVisible; //yo ta visible grna lagi ho
//    String name ,bloodgroup,location , pints, age,imageButton;
//    public DonorModel(int img, String name, String age, String bloodgroup,String pints ,String location) {
//        // Initialize other properties as before
//        this.img = img;
//        this.name = name;
//        this.age = age;
//        this.bloodgroup=bloodgroup;
//        this.pints=pints;
//        this.location=location;
//       // this.acceptButtonVisible = true;// Set the button initially visible yo t button visible garna lagi ho but hamilai button cahiyo
//// Initialize other properties as before
//        this.acceptButtonText = "Accept"; // Initial button text
//
//    }
//    //yo hamro visibility ko lagi  ho
////    public boolean isAcceptButtonVisible() {
////        return acceptButtonVisible;
////    }
////    public void setAcceptButtonVisible(boolean acceptButtonVisible) {
////        this.acceptButtonVisible = acceptButtonVisible;
////    }
//
//    public String getAcceptButtonText() {
//        return acceptButtonText;
//    }
//
//
//    public void setAcceptButtonText(String acceptButtonText) {
//        this.acceptButtonText = acceptButtonText;
//    }
//
//    public int getImg() {
//        return img;
//    }
//
//    public void setImg(int img) {
//        this.img = img;
//    }
//
//    public boolean isAcceptButtonVisible() {
//        return acceptButtonVisible;
//    }
//
//    public void setAcceptButtonVisible(boolean acceptButtonVisible) {
//        this.acceptButtonVisible = acceptButtonVisible;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getBloodgroup() {
//        return bloodgroup;
//    }
//
//    public void setBloodgroup(String bloodgroup) {
//        this.bloodgroup = bloodgroup;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public String getPints() {
//        return pints;
//    }
//
//    public void setPints(String pints) {
//        this.pints = pints;
//    }
//
//    public String getAge() {
//        return age;
//    }
//
//    public void setAge(String age) {
//        this.age = age;
//    }
//
//    public String getImageButton() {
//        return imageButton;
//    }
//
//    public void setImageButton(String imageButton) {
//        this.imageButton = imageButton;
//    }
//}
//
//

package com.example.bloodlink.donorpage;

public class DonorModel {
    private String name;
    private String age;
    private String bloodgroup;
    private int pints;
    private String location;
    private String acceptButtonText;
    private String requestId;

    public DonorModel(String name, String age, String bloodgroup, int pints, String location, String requestId) {
        this.name = name;
        this.age = age;
        this.bloodgroup = bloodgroup;
        this.pints = pints;
        this.location = location;
        this.acceptButtonText = "Accept"; // Default value, change as needed
        this.requestId = requestId;
    }

    // Getters and setters as needed

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public int getPints() {
        return pints;
    }

    public void setPints(int pints) {
        this.pints = pints;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAcceptButtonText() {
        return acceptButtonText;
    }

    public void setAcceptButtonText(String acceptButtonText) {
        this.acceptButtonText = acceptButtonText;
    }
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
