package com.example.bloodlink.requestedpage;


    public class RequesterModel {
        private final Long phone;
        private String name;
        private String bloodGroup;
        private int pints;
        private double latitude;
        private double longitude;

        public RequesterModel(String name, String bloodGroup, int pints, double latitude, double longitude,Long phone) {
            this.name = name;
            this.bloodGroup = bloodGroup;
            this.pints = pints;
            this.latitude = latitude;
            this.longitude = longitude;
            this.phone = phone;
        }

        // Getter and Setter methods
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public int getPints() {
            return pints;
        }

        public void setPints(int pints) {
            this.pints = pints;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }



        public Long getPhone() {
            return phone;
        }

        public void setPhone(Long phone) {
            this.longitude = longitude;
        }

    }


