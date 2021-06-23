public class RequestType {
        static final public int TEXT_TYPE = 17;         // message to show on server
        static final public int PING_TYPE = 255;        // just ping response
        static final public int GAME_TYPE = 10;         // simulation package
        static final public int PAIR_TYPE = 2;          // attempt to pair
        static final public int UNPAIR_TYPE = 3;        // unpair
        static final public int FAILPAIR_TYPE = 4;      // failed to pair
        static final public int LIST_TYPE = 11;         // data with user list
}

// TODO create pair structure

/*
PAIR_TYPE:
when server receives this, it checks - possible to pair?
if possible to pair - server sends PAIR_TYPE request to client-to-pair
and waits response for 10 seconds. !!! (create timer to wait???)
if no response - send to first client request FAILPAIR_TYPE
if positive - start transferring GAME_TYPE requests
 */
