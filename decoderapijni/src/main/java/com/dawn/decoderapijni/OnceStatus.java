package com.dawn.decoderapijni;

public class OnceStatus {

        int wait;
        int decode;
        int status;

        public OnceStatus(int wait, int decode, int status) {
            this.wait = wait;
            this.decode = decode;
            this.status = status;
        }

        @Override
        public String toString() {
            return "{" +
                    "wait=" + wait/1000 +
                    "ms, decode=" + decode/1000 +
                    "ms, status=" + status +
                    "}";
        }

}
