package hyman.entity;

public class ResponseData {

    private String state;
    private String msg;
    private Object data;

    public ResponseData(){

    }
    public ResponseData(String state){
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static class ResponseState{
        public static String FAILED_STATE = "0";
        public static String SUCESS_STATE = "1";
    }


}
