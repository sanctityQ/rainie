package com.itiancai.galaxy.dts.domain;

public class Status {

    public enum Activity {
        UNKNOWN(0),SUCCESS(2), FAILL(3);

        private final int statusActivity;

        Activity(int statusActivity) {
            this.statusActivity = statusActivity;
        }

        public int getStatusActivity() {
            return statusActivity;
        }
        public static Activity getStatusActivity(int status){
            for(Activity activity : Activity.values()){
                if(activity.getStatusActivity() == status){
                    return activity;
                }
            }
            return null;
        }
    }

    public enum Action {
        UNKNOWN(0), PREPARE(1), SUCCESS(2), FAILL(3);

        public final int statusAction;

        Action(int statusAction) {
            this.statusAction = statusAction;
        }

        public int getStatusAction() {
            return statusAction;
        }

        public static Action getStatusAction(int status){
            for(Action action : Action.values()){
                if(action.getStatusAction() == status){
                    return action;
                }
            }
            return null;
        }

    }

    public static void main(String []args){
        System.out.println(Action.getStatusAction(1).name());
    }
}
