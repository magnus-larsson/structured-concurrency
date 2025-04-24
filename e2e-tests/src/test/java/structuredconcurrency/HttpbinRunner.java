package structuredconcurrency;

import com.intuit.karate.junit5.Karate;

class HttpbinRunner {
    
    @Karate.Test
    Karate testUsers() {
        return Karate.run("httpbin.feature").relativeTo(getClass());
    }    

}
