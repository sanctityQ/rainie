package com.itiancai.galaxy.dts.domain;

public class Status {

    public enum Activity {
        UNKNOWN(0),SUCCESS(2), FAIL(3);

        private final int status;

        Activity(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
        public static Activity getStatus(int status){
            for(Activity activity : Activity.values()){
                if(activity.getStatus() == status){
                    return activity;
                }
            }
            return null;
        }
    }

    public enum Action {
        UNKNOWN(0), PREPARE(1), SUCCESS(2), FAIL(3);

        public final int status;

        Action(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public static Action getStatus(int status){
            for(Action action : Action.values()){
                if(action.getStatus() == status){
                    return action;
                }
            }
            return null;
        }

    }

    public static void main(String []args){
        System.out.println(Action.getStatus(1).name());
    }
}
