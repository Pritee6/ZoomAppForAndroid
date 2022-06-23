package us.zoom.sdkexample2;

public interface Constants {

	// TODO Change it to your web domain
    String WEB_DOMAIN = "zoom.us";

	// TODO change it to your user ID
    String USER_ID = "Your user ID from REST API";
	
	// TODO change it to your token
    String ZOOM_ACCESS_TOKEN = "Your zak from REST API";
	
	// TODO Change it to your exist meeting ID to start meeting
    String MEETING_ID = "9992743515";
    String MEETING_PASSWORD = "8jDsH3";
    String SDK_KEY = "OcGYWDjNVm1gEdE1BVDG8m8Tzkof6hDhSKiO";
    String SDK_SECRET = "acdGfr2XCtIzCj1NVySIy5SzhGOnVvmJLASo";
    String DISPLAY_NAME = "Pritee Mali";
    /**
     * We recommend that, you can generate jwttoken on your own server instead of hardcore in the code.
     * We hardcore it here, just to run the demo.
     *
     * You can generate a jwttoken on the https://jwt.io/
     * with this payload:
     * {
     *     "appKey": "string", // app key
     *     "iat": long, // access token issue timestamp
     *     "exp": long, // access token expire time
     *     "tokenExp": long // token expire time
     * }
     */
    //public final static String SDK_JWTTOKEN = YOUR JWTTOKEN;

}
