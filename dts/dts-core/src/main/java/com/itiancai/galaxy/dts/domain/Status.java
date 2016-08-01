package com.itiancai.galaxy.dts.domain;

/**
 * Created by lsp on 16/7/28.
 *
 * 事务状态
 */
public class Status {

    public enum Activity {
        UNKNOWN(0), SUCCESS(2), FAILL(3);

        private int status;

        Activity(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    public enum Action {
        UNKNOWN(0), PREPARE(1), SUCCESS(2), FAILL(3);

        private int status;

        Action(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public static Action getStatusAction(int status){
            for(Action action : Action.values()){
                if(action.getStatus() == status){
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
